<?php

namespace App\Controller;

use App\Entity\ReactionPost;
use App\MyHelpers\ImageExpliciteApi;
use App\Entity\CommentPost;
use App\Entity\ImagePsot;
use App\MyHelpers\RealTimeUpdater;
use App\MyHelpers\UploadImage;
use App\Repository\CommentPostRepository;
use App\Repository\ReactionPostRepository;
use App\Repository\UserRepository;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use DateTime;
use App\Entity\Post;
use App\Repository\PostRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Validator\Validator\ValidatorInterface;


class BlogController extends AbstractController
{
    #[Route('/blog', name: 'app_blog')]
    public function index(PostRepository $postRepository): Response
    {
        $posts = $postRepository->findBy([], ['date_post' => 'DESC']);

        return $this->render('blog/index.html.twig', [
            'posts' => $posts,
        ]);
    }


    #[Route('/blog/showSingleBlog', name: 'app_blog_showSingleBlog')]
    public function showSingleBlog(PostRepository $postRepository, Request $request): Response
    {
        if ($request->isXmlHttpRequest()) {
            $post = $postRepository->findOneBy(['id' => $request->get('idPost')]);
            $paths=[];

            foreach ($post->getImages() as $image){
                $paths[]=$image->getPath();
            }

            $newPos=[
                'id' => $post->getId(),
                'caption' => $post->getCaption(),
                'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                'nbReactions' => $post->getNbReactions(),
                'images' => $paths,
                'url' => $this->generateUrl('app_PostDetail', ['id' => $post->getId()]),
                'nbComments' => 0,
                'userName' => $post->getUser()->getFirstName(),
                'userSurname' => $post->getUser()->getLastName(),
                'userImage' => $post->getUser()->getImage(),
                'userId' => $post->getUser()->getId(),
            ];
            dump($newPos);
            return new JsonResponse([
                'success' => true,
                'post' => $newPos
            ]);

        }
        return new Response('error ',Response::HTTP_INTERNAL_SERVER_ERROR);
    }

