<?php

namespace App\Controller;

use App\MyHelpers\UserVerifierMessage;
use App\Repository\TransactionRepository;
use App\Service\GeocodingService;
use Carbon\Traits\Timestamp;
use Knp\Bundle\SnappyBundle\Snappy\Response\PdfResponse;
use Knp\Snappy\Pdf;
use phpDocumentor\Reflection\Types\This;
use Symfony\Component\Filesystem\Filesystem;
use Symfony\Component\HttpClient\HttpClient;
use Symfony\Component\HttpFoundation\StreamedResponse;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Messenger\Stamp\DelayStamp;
use Symfony\Component\Translation\Translator;
use App\Entity\Municipalite;
use App\MyHelpers\AiVerification;
use App\MyHelpers\ImageHelperUser;
use App\Repository\MunicipaliteRepository;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Ivory\GoogleMap\Map;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\Uuid;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Symfony\Contracts\HttpClient\Exception\TransportExceptionInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Twig\Environment;
use Dompdf\Dompdf;
use Dompdf\Options;


class UserController extends AbstractController
{
    #[Route('/user', name: 'app_user', methods: ['GET', 'POST'])]
    public function index(): Response
    {
        return $this->render('user/index.html.twig', [
            'controller_name' => 'UserController',
        ]);
    }



    #[Route('/user/generatePdfWithoutMail/{idUser}', name: 'app_user_generatePdfWithoutMail')]
    public function generatePdfWithoutMail($idUser,UserRepository $userRepository,HttpClientInterface $client,Pdf $knpSnappyPdf): PdfResponse|Response
    {
        $user=$userRepository->find($idUser);
        $path = '../../files/usersJsonFiles/' . md5('user' . ($idUser * 1000 + 17)) . '.json';
        $filesystem = new Filesystem();
        if (!$filesystem->exists([$path]))
            return new Response('error',Response::HTTP_BAD_REQUEST);

        $jsonString = file_get_contents($path);
        $jsonDataCin = json_decode($jsonString, true);
        $geocoder = new GeocodingService($client);
        $addressDetails=$geocoder->getMadhmounData($jsonDataCin['العنوان']['data']);


        $html = $this->renderView('user_dashboard/madhmoun.html.twig',[
            'firstName'=>$jsonDataCin['الاسم']['data'],
            'lastName'=>$jsonDataCin['اللقب']['data'],
            'dob'=>$jsonDataCin['الولادة']['data'],
            'location'=>$jsonDataCin['مكانها']['data'],
            'gender'=>($user->getGender()=='Male')?'ذكر':'أنثى',
            'fatherName'=>$jsonDataCin['بن']['data'],
            'motherName'=>$jsonDataCin['الأم']['data'],
            'addressDetails'=>$addressDetails,
        ]);

        return new PdfResponse(
            $knpSnappyPdf->getOutputFromHtml($html),
            'file.pdf'
        );
//        $pdfOutput = $knpSnappyPdf->getOutputFromHtml($html);
//
//        return new Response(
//            $pdfOutput,
//            200,
//            [
//                'Content-Type' => 'application/pdf',
//                'Content-Disposition' => 'attachment; filename="file.pdf"'
//            ]
//        );
    }

