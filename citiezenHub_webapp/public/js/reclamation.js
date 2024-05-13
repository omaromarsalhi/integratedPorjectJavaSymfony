function addReclamation() {
    let subject = $('#subject').val().toLowerCase();
    let message = $('#contact-message').val().toLowerCase();
    let privatekey = $('#private-key').val();

    if($('#condition').prop('checked') === false || subject === '' || message === ''){
        showInvalidPop('You have to agree to the terms and conditions');
        return;
    }


    let form_data = new FormData();
    const image = $('#reclamation_imageFile_file').prop('files')[0];
    console.log(image);
    form_data.append('image', image);
    form_data.append('subject', subject);
    form_data.append('message', message);
    form_data.append('privatekey', privatekey);


    const badWords = ['bad', 'khalil', 'rmila', '3A4'];
    let foundBadWordInSubject = null;
    let foundBadWordInMessage = null;

    // Check each bad word to find a match and store which word was found
    badWords.forEach(badWord => {
        if (subject.includes(badWord) && !foundBadWordInSubject) {
            foundBadWordInSubject = badWord;
        }
        if (message.includes(badWord) && !foundBadWordInMessage) {
            foundBadWordInMessage = badWord;
        }
    });

    if (foundBadWordInSubject || foundBadWordInMessage) {
        let alertMessage = "Please avoid using inappropriate language. Found: ";
        if (foundBadWordInSubject) {
            alertMessage += `"${foundBadWordInSubject[0]}${'*'.repeat(foundBadWordInSubject.length - 1)}" in Subject`;
        }
        if (foundBadWordInMessage) {
            if (foundBadWordInSubject) {
                alertMessage += " and ";
            }
            alertMessage += `"${foundBadWordInMessage[0]}${'*'.repeat(foundBadWordInMessage.length - 1)}" in Message`;
        }
        showInvalidPop(alertMessage);
        return;
    }

    $.ajax({
        url: "/reclamation/new",
        type: "POST",
        data: form_data,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            $('#subject').val('');
            $('#contact-message').val('');
            $('#condition').prop('checked', false);
            showValidPop('Reclamation made successfully');
        }
    });
}


function deletee(reclamationId, event) {
    event.preventDefault(); // Stop the link behavior

    $.ajax({
        url: "/reclamation/delete/" + reclamationId,
        type: "POST",
        success: function(response) {
            if (response.success) {
                // Remove the item from the DOM by ID
                $('#reclamationItem' + reclamationId).remove();
            } else {
                alert('Failed to delete reclamation. Server returned an error.');
            }
        },
        error: function() {
            alert('Failed to delete reclamation. Check your network and try again.');
        }
    });
}
//spech to text
document.addEventListener('DOMContentLoaded', function() {
    function startRecording() {
        const recognition = new window.webkitSpeechRecognition();
        recognition.lang = 'en-US';

        recognition.onresult = function(event) {
            const transcript = event.results[0][0].transcript;
            document.getElementById('contact-message').value = transcript;
        }

        recognition.start();
    }

    document.getElementById('startRecording').addEventListener('click', startRecording);
});



document.addEventListener('DOMContentLoaded', function() {
    // Using event delegation to manage events for dynamically added content
    document.body.addEventListener('click', function(event) {
        if (event.target.matches('.title')) {
            showReclamationDetails(event.target.closest('.lg-product-wrapper').id.replace('reclamationItem', ''), event);
        } else if (event.target.closest('.pdfBtn')) {
            deleteReclamation(event.target.closest('.lg-product-wrapper').id.replace('reclamationItem', ''), event);
        }
    });
});

function showReclamationDetails(reclamationId, event) {
    event.preventDefault();
    fetch(`/api/reclamations/${reclamationId}`)
        .then(response => response.json())
        .then(data => {
            updateModalContent(data);
            new bootstrap.Modal(document.getElementById('reclamationDetailModal')).show();
        })
        .catch(error => console.error('Error fetching reclamation details:', error));
}

function updateModalContent(data) {
    const modalBody = document.querySelector('#reclamationDetailModal .modal-body');
    modalBody.setAttribute('data-reclamation-id', data.id); // Set the reclamation ID on the modal body for reference in the update function

    let detailsHtml = `
        <div><label>Private Key: ${data.privateKey}</label></div>
        <div><label>Subject:</label><input type="text" id="modalSubject" value="${data.subject}"></div>
        <div><label>Description:</label><textarea id="modalDescription">${data.description}</textarea></div>
        ${data.image ? `<div><label>Image:</label><img src="${data.image}" alt="Reclamation Image" style="max-width:100px;"></div>` : ''}
    `;
    modalBody.innerHTML = detailsHtml;
}


function updateReclamationDetails() {
    const modal = document.getElementById('reclamationDetailModal');
    const reclamationId = modal.querySelector('.modal-body').getAttribute('data-reclamation-id'); // Ensure this attribute is set correctly when loading the modal
    const subject = document.getElementById('modalSubject').value;
    const description = document.getElementById('modalDescription').value;

    fetch(`/api/reclamations/update/${reclamationId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ subject, description })
    })
    .then(response => response.json())
    .then(() => {
        alert('Reclamation updated successfully');
        document.querySelector(`#reclamationItem${reclamationId} .title`).textContent = subject;
        document.querySelector(`#reclamationItem${reclamationId} .latest-bid`).textContent = description;
        new bootstrap.Modal(document.getElementById('reclamationDetailModal')).hide();
    })
    .catch(error => {
        console.error('Error updating reclamation:', error);
        alert('Failed to update reclamation.');
    });
}
