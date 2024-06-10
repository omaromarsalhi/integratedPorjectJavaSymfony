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
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Writer\PngWriter;
use phpDocumentor\Reflection\Types\This;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Filesystem\Filesystem;
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
    public function index(FavoriteRepository $favoriteRepository,HttpClientInterface $client): Response
    {


        $geocoder = new GeocodingService($client);
        $path = '../../files/usersJsonFiles/' . md5('user' . ($this->getUser()* 1000 + 17)) . '.json';
        $filesystem = new Filesystem();
        if (!$filesystem->exists([$path]))
            return 'none';


        $jsonString = file_get_contents($path);
        $jsonDataCin = json_decode($jsonString, true);
        $address_components=$geocoder->geocode(' الشرايع سبيطلة القصرين ');
        $address_components=$address_components[0]['address_components'];


        $result = array(
            "ولاية" => null,
            "معتمدية" => null,
            "بلدية" => null,
            "الدائرة البلدية" => null,
            "عمادة" => null
        );

        foreach ($address_components as $component) {
            $types = $component['types'];
            if (in_array('administrative_area_level_1', $types)) {
                $result["ولاية"] = $component['long_name'];
            } elseif (in_array('administrative_area_level_2', $types)) {
                $result["معتمدية"] = $component['long_name'];
            } elseif (in_array('locality', $types)) {
                $result["بلدية"] = $component['long_name'];
            } elseif (in_array('administrative_area_level_3', $types)) {
                $result["بلدية"] = $component['long_name'];
            } elseif (in_array('sublocality', $types)) {
                $result["الدائرة البلدية"] = $component['long_name'];
            } elseif (in_array('neighborhood', $types)) {
                $result["عمادة"] = $component['long_name'];
            }
        }

         dump($result);
        die();
        return new Response("done");
    }


    #[Route('/test/page', name: 'test_page', methods: ['GET', 'POST'])]
    public function generateQrCode(string $text, int $size = 300): string
    {
        $result = Builder::create()
            ->data($text)
            ->size($size)
            ->writer(new PngWriter())
            ->build();

        // Save the QR code to a temporary file and return its path
        $path = sys_get_temp_dir() . '/qrcode.png';
        file_put_contents($path, $result->getString());

        return $path;
    }

    #[Route('/test/qr', name: 'test_page_qr', methods: ['GET', 'POST'])]
    public function generate(): Response
    {

        $qrCodePath = $this->generateQrCode('http://127.0.0.1:8000/user/generatePdfWithoutMail/195');
        return new Response(file_get_contents($qrCodePath), 200, [
            'Content-Type' => 'image/png',
        ]);

    }
}
