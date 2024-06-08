<?php

namespace App\Controller;

use App\Entity\Favorite;
use App\Entity\Product;
use App\MyHelpers\SendSms;
use App\Repository\FavoriteRepository;

;

use Doctrine\ORM\EntityManagerInterface;
use phpDocumentor\Reflection\Types\This;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use function PHPUnit\Framework\isEmpty;


class FavoriteController extends AbstractController
{
    #[Route('/favorite', name: 'app_favorite')]
    public function index(): Response
    {
        return $this->render('favorite/index.html.twig', [
            'controller_name' => 'FavoriteController',
        ]);
    }

    #[Route('/favorite/new', name: 'app_favorite_new')]
    public function new(Request $request, EntityManagerInterface $entityManager, FavoriteRepository $favoriteRepository): Response
    {
        if ($request->isXmlHttpRequest()) {
            $category = $request->get('category');
            $quantityFavorite = $request->get('quantityFavorite');
            $fromDateFavorite = $request->get('fromDateFavorite');
            $toDateFavorite = $request->get('toDateFavorite');
            $priceMin = $request->get('priceMin');
            $priceMax = $request->get('priceMax');
            $specifications = ($fromDateFavorite ?: (new \DateTime())->format('Y-m-d')) . '__' . $toDateFavorite . '__' . $priceMin . '__' . $priceMax . '__' . $quantityFavorite . '__' . $category;

            $favorite = new Favorite();
            $favorite->setUser($this->getUser());
            $favorite->setSpecifications($specifications);
            $entityManager->persist($favorite);
            $entityManager->flush();
            return $this->show($favoriteRepository);
        }
        return new Response('something went wrong', Response::HTTP_BAD_REQUEST);

    }

    #[Route('/favorite/delete', name: 'app_favorite_delete')]
    public function delete(Request $request, EntityManagerInterface $entityManager, FavoriteRepository $favoriteRepository): Response
    {
        if ($request->isXmlHttpRequest()) {
            $id = $request->get('id');
            $favorite = $favoriteRepository->findOneBy(['id' => $id]);
            $entityManager->remove($favorite);
            $entityManager->flush();
            return $this->show($favoriteRepository);
        }
        return new Response('something went wrong', Response::HTTP_BAD_REQUEST);

    }


    #[Route('/show', name: 'app_favorite_show')]
    public function show(FavoriteRepository $favoriteRepository): Response
    {
        $favorites = $favoriteRepository->findBy(['user' => $this->getUser()]);
        return $this->render('user_dashboard/favorite.html.twig', [
            'favorites' => $favorites,
        ]);
    }

    #[Route('/favorite/filterProductForFavorite', name: 'app_favorite_filterProductForFavorite')]
    public static function filterProductForFavorite($favorites, $product): void
    {
        foreach ($favorites as $favorite) {
            $checkIfProductIsValid = true;
            $parts = explode('__', $favorite->getSpecifications());
            $today = (new \DateTime())->format('Y-m-d');
            if ($today >= $parts[0] && ($parts[1] == '' || $today <= $parts[1])) {
                if ($parts[2] != '' && intval($parts[2]) >= $product->getPrice())
                    $checkIfProductIsValid = false;

                else if ($parts[3] != '' && intval($parts[3]) <= $product->getPrice())
                    $checkIfProductIsValid = false;
                else if ($parts[4] != '' && intval($parts[4]) != $product->getQuantity())
                    $checkIfProductIsValid = false;
                else if ($parts[5] != '' && $parts[5] != $product->getCategory() && $parts[5] != 'All')
                    $checkIfProductIsValid = false;
            } else
                $checkIfProductIsValid = false;

            if ($checkIfProductIsValid)
                SendSms::send('new ' . $product->getName() . ' has been added');
        }

    }

}
