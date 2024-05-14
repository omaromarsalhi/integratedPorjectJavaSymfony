<?php

namespace App\Controller;

use App\MyHelpers\NewsDataApi;
use App\Repository\MunicipaliteRepository;
use App\Repository\ProductRepository;
use App\Entity\Product;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home')]
    public function index(ProductRepository $productRepository, MunicipaliteRepository $municipaliteRepository): Response
    {
        //news
        //$newsDataApi = new NewsDataApi();
        //$newsList = $newsDataApi->getNews();
        //$cinqPremier = array_slice($newsList, 0, 5);
        $cinqPremier = [];

        //users_municipality
        $top = $municipaliteRepository->findTopMunicipalities();

        return $this->render('home/index.html.twig', [
            'newsList' => $cinqPremier,
            'top' => $top
        ]);
    }
}
