<?php
namespace App\Websocket;


use Ratchet\ConnectionInterface;
use Ratchet\MessageComponentInterface;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;
use function PHPUnit\Framework\throwException;

class MessageHandler implements MessageComponentInterface
{
    private $userConnections = []; // User ID to Connection mapping

    public function onOpen(ConnectionInterface $conn): void
    {
        // Extract user ID from query string (adjust as needed)
        $queryString = $conn->httpRequest->getUri()->getQuery();
        parse_str($queryString, $queryParameters);
        $userId = $queryParameters['userId'] ?? null;

        if ($userId) {
            // Store the connection based on user ID
            $this->userConnections[$userId] = $conn;
            echo "New connection for user {$userId}! ({$conn->resourceId})\n";
            echo "size " . sizeof($this->userConnections) . "\n";
        } else {
            // Handle cases where user ID is missing
            echo "User ID not provided. Connection rejected.\n";
            $conn->close();
        }
    }

    public function onMessage(ConnectionInterface $from, $msg): void
    {
        echo $msg . "\n";
        $data = json_decode($msg, true);

        if (isset($data['action'])) {
            switch ($data['action']) {
                case 'chat':
                    $this->handleChatMessage($from, $data);
                    break;
                case 'productEvent':
                    $this->handleBroadcastMessage($data);
                    break;
                default:
                    echo "Unknown action: {$data['action']}\n";
            }
        } else {
            echo "Action not provided.\n";
        }
    }


    private function handleChatMessage(ConnectionInterface $from, array $data): void
    {
        if (isset($data['senderId'], $data['recipientId'], $data['message'])) {
            $senderId = $data['senderId'];
            $recipientId = $data['recipientId'];

            // Look up recipient's connection based on recipient's ID
            $recipientConnection = $this->userConnections[$recipientId] ?? null;
            $senderConnection = $this->userConnections[$senderId] ?? null;

            if ($recipientConnection && $recipientConnection !== $from && $senderConnection === $from) {
                $serializer = $this->createSerializer();
                $json = $serializer->serialize($data, 'json');

                // Send the message to the recipient
                echo "Message sent from {$senderId} to {$recipientId}\n";
                $recipientConnection->send($json);
            }
        } else {
            echo "Incomplete message data.\n";
        }
    }

    public function handleBroadcastMessage(array $data): void
    {
        echo "salhi from soket \n";
        // Serialize broadcast data
        $serializer = $this->createSerializer();
        $json = $serializer->serialize($data, 'json');

        // Broadcast the message to all connected users
        echo "size2 " . sizeof($this->userConnections) . "\n";
        foreach ($this->userConnections as $userId => $connection) {
            echo "omar from soket \n";
            echo "Broadcasting message to user {$userId}\n";
            $connection->send($json);
        }
    }

    public function onClose(ConnectionInterface $conn): void
    {
        // Remove the connection when it's closed
        foreach ($this->userConnections as $userId => $connection) {
            if ($connection === $conn) {
                unset($this->userConnections[$userId]);
                break;
            }
        }
        echo "Connection {$conn->resourceId} has disconnected\n";
    }

    public function onError(ConnectionInterface $conn, \Exception $e): void
    {
        // Handle errors (e.g., log or close the connection)
        echo "An error occurred: {$e->getMessage()}\n";
        $conn->close();
    }

    private function createSerializer(): Serializer
    {
        $encoders = [new JsonEncoder()];
        $normalizers = [new ObjectNormalizer()];

        return new Serializer($normalizers, $encoders);
    }
}














//
//// src/Websocket/MessageHandler.php
//namespace App\Websocket;
//
//use Ratchet\MessageComponentInterface;
//use Ratchet\ConnectionInterface;
//use SplObjectStorage;
//use Symfony\Component\Serializer\Encoder\JsonEncoder;
//use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
//use Symfony\Component\Serializer\Serializer;
//
//class MessageHandler implements MessageComponentInterface
//{
//
//    private $userConnections = []; // User ID to Connection mapping
//
//
//    public function onOpen(ConnectionInterface $conn): void
//    {
//        // Extract user ID from query string (adjust as needed)
//        $queryString = $conn->httpRequest->getUri()->getQuery();
//        parse_str($queryString, $queryParameters);
//        $userId = $queryParameters['userId'] ?? null;
//
//        if ($userId) {
//            // Store the connection based on user ID
//            $this->userConnections[$userId] = $conn;
//            echo "New connection for user {$userId}! ({$conn->resourceId})\n";
//            echo "size ".sizeof($this->userConnections)."\n";
//        } else {
//            // Handle cases where user ID is missing
//            echo "User ID not provided. Connection rejected.\n";
//            $conn->close();
//        }
//    }
//
//    public function onMessage(ConnectionInterface $from, $msg): void
//    {
//        $data = json_decode($msg, true);
//
//        if (isset($data['senderId'], $data['recipientId'], $data['message'])) {
//            $senderId = $data['senderId'];
//            $recipientId = $data['recipientId'];
//
//            // Look up recipient's connection based on recipient's ID
//            $recipientConnection = $this->userConnections[$recipientId] ?? null;
//            $senderConnection = $this->userConnections[$senderId] ?? null;
//
//            if ($recipientConnection && $recipientConnection !== $from && $senderConnection===$from ) {
//                $encoders = [new JsonEncoder()];
//                $normalizers = [new ObjectNormalizer()];
//
//                $serializer = new Serializer($normalizers, $encoders);
//                $json = $serializer->serialize($data, 'json');
//                // Send the message to the recipient
//                echo "data sent from {$senderId}! to ({$recipientConnection->resourceId})\n";
//
//                $recipientConnection->send($json);
//
//            }
//        }
//
//    }
//
//    public function onClose(ConnectionInterface $conn): void
//    {
//        // Remove the connection when it's closed
//        foreach ($this->userConnections as $userId => $connection) {
//            if ($connection === $conn) {
//                unset($this->userConnections[$userId]);
//                break;
//            }
//        }
//        echo "Connection {$conn->resourceId} has disconnected\n";
//    }
//
//    public function onError(ConnectionInterface $conn, \Exception $e): void
//    {
//        // Handle errors (e.g., log or close the connection)
//        echo "An error occurred: {$e->getMessage()}\n";
//        $conn->close();
//    }
//}
