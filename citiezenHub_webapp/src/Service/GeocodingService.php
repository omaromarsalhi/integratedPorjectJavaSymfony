<?php

namespace App\Service;

use Symfony\Component\HttpClient\HttpClient;
use Symfony\Contracts\HttpClient\Exception\TransportExceptionInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class GeocodingService
{
    private $apiKey;
    private $client;

    public function __construct(HttpClientInterface $client)
    {
        $this->client = $client;
        $this->apiKey = 'AIzaSyAzyZU0FXv0ZmoL9hnUhRkM-zV-BdoQRo4';
    }

    public function geocode(string $address, string $lng): array
    {
        try {
            $response = $this->client->request(
                'GET',
                'https://maps.googleapis.com/maps/api/geocode/json',
                [
                    'query' => [
                        'address' => $address,
                        'key' => $this->apiKey,
                        'language' => $lng
                    ]
                ]
            );
        } catch (TransportExceptionInterface $e) {
        }

        $data = $response->toArray();

        if ($data['status'] !== 'OK') {
            throw new \Exception('Error fetching geocode data: ' . $data['status']);
        }

        return $data['results'];
    }

    public function isInMunicipality(string $address, string $municipality): bool
    {
        $results = $this->geocode($address, 'fr');
        foreach ($results as $result) {
            foreach ($result['address_components'] as $component) {
                if (in_array('administrative_area_level_2', $component['types']) && $component['long_name'] === $municipality) {
                    return true;
                }
            }
        }
        return false;
    }

    public function getMadhmounData(string $address): array
    {
        $results = $this->geocode($address, 'ar');
        $address_components = $results[0]['address_components'];
        $result = array(
            "ولاية" => null,
            "معتمدية" => null,
            "بلدية" => null,
            "الدائرة البلدية" => null,
            "عمادة" => null
        );

        foreach ($address_components as $component) {
            $types = $component['types'];
            if (in_array('administrative_area_level_1', $types)) {
                $result["ولاية"] = $component['long_name'];
            } elseif (in_array('administrative_area_level_2', $types)) {
                $result["معتمدية"] = $component['long_name'];
            } elseif (in_array('locality', $types)) {
                $result["بلدية"] = $component['long_name'];
            } elseif (in_array('administrative_area_level_3', $types)) {
                $result["بلدية"] = $component['long_name'];
            } elseif (in_array('sublocality', $types)) {
                $result["الدائرة البلدية"] = $component['long_name'];
            } elseif (in_array('neighborhood', $types)) {
                $result["عمادة"] = $component['long_name'];
            }
        }
        return $result;
    }
}
