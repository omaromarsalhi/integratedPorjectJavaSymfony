<?php

namespace App\Controller;

use App\Entity\Reponse;
use App\Entity\Reclamation;
use App\Repository\ReclamationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

class ReponseController extends AbstractController
{
    #[Route('/reclamation/{id}/response', name: 'app_add_response', methods: ['POST'])]
    public function addResponse(Request $request, EntityManagerInterface $entityManager, ReclamationRepository $reclamationRepository): JsonResponse
    {
        $data = json_decode($request->getContent(), true);
        $reclamationId = $data['reclamationId'] ?? null;
        $responseText = $data['responseText'] ?? null;

        if (!$reclamationId || !$responseText) {
            return new JsonResponse(['success' => false, 'message' => 'Invalid data']);
        }

        $reclamation = $reclamationRepository->find($reclamationId);

        if (!$reclamation) {
            return new JsonResponse(['success' => false, 'message' => 'Reclamation not found']);
        }

        $reponse = new Reponse();
        $reponse->setDescription($responseText);
        $reponse->setReclamation($reclamation);

        $entityManager->persist($reponse);
        $entityManager->flush();

        return new JsonResponse(['success' => true]);
    }
}
