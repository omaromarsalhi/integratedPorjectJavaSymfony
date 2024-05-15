<?php

namespace App\Controller;

use App\Entity\Reclamation;
use App\MyHelpers\ImageHelper;
use App\MyHelpers\ImageHelperUser;
use Doctrine\ORM\EntityManagerInterface;
use App\Repository\ReclamationRepository;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use DateTime;
use DateInterval;
use Psr\Log\LoggerInterface;
class ReclamationController extends AbstractController
{
    #[Route('/reclamation', name: 'app_reclamation')]
    public function index(): Response
    {
        $privateKey = substr(str_shuffle(str_repeat('0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ', rand(1, 10))), 0, 9);
        return $this->render('reclamation/contact.html.twig', [
            'controller_name' => 'ReclamationController',
            'privateKey' => $privateKey,

        ]);
    }


    #[Route('/reclamation/show', name: 'app_reclamation_show')]
    public function show(ReclamationRepository $reclamationRepository): Response
    {
        $user = $this->getUser();
        $reclamations = $reclamationRepository->findBy(['user' => $user]);

        return $this->render('reclamation/show.html.twig', [
            'reclamations' => $reclamations,
        ]);
    }



    #[Route('/reclamation/new', name: 'app_reclamation_new')]
    public function new(ImageHelperUser $imageHelperUser,Request $request,EntityManagerInterface $entityManager,ValidatorInterface $validator): Response
    {
        if($request->isXmlHttpRequest()) {

            $subject=$request->request->get('subject');
            $message=$request->request->get('message');
            $privatekey=$request->request->get('privatekey');
            $fichierImage = $request->files->get('image');
            $reclamation = new Reclamation();

            $reclamation->setSubject($subject);
            $reclamation->setUser($this->getUser());
            $reclamation->setDescription($message);
            $reclamation->setPrivateKey($privatekey);

            $reclamation->setImage($imageHelperUser->saveImages($fichierImage));


            $errors = $validator->validate($reclamation);
 
            if (count($errors) > 0) {
                // If validation fails, return the validation errors
                $validationMessages = [];
                foreach ($errors as $error) {
                    $validationMessages[] = $error->getMessage();
                }
                $responses = [
                   'messages' => $validationMessages,
                   'error' => 'VALIDATION_ERROR'            
               ];


            }
            $entityManager->persist($reclamation);
            $entityManager->flush();


            return new Response("done", Response::HTTP_OK);
        }


        return $this->render('reclamation/contact.html.twig', [
            'controller_name' => 'ReclamationController',
        ]);
    }

// Assuming you're using Symfony Framework


#[Route('/reclamation/delete/{id}', methods: ['POST'])]
public function deleteReclamation(ReclamationRepository $repo, EntityManagerInterface $em, LoggerInterface $logger, $id): JsonResponse
{
    try {
        $reclamation = $repo->find($id);
        if (!$reclamation) {
            return new JsonResponse(['success' => false, 'message' => 'Reclamation not found'], 404);
        }
        
        $em->remove($reclamation);
        $em->flush();
        return new JsonResponse(['success' => true]);
    } catch (\Exception $e) {
        $logger->error('Failed to delete reclamation: ' . $e->getMessage());
        return new JsonResponse(['success' => false, 'message' => 'Internal server error'], 500);
    }
}


/**
 * @Route("/api/reclamations/{id}", name="api_reclamation_show", methods={"GET"})
 */
public function apiShow(ReclamationRepository $reclamationRepository, $id): JsonResponse
{
    $reclamation = $reclamationRepository->find($id);
    if (!$reclamation) {
        return new JsonResponse(['message' => 'Reclamation not found'], Response::HTTP_NOT_FOUND);
    }

    // Assuming your Reclamation entity has a method to convert the data to an array format
    // You need to implement the toArray() method in your Reclamation entity
    return new JsonResponse($reclamation->toArray(), Response::HTTP_OK);
}


#[Route('/api/reclamations/update/{id}', name: 'api_reclamation_update', methods: ['POST'])]
public function apiUpdate(Request $request, ReclamationRepository $reclamationRepository, EntityManagerInterface $entityManager, $id): JsonResponse
{
    $data = json_decode($request->getContent(), true);
    $reclamation = $reclamationRepository->find($id);
    if (!$reclamation) {
        return new JsonResponse(['message' => 'Reclamation not found'], Response::HTTP_NOT_FOUND);
    }

    $reclamation->setSubject($data['subject'] ?? $reclamation->getSubject());
    $reclamation->setDescription($data['description'] ?? $reclamation->getDescription());
    // Add more fields as needed

    $entityManager->persist($reclamation);
    $entityManager->flush();

    return new JsonResponse([
        'id' => $reclamation->getId(),
        'subject' => $reclamation->getSubject(),
        'description' => $reclamation->getDescription(),
        'message' => 'Reclamation updated successfully'
    ], Response::HTTP_OK);
}


// In your controller
#[Route('/admin/reclamation', name: 'admin_reclamation')]
public function list(ReclamationRepository $reclamationRepository): Response
{
    $reclamations = $reclamationRepository->findAll();
    $stats = $reclamationRepository->getStatistics();

    // Current date
    $currentDate = new DateTime();
    // Date three months ago
    $threeMonthsAgo = (clone $currentDate)->sub(new DateInterval('P3M'));

    // Grouping by date and filtering last 3 months
    $dateCounts = [];
    $responseCounts = [];
    $responseCount = 0; // Initialize response count
    foreach ($reclamations as $reclamation) {
        $reclamationDate = $reclamation->getDate(); // Assuming $reclamation->getDate() returns a DateTime object
        if ($reclamationDate >= $threeMonthsAgo) {
            $date = $reclamationDate->format('Y-m-d');
            if (!isset($dateCounts[$date])) {
                $dateCounts[$date] = 0;
            }
            $dateCounts[$date]++;

            if ($reclamation->getReponses()->count() > 0) {
                if (!isset($responseCounts[$date])) {
                    $responseCounts[$date] = 0;
                }
                $responseCounts[$date]++;
                $responseCount++;
            }
        }
    }

    // Sort by date (optional)
    ksort($dateCounts);
    ksort($responseCounts);

    $labels = array_keys($dateCounts);
    $counts = array_values($dateCounts);
    $responseCounts = array_values($responseCounts);

    // Calculate the prediction for tomorrow
    $averageDailyReclamations = !empty($counts) ? array_sum($counts) / count($counts) : 0;
    $prediction = round($averageDailyReclamations); // Round to the nearest integer

    return $this->render('reponse/index.html.twig', [
        'reclamations' => $reclamations,
        'labels' => $labels,
        'counts' => $counts,
        'responseCounts' => $responseCounts,
        'prediction' => $prediction,
        'responseCount' => $responseCount // Pass the response count to the Twig template
    ]);
}
/**
 * @Route("/api/reclamations/{id}", name="api_reclamation_show", methods={"GET"})
 */
public function apiShow1(ReclamationRepository $reclamationRepository, $id): JsonResponse
{
    $reclamation = $reclamationRepository->find($id);
    if (!$reclamation) {
        return new JsonResponse(['message' => 'Reclamation not found'], Response::HTTP_NOT_FOUND);
    }

    $responseArray = [];
    foreach ($reclamation->getReponses() as $response) {
        $responseArray[] = [
            'id' => $response->getId(),
            'description' => $response->getDescription()
        ];
    }

    return new JsonResponse([
        'id' => $reclamation->getId(),
        'subject' => $reclamation->getSubject(),
        'description' => $reclamation->getDescription(),
        'privateKey' => $reclamation->getPrivateKey(),
        'user'=> $reclamation->getUser(),
        'image' => $reclamation->getImage(),
        'responses' => $responseArray
    ], Response::HTTP_OK);
}




}
