<?php

namespace App\Repository;

use App\Entity\User;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use phpDocumentor\Reflection\Types\Boolean;

/**
 * @extends ServiceEntityRepository<User>
 *
 * @method User|null find($id, $lockMode = null, $lockVersion = null)
 * @method User|null findOneBy(array $criteria, array $orderBy = null)
 * @method User[]    findAll()
 * @method User[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class UserRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, User::class);
    }
//
//    /**
//     * Récupère les utilisateurs dont au moins un champ est vide.
//     *
//     * @return User[]
//     */
    public function findIncompleteUsers(): array
    {
        return $this->createQueryBuilder('u')
            ->where('u.age IS NULL OR u.phoneNumber IS NULL') // Vérifiez si au moins un champ est vide
            // Ajoutez d'autres conditions pour vérifier d'autres champs si nécessaire
            ->getQuery()
            ->getResult();
    }

    public function getnbruser()
    {
        /*$sql = "
    SELECT calendar.date AS creation_date, COUNT(user.date) AS compte_cree
    FROM (
        SELECT DATE_SUB(CURDATE(), INTERVAL (a.a + (10 * b.a) + (100 * c.a)) DAY) AS date
        FROM (SELECT 0 AS a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS a
        CROSS JOIN (SELECT 0 AS a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS b
        CROSS JOIN (SELECT 0 AS a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS c
    ) calendar
    LEFT JOIN user ON DATE(user.date) = calendar.date
    WHERE calendar.date >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK)
    GROUP BY calendar.date
    ORDER BY calendar.date
";

        $rsm = new \Doctrine\ORM\Query\ResultSetMappingBuilder($this->getEntityManager());
        $rsm->addScalarResult('creation_date', 'creation_date');
        $rsm->addScalarResult('compte_cree', 'compte_cree');

        $query = $this->getEntityManager()->createNativeQuery($sql, $rsm);

        return $query->getResult();*/

       /* $lastWeek = new \DateTime('-1 Week');

        return $this->createQueryBuilder('u')
            ->select("u.date as creation_date ,COUNT('*') as compte_cree")
            ->andWhere('u.date >= :date ')
            ->setParameter('date', $lastWeek)
            ->groupBy('u.date')
            ->getQuery()
            ->getResult();*/


        $dates = [];
        $lastWeek = new \DateTime('-1 Week');
        $today = new \DateTime();
        for ($date = $lastWeek; $date <= $today; $date->modify('+1 day')) {
            $dates[$date->format('Y-m-d')] = 0;
        }

        $lastWeek = new \DateTime('-1 Week');

        $data = $this->createQueryBuilder('u')
            ->select('CASE WHEN COUNT(u) > 0 THEN COUNT(u) ELSE 0 END AS compte_cree')
            ->addSelect("SUBSTRING(u.date, 1, 10) as creation_date")
            ->andWhere("SUBSTRING(u.date, 1, 10) >= :date")
            ->setParameter('date', $lastWeek->format('Y-m-d'))
            ->groupBy('creation_date')
            ->getQuery()
            ->getResult();


        foreach ($data as $item) {
            $dates[$item['creation_date']] = $item['compte_cree'];
        }

        $newData=[];
        foreach ($dates as $item) {
            $newData[]=[
                "compte_cree" => $item,
            ];
        }

        return $newData;
    }


    public function getnbrUsersPerWeek()
    {
        $lastWeek = new \DateTime('-1 Week');

        return $this->createQueryBuilder('u')
            ->select("COUNT('*') as nbrUsers")
            ->andWhere('SUBSTRING(u.date, 1, 10) >= :date ')
            ->setParameter('date', $lastWeek->format('Y-m-d'))
            ->getQuery()
            ->getResult();
    }

    public function findByGovernment(): array
    {
        return $this->createQueryBuilder('m')
            ->select("m.goverment as govenmentName , COUNT('') as numberMunicipalities")
            ->groupBy('m.goverment')
            ->getQuery()
            ->getResult();
    }


}
