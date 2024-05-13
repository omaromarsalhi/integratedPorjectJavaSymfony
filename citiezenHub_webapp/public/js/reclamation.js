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


// Define the function globally
function showReclamationDetails(reclamationId, event) {
    event.preventDefault();
  
    fetch(`/api/reclamations/${reclamationId}`)
      .then(response => response.json())
      .then(data => {
        const modalBody = document.querySelector('#reclamationDetailModal .modal-body');
        const basePath = document.querySelector('#basePath').dataset.basePath;
  
        // Check if modalBody exists before accessing it
        if (modalBody) {
          let detailsHtml = `
            <div>
              <label>Private Key: ${data.privateKey}</label>
            </div>
            <div>
              <label>Subject:</label>
              <input type="text" id="modalSubject" value="${data.subject}" />
            </div>
            <div>
              <label>Description:</label>
              <textarea id="modalDescription">${data.description}</textarea>
            </div>`;
          if (data.image) {
            detailsHtml += `<div><label>Image:</label><img src="${basePath}${data.image}" alt="Reclamation Image" style="max-width:100px;"></div>`;
          }
          modalBody.innerHTML = detailsHtml;
        } else {
          console.error("Modal body element not found!");
        }
  
        document.getElementById('updateButton').setAttribute('data-reclamation-id', reclamationId);
        var modal = new bootstrap.Modal(document.getElementById('reclamationDetailModal'));
        modal.show();
      })
      .catch(error => console.error('Error fetching reclamation details:', error));
  }
  

document.addEventListener('DOMContentLoaded', function() {
    document.body.addEventListener('click', function(event) {
        if (event.target.matches('[data-reclamation-detail]')) {
            showReclamationDetails(event.target.getAttribute('data-reclamation-id'), event);
        } else if (event.target.matches('#deleteReclamation')) {
            deleteReclamation(event.target.getAttribute('data-reclamation-id'), event);
        }
    });
});
document.getElementById('updateButton').addEventListener('click', function() {
    const reclamationId = this.getAttribute('data-reclamation-id');
    const subject = document.getElementById('modalSubject').value;
    const description = document.getElementById('modalDescription').value;

    const updateData = {
        subject: subject,
        description: description
    };

    fetch(`/api/reclamations/update/${reclamationId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateData)
    })
    .then(response => response.json())
    .then(data => {
        alert('Reclamation updated successfully');

        // Dynamically update the elements on the page
        document.querySelector(`#reclamationItem${reclamationId} .title`).textContent = subject;
        document.querySelector(`#reclamationItem${reclamationId} .latest-bid`).textContent = description;

        // Close the modal
        var modal = bootstrap.Modal.getInstance(document.getElementById('reclamationDetailModal'));
        modal.hide();
    })
    .catch(error => {
        console.error('Error updating reclamation:', error);
        alert('Failed to update reclamation.');
    });
});