    #[
        Route('/blog/page/{page}', name: 'app_blog_page', methods: ['GET'])]
    public function page(int $page, PostRepository $postRepository, ReactionPostRepository $reactionPostRepository): Response
    {
        $postsPerPage = 5;
        $offset = ($page - 1) * $postsPerPage;

        $posts = $postRepository->findBy([], ['date_post' => 'DESC'], $postsPerPage, $offset);

        $user = $this->getUser(); // Get the logged-in user

        $postsArray = array_map(function ($post) use ($user, $reactionPostRepository) {
            $images = $post->getImages();
            $imagesArray = array_map(function ($image) {
                return $image->getPath();
            }, $images);

            $postUrl = $this->generateUrl('app_PostDetail', ['id' => $post->getId()]);

            $nbComments = count($post->getComments());

            // Get the user's reaction to this post
            $userReaction = $reactionPostRepository->findOneBy(['post' => $post, 'user' => $user]);
            $userReactionType = $userReaction ? $userReaction->getType() : null;

            // Get the number of reactions for this post
            $nbReactions = $reactionPostRepository->countReactionsForPost($post);

            $user = $post->getUser();
            $userName = $user->getLastName();
            $userSurname = $user->getFirstName();
            $userImage = $user->getImage();
            $userId = $user->getId();

            return [
                'id' => $post->getId(),
                'caption' => $post->getCaption(),
                'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                'nbReactions' => $nbReactions,
                'images' => $imagesArray,
                'url' => $postUrl,
                'nbComments' => $nbComments,
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
                'userReactionType' => $userReactionType, // Add the user's reaction type to the returned array
            ];
        }, $posts);

        return new JsonResponse(['posts' => $postsArray]);
    }

    #[Route('/getUserId', name: 'app_get_user_id', methods: ['GET'])]
    public function getUserId(UserRepository $userRepository): Response
    {
        $userEmail = $this->getUser()->getUserIdentifier();
        $user = $userRepository->findOneBy(['email' => $userEmail]);
        $userId = null;
        if ($user) {
            $userId = $user->getId();
        }

        return new JsonResponse(['userId' => $userId]);
    }

    #[Route('/blog/count', name: 'app_blog_count', methods: ['GET'])]
    public function count(PostRepository $postRepository, UserRepository $userRepository): Response
    {
        $count = $postRepository->count([]);
        return new JsonResponse(['count' => $count]);
    }

    #[Route('/new', name: 'app_blog_new', methods: ['GET', 'POST'])]
    public function new(RealTimeUpdater $realTimeUpdater, Request $req, ManagerRegistry $doc, ValidatorInterface $validator): Response
    {
        if ($req->isXmlHttpRequest()) {
            $post = new Post();
            $caption = $req->get('caption');
            $imageFiles = $req->files->get('images');

            $user = $this->getUser();
            $currentTimestamp = new DateTime();
            $currentTimestamp->setTimezone(new DateTimeZone('Africa/Tunis'));
            $post->setCaption($caption);
            $post->setDatePost($currentTimestamp);
            $post->setNbReactions(0);
            $post->setUser($user);

            // Add images to the Post entity before validation
            if ($imageFiles) {
                foreach ($imageFiles as $imageFile) {
                    $postImage = new ImagePsot();
                    $postImage->setImageFile($imageFile);
                    $postImage->setPost($post);
                    $post->addImage($postImage);
                }
            }

            $errors = $validator->validate($post);

            if (count($errors) > 1) {
                $errorsString = '';
                foreach ($errors as $error) {
                    $errorsString .= $error->getMessage() . "\n";
                }

                return new JsonResponse(['success' => false, 'message' => $errorsString]);
            }

            $em = $doc->getManager();
            $em->persist($post);

            foreach ($post->getImages() as $postImage) {
                $em->persist($postImage);
            }

            $em->flush();

            $imagesArray = array_map(function ($image) {
                return $image->getPath();
            }, $post->getImages());

            $nbComments = count($post->getComments());

            $postUrl = $this->generateUrl('app_PostDetail', ['id' => $post->getId()]);

            $user = $post->getUser();
            $userName = $user->getLastName();
            $userSurname = $user->getFirstName();
            $userImage = $user->getImage();
            $userId = $user->getId();

            $realTimeUpdater->notifyApp(['Data' => ['idPost' => $post->getId()], 'action' => 'postEvent', 'subAction' => 'ADD'], $userId);

            return new JsonResponse([
                'success' => true,
                'post' => [
                    'id' => $post->getId(),
                    'caption' => $post->getCaption(),
                    'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                    'nbReactions' => $post->getNbReactions(),
                    'images' => $imagesArray,
                    'url' => $postUrl,
                    'nbComments' => $nbComments,
                    'userName' => $userName,
                    'userSurname' => $userSurname,
                    'userImage' => $userImage,
                    'userId' => $userId,
                ]
            ]);
        }
        return $this->redirectToRoute('app_blog');
    }


    #[Route('/blog/{id}', name: 'app_blog_delete', methods: ['DELETE'])]
    public function delete(RealTimeUpdater $realTimeUpdater, ManagerRegistry $doctrine, $id, PostRepository $postRepository, Request $req): Response
    {
        if ($req->isXmlHttpRequest()) {
            $auteur = $postRepository->find($id);
            $em = $doctrine->getManager();
            $em->remove($auteur);
            $em->flush();

            $realTimeUpdater->notifyApp(['Data' => ['idPost' => $id], 'action' => 'postEvent', 'subAction' => 'DELETE'], $this->getUser()->getId());

            return new Response('Post supprimé avec succès', Response::HTTP_OK);
        }
        return $this->redirectToRoute('app_blog');
    }

    #[Route('/edit/{id}', name: 'app_blog_update', methods: ['POST'])]
    public function update(RealTimeUpdater $realTimeUpdater, ManagerRegistry $doctrine, $id, Request $req): Response
    {
        $post = $doctrine->getRepository(Post::class)->find($id);

        if (!$post) {
            throw $this->createNotFoundException('Le post d\'id ' . $id . ' n\'a pas été trouvé.');
        }
        $caption = $req->get('caption');
        $imageFiles = $req->files->get('images');
        $currentTimestamp = new DateTime();
        $currentTimestamp->setTimezone(new DateTimeZone('Africa/Tunis'));
        $post->setDatePost($currentTimestamp);
        $post->setCaption($caption);

        if ((empty($caption) && empty($imageFiles)) || (ctype_space($caption) && empty($imageFiles))) {
            return new JsonResponse(['success' => false, 'message' => 'Le caption ne peut pas être vide si aucune image n\'est fournie, et vice versa.']);
        }

        $em = $doctrine->getManager();

        $imagesArray = [];
        if ($imageFiles) {
            foreach ($imageFiles as $imageFile) {
                $postImage = new ImagePsot();
                $postImage->setImageFile($imageFile);
                $postImage->setPost($post);
                $em->persist($postImage);
                $imagesArray[] = $postImage->getPath();
            }
        }

        foreach ($post->getImages() as $image) {
            $imagesArray[] = $image->getPath();
        }

        $nbComments = count($post->getComments());

        $em->persist($post);
        $em->flush();

        $postUrl = $this->generateUrl('app_PostDetail', ['id' => $post->getId()]);

        $user = $post->getUser();
        $userName = $user->getLastName();
        $userSurname = $user->getFirstName();
        $userImage = $user->getImage();
        $userId = $user->getId();

        $this->addFlash('success', 'Le post a bien été modifié.');

        $realTimeUpdater->notifyApp(['Data' => ['idPost' => $post->getId()], 'action' => 'postEvent', 'subAction' => 'UPDATE'], $this->getUser()->getId());

        return new JsonResponse([
            'success' => true,
            'post' => [
                'id' => $post->getId(),
                'caption' => $post->getCaption(),
                'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                'nbReactions' => $post->getNbReactions(),
                'images' => $imagesArray,
                'url' => $postUrl,
                'nbComments' => $nbComments,
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
            ]
        ]);
    }

    #[Route('/blog/search/{caption}', name: 'app_blog_search', methods: ['GET'])]
    public function search(string $caption, PostRepository $postRepository): Response
    {
        $posts = $postRepository->findByCaptionLike($caption);

        $postsArray = array_map(function ($post) {
            $images = $post->getImages();
            $imagesArray = array_map(function ($image) {
                return $image->getPath();
            }, $images);

            $postUrl = $this->generateUrl('app_PostDetail', ['id' => $post->getId()]);

            $nbComments = count($post->getComments());

            $user = $post->getUser();
            $userName = $user->getLastName();
            $userSurname = $user->getFirstName();
            $userImage = $user->getImage();
            $userId = $user->getId();

            return [
                'id' => $post->getId(),
                'caption' => $post->getCaption(),
                'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                'nbReactions' => $post->getNbReactions(),
                'images' => $imagesArray,
                'url' => $postUrl,
                'nbComments' => $nbComments,
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
            ];
        }, $posts);

        return new JsonResponse(['posts' => $postsArray]);
    }

    #[Route('/edit/{id}/remove-image', name: 'app_blog_remove_image', methods: ['POST'])]
    public function removeImage(ManagerRegistry $doctrine, $id): Response
    {
        $post = $doctrine->getRepository(Post::class)->find($id);

        if (!$post) {
            throw $this->createNotFoundException('Le post d\'id ' . $id . ' n\'a pas été trouvé.');
        }

        $em = $doctrine->getManager();
        $em->persist($post);
        $em->flush();

        return new JsonResponse(['success' => true]);
    }

    #[Route('/blogAdmin2', name: 'app_blogAdmin')]
    public function indexAdmin(PostRepository $postRepository): Response
    {
        $posts = $postRepository->findBy([], ['date_post' => 'DESC']);

        $postsArray = array_map(function ($post) {
            $images = $post->getImages();
            $imagesArray = array_map(function ($image) {
                $imagePath = $this->getParameter('images_directory') . '/' . $image->getPath();
                list($width, $height) = getimagesize($imagePath);
                $imageSize = $width > $height ? 'large' : 'small';
                return [
                    'path' => $image->getPath(),
                    'size' => $imageSize,
                ];
            }, $images);

            $user = $post->getUser();
            $userName = $user->getLastName();
            $userSurname = $user->getFirstName();
            $userImage = $user->getImage();
            $userId = $user->getId();


            $comments = $post->getComments()->toArray();
            $commentsArray = array_map(function ($comment) {
                return [
                    'id' => $comment->getIdComment(),
                    'caption' => $comment->getCaption(),
                    'dateComment' => $comment->getDateComment()->format('D, d M y h:i A'),
                    'userName' => $comment->getUser()->getLastName(),
                    'userSurname' => $comment->getUser()->getFirstName(),
                    'userImage' => $comment->getUser()->getImage(),
                    'userId' => $comment->getUser()->getId(),
                ];
            }, $comments);

            $nbeReaction = $post->getReactions()->toArray();

            return [
                'id' => $post->getId(),
                'caption' => $post->getCaption(),
                'datePost' => $post->getDatePost()->format('D, d M y h:i A'),
                'nbReactions' => $post->getNbReactions(),
                'images' => $imagesArray,
                'comments' => $commentsArray,
                'nbComments' => count($comments),
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
                'reactionsNbr' => count($nbeReaction)
            ];
        }, $posts);

        return $this->render('blog/blogAdmin2.html.twig', [
            'posts' => $postsArray,
        ]);
    }

    #[Route('/PostDetail/{id}', name: 'app_PostDetail')]
    public function indexPostDetail($id, PostRepository $postRepository, CommentPostRepository $commentPostRepository, UserRepository $userRepository, ReactionPostRepository $reactionPostRepository): Response
    {
        $post = $postRepository->find($id);

        if (!$post) {
            throw $this->createNotFoundException('Le post d\'id ' . $id . ' n\'a pas été trouvé.');
        }

        $images = $post->getImages();
        $imagesArray = array_map(function ($image) {
            return $image->getPath();
        }, $images);

        $comments = $commentPostRepository->findBy(['post' => $post->getId()], ['dateComment' => 'DESC']);


        $userEmail = $this->getUser()->getUserIdentifier();
        $user = $userRepository->findOneBy(['email' => $userEmail]);
        $currentUser = $user->getId();

        $currentUserImg = $user->getImage();

        $nbReactions = $reactionPostRepository->count(['post' => $post]);

        $commentsArray = array_map(function ($comment) {
            return [
                'id' => $comment->getIdComment(),
                'idPost' => $comment->getPost()->getId(),
                'caption' => $comment->getCaption(),
                'dateComment' => $comment->getDateComment()->format('d M y, h:i A'),
                'userName' => $comment->getUser()->getLastName(),
                'userSurname' => $comment->getUser()->getFirstName(),
                'userImage' => $comment->getUser()->getImage(),
                'userId' => $comment->getUser()->getId(),

            ];
        }, $comments);


        return $this->render('blog/postDetails2.html.twig', [
            'post' => $post,
            'images' => $imagesArray,
            'comments' => $commentsArray,
            'nbComments' => count($comments),
            'nom' => $post->getUser()->getLastName(),
            'prenom' => $post->getUser()->getFirstName(),
            'imguser' => $post->getUser()->getImage(),
            'currentUserImg' => $currentUserImg,
            'currentUser' => $currentUser,
            'nbReactions' => $nbReactions,
        ]);
    }

    #[Route('/newComment', name: 'new_comment', methods: ['POST'])]
    public function newComment(Request $request): Response
    {
        $entityManager = $this->getDoctrine()->getManager();


        $caption = $request->request->get('caption');
        $postId = $request->request->get('post_id');

        $post = $entityManager->getRepository(Post::class)->find($postId);

        if (!$post) {
            throw $this->createNotFoundException(
                'No post found for id ' . $postId
            );
        }

        $comment = new CommentPost();
        $user = $this->getUser();

        $comment->setCaption($caption);
        $comment->setPost($post);
        $comment->setDateComment(new \DateTime());
        $comment->setUser($user);

        $entityManager->persist($comment);
        $entityManager->flush();

        $user = $comment->getUser();
        $userName = $user->getLastName();
        $userSurname = $user->getFirstName();
        $userImage = $user->getImage();
        $userId = $user->getId();

        return new JsonResponse([
            'success' => true,
            'comment' => [
                'id' => $comment->getIdComment(),
                'idPost' => $comment->getPost()->getId(),
                'caption' => $comment->getCaption(),
                'dateComment' => $comment->getDateComment()->format('D, d M y h:i A'),
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
            ]
        ]);
    }


    #[Route('/deleteComment/{id}', name: 'delete_comment', methods: ['DELETE'])]
    public function deleteComment($id): Response
    {
        $em = $this->getDoctrine()->getManager();
        $comment = $em->getRepository(CommentPost::class)->find($id);

        if (!$comment) {
            return new JsonResponse(['success' => false, 'message' => 'Commentaire non trouvé.']);
        }

        try {
            $em->remove($comment);
            $em->flush();
            return new JsonResponse(['success' => true]);
        } catch (\Exception $e) {
            return new JsonResponse(['success' => false, 'message' => 'Une erreur est survenue lors de la suppression du commentaire.']);
        }
    }

    #[Route('/updateComment/{id}', name: 'update_comment', methods: ['POST'])]
    public function updateComment($id, Request $request): Response
    {
        $repository = $this->getDoctrine()->getRepository(CommentPost::class);


        $comment = $repository->find($id);

        if (!$comment) {

            return new JsonResponse(['success' => false, 'message' => 'Comment not found']);
        }


        $newCaption = $request->request->get('caption');


        $comment->setCaption($newCaption);
        $comment->setDateComment(new \DateTime());


        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($comment);
        $entityManager->flush();

        $user = $comment->getUser();
        $userName = $user->getLastName();
        $userSurname = $user->getFirstName();
        $userImage = $user->getImage();
        $userId = $user->getId();



        return new JsonResponse([
            'success' => true,
            'comment' => [
                'id' => $comment->getIdComment(),
                'idPost' => $comment->getPost()->getId(),
                'caption' => $comment->getCaption(),
                'dateComment' => $comment->getDateComment()->format('D, d M y h:i A'),
                'userName' => $userName,
                'userSurname' => $userSurname,
                'userImage' => $userImage,
                'userId' => $userId,
            ]
        ]);
    }

    /**
     * @throws \Exception
     */
    #[Route('/checkImage', name: 'checkImage', methods: ['POST'])]
    public function checkImage(Request $request, ImageExpliciteApi $imageExpliciteApi, UploadImage $uploadImage): Response
    {
        $imageFile = $request->files->get('image');

        if ($imageFile) {
            $imageUrl = $uploadImage->uploadImageToImgBB($imageFile);

            $response = $imageExpliciteApi->checkExplicitContent($imageUrl);

            return new JsonResponse($response);
        }

        return new JsonResponse(['error' => 'Aucune image n\'a été reçue.']);
    }


    #[Route('/addReaction', name: 'add_reaction', methods: ['POST'])]
    public function addReaction(Request $request, PostRepository $postRepository, ReactionPostRepository $reactionPostRepository, ManagerRegistry $doc): Response
    {
        $postId = $request->request->get('postId');
        $reactionType = $request->request->get('reactionType');

        $post = $postRepository->find($postId);
        $user = $this->getUser();

        $reaction = $reactionPostRepository->findOneBy(['post' => $post, 'user' => $user]);

        if (!$reaction) {
            $reaction = new ReactionPost();
            $reaction->setPost($post);
            $reaction->setUser($user);
        }

        $reaction->setType($reactionType);

        $entityManager = $doc->getManager();
        $entityManager->persist($reaction);
        $entityManager->flush();

        return new JsonResponse(['status' => 'success']);
    }

    #[Route('/deleteReaction', name: 'delete_reaction', methods: ['POST'])]
    public function deleteReaction(Request $request, ReactionPostRepository $reactionPostRepository, ManagerRegistry $doc): Response
    {
        $postId = $request->request->get('postId');
        $user = $this->getUser(); // Get the logged-in user

        // Find the user's reaction to this post
        $userReaction = $reactionPostRepository->findOneBy(['post' => $postId, 'user' => $user]);

        if ($userReaction) {
            // If the user has reacted to this post, delete the reaction
            $em = $doc->getManager();
            $em->remove($userReaction);
            $em->flush();
        }

        return new JsonResponse(['message' => 'Reaction deleted successfully']);
    }
}
