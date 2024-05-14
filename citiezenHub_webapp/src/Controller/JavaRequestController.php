<?php

namespace App\Controller;

use App\MyHelpers\AiVerificationMessage;
use App\Repository\ProductRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Routing\Annotation\Route;

class JavaRequestController extends AbstractController
{
    #[Route('/java/request', name: 'app_java_request')]
    public function index(): Response
    {
        return $this->render('java_request/index.html.twig', [
            'controller_name' => 'JavaRequestController',
        ]);
    }

    #[Route('/java/request/verifyProduct', name: 'app_java_request')]
    public function verifyProduct(Request $request,ProductRepository $productRepository,MessageBusInterface $messageBus,): Response
    {
        $product=$productRepository->findOneBy(['idProduct'=>$request->get('idProduct')]);
        $newImagesPath=[];
        foreach ($product->getImages() as $image) {
            $newImagesPath[]=str_replace("usersImg/",'',$image->getPath());
        }
        $obj = [
            'title' => $product->getName(),
            'category' => $product->getCategory(),
            'id' => $product->getIdProduct(),
            'images' => $newImagesPath,
            'mode' => 'add'
        ];

        $messageBus->dispatch(new AiVerificationMessage($obj));
        return new Response('done', Response::HTTP_OK);
    }
}
