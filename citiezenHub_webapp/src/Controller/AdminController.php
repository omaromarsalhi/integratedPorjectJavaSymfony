<?php

namespace App\Controller;

use App\Repository\MunicipaliteRepository;
use App\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AdminController extends AbstractController
{
    #[Route('/admin', name: 'app_admin')]
    public function index(): Response
    {
        return $this->render('admin/DashBoard.html.twig');
    }

    #[Route('/admin2', name: 'app_admin2')]
    public function index2(MunicipaliteRepository $municipaliteRepository, UserRepository $userRepository): Response
    {
        $muniCount = $municipaliteRepository->findTopMunicipalitiesdashBoard();
        $nbrUser = $userRepository->getnbrUsersPerWeek();

        return $this->render('admin/DashBoard2.html.twig',[
                'muniCount' => $muniCount,
                'nbrUser' => $nbrUser
            ]);
    }
}
