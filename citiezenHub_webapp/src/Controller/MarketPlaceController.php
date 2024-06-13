<?php

namespace App\Controller;

use App\MyHelpers\AiVerification;
use App\MyHelpers\ImageHelper;
use App\Repository\AiResultRepository;
use App\Repository\ProductRepository;
use App\Repository\TransactionRepository;
use App\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/market/place')]
class MarketPlaceController extends AbstractController
{

    #[Route('/', name: 'app_market_place_index', methods: ['GET', 'POST'])]
    public function index(ProductRepository $productRepository, Request $request): Response
    {
        $session = $request->getSession();

        if ($request->isXmlHttpRequest()) {

            $movement_direction = $request->get("movement_direction");
            if($movement_direction==-100){
                $session->set('allProducts', $productRepository->findBy(['state'=>'verified'],['idProduct' => 'DESC']   ));
                $prods = $session->get('allProducts');
                $current_page = 1;
                $previous_page = 2;
                $session->set('nbr_pages', ceil(sizeof($prods) / 12));
                $session->set('idList', [-1]);
            }else{
                $prods = $session->get('allProducts');
                $nbr_pages = $session->get('nbr_pages');
                $current_page = $session->get('current_page');
                $previous_page = $current_page;

                if ($current_page != $nbr_pages && $movement_direction == "next")
                    $current_page++;
                else if ($current_page != 1 && $movement_direction == "previous")
                    $current_page--;
                else
                    $current_page = $movement_direction;
            }


            $session->set('current_page', $current_page);
            $productsToShow=array_slice($prods, ($current_page - 1) * 12, 12);
            $subMarket=$this->render('market_place/sub_market.html.twig', [
                'products' => $productsToShow,
                'current_page' => $current_page,
                'previous_page' => $previous_page,
            ]);

            $nav=$this->render('market_place/nav.html.twig', [
                'nbr_pages' => $session->get('nbr_pages'),
            ]);

            $forms=$this->render('market_place/forms.html.twig', [
                'products' => $productsToShow,
            ]);

            return new JsonResponse(['subMarket'=>$subMarket->getContent(),'nav'=>$nav->getContent(),'forms'=>$forms]);

        }

        $session->set('allProducts', $productRepository->findBy(['state'=>'verified'],['idProduct' => 'DESC']   ));
        $prods = $session->get('allProducts');
        $session->set('nbr_pages', ceil(sizeof($prods) / 12));
        $session->set('current_page', 1);
        $session->set('idList', [-1]);


        return $this->render('market_place/market.html.twig', [
            'products' => array_slice($prods, 0, 12),
            'nbr_pages' => ceil(sizeof($prods) / 12),
            'current_page' => 1,
            'previous_page' => 2,
            'sortingData'=>$productRepository->findMinMaxPrices()
        ]);
    }

    #[Route('/filtered', name: 'app_market_place_filtered', methods: ['GET', 'POST'])]
    public function filtered(ProductRepository $productRepository, Request $request): Response
    {
        $session = $request->getSession();

        if ($request->isXmlHttpRequest()) {
            $session = $request->getSession();

            $filterBy=$request->get('filterBy');
            $idList=$session->get('idList');
            dump($filterBy);
            dump($idList);
            $prods=$productRepository->findByPriceTest($filterBy,$idList);
            $session->set('allProducts',$prods);
            $session->set('nbr_pages', ceil(sizeof($prods) / 12));
            $session->set('current_page', 1);


            $productsToShow=array_slice($prods, 0, 12);
            $subMarket=$this->render('market_place/sub_market.html.twig', [
                'products' => $productsToShow,
                'current_page' => 1,
                'previous_page' => 2,
            ]);
            $nav=$this->render('market_place/nav.html.twig', [
                'nbr_pages' => $session->get('nbr_pages'),
            ]);

            $forms=$this->render('market_place/forms.html.twig', [
                'products' => $productsToShow,
            ]);

            return new JsonResponse(['subMarket'=>$subMarket->getContent(),'nav'=>$nav->getContent(),'forms'=>$forms]);
        }

        return new Response('', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/searChByImage', name: 'app_market_searChByImage', methods: ['GET', 'POST'])]
    public function searChByImage(AiVerification $aiVerification,ProductRepository $productRepository, Request $request): Response
    {
        $session = $request->getSession();

        if ($request->isXmlHttpRequest()) {

            $image=$request->files->get('image');
            $data=$productRepository->findAll();
            $fileName=ImageHelper::saveSingleImage($image);
            $idList=$aiVerification->runImageSearch($fileName,$data);
            $session->set('idList', $idList);
            $products = $productRepository->findBy(['idProduct' => $idList,'state'=>'verified']);
            $session->set('allProducts',$products);
            $session->set('nbr_pages', ceil(sizeof($products) / 12));
            $session->set('current_page', 1);


            $productsToShow=array_slice($products, 0, 12);
            $subMarket=$this->render('market_place/sub_market.html.twig', [
                'products' => $productsToShow,
                'current_page' => 1,
                'previous_page' => 2,
            ]);
            $nav=$this->render('market_place/nav.html.twig', [
                'nbr_pages' => $session->get('nbr_pages'),
            ]);

            $forms=$this->render('market_place/forms.html.twig', [
                'products' => $productsToShow,
            ]);

            return new JsonResponse(['subMarket'=>$subMarket->getContent(),'nav'=>$nav->getContent(),'forms'=>$forms]);
        }

        return new Response('', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/renderSingleProduct', name: 'app_market_renderSingleProduct', methods: ['GET', 'POST'])]
    public function renderSingleProduct(ProductRepository $productRepository, Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $product = $productRepository->findOneBy(['idProduct' => $request->get('idProduct')]);

            return $this->render('market_place/singleProduct.html.twig', [
                'product' => $product,
                'index' => $request->get('index'),
            ]);
        }

        return new Response('', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/showProduitAdmin', name: 'app_market_place_Admin', methods: ['GET', 'POST'])]
    public function showProduitAdmin(ProductRepository $productRepository, Request $request): Response
    {
        $product = $productRepository->findAll();

        return $this->render('market_place/marketplace.html.twig', [
            'list' => $product,
        ]);
    }

    #[Route('/showTransanctionAdmin', name: 'app_transaction_Admin', methods: ['GET', 'POST'])]
    public function showTransAdmin(TransactionRepository $transactionRepository, UserRepository $userRepository): Response
    {
        $transactions = $transactionRepository->findAll();

        $transactionData = array_map(function ($transaction) use ($userRepository) {
            $seller = $userRepository->find($transaction->getIdSeller());
            $buyer = $userRepository->find($transaction->getIdBuyer());

            return [
                'idTransaction' => $transaction->getId(),
                'sellerImage' => $seller->getImage(),
                'sellerName' => $seller->getFirstName() . ' ' . $seller->getLastName(),
                'buyerImage' => $buyer->getImage(),
                'buyerName' => $buyer->getFirstName() . ' ' . $buyer->getLastName(),
                'quantity' => $transaction->getQuantity(),
                'pricePerUnit' => $transaction->getPricePerUnit(),
                'datee' => $transaction->getTimestamp()->format('D, d M y h:i A'),
            ];
        }, $transactions);

        return $this->render('market_place/transaction.html.twig', [
            'transactionData' => $transactionData,
        ]);
    }

}


