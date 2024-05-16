<?php

namespace App\Repository;

use App\Entity\Municipalite;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Municipalite>
 *
 * @method Municipalite|null find($id, $lockMode = null, $lockVersion = null)
 * @method Municipalite|null findOneBy(array $criteria, array $orderBy = null)
 * @method Municipalite[]    findAll()
 * @method Municipalite[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class MunicipaliteRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Municipalite::class);
    }

//    /**
//     * @return Municipalite[] Returns an array of Municipalite objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('m')
//            ->andWhere('m.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('m.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Municipalite
//    {
//        return $this->createQueryBuilder('m')
//            ->andWhere('m.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

    public function findTopMunicipalities(): array
    {
        $queryBuilder = $this->createQueryBuilder('m');
        $queryBuilder->select('m as municipalite, COUNT(u.idUser) as userCount')
            ->join('m.users', 'u')
            ->groupBy('m.goverment')
            ->orderBy('userCount', 'DESC')
            ->setMaxResults(10);

        return $queryBuilder->getQuery()->getResult();
    }


    public function findByGovernment(): array
    {
        return $this->createQueryBuilder('m')
            ->select("m.goverment as govenmentName , COUNT('') as numberMunicipalities")
            ->groupBy('m.goverment')
            ->getQuery()
            ->getResult();
    }

    public function findTopMunicipalitiesdashBoard(): array
    {
        $queryBuilder = $this->createQueryBuilder('m');
        $queryBuilder->select('m.name as municipalite, COUNT(u.idUser) as userCount')
            ->join('m.users', 'u')
            ->groupBy('m.goverment')
            ->orderBy('userCount', 'DESC')
            ->setMaxResults(5);
        return $queryBuilder->getQuery()->getResult();
    }
}
