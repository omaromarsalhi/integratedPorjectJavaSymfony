<?php

namespace App\MyHelpers;

class UserVerifierMessage
{
    private  $obj;

    public function __construct($obj)
    {
        $this->obj = $obj;
    }

    public function getObj()
    {
        return $this->obj;
    }

    public function getId()
    {
        return $this->obj->getId(); // Assuming $obj has a getId method
    }

}