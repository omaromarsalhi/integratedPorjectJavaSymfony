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

    public function geocode(string $address): array
    {
        try {
            $response = $this->client->request(
                'GET',
                'https://maps.googleapis.com/maps/api/geocode/json',
                [
                    'query' => [
                        'address' => $address,
                        'key' => $this->apiKey,
                        'language' => 'fr'
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
        $results = $this->geocode($address);
        foreach ($results as $result) {
            foreach ($result['address_components'] as $component) {
                if (in_array('administrative_area_level_2', $component['types']) && $component['long_name'] === $municipality) {
                    return true;
                }
            }
        }
        return false;
    }
}
