<?php

namespace App\MyHelpers;

use App\Entity\AiResult;
use App\Entity\Product;
use Exception;
use Symfony\Component\Filesystem\Filesystem;
use Symfony\Component\HttpClient\HttpClient;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;


class AiVerification
{
    private $aiDataHolder;


    public function run($obj): AiDataHolder
    {
        $this->aiDataHolder = new AiDataHolder();
        $this->getAllDesc($obj['images'], $obj['title']);
        $this->compareDescWithTitleAndCategory($this->aiDataHolder->getDescriptions(), $obj);
        return $this->aiDataHolder;
    }


    public function runImageSearch($fileName, $data): array
    {
        $imageDescription = $this->generateImageDescriptionWithNoTitle($fileName);
        $result = [];
        for ($i = 0; $i < count($data); $i++) {
            if ($data[$i] instanceof Product) {
                $rep = $this->looksForsemilairity($imageDescription, $data[$i]->getName());
                if (str_starts_with(strtolower($rep), " yes") || str_starts_with(strtolower($rep), "yes"))
                    $result[] = $data[$i]->getIdProduct();
            }
        }
        return $result;
    }

//    public function runImageSearch($fileName, $data): array
//    {
//        $serializer = new Serializer([new ObjectNormalizer()], [new JsonEncoder()]);
//        $imageDescription = $this->generateImageDescriptionWithNoTitle($fileName);
//        $result = [];
//        for ($i = 0; $i < count($data); $i++) {
//            if ($data[$i] instanceof AiResult) {
//                $aiDataHolder = $serializer->deserialize($data[$i]->getBody(), AiDataHolder::class, 'json');
//                $descriptions = $aiDataHolder->getDescriptions();
//                $rep = $this->looksForsemilairity($imageDescription, $descriptions[0]);
//                if (str_starts_with(strtolower($rep), " yes") || str_starts_with(strtolower($rep), "yes"))
//                    $result[] = $data[$i]->getIdProduct();
//
//            }
//        }
//        return $result;
//    }

    /**
     * @throws Exception
     */
    public function runOcr($obj): void
    {
        $this->getOcrResult($obj['pathFrontCin'], $obj['fileNameFront']);
        $this->getOcrResult($obj['pathBackCin'], $obj['fileNameBackCin']);
        try {
        $this->formatJsonFilesOfCin($obj['fileNameFront'], $obj['fileNameBackCin'], $obj['path']);
        } catch (Exception $e) {
            throw new \Exception('something went wrong');
        }
    }

