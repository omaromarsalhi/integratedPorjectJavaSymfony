<?php

namespace App\Repository;

use App\Entity\Product;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Product>
 *
 * @method Product|null find($id, $lockMode = null, $lockVersion = null)
 * @method Product|null findOneBy(array $criteria, array $orderBy = null)
 * @method Product[]    findAll()
 * @method Product[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ProductRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Product::class);
    }

//    /**
//     * @return Product[] Returns an array of Product objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('p.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }


//    public function findOneBySomeField($value): ?Product
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }


    /**
     * @return Product[] Returns an array of Product objects
     */
    public function findByIdUser($value): array
    {
        return $this->createQueryBuilder('p')
            ->select('p.idProduct')
            ->andWhere('p.user = :val')
            ->setParameter('val', $value)
            ->getQuery()
            ->getResult();
    }


    public function findMinMaxPrices(): array
    {
        $queryBuilder = $this->createQueryBuilder('p');

        // Select the minimum and maximum price
        $queryBuilder
            ->select('MIN(p.price) AS minPrice', 'MAX(p.price) AS maxPrice')
            ->andWhere("p.state = 'verified' ")
            ->getQuery();

        // Execute the query and get the result
        $result = $queryBuilder->getQuery()->getSingleResult();

        // Access the min and max prices
        $minPrice = $result['minPrice'];
        $maxPrice = $result['maxPrice'];

        return ['minPrice' => $minPrice, 'maxPrice' => $maxPrice];
    }

    /**
     * @return Product[] Returns an array of Product objects
     */
    public function findByPrice($min, $max): array
    {
        return $this->createQueryBuilder('p')
            ->andWhere("p.state = 'verified' AND ( p.price BETWEEN :min AND :max )")
            ->setParameter('min', $min)
            ->setParameter('max', $max)
            ->getQuery()
            ->getResult();

    }

    /**
     * @return Product[] Returns an array of Product objects
     */
    public function findByPriceTest($filterData, $idList): array
    {
        $qb = $this->createQueryBuilder('p');

        $qb->andWhere('p.state = :value')
            ->setParameter('value', 'verified');


        if ($filterData['price']['allPrices'] === "false") {
            if ($filterData['priceIntervale']['min']) {
                $qb->andWhere('p.price >= :minPrice')
                    ->setParameter('minPrice', $filterData['priceIntervale']['min']);
            }
            if ($filterData['priceIntervale']['max']) {
                $qb->andWhere('p.price <= :maxPrice')
                    ->setParameter('maxPrice', $filterData['priceIntervale']['max']);
            }
        }

        if ($filterData['category']['food'] === "true") {
            $qb->andWhere('p.category = :category')
                ->setParameter('category', 'food');
        }

        if ($filterData['category']['sports'] === "true") {
            $qb->andWhere('p.category = :category')
                ->setParameter('category', 'sports');
        }

        if ($filterData['category']['entertainment'] === "true") {
            $qb->andWhere('p.category = :category')
                ->setParameter('category', 'entertainment');
        }

        if ($filterData['category']['realEstate'] === "true") {
            $qb->andWhere('p.category = :category')
                ->setParameter('category', 'realEstate');
        }

        if ($filterData['category']['vehicle'] === "true") {
            $qb->andWhere('p.category = :category')
                ->setParameter('category', 'vehicle');
        }


        if ($filterData['datetime']['today'] === "true") {
            $today = new \DateTime('today');
            $qb->andWhere('p.timestamp >= :date ')
                ->setParameter('date', $today);
        }

        if ($filterData['datetime']['thisWeek'] === "true") {
            $lastWeek = new \DateTime('-1 week');
            $qb->andWhere('p.timestamp >= :date ')
                ->setParameter('date', $lastWeek);
        }

        if ($filterData['datetime']['thisMonth'] === "true") {
            $lastMonth = new \DateTime('-1 month');
            $qb->andWhere('p.timestamp >= :date ')
                ->setParameter('date', $lastMonth);
        }

        if ($idList[0] !== -1) {
            $qb->andWhere($qb->expr()->in('p.idProduct', ':list'))
                ->setParameter('list', $idList);
        }

        if ($filterData['price']['asc'] === "true") {
            $qb->orderBy('p.price', 'ASC');
        }

        if ($filterData['price']['desc'] === "true") {
            $qb->orderBy('p.price', 'DESC');
        }

        if ($filterData['price']['desc'] === "false" && $filterData['price']['asc'] === "false")
            $qb->orderBy('p.idProduct', 'DESC');


        return $qb->getQuery()->getResult();
    }

    public function findVerifiedAndInStock(): array
    {
        $queryBuilder = $this->createQueryBuilder('p');
        $queryBuilder->where('p.state = :state')
            ->andWhere('p.quantity > :quantity')
            ->setParameter('state', 'verified')
            ->setParameter('quantity', 0)
            ->orderBy('p.timestamp', 'DESC')
            ->setMaxResults(5);

        return $queryBuilder->getQuery()->getResult();
    }

    public function findByProductsField(): array
    {
        $data = $this->createQueryBuilder('p')
            ->select('p.category category, p.state state , COUNT(p.idProduct) as count')
            ->groupBy('p.category , p.state ')
            ->getQuery()
            ->getResult();

        $formattedData = [];
        foreach ($data as $value) {
            $formattedData[$value['category']] = ['verified' => 0, 'unverified' => 0];
        }
        foreach ($data as $value) {
            $formattedData[$value['category']][$value['state']] = $value['count'];
        }

        return $formattedData;
    }


    /**
     * @return Product[] Returns an array of Product objects
     */

    public function findByExampleField(): array
    {
        $data = $this->createQueryBuilder('p')
            ->select('p.category category, p.state state , COUNT(p) as count')
            ->groupBy('p.category , p.state ')
            ->getQuery()
            ->getResult();

        $formattedData = [];
        foreach ($data as $value) {
            $formattedData[$value['category']] = ['verified' => 0, 'unverified' => 0];
        }
        foreach ($data as $value) {
            $formattedData[$value['category']][$value['state']] = $value['count'];
        }

        return $formattedData;
    }


    public function getVerifiedProductCount(): int
    {
        return $this->createQueryBuilder('p')
            ->select('count(p.idProduct)')
            ->where('p.state = :val')
            ->setParameter('val', 'verified')
            ->getQuery()
            ->getSingleScalarResult();
    }

    public function getUnverifiedProductCount(): int
    {
        return $this->createQueryBuilder('p')
            ->select('count(p.idProduct)')
            ->where('p.state = :val')
            ->setParameter('val', 'unverified')
            ->getQuery()
            ->getSingleScalarResult();
    }





    public function getStat2($user)
    {
        $qb = $this->createQueryBuilder('p');
        $qb->select('count(p.idProduct) as number');
        $qb->where(
            $qb->expr()->gte('p.timestamp', ':startOfMonth')
        )
            ->andWhere(
                $qb->expr()->lt('p.timestamp', ':startOfNextMonth')
            )
            ->andWhere('p.user = :user'
            )
            ->setParameter('startOfMonth', new \DateTime('first day of this month'))
            ->setParameter('startOfNextMonth', new \DateTime('first day of next month'))
            ->setParameter('user', $user);
        return $qb->getQuery()->getResult();
    }

    public function getStat4($user)
    {
        $qb = $this->createQueryBuilder('p');
        $qb->select('count(p.idProduct) as number');
        $qb->where(
            $qb->expr()->gte('p.timestamp', ':startOfMonth')
        )
            ->andWhere(
                $qb->expr()->lt('p.timestamp', ':startOfNextMonth')
            )
            ->andWhere('p.user = :user'
            )
            ->setParameter('startOfMonth', new \DateTime('first day of this year'))
            ->setParameter('startOfNextMonth', new \DateTime('first day of next year'))
            ->setParameter('user', $user);
        return $qb->getQuery()->getResult();
    }




}
