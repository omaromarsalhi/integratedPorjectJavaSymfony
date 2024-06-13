<?php

namespace App\Controller;


use App\Entity\Product;
use App\MyHelpers\AiDataHolder;
use App\MyHelpers\AiVerificationMessage;
use App\MyHelpers\ImageHelper;
use App\MyHelpers\RealTimeUpdater;
use App\Repository\AiResultRepository;
use App\Repository\FavoriteRepository;
use App\Repository\ProductRepository;
use App\Repository\TransactionRepository;
use Doctrine\ORM\EntityManagerInterface;
use phpDocumentor\Reflection\Types\This;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\EventDispatcher\EventDispatcherInterface;
use Symfony\Component\HttpClient\HttpClient;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Messenger\Stamp\DelayStamp;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Validator\Validator\ValidatorInterface;


#[Route('/product', name: 'app_product')]
class ProductController extends AbstractController
{

    private $eventDispatcher;


    public function __construct(EventDispatcherInterface $eventDispatcher)
    {
        $this->eventDispatcher = $eventDispatcher;
    }

    #[Route('/getStat', name: '_getStat', methods: ['GET', 'POST'])]
    public function getStat(Request $request,ProductRepository $productRepository,TransactionRepository $transactionRepository): Response
    {
        if($request->isXmlHttpRequest()){
            $one=$transactionRepository->countSold($this->getUser()->getId());
            $two=$productRepository->getStat2($this->getUser());
            $three=$transactionRepository->countPurchased($this->getUser()->getId());
            $for=$productRepository->getStat4($this->getUser());
            return new  JsonResponse(['one'=>$one,'two'=>$two,'three'=>$three,'for'=>$for]);
        }
        return new Response('something went wrong', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/new', name: '_new', methods: ['GET', 'POST'])]
    public function new(RealTimeUpdater $realTimeUpdater, MessageBusInterface $messageBus, Request $request, EntityManagerInterface $entityManager, ImageHelper $imageHelper, ProductRepository $productRepository, ValidatorInterface $validator): Response
    {

        if ($request->isXmlHttpRequest()) {

            $name = $request->get("name");
            $description = $request->get("description");
            $price = $request->get("price");
            $quantity = $request->get("quantity");
            $category = $request->get("category");

            $new_product = new Product();
            $user = $this->getUser();
            $new_product->setUser($user);
            $new_product->setName($name);
            $new_product->setDescription($description);
            $new_product->setPrice(floatval($price));
            $new_product->setQuantity(floatval($quantity));
            $new_product->setCategory($category);
            $new_product->setIsDeleted(0);
            $new_product->setState('unverified');
            $new_product->setType('BIEN');

            $errors = $validator->validate($new_product);

            if (count($errors) > 0) {
                $errorMessages = [];
                foreach ($errors as $error) {
                    $field = $error->getPropertyPath();
                    $errorMessages[] = $field;
                }
                if (sizeof($request->files->all()) == 0)
                    $errorMessages[] = 'image';
                return new JsonResponse(['error' => $errorMessages], Response::HTTP_BAD_REQUEST);
            }

            $entityManager->persist($new_product);
            $entityManager->flush();

            $product = $productRepository->findOneBy([], ['idProduct' => 'DESC']);

            $images = $request->files->all();
            $newImagesPath = $imageHelper->saveImages($images, $product);

            $obj = [
                'title' => $product->getName(),
                'category' => $product->getCategory(),
                'id' => $product->getIdProduct(),
                'images' => $newImagesPath,
                'mode' => 'add',
                'idUser' => $this->getUser()->getId(),
                'initiator' => 'symfony',
            ];

            $messageBus->dispatch(new AiVerificationMessage($obj));

            $product->getUser()->setMunicipalite(null);

//            $realTimeUpdater->notifyApp(['Data' => ['idProduct' => $product->getIdProduct()], 'action' => 'productEvent', 'subAction' => 'ADD'], $this->getUser()->getId());

            return new JsonResponse(['state' => 'done'], Response::HTTP_OK);
        }

        return $this->render('market_place/create.html.twig', ['update' => false]);
    }

    #[Route('/generate_description', name: '_show', methods: ['POST'])]
    public function generateDescription(Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $title = $request->get("title");
            $client = HttpClient::create();
            $response = $client->request('POST', 'http://127.0.0.1:5000/get-descreption?title=' . $title);
            $substringsToRemove = ['\"', '""\\', '"\n', '"', '\n',' \u','Title','\u0027',' :'];
            $content = str_replace($substringsToRemove, "", $response->getContent());
            return new JsonResponse(['description' => $content]);
        }
        $title = $request->get("title");
        $client = HttpClient::create();
        $response = $client->request('POST', 'http://127.0.0.1:5000/get-descreption?title=' . $title);
        $substringsToRemove = ['\"', '""\\', '"\n', '"', '\n',' \u','Title','\u0027',' :'];
        $content = str_replace($substringsToRemove, "", $response->getContent());
        return new JsonResponse(['description' => $content]);
    }


    #[Route('/details/{index}', name: '_product_details', methods: ['POST'])]
    public function details(Request $request, ProductRepository $productRepository, int $index): Response
    {
        return $this->render('market_place/product-details.html.twig', [
            'product' => $productRepository->findOneBy(['idProduct' => $request->get('_token_' . $index)])
        ]);
    }


    #[Route('/{index}/edit', name: '_edit', methods: ['POST'])]
    public function edit(RealTimeUpdater $realTimeUpdater, AiResultRepository $aiResultRepository, MessageBusInterface $messageBus, ImageHelper $imageHelper, Request $request, ProductRepository $productRepository, EntityManagerInterface $entityManager, int $index): Response
    {

        if ($request->isXmlHttpRequest()) {
//            $aiResult = $aiResultRepository->findOneBy(['idProduct' => $request->get('idProduct')]);
//            if ($aiResult != null)
//                AiResultController::delete($aiResult, $entityManager);

            $product = $productRepository->findOneBy(['idProduct' => $request->get('idProduct')]);
            $name = $request->get("name");
            $description = $request->get("description");
            $price = $request->get("price");
            $quantity = $request->get("quantity");
            $category = $request->get("category");
            $imagesNotToDelete = preg_split('/_/', $request->get("savedImages"), -1, PREG_SPLIT_NO_EMPTY);


            $product->setName($name);
            $product->setDescription($description);
            $product->setPrice(floatval($price));
            $product->setQuantity(floatval($quantity));
            $product->setCategory($category!=null?:'food');
            $product->setState('unverified');


            $entityManager->flush();

            $images = $request->files->all();
            $imageHelper->saveImages($images, $product);
            dump($imagesNotToDelete);
            $imageHelper->deleteImages($imagesNotToDelete, $product->getImages());


            $paths = [];
            $s = 0;
            for ($i = 0; $i < sizeof($product->getImages()); $i++) {
                if ($product->getImages()[$i] != null) {
                    if ($product->getImages()[$i]->getIdImage() == $imagesNotToDelete[$s]) {
                        $paths[] = str_replace('usersImg/', '', $product->getImages()[$i]->getPath());
                        $s++;
                    }
                }
            }

            $obj = [
                'title' => $product->getName(),
                'category' => $product->getCategory(),
                'id' => $product->getIdProduct(),
                'images' => $paths,
                'mode' => 'edit',
                'idUser' => $this->getUser()->getId(),
                'initiator' => 'symfony',
            ];

            $messageBus->dispatch(new AiVerificationMessage($obj));

            $product->getUser()->setMunicipalite(null);

//            $realTimeUpdater->notifyApp(['Data' => ['idProduct' => $product->getIdProduct()], 'action' => 'productEvent', 'subAction' => 'UPDATE'], $this->getUser()->getId());

            return new JsonResponse(['state' => 'done'], Response::HTTP_OK);
        }

        $product = $productRepository->findOneBy(['idProduct' => $request->get('_token_' . $index)]);

        return $this->render('market_place/create.html.twig', [
            'product' => $product,
            'update' => true
        ]);
    }


    #[Route('/test', name: 'test', methods: ['POST', 'GET'])]
    public static function changeState( ProductRepository $productRepository, EntityManagerInterface $entityManager, AiDataHolder $aiDataHolder, int $idProduct): Response
    {
        for ($i = 0; $i < sizeof($aiDataHolder->getDescriptions()); $i++) {
            if (str_starts_with(strtolower($aiDataHolder->getTitleValidation()[$i]), " no") || str_starts_with(strtolower($aiDataHolder->getCategoryValidation()[$i]), " no"))
                return new Response('done', Response::HTTP_OK);
        }

        $product = $productRepository->findOneBy(['idProduct' => $idProduct]);
        $product->setState('verified');
        $entityManager->flush();


        return new Response('done', Response::HTTP_OK);
    }

    #[Route('/delete', name: '_delete', methods: ['POST'])]
    public function delete(RealTimeUpdater $realTimeUpdater, AiResultRepository $aiResultRepository, Request $request, EntityManagerInterface $entityManager, ProductRepository $productRepository): Response
    {
        $session = $request->getSession();
        if ($request->isXmlHttpRequest()) {

            $aiResult = $aiResultRepository->findOneBy(['idProduct' => $request->get('id')]);
            if ($aiResult != null)
                AiResultController::delete($aiResult, $entityManager);

            $prod2Remove = $productRepository->findOneBy(['idProduct' => $request->get('id')]);

            $entityManager->remove($prod2Remove);
            $entityManager->flush();

            $page = $request->get("type");
            $map = $session->get('user_products_map');

            if ($page == 'on_sale')
                $state = 'verified';
            else
                $state = 'unverified';

            $map[$page]->setProducts($productRepository->findBy(['isDeleted' => false, 'state' => $state,'user'=>$this->getUser()]));

            $session->set('user_products_map', $map);

            $realTimeUpdater->notifyApp(['Data' => ['idProduct' => $request->get('id')], 'action' => 'productEvent', 'subAction' => 'DELETE'], $this->getUser()->getId());

            return new JsonResponse(['page' => $map[$page]->getCurrentPage()]);
        }
        return new Response('something went wrong', Response::HTTP_BAD_REQUEST);
    }

    #[Route('/statProd', name: 'statProd', methods: ['GET'])]
    public function statProd(ProductRepository $productRepository): Response
    {
        $prod = $productRepository->findByProductsField();

        return new jsonResponse($prod);
    }


    #[Route('/deleteproductAdmin/{id}', name: 'app_product_admin', methods: ['DELETE'])]
    public function deleteProduit($id, ProductRepository $prodRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        if ($request->isXmlHttpRequest()) {
            $prod = $prodRepository->find($id);
            $entityManager->remove($prod);
            $entityManager->flush();

            $products = $prodRepository->findAll();
            $productsArray = array_map(function  ($products) {
                return [
                    'images' => $products->getImages(),
                    'name' => $products->getName(),
                    'price' => $products->getPrice(),
                    'category' => $products->getCategory(),
                    'state' => $products->getState(),
                    'quantity' => $products->getQuantity(),
                    'timestamp' => $products->getTimestamp(),
                    'idProduct' => $products->getIdProduct(),

                ];
            }, $products);

            return new JsonResponse(['products' => $productsArray]);        }

        $responses = [

            'message' => 'Product Deleted successfully.'
        ];

        return new JsonResponse($responses);

    }

}
