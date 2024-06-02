<?php

// src/Command/WebsocketServerCommand.php
namespace App\Command;

use App\Websocket\MessageHandler;
use Ratchet\Server\IoServer;
use Ratchet\Http\HttpServer;
use Ratchet\WebSocket\WsServer;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\DependencyInjection\ContainerInterface;

class WebsocketServerCommand extends Command
{
    protected static $defaultName = 'app:websocket-server';

    private $messageHandler;

    public function __construct(MessageHandler $messageHandler)
    {
        $this->messageHandler = $messageHandler;
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): void
    {
        $server = IoServer::factory(
            new HttpServer(
                new WsServer($this->messageHandler) // Use injected MessageHandler
            ),
            8091 // Port number
        );

        $server->run();
    }
}
