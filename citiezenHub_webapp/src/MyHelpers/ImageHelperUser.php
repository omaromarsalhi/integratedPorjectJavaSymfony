<?php

namespace App\MyHelpers;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Request;


class ImageHelperUser
{
    private $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }


    public  function saveImages($file): string
    {

            $fileName = md5(uniqid()) . '.' . $file->guessClientExtension();
            $file->move(
                '../public/usersImg/',
                $fileName
            );

        return $fileName;
    }
    function traduireNomPrenomArabeVersFrancais($nomPrenomArabe) {
        $targetLanguage = 'fr';
        $url = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyC_T-LX7HSxtA_4NkvIw1dBmjA0Lf2KPrk";

        $data = [
            'q' => $nomPrenomArabe,
            'target' => $targetLanguage,
        ];
        $options = [
            'http' => [
                'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
                'method'  => 'POST',
                'content' => http_build_query($data),
            ],
        ];
        $context  = stream_context_create($options);
        $response = file_get_contents($url, false, $context);
        $result = json_decode($response, true);
        $translatedName = $result['data']['translations'][0]['translatedText'];

        return $translatedName;
    }


}