    public function formatJsonFilesOfCin($filePathFrontCin, $filePathBackCin, $path): void
    {
        try {
        $pathFrontCin = '../../files/usersJsonFiles/' . $filePathFrontCin . '.json';
        $pathBackCin = '../../files/usersJsonFiles/' . $filePathBackCin . '.json';

        $jsonString = file_get_contents($pathFrontCin);
        $jsonDataFrontCin = json_decode($jsonString, true);

        $jsonString = file_get_contents($pathBackCin);
        $jsonDataBackCin = json_decode($jsonString, true);

        $bounding_boxesFront = $jsonDataFrontCin['google']['bounding_boxes'];
        $bounding_boxesBack = $jsonDataBackCin['google']['bounding_boxes'];

        $userCinData = [
            'الاسم' => ['topPlusHeight' => 0]
        ];
        for ($i = 0; $i < sizeof($bounding_boxesFront); $i++) {
            switch ($bounding_boxesFront[$i]['text']) {
                case 'اللقب':
                    $userCinData['اللقب']['top'] = $bounding_boxesFront[$i]['top'];
                    $userCinData['اللقب']['topPlusHeight'] = $bounding_boxesFront[$i]['height'] + $bounding_boxesFront[$i]['top'];
                    $userCinData['اللقب']['data'] = '';
                    break;
                case 'الاسم':
                    $userCinData['الاسم']['top'] = $bounding_boxesFront[$i]['top'];
                    $userCinData['الاسم']['topPlusHeight'] = $bounding_boxesFront[$i]['height'] + $bounding_boxesFront[$i]['top'];
                    $userCinData['الاسم']['data'] = '';
                    break;
                case 'بن':
                    if (!array_key_exists('بن', $userCinData) && $bounding_boxesFront[$i]['top'] > $userCinData['الاسم']['topPlusHeight']) {
                        $userCinData['بن']['top'] = $bounding_boxesFront[$i]['top'];
                        $userCinData['بن']['topPlusHeight'] = $bounding_boxesFront[$i]['height'] + $bounding_boxesFront[$i]['top'];
                        $userCinData['بن']['data'] = '';

                    }
                    break;
                case'الولادة':
                    $userCinData['الولادة']['top'] = $bounding_boxesFront[$i]['top'];
                    $userCinData['الولادة']['topPlusHeight'] = $bounding_boxesFront[$i]['height'] + $bounding_boxesFront[$i]['top'];
                    $userCinData['الولادة']['left'] = $bounding_boxesFront[$i]['left'];
                    $userCinData['الولادة']['data'] = '';
                    break;
                case 'مكانها':
                    $userCinData['مكانها']['top'] = $bounding_boxesFront[$i]['top'];
                    $userCinData['مكانها']['topPlusHeight'] = $bounding_boxesFront[$i]['height'] + $bounding_boxesFront[$i]['top'];
                    $userCinData['مكانها']['data'] = '';
                    break;
            }
        }


        for ($i = 0;
             $i < sizeof($bounding_boxesBack);
             $i++) {
            switch ($bounding_boxesBack[$i]['text']) {
                case 'المهنة':
                    $userCinData['المهنة']['top'] = $bounding_boxesBack[$i]['top'];
                    $userCinData['المهنة']['topPlusHeight'] = $bounding_boxesBack[$i]['height'] + $bounding_boxesBack[$i]['top'];
                    $userCinData['المهنة']['data'] = '';
                    break;
                case 'الأم':
                    $userCinData['الأم']['top'] = $bounding_boxesBack[$i]['top'];
                    $userCinData['الأم']['topPlusHeight'] = $bounding_boxesBack[$i]['height'] + $bounding_boxesBack[$i]['top'];
                    $userCinData['الأم']['left'] = $bounding_boxesBack[$i]['left'];
                    $userCinData['الأم']['data'] = '';
                    break;
                case 'العنوان':
                    $userCinData['العنوان']['top'] = $bounding_boxesBack[$i]['top'];
                    $userCinData['العنوان']['topPlusHeight'] = $bounding_boxesBack[$i]['height'] + $bounding_boxesBack[$i]['top'];
                    $userCinData['العنوان']['data'] = '';
                    break;
                case 'في':
                    $userCinData['في']['top'] = $bounding_boxesBack[$i]['top'];
                    $userCinData['في']['topPlusHeight'] = $bounding_boxesBack[$i]['height'] + $bounding_boxesBack[$i]['top'];
                    $userCinData['في']['left'] = $bounding_boxesBack[$i]['left'];
                    $userCinData['في']['data'] = '';
                    break;
            }
        }

        for ($i = 0; $i < sizeof($bounding_boxesFront); $i++) {
            if (($bounding_boxesFront[$i]['text'] !== 'اللقب' && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) < $userCinData['اللقب']['topPlusHeight'] && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) > $userCinData['اللقب']['top']) || ($bounding_boxesFront[$i]['text'] !== 'اللقب' && ($bounding_boxesFront[$i]['top'] > $userCinData['اللقب']['top'] && $bounding_boxesFront[$i]['top'] < $userCinData['اللقب']['topPlusHeight']))) {
                $userCinData['اللقب']['data'] = $userCinData['اللقب']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            } else if (($bounding_boxesFront[$i]['text'] !== 'الاسم' && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) < $userCinData['الاسم']['topPlusHeight'] && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) > $userCinData['الاسم']['top']) || ($bounding_boxesFront[$i]['text'] !== 'الاسم' && ($bounding_boxesFront[$i]['top'] > $userCinData['الاسم']['top'] && $bounding_boxesFront[$i]['top'] < $userCinData['الاسم']['topPlusHeight']))) {
                $userCinData['الاسم']['data'] = $userCinData['الاسم']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            } else if ((($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) > $userCinData['الاسم']['topPlusHeight'] && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) < $userCinData['الولادة']['top'])) {
                $userCinData['بن']['data'] = $userCinData['بن']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            } else if ($bounding_boxesFront[$i]['text'] !== 'الولادة' && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) < $userCinData['الولادة']['topPlusHeight'] && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) > $userCinData['الولادة']['top'] && $bounding_boxesFront[$i]['left'] < $userCinData['الولادة']['left']) {
                $userCinData['الولادة']['data'] = $userCinData['الولادة']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            } else if ($bounding_boxesFront[$i]['text'] !== 'مكانها' && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) < $userCinData['مكانها']['topPlusHeight'] && ($bounding_boxesFront[$i]['top'] + $bounding_boxesFront[$i]['height'] / 2) > $userCinData['مكانها']['top']) {
                $userCinData['مكانها']['data'] = $userCinData['مكانها']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            } else if (intval($bounding_boxesFront[$i]['text']) > 9999999 && is_numeric($bounding_boxesFront[$i]['text'])) {
                $userCinData['cart id']['data'] = $userCinData['مكانها']['data'] . ' ' . $bounding_boxesFront[$i]['text'];
            }
        }


        for ($i = 0; $i < sizeof($bounding_boxesBack); $i++) {
            if ($bounding_boxesBack[$i]['text'] !== 'المهنة' && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) < $userCinData['المهنة']['topPlusHeight'] && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) > $userCinData['المهنة']['top']) {
                $userCinData['المهنة']['data'] = $userCinData['المهنة']['data'] . ' ' . $bounding_boxesBack[$i]['text'];
            } else if ($bounding_boxesBack[$i]['text'] !== 'الأم' && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) < $userCinData['الأم']['topPlusHeight'] && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) > $userCinData['الأم']['top'] && $bounding_boxesBack[$i]['left'] < $userCinData['الأم']['left']) {
                $userCinData['الأم']['data'] = $userCinData['الأم']['data'] . ' ' . $bounding_boxesBack[$i]['text'];
            } else if ($bounding_boxesBack[$i]['text'] !== 'العنوان' && ((($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) < $userCinData['العنوان']['topPlusHeight'] && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) > $userCinData['العنوان']['top']) || (($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) > $userCinData['العنوان']['topPlusHeight'] && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) < $userCinData['في']['top']))) {
                $userCinData['العنوان']['data'] = $userCinData['العنوان']['data'] . ' ' . $bounding_boxesBack[$i]['text'];
            } else if ($bounding_boxesBack[$i]['text'] !== 'في' && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) < $userCinData['في']['topPlusHeight'] && ($bounding_boxesBack[$i]['top'] + $bounding_boxesBack[$i]['height'] / 2) > $userCinData['في']['top'] && $bounding_boxesBack[$i]['left'] < $userCinData['في']['left']) {
                $userCinData['في']['data'] = $userCinData['في']['data'] . ' ' . $bounding_boxesBack[$i]['text'];
            }
        }


        $filesystem = new Filesystem();
        $filesystem->remove($pathFrontCin);
        $filesystem->remove($pathBackCin);

        $userCinData['الولادة']['data'] = trim($userCinData['الولادة']['data']);
        $userCinData['في']['data'] = trim($userCinData['في']['data']);
        $date = explode(" ", $userCinData['الولادة']['data']);
        $date2 = explode(" ", $userCinData['في']['data']);

        for ($i = 0; $i < sizeof($date); $i++) {
            if (is_numeric($date[$i]) && intval($date[$i]) < 100)
                $day = $date[$i];
            elseif (is_numeric($date[$i]))
                $year = $date[$i];
            else
                $arabicMonth = $date[$i];
        }
        for ($i = 0; $i < sizeof($date2); $i++) {
            if (is_numeric($date2[$i]) && intval($date2[$i]) < 100)
                $day2 = $date2[$i];
            elseif (is_numeric($date2[$i]))
                $year2 = $date2[$i];
            else
                $arabicMonth2 = $date2[$i];
        }

        $englishMonth = [
            'جانفي' => 'January',
            'فيفري' => 'February',
            'مارس' => 'March',
            'افريل' => 'April',
            'ماي' => 'May',
            'جوان' => 'June',
            'جويلية' => 'July',
            'اوت' => 'August',
            'سبتمبر' => 'September',
            'اكتوبر' => 'October',
            'نوفمبر' => 'November',
            'ديسمبر' => 'December',
        ];

        $englishMonthName = $englishMonth[trim($arabicMonth)];
        $englishMonthName2 = $englishMonth[trim($arabicMonth2)];

        $dateString = $day . '' . $englishMonthName . ' ' . $year;
        $dateString2 = $day2 . '' . $englishMonthName2 . ' ' . $year2;
        $userCinData['الولادة']['data'] = date('m-d-Y', strtotime($dateString));
        $userCinData['في']['data'] = date('m-d-Y', strtotime($dateString2));
