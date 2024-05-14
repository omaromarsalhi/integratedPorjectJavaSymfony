<?php

namespace App\MyHelpers;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ImageSaverForJava extends AbstractController
{

    #[Route('/api/upload-image', name: 'api_upload_image')]
    public function uploadImage(Request $request): Response
    {
        $uploadedFile = $request->files->get('image_url');

        if ($uploadedFile) {
            $newFilename = "omar_".md5(uniqid()) . '.' . $uploadedFile->guessExtension();
            $uploadedFile->move('../public/usersImg/', $newFilename);
            return new JsonResponse(['message' => 'Image uploaded successfully']);
        }
        return new JsonResponse(['error' =>dump($request)], Response::HTTP_BAD_REQUEST);
    }

}