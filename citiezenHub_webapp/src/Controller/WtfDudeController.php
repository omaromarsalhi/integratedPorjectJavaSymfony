<?php

namespace App\Controller;

use App\Entity\Municipalite;
use App\Entity\User;
use App\MyHelpers\AiVerification;
use App\Repository\ChatRepository;
use App\Repository\MunicipaliteRepository;
use App\Repository\ProductRepository;
use App\Repository\UserRepository;
use App\Service\GeocodingService;
use phpDocumentor\Reflection\Types\This;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\String\ByteString;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class WtfDudeController extends AbstractController
{
    #[Route('/wtf/dude', name: 'app_wtf_dude')]
    public function index(ChatRepository $chatRepository,UserRepository $userRepository): Response
    {
//
//        $filter = [
//        'datetime'=>['today'=> 'false', 'lastWeek'=> 'false', 'lastMonth'=> 'false'],
//        'category'=>['food'=> 'true', 'sports'=> 'false', 'entertainment'=> 'false', 'realEstate'=> 'false', 'vehicle'=> 'false'],
//        'price'=>['allPrices'=> 'true', 'asc'=> 'false', 'desc'=> 'false']
//        ];
//
//        dump($productRepository->findByPriceTest($filter));

//        $aiVerification = new AiVerification();
//        $aiVerification->formatJsonFilesOfCin(md5('user_front' . ($this->getUser()->getId() * 1000 + 17)), md5('user_backCin' . ($this->getUser()->getId() * 1000 + 17)));
//        $geocoder=new GeocodingService($client);
//        $user=$this->getUser();
//        $path = '../../files/usersJsonFiles/' . md5('user' . ($user->getId() * 1000 + 17)) . '.json';
//
//        $jsonString = file_get_contents($path);
//        $jsonDataCin = json_decode($jsonString, true);
//
//        try {
//            $data = $geocoder->geocode($jsonDataCin['العنوان']['data']);
//        } catch (\Exception $e) {
//        }
//
//        dump($data);
//        dump($geocoder->isInMunicipality($jsonDataCin['العنوان']['data'],$user->getMunicipalite()->getName()));
        $users = $userRepository->findAll();

        foreach ($users as $user) {
            if ($user !== $this->getUser()) {
                $chat = $chatRepository->selectLastMessage($user->getId());
                if($user->getId()==195)
                    dump($chat);
                if(sizeof($chat) > 0) {
                    $chat = $chat[0];
                    $messages[$user->getId()] = [$chat->getMessage(), $chat->getTimestamp()->format('Y-m-d H:i'), $chat->getMsgState()];
                }
                else{
                    $messages[$user->getId()] = ['', '',-1];
                }
            }
        }
        die();
        return new Response("done");
    }
}
