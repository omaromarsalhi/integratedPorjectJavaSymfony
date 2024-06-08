<?php

namespace App\Controller;

use App\Entity\Chat;
use App\Entity\User;
use App\Repository\ChatRepository;
use App\Repository\UserRepository;
use App\Websocket\MessageHandler;
use Doctrine\ORM\EntityManagerInterface;
use Ratchet\Client\Connector;
use Ratchet\Client\WebSocket;
use React\EventLoop\Factory;
use React\Promise\Deferred;
use SebastianBergmann\Diff\Exception;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;


#[Route('/chat', name: 'app_chat')]
class ChatController extends AbstractController
{

    #[Route('/show', name: '_show')]
    public function show(UserRepository $userRepository, ChatRepository $chatRepository): Response
    {
        $users = $userRepository->findByChat($this->getUser());
        $messages = [];

        foreach ($users as $user) {
            if ($user !== $this->getUser()) {
                $chat = $chatRepository->selectLastMessage($user->getId());
                if(sizeof($chat) > 0) {
                    $chat = $chat[0];
                    $messages[$user->getId()] = [$chat->getMessage(), $chat->getTimestamp()->format('Y-m-d H:i'), $chat->getMsgState()];
                }
                else{
                    $messages[$user->getId()] = ['', '',-1];
                }
            }
        }


        return $this->render('chat/chat.html.twig', [
            'users' => $users,
            'messages' => $messages,
        ]);
    }


    #[Route('/getData', name: '_getData')]
    public function getData(ChatRepository $chatRepository, Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $idSender = $request->get('idSender');
            $idReciver = $request->get('idReciver');

            $messages = [];
            foreach ($chatRepository->findByReciverOrSender(intval($idSender), intval($idReciver)) as $chat) {
                $messages[] = [$chat->getMessage(), $chat->getTimestamp()->format('Y-m-d H:i'), $chat->getSender() === $this->getUser()];
            }

            $chatRepository->updateChatState($idReciver);


            return new JsonResponse(['messages' => $messages]);
        }

        return new Response('bad', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/new', name: '_new')]
    public function new(UserRepository $userRepository, EntityManagerInterface $entityManager, Request $request): Response
    {

        if ($request->isXmlHttpRequest()) {
            $chat = new Chat();
            $chat->setMessage($request->get('msg')["message"]);
            $chat->setReciver($userRepository->findOneBy(['idUser' => intval($request->get('reciverId'))]));
            $chat->setSender($this->getUser());
            $chat->setMsgState(0);
            $entityManager->persist($chat);
            $entityManager->flush();

            return new Response('done', Response::HTTP_OK);
        }
        return $this->render('chat/chat.html.twig', [
            'users' => $userRepository->findByChat($this->getUser()),
        ]);
    }


    #[Route('/view', name: 'view')]
    public function view(ChatRepository $chatRepository, Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $chatRepository->updateChatState($this->getUser());
            return new JsonResponse(['messages' => 'done']);
        }
        return new Response('bad', Response::HTTP_BAD_REQUEST);
    }



}
