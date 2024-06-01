<?php

namespace App\MyHelpers;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ImageSaverForJava extends AbstractController
{

    #[Route('/api/upload-image', name: 'api_upload_image')]
    public function uploadImage(Request $request): Response
    {
        $fileParameters = $request->files->get('image');
        if ($fileParameters) {
            $newFilename = md5(uniqid()) . '.' . $fileParameters->guessExtension();
            $fileParameters->move('../public/usersImg/', $newFilename);
            return new JsonResponse(['path' =>$newFilename],Response::HTTP_OK);
        }
        return new JsonResponse(['error' =>'error'], Response::HTTP_BAD_REQUEST);
    }

}