<?php

namespace App\MyHelpers;

use App\Controller\MailerController;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;


#[AsMessageHandler]
class UserVerifierMessageHandler
{
    public function __construct(private EntityManagerInterface $entityManager, private UserRepository $userRepository, private RealTimeUpdater $realTimeUpdater)
    {
    }

    public function __invoke(UserVerifierMessage $message): void
    {
        $obj = $message->getObj();
        $user = $this->userRepository->findOneBy(['idUser' => $obj['idUser']]);
        if ($obj['UMID'] != $user->getUMID())
            return;

        if ($user->getState() == 0) {
//            MailerController::sendNormalMail('salhiomar362@gmail.com','Account Deleted','sorry your account has been deleted');
            $this->entityManager->remove($user);
            $this->entityManager->flush();
            $this->realTimeUpdater->notifyFromSystem(['message' => 'your account has been deleted', 'senderId' => -100, 'recipientId' => $obj['idUser'], 'action' => 'accountDeletion']);

         }
    }
}