<?php

namespace App\Entity;

use App\Repository\ReactionPostRepository;
use Doctrine\ORM\Mapping as ORM;
use PhpParser\Node\Scalar\String_;

#[ORM\Entity(repositoryClass: ReactionPostRepository::class)]
class ReactionPost
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(name:'idReactionPost')]
    private ?int $idReactionPost;

    #[ORM\ManyToOne(targetEntity: Post::class, inversedBy: "reactions")]
    #[ORM\JoinColumn(name: "idPost", referencedColumnName: "id")]
    private $post;

    #[ORM\ManyToOne(targetEntity: User::class, inversedBy: "reactions")]
    #[ORM\JoinColumn(name: "idCompte", referencedColumnName: "idUser")]
    private $user;

    #[ORM\Column(type: "string", length: 255)]
    private ?String $type;

    public function getIdReactionPost(): ?int
    {
        return $this->idReactionPost;
    }

    public function getPost(): ?Post
    {
        return $this->post;
    }

    public function setPost(?Post $post): self
    {
        $this->post = $post;

        return $this;
    }

    public function getUser(): ?User
    {
        return $this->user;
    }

    public function setUser(?User $user): self
    {
        $this->user = $user;

        return $this;
    }

    public function getType(): ?string
    {
        return $this->type;
    }

    public function setType(string $type): self
    {
        $this->type = $type;

        return $this;
    }
}