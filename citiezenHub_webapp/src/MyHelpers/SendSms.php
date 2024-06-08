<?php

namespace App\MyHelpers;


use Symfony\Bundle\MakerBundle\Str;
use Twilio\Exceptions\ConfigurationException;
use Twilio\Rest\Client;

class SendSms
{
    public static function send($message): void
    {
        $accountSid = "AC5923489529865676cd1fbb7be39d18c5";
        $authToken = "c411c6de2588d7110006820c384409d1";

        try {
            $twilio = new Client($accountSid, $authToken);
            $message = $twilio->messages->create(
                '+21629624921', // To phone number
                [
                    'from' => '+14705706292', // From phone number (your Twilio number)
                    'body' => $message,
                ]
            );
        } catch (ConfigurationException $e) {
        }
    }

}