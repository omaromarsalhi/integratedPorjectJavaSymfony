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


function showReclamationDetails(reclamationId, event) {
    event.preventDefault();
  
    // Fetch reclamation details from a backend API
    fetch(`/api/reclamations/${reclamationId}`)
      .then(response => response.json())
      .then(data => {
        // Assuming `data` is the object containing reclamation details
        const modalBody = document.querySelector('#reclamationDetailModal .modal-body');
        modalBody.innerHTML = `
          <p><strong>Subject:</strong> ${data.subject}</p>
          <p><strong>Description:</strong> ${data.description}</p>
          <p><strong>Date:</strong> ${data.date}</p>
          <p><strong>Status:</strong> ${data.status}</p>
          <!-- Include other fields as necessary -->
        `;
  
        // Show the modal
        var modal = new bootstrap.Modal(document.getElementById('reclamationDetailModal'));
        modal.show();
      })
      .catch(error => console.error('Error fetching reclamation details:', error));
  }
  
  function updateReclamation() {
    // Logic to update the reclamation details
    console.log('Update functionality needs to be implemented.');
  }
  