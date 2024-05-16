<?php

namespace App\Controller;

use App\MyHelpers\AiDataHolder;
use App\MyHelpers\AiVerificationMessage;
use App\Repository\AiResultRepository;
use App\Repository\ProductRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;

class JavaRequestController extends AbstractController
{
    #[Route('/java/request', name: 'app_java_request')]
    public function index(): Response
    {
        return $this->render('java_request/index.html.twig', [
            'controller_name' => 'JavaRequestController',
        ]);
    }

    #[Route('/java/request/verifyProduct', name: 'app_java_request_verifyProduct')]
    public function verifyProduct(Request $request, ProductRepository $productRepository, MessageBusInterface $messageBus): Response
    {
        $product = $productRepository->findOneBy(['idProduct' => $request->get('idProduct')]);
        $mode=$request->get('mode');


        $newImagesPath = [];
        foreach ($product->getImages() as $image) {
            $newImagesPath[] = str_replace("usersImg/", '', $image->getPath());
        }
        $obj = [
            'title' => $product->getName(),
            'category' => $product->getCategory(),
            'id' => $product->getIdProduct(),
            'images' => $newImagesPath,
            'mode' => $mode
        ];

        $messageBus->dispatch(new AiVerificationMessage($obj));
        return new Response('done', Response::HTTP_OK);
    }


    #[Route('/java/request/verifyAiResultState', name: 'app_java_request_verifyAiResultState')]
    public function verifyAiResultState(Request $request, AiResultRepository $aiResultRepository): Response
    {
        $aiResult = $aiResultRepository->findOneBy(['idProduct' => $request->get('idProduct')]);
        if ($aiResult != null) {
            $serializer = new Serializer([new ObjectNormalizer()], [new JsonEncoder()]);
            $aiDataHolder = $serializer->deserialize($aiResult->getBody(), AiDataHolder::class, 'json');

            $result = [];
            $sub_result = [];
            for ($i = 0; $i < sizeof($aiDataHolder->getDescriptions()); $i++) {
                $sub_result['title'] = str_starts_with(strtolower($aiDataHolder->getTitleValidation()[$i]), " yes");
                $sub_result['titleData'] = $aiDataHolder->getTitleValidation()[$i];
                $sub_result['category'] = str_starts_with(strtolower($aiDataHolder->getCategoryValidation()[$i]), " yes");
                $sub_result['categoryData'] = $aiDataHolder->getCategoryValidation()[$i];
                $result[] = $sub_result;
            }
            return new JsonResponse(['doesItExist' => true, 'data' => $result], Response::HTTP_OK);
        }
        return new JsonResponse(['doesItExist' => false], Response::HTTP_OK);
    }
}