//


        $modifiedJsonString = json_encode($userCinData, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
        file_put_contents('../../files/usersJsonFiles/' . $path . '.json', $modifiedJsonString);
        } catch (Exception $e) {
            throw new \Exception('something went wrong');
        }
    }


    function getOcrResult($path, $fileName): string
    {
        return $this->Http('get-OCR_result?path=' . $path . '&fileName=' . $fileName);
    }


    private
    function getAllDesc($images_url, $title): void
    {
        $result = [];
        for ($i = 0; $i < sizeof($images_url); $i++) {
            $result[] = $this->generateImageDescription($images_url[$i], $title);
        }
        $this->aiDataHolder->setDescriptions($result);
    }


    private
    function compareDescWithTitleAndCategory($descriptions, $obj): void
    {
        $result1 = [];
        $result2 = [];
        for ($i = 0; $i < sizeof($descriptions); $i++) {
            $result1[] = $this->getTitleValidation($descriptions[$i], $obj['title']);
            $result2[] = $this->getCategoryValidation($descriptions[$i], $obj['category']);
        }
        $this->aiDataHolder->setTitleValidation($result1);
        $this->aiDataHolder->setCategoryValidation($result2);

    }


    private function getTitleValidation($desc, $title): string
    {
        return $this->Http('get-title_validation?desc=' . $desc . '&title=' . $title);
    }

    private function getCategoryValidation($desc, $category): string
    {
        return $this->Http('get-category_validation?desc=' . $desc . '&category=' . $category);
    }

    private function generateImageDescription($image_url, $title): string
    {
        $absolute_path = 'C:\Users\omar salhi\Desktop\integratedPorjectJavaSymfony\citiezenHub_webapp\public\usersImg\\' . $image_url;
        return $this->Http('get-product_image_descreption_title?image_url=' . $absolute_path . '&fileName=' . $title);
    }

    public function generateImageDescriptionWithNoTitle($image_url): string
    {
        $absolute_path = 'C:\Users\omar salhi\Desktop\integratedPorjectJavaSymfony\citiezenHub_webapp\public\usersImg\\' . $image_url;
        return $this->Http('get-product_image_descreption?image_url=' . $absolute_path);
    }

    public function looksForsemilairity($p1, $p2): string
    {
        return $this->Http('looksForsemilairity?p1=' . $p1 . '&p2=' . $p2);
    }


    private
    function Http($url): string
    {
        $client = HttpClient::create();
        $response = $client->request('POST', 'http://127.0.0.1:5000/' . $url);
        $substringsToRemove = ['\"', '""\\', '"\n', '"', '\n'];
        $content = str_replace($substringsToRemove, "", $response->getContent());
        return $content;
    }


}