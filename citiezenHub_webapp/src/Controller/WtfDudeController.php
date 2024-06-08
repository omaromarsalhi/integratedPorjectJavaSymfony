<?php

namespace App\Controller;

use App\Entity\Municipalite;
use App\Entity\User;
use App\MyHelpers\AiVerification;
use App\MyHelpers\SendSms;
use App\MyHelpers\UserVerifierMessage;
use App\Repository\ChatRepository;
use App\Repository\FavoriteRepository;
use App\Repository\MunicipaliteRepository;
use App\Repository\ProductRepository;
use App\Repository\UserRepository;
use App\Service\GeocodingService;
use Doctrine\ORM\EntityManagerInterface;
use phpDocumentor\Reflection\Types\This;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Messenger\Stamp\DelayStamp;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\String\ByteString;
use Symfony\Component\Uid\Uuid;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class WtfDudeController extends AbstractController
{
    #[Route('/wtf/dude', name: 'app_wtf_dude')]
    public function index(FavoriteRepository $favoriteRepository,ProductRepository $productRepository): Response
    {
        $favorites = $favoriteRepository->findAll();
        $product=$productRepository->findOneBy(['idProduct'=>783]);
        foreach ($favorites as $favorite) {
            $checkIfProductIsValid = true;
            $parts = explode('__', $favorite->getSpecifications());
            $today = (new \DateTime())->format('Y-m-d');
            if ($today >= $parts[0] && ($parts[1] == '' || $today <= $parts[1])) {
                if ($parts[2] != '' && intval($parts[2]) >= $product->getPrice()) {
                    $checkIfProductIsValid = false;
                    dump (1);
                }
                else if ($parts[3] != '' && intval($parts[3]) <= $product->getPrice()) {
                    $checkIfProductIsValid = false;
                    dump (2);
                }
                else if ($parts[4] != '' && intval($parts[4]) != $product->getQuantity()) {
                    $checkIfProductIsValid = false;
                    dump (3);
                }
                else if ($parts[5] != '' && $parts[5] != $product->getCategory() && $parts[5] != 'All') {
                    $checkIfProductIsValid = false;
                    dump (4);
                }
            } else
                $checkIfProductIsValid = false;

//            if ($checkIfProductIsValid)
//                SendSms::send('new '.$product->getName().' has been added');
//                SendSms::send('new product has been added');
            dump($checkIfProductIsValid);
        }
        die();
        return new Response("done");
    }


    #[Route('/test/page', name: 'test_page', methods: ['GET', 'POST'])]
    public function page(): Response
    {
        return $this->render('index.html.twig');
    }
}
