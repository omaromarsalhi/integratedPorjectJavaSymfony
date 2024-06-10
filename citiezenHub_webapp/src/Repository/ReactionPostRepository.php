<?php

namespace App\Repository;

use App\Entity\Post;
use App\Entity\ReactionPost;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<ReactionPost>
 *
 * @method ReactionPost|null find($id, $lockMode = null, $lockVersion = null)
 * @method ReactionPost|null findOneBy(array $criteria, array $orderBy = null)
 * @method ReactionPost[]    findAll()
 * @method ReactionPost[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ReactionPostRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, ReactionPost::class);
    }

//    /**
//     * @return ReactionPost[] Returns an array of ReactionPost objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('r.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?ReactionPost
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

// src/Repository/ReactionPostRepository.php

    public function countReactionsForPost(Post $post): int
    {
        return $this->createQueryBuilder('r')
            ->select('count(r.idReactionPost)')
            ->where('r.post = :post')
            ->setParameter('post', $post)
            ->getQuery()
            ->getSingleScalarResult();
    }
}