    #[Route('/cinTimeInfo', name: 'app_cinTimeInfo', methods: ['GET', 'POST'])]
    public function cinTimeInfo(Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $user = $this->getUser();
            if ($user->getIsVerified() == 0) {
                $dateString = $user->getDate()->format('Y-m-d H:i:s');
                $userDate = \DateTime::createFromFormat('Y-m-d H:i:s', $dateString);
                $now = new \DateTime();
                $timeDifferenceMillis = 48 * 3600 * 1000 - ($now->getTimestamp() * 1000 - $userDate->getTimestamp() * 1000);
                $hours = intdiv($timeDifferenceMillis, 3600 * 1000);
                $minutes = intdiv($timeDifferenceMillis % (3600 * 1000), 60 * 1000);
                return new JsonResponse(['state' => 'notVerified', 'hours' => $hours, 'minutes' => $minutes], Response::HTTP_OK);
            } else
                return new JsonResponse(['state' => 'verified'], Response::HTTP_OK);
        }
        return new Response('error', Response::HTTP_FORBIDDEN);
    }


    #[Route('/verifyInfoCinWithOtherInfo', name: 'app_verifyInfoCinWithOtherInfo', methods: ['GET', 'POST'])]
    public function verifyInfoCinWithOtherInfo(HttpClientInterface $client, $user): string
    {
        $geocoder = new GeocodingService($client);
        $path = '../../files/usersJsonFiles/' . md5('user' . ($user->getId() * 1000 + 17)) . '.json';
        $filesystem = new Filesystem();
        if (!$filesystem->exists([$path]))
            return 'none';

        $jsonString = file_get_contents($path);
        $jsonDataCin = json_decode($jsonString, true);

        if (trim($jsonDataCin['الولادة']['data']) !== $user->getDob()->format('m-d-Y'))
            return 'error_dob';

        if (trim($jsonDataCin['cart id']['data']) !== $user->getCin())
            return 'error_cin';

        if (!$geocoder->isInMunicipality($jsonDataCin['العنوان']['data'], $user->getMunicipalite()->getName()))
            return 'error_location';

        return 'success';
    }


    private function updateState(MessageBusInterface $messageBus, EntityManagerInterface $entityManager, $state, $user)
    {
        $user->setIsVerified(intval($state));
        $user->setState($state);
        $user->setUMID(Uuid::v4()->toBase32());
        $entityManager->flush();

        if ($state == 0) {
            $obj = [
                'idUser' => $user->getId(),
                'UMID' => $user->getUMID()
            ];
            $delayInSeconds = 1 * 60;
            $messageBus->dispatch(new UserVerifierMessage($obj), [new DelayStamp($delayInSeconds * 1000),]);
        }
    }

    #[Route('/java/testForJava', name: 'app_user_testForJava', methods: ['GET', 'POST'])]
    public function testForJava(Request $request, HttpClientInterface $client, MessageBusInterface $messageBus, EntityManagerInterface $entityManager, UserRepository $userRepository): Response
    {
        $user = $userRepository->findOneBy(['idUser' => $request->get('idUser')]);

        $returnedData = $this->verifyInfoCinWithOtherInfo($client, $user);
        $this->updateState($messageBus, $entityManager, $returnedData == 'success' ? 1 : 0, $user);

        return new Response($returnedData, Response::HTTP_OK);
    }


    #[Route('/cinUpdate', name: 'app_user_cinUpdate', methods: ['GET', 'POST'])]
    public function cinUpdate(MessageBusInterface $messageBus, HttpClientInterface $client, AiVerification $aiVerification, Request $request, EntityManagerInterface $entityManager, ImageHelperUser $imageHelperUser): Response
    {
        if ($request->isXmlHttpRequest()) {

            $frontId = $request->files->get('frontId');
            $backId = $request->files->get('backId');
            $frontIdPath = $imageHelperUser->saveImages($frontId);
            $backIdPath = $imageHelperUser->saveImages($backId);
            $user = $this->getUser();
            $user->setCinImages($frontIdPath . '_' . $backIdPath);
            $entityManager->flush();
            $obj = [
                'pathFrontCin' => 'C:\\Users\\omar salhi\\Desktop\\integratedPorjectJavaSymfony\\citiezenHub_webapp\\public\\usersImg\\' . $frontIdPath,
                'pathBackCin' => 'C:\\Users\\omar salhi\\Desktop\\integratedPorjectJavaSymfony\\citiezenHub_webapp\\public\\usersImg\\' . $backIdPath,
                'fileNameFront' => md5('user_front' . ($user->getId() * 1000 + 17)),
                'fileNameBackCin' => md5('user_backCin' . ($user->getId() * 1000 + 17)),
                'path' => md5('user' . ($user->getId() * 1000 + 17)),
            ];
            try {
                $aiVerification->runOcr($obj);
            } catch (\Exception $exception) {
                $this->updateState($messageBus, $entityManager, 0,$this->getUser());
                return new Response('error', Response::HTTP_OK);
            }

            $returnedData = $this->verifyInfoCinWithOtherInfo($client, $this->getUser());
            $this->updateState($messageBus, $entityManager, $returnedData == 'success' ? 1 : 0, $this->getUser());

            return new Response($returnedData, Response::HTTP_OK);
        }
        return new Response('error', Response::HTTP_FORBIDDEN);
    }


    #[Route('/updateAddress', name: 'app_user_updateAddress', methods: ['GET', 'POST'])]
    public function updateAddress(MessageBusInterface $messageBus, HttpClientInterface $client, Request $request, EntityManagerInterface $entityManager, MunicipaliteRepository $municipalityRepository): Response
    {
//        $address=$this->userDate();
        if ($request->isXmlHttpRequest()) {
            $municipalityName = $request->get('municipality');
            $mapAddress = $request->get('mapAddress');
            $municipalityAddress = $request->get('municipalityAddressNew');
            $state = $request->get('state');
            if (!$this->getUser()) {
                return new Response('error', Response::HTTP_FORBIDDEN);
            }
            $user = $this->getUser();
            $user->setAddress($mapAddress);
            if ($user->getMunicipalite() !== null && $user->getMunicipalite()->getName() === $municipalityName) {
                $entityManager->flush();
                return new Response('done', Response::HTTP_OK);
            }

            $municipality = new Municipalite();
            $municipality->setName($municipalityName);
            $municipality->setAddress($municipalityAddress);
            $municipality->setGoverment($state);
            $user->setMunicipalite($municipality);
            $entityManager->persist($municipality);
            $entityManager->flush();

            $returnedData = $this->verifyInfoCinWithOtherInfo($client, $this->getUser());
            $this->updateState($messageBus, $entityManager, $returnedData == 'success' ? 1 : 0, $this->getUser());

            return new Response($returnedData, Response::HTTP_OK);
        }
        return new Response('error', Response::HTTP_FORBIDDEN);
    }


    #[Route('/editProfile', name: 'editProfile', methods: ['GET', 'POST'])]
    public function editUser(MessageBusInterface $messageBus, HttpClientInterface $client, ImageHelperUser $imageHelperUser, UserRepository $rep, ManagerRegistry $doc, Request $req, ValidatorInterface $validator, ImageHelperUser $imageHelper, SessionInterface $session): Response
    {
        $user = $rep->findOneBy(['email' => $this->getUser()->getUserIdentifier()]);

        $routePrecedente = $req->headers->get('referer');
        $parsedUrl = parse_url($routePrecedente);
        $path = $parsedUrl['path'];
        $alertMessage = "Votre profil a été modifié avec succès !";
        $session->set('profile_alert_message', $alertMessage);
        $errorMessages = [];
        if ($req->isXmlHttpRequest()) {
//              $emailService->envoyerEmail($mailer);
            $email = $req->get('email');
            $name = $req->get('name');
            $lastname = $req->get('lastname');
            $age = $req->get('age');
            $gender = $req->get('gender');
            $status = $req->get('status');
            $cin = $req->get('cin');
            $phoneNumber = $req->get('phoneNumber');
            $date = $req->get('date');
            $fichierImage = $req->files->get('image');
            $user->setFirstName($name);
            $user->setLastName($lastname);
            $user->setAge($age);
            $user->setPhoneNumber($phoneNumber);
            $user->setCin($cin);
            $user->setStatus($status);
            $user->setGender($gender);
            if ($fichierImage != null)
                $user->setImage($imageHelper->saveImages($fichierImage));
            $datee = date_create($date);
            $user->setDob($datee);
            $errors = $validator->validate($user, null, 'creation');
            foreach ($errors as $error) {
                $field = $error->getPropertyPath();
                $errorMessages[$field] = $error->getMessage();
            }

            if (count($errors) === 0) {
                $em = $doc->getManager();
                $em->persist($user);
                $em->flush();

                $returnedData = $this->verifyInfoCinWithOtherInfo($client, $this->getUser());
                $this->updateState($messageBus, $em, $returnedData == 'success' ? 1 : 0, $this->getUser());

                return new Response($returnedData, Response::HTTP_OK);
            }
            return new JsonResponse([
                'success' => false,
                'errors' => $errorMessages,
            ], 422);
        }

        $map = new Map();

        return $this->render('user/edit_profile.html.twig', [
            'name' => $user->getFirstName(),
            'lastname' => $user->getLastName(),
            'email' => $user->getEmail(),
            'address' => $user->getAddress(),
            'cin' => $user->getCin(),
            'phoneNumber' => $user->getPhoneNumber(),
            'age' => $user->getAge(),
            'status' => $user->getStatus(),
            'image' => $user->getImage(),
            'gender' => $user->getGender(),
            'dob' => $user->getDob(),
            'errors' => $errorMessages,
            'routePrecedente' => $path,
            'date' => $user->getDate(),
            'map' => $map,
        ]);
    }

    #[Route('/editImage', name: 'editImage', methods: ['GET', 'POST'])]
    public function editUserImage(EntityManagerInterface $entityManager, Request $req, ImageHelperUser $imageHelper): Response
    {
        if ($req->isXmlHttpRequest()) {
            $fichierImage = $req->files->get('imagee');
            $user = $this->getUser();
            if ($fichierImage != null)
                $user->setImage($imageHelper->saveImages($fichierImage));
            $entityManager->flush();
            return new JsonResponse(['image'=>$user->getImage()], Response::HTTP_OK);
        }
        return new Response('bad');
    }

    #[Route('/delete', name: 'app_user_delete')]
    public function delete(ManagerRegistry $doctrine, UserRepository $userRepository, Request $req): Response
    {
        $user = $userRepository->findOneBy(['email' => $this->getUser()->getUserIdentifier()]);
        $em = $doctrine->getManager();
        $em->remove($user);
        $em->flush();
        return $this->redirectToRoute('app_login');
    }

    #[Route('/profile', name: 'profile', methods: ['GET', 'POST'])]
    public function consulterProfile(Request $req): Response
    {
        $name = $req->get('name');
        $lastname = $req->get('lastname');
        $email = $req->get('email');
        $image = $req->get('image');

        return $this->render('user/profile.html.twig', [
            'name' => $name,
            'lastname' => $lastname,
            'email' => $email,
            'image' => $image,
        ]);
    }

    #[Route('/changePassword', name: 'changePassword', methods: ['GET', 'POST'])]
    public function changePassword(UserPasswordHasherInterface $userPasswordHasher, ManagerRegistry $doc, UserRepository $userRepository, Request $req, ValidatorInterface $validator)
    {
        if ($req->isXmlHttpRequest()) {
            $errorMessages = [];
            $user = $this->getUser();
            $newPassword = $req->get('NewPass');
            $confirmPassword = $req->get('rePass');
            $oldPassword = $req->get('oldPass');
            if ($userPasswordHasher->isPasswordValid($user, $oldPassword) && strcmp($newPassword, $confirmPassword) == 0) {
                $hashedPassword = $userPasswordHasher->hashPassword(
                    $user,
                    $newPassword
                );
                $user->setPassword($newPassword);
                $errors = $validator->validate($user, null, 'editPassword');
                foreach ($errors as $error) {
                    $field = $error->getPropertyPath();
                    $errorMessages[$field] = $error->getMessage();
                    dump($field);
                }
                $errorMessages["other"] = 'nnnnnnnnnn';
            }
            $user->setPassword($hashedPassword);
            $em = $doc->getManager();
            $em->flush();

            return new JsonResponse([
                'success' => false,
                'errors' => count($errors),
                'errorMessages' => $errorMessages,
            ], 200);
        }
        return new Response('bad');
    }


    #[Route('/page404', name: 'page404', methods: ['GET', 'POST'])]
    public function loadPage404(): Response
    {
        return $this->render('user/404.html.twig', [

        ]);
    }


    #[Route('/GovrGet', name: 'GovrGet', methods: ['GET'])]
    public function getGovermentsMuni(MunicipaliteRepository $municipaliteRepository): Response
    {
        $govCount = $municipaliteRepository->findByGovernment();
        return $this->json($govCount);
    }

    #[Route('/userCountLastSixDays', name: 'userCountLastSixDays', methods: ['GET'])]
    public function getUserCountLastSixDays(UserRepository $userRepository): Response
    {
        $userCounts = $userRepository->getnbruser();
        return $this->json($userCounts);
    }

}

