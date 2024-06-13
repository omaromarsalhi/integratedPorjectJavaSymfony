<?php

namespace App\MyHelpers;

use App\Controller\AiResultController;
use App\Controller\FavoriteController;
use App\Controller\ProductController;
use App\Entity\AiResult;
use App\Repository\AiResultRepository;
use App\Repository\FavoriteRepository;
use App\Repository\ProductRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\SerializerInterface;

#[AsMessageHandler]
class AiVerificationMessengerHandler
{

    public function __construct(private FavoriteRepository $favoriteRepository, private RealTimeUpdater $realTimeUpdater, private EntityManagerInterface $entityManager, private ProductRepository $productRepository, private AiResultRepository $aiResultRepository)
    {
    }

    public function __invoke(AiVerificationMessage $message): void
    {

        $obj = $message->getObj();

        $aiVerification = new AiVerification();

        $aiDataHolder = $aiVerification->run($obj);

        ProductController::changeState($this->productRepository, $this->entityManager, $aiDataHolder, $obj['id']);



        $product = $this->productRepository->findOneBy(['idProduct' => $obj['id']]);
        $favorites = $this->favoriteRepository->findAll();
        if ($product->getState() == 'verified')
            FavoriteController::filterProductForFavorite($favorites, $product);

        if ($obj['mode'] === 'edit' && $obj['initiator'] !== 'java' && $product->getState()=="verified") {
            $this->realTimeUpdater->notifyFromSystem(['Data' => ['idProduct' => $product->getIdProduct()], 'action' => 'productEvent', 'subAction' => 'UPDATE']);
        } else if ($obj['mode'] === 'add' && $obj['initiator'] !== 'java' && $product->getState()=="verified") {
            $this->realTimeUpdater->notifyFromSystem(['Data' => ['idProduct' => $product->getIdProduct()], 'action' => 'productEvent', 'subAction' => 'ADD']);
        }
        if ($obj['initiator'] === 'java')
            $this->realTimeUpdater->notifyFromSystem(['action' => 'aiTermination', 'senderId' => -100, 'recipientId' => $obj['idUser'], 'idProduct' => strval($obj['id']), 'message' => $product->getState()]);


        $aiResultController = new AiResultController();

        $serializer = new Serializer([new ObjectNormalizer(    )], [new JsonEncoder()]);
        $serializedData = $serializer->serialize($aiDataHolder, 'json');
        $aiResult = new AiResult();


        if ($obj['mode'] === 'edit') {
            $aiResultController->edit($serializedData, $obj['id'], $this->entityManager, $this->aiResultRepository);

        } else {
            $aiResult->setBody($serializedData);
            $aiResult->setIdProduct($obj['id']);
            $aiResult->setTerminationDate();
            $aiResultController->new($aiResult, $this->entityManager);

        }

//        SendSms::send();
    }
}