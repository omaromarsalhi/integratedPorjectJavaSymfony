<?php

namespace App\Controller;

use App\Entity\Reclamation;
use App\Entity\Reponse as ReclamationResponse;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response as HttpResponse;
use Symfony\Component\Routing\Annotation\Route;

class ReponseController extends AbstractController
{
    #[Route('/reponse', name: 'app_reponse')]
    public function index(): HttpResponse
    {
        return $this->render('reponse/index.html.twig', [
            'controller_name' => 'ReponseController',
        ]);
    }

    #[Route('/reclamation/{id}/add-response', name: 'add_response', methods: ['POST'])]
    public function addResponse( Request $request, EntityManagerInterface $entityManager,$id): JsonResponse
    {
        // Retrieve the reclamation
        $reclamation = $entityManager->getRepository(Reclamation::class)->find($id);

        if (!$reclamation) {
            return new JsonResponse(['error' => 'Reclamation not found'], HttpResponse::HTTP_NOT_FOUND);
        }

        // Get the response text from the request
        $responseText = $request->request->get('response');

        // Validate the response text
        if (empty($responseText)) {
            return new JsonResponse(['error' => 'Response text cannot be empty'], HttpResponse::HTTP_BAD_REQUEST);
        }

        // Create and save the new response
        $response = new ReclamationResponse();
        $response->setReclamation($reclamation);
        $response->setDescription($responseText);
        $reclamation->addReponse($response);

        $entityManager->persist($response);
        $entityManager->persist($reclamation);
        $entityManager->flush();

        // Return the new response in the JSON response
        return new JsonResponse(['newResponse' => $response->getDescription()], HttpResponse::HTTP_OK);
    }
}
