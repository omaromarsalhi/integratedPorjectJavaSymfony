<?php

namespace App\MyHelpers;


use App\Entity\Product;
use phpDocumentor\Reflection\DocBlock\Serializer;
use phpDocumentor\Reflection\Types\This;
use Ratchet\Client\Connector;
use Ratchet\Client\WebSocket;
use React\EventLoop\Factory;
use Symfony\Component\Serializer\SerializerInterface;

class RealTimeUpdater
{

    public function __construct(private SerializerInterface $serializer)
    {
    }


    private function serializer($data): string
    {
//        if($data['action']==='productEvent' && $data['Data'] instanceof Product){
//            $product=$data['Data'];
//            foreach ($product->getImages() as $image) {
//                $image->setProduct(null);
//            }
//        }
        return $this->serializer->serialize($data, 'json');
    }

    public function notifyApp($data, $userId): void
    {
        $localData = $this->serializer($data);
        $loop = Factory::create();
        $connector = new Connector($loop);
        $connector('ws://localhost:8091?userId=' . $userId."&app=symfony")->then(function (WebSocket $connection) use ($localData) {
            $productData = $localData;
            $connection->send($productData);
            $connection->close();
        }, function (\Exception $e) {
            echo "Could not connect: {$e->getMessage()}\n";
        });
        $loop->run();
    }

    public function notifyFromSystem($data): void
    {
        $localData = $this->serializer($data);
        $loop = Factory::create();
        $connector = new Connector($loop);
        $connector('ws://localhost:8091?userId=' . (-100)."&app=symfony")->then(function (WebSocket $connection) use ($localData) {
            $productData = $localData;
            $connection->send($productData);
            $connection->close();
        }, function (\Exception $e) {
            echo "Could not connect: {$e->getMessage()}\n";
        });
        $loop->run();
    }

}

