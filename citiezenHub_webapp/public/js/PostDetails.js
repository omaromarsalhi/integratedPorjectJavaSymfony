function addComment(event) {
    event.preventDefault();
    let caption = document.querySelector('#contact-message').value;
    let postId = document.querySelector('#post_id').value;


    let formData = new FormData();
    formData.append('caption', caption);
    formData.append('post_id', postId);


    $.ajax({
        url: '/newComment',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            // Gérer la réponse du serveur
            if (response.success) {
                if (response.success) {
                    var newCommentHTML = createCommentHTML(response.comment, response.comment.userId);
                    $('#commentContainer').prepend(newCommentHTML);
                    document.querySelector('#contact-message').value = '';

                    let commentsCountElement = document.querySelector('.comments span');
                    let currentCount = parseInt(commentsCountElement.textContent);
                    commentsCountElement.textContent = (currentCount + 1) + ' Comments';
                }
            } else {
                console.error('Une erreur est survenue lors de l\'ajout du commentaire.');
            }
        },
        error: function () {
            console.error('Une erreur est survenue lors de l\'envoi de la requête AJAX.');
        }
    });
}

function createCommentHTML(comment, idU) {

    var dropDown = '';
    if (idU === idUser) {
        console.log('idU : ' + idU + ' idUser : ' + idUser);
        dropDown = `
            <div class="dropdown">
                                                <button class="dropbtnComment"><i class="fa-solid fa-ellipsis"></i>
                                                </button>
                                                <div class="dropdown-content">
                                                    <button onclick="handleMenuAction(this, ${comment.id}, '${comment.caption}', '${comment.idPost}', 'modifier')">Modifier</button>
                                                    <button onclick="handleMenuAction(this, ${comment.id}, '${comment.caption}', '${comment.idPost}', 'supprimer')">Supprimer</button>
                                                </div>
                                            </div>
        `;
    }

    return `
        <div class="forum-single-ans" id="comment-${comment.id}">
                                    <div class="ans-header d-flex justify-content-between align-items-center">
                                        <div class="d-flex align-items-center">
                                            <a><img
                                                        src="../usersImg/${comment.userImage}"
                                                        alt="Nft-Profile"></a>

                                            <p class="name">${comment.userSurname} ${comment.userName}</p>
                                            <div class="date">
                                                <i class="feather-watch"></i>
                                                <span>${comment.dateComment}</span>
                                            </div>

                                        </div>
                                        ${dropDown}
                                    </div>
                                    <div class="ans-content">
                                        <input id="caption-${comment.id}" type="text" value="${comment.caption}" disabled>
                                       
                                        <div id="updateBtn-${comment.id}"></div>
                                        
                                        <hr class="form-ans-separator">
                                    </div>
                                </div>
    `;
}

function handleMenuAction(button, id, caption, postId, action) {
    var captionElement = document.getElementById('caption-' + id);
    if (action === "modifier") {
        captionElement.disabled = false;
        captionElement.style.border = "1px solid #fff";
        captionElement.style.borderRadius = "5px";
        captionElement.style.cursor = "text";
        captionElement.style.whiteSpace = "pre-wrap";

        var updateBtn = createUpdateBtn(id);
        document.getElementById('updateBtn-' + id).appendChild(updateBtn);

        updateBtn.addEventListener('click', function () {
            $.ajax({
                url: '/updateComment/' + id,
                type: 'POST',
                data: {
                    caption: captionElement.value,
                    post_id: postId
                },
                success: function (response) {
                    document.getElementById('comment-' + id).remove();
                    var newCommentHTML = createCommentHTML(response.comment, response.comment.userId);
                    $('#commentContainer').prepend(newCommentHTML);
                },
                error: function (xhr, status, error) {
                    console.error(error);
                }
            });
        });
    } else if (action === "supprimer") {
        deleteComment(id);
    }
}

function createUpdateBtn(id) {

    var updateBtn = document.createElement('div');
    updateBtn.className = 'reply-edit';


    var replyDiv = document.createElement('div');
    replyDiv.className = 'reply';
    var link = document.createElement('a');
    link.className = 'comment-reply-link';
    link.style.cursor = 'pointer';
    var icon = document.createElement('i');
    icon.className = 'rbt feather-corner-down-right';
    link.appendChild(icon);
    link.appendChild(document.createTextNode('Update'));
    replyDiv.appendChild(link);


    updateBtn.appendChild(replyDiv);

    return updateBtn;
}

function deleteComment(id) {

    $.ajax({
        url: '/deleteComment/' + id,
        type: 'DELETE',
        success: function (response) {
            if (response.success) {
                document.getElementById('comment-' + id).remove();
                let commentsCountElement = document.querySelector('.comments span');
                let currentCount = parseInt(commentsCountElement.textContent);
                commentsCountElement.textContent = (currentCount === 0 ? 0 : currentCount - 1) + ' Comments';
                Swal.fire(
                    'Supprimé!',
                    'Votre commentaire a été supprimé.',
                    'success'
                )
            } else {
                console.error('Une erreur est survenue lors de la suppression du commentaire.');
            }
        },
        error: function (xhr, status, error) {
            console.error(error);
        }
    });
}