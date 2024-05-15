function saveCin() {

    let formData = new FormData();
    formData.append('frontId', $('#createinputfile4').prop('files')[0]);
    formData.append('backId', $('#createinputfile5').prop('files')[0]);

    $.ajax({
        url: '/cinUpdate',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            console.log('done');
        }, error: function (response) {
            console.log("error");
        },
    });

}


function saveUserLocation() {
    let mapAddress = $('#mapAddress').val()
    let municipality = $('#municipality').val()
    let municipalityAddressNew = $('#municipalityAddressNew').val()
    let state = $('#state').val()

    if (mapAddress === '' || municipality === '') {
        alert('error')
    }

    $.ajax({
        url: '/updateAddress',
        type: "POST",
        data: {
            mapAddress: mapAddress,
            municipality: municipality,
            municipalityAddressNew: municipalityAddressNew,
            state: state,
        },
        async: true,
        success: function (response) {
            showValidPop("Address updated successfully");
        },
        error: function (response) {
            showValidPop("Address not updated successfully");
        },
    });

}


function afficherMessage() {
    $('#notification_box').html('<div class="woocommerce-message" id="notifDiv" role="alert">\n' +
        '<i class="notifIcon mt-6 pb-0 fa-solid fa-circle-check"></i>  removed.\n' +
        '<a href=""\n' +
        '   class="restore-item">Undo?</a>\n' +
        '</div>')

}

function parserMessagesErreur(reponseTexte) {
    const startIndex = reponseTexte.indexOf('{"success":false,"errors":');
    if (startIndex === -1) {
        console.error("Format de réponse d'erreur invalide.");
        return {};
    }
    const erreurJSON = reponseTexte.substring(startIndex);
    try {
        const errorObj = JSON.parse(erreurJSON);
        return errorObj.errors || {};
    } catch (error) {
        console.error("Erreur d'analyse JSON :", error);
        return {};
    }
}

function afficherMessagesErreur(erreurs) {
    if (Object.keys(erreurs).length === 0) {
        return;
    }
    removeInputs();
    for (const champ in erreurs) {
        const conteneurErreurs = document.getElementById(champ);
        const contientTexte = conteneurErreurs.textContent.trim().length > 0;
        const messageErreur = erreurs[champ];
        conteneurErreurs.classList.add('test');
        conteneurErreurs.textContent = messageErreur;
    }
}

const customAlert = {
    alertWithPromise: function (message) {
        return new Promise(function (resolve, reject) {
            if (confirm(message)) {
                resolve();
            } else {
                reject();
            }
        });
    }
};

function removeInputs() {
    const elementsSansStyle = document.querySelectorAll('.test');
    elementsSansStyle.forEach(element => {
        element.innerHTML = '';
    });
}

function editProfile(event) {
    event.preventDefault();
    let formData = new FormData();
    let name = $('#firstnamee').val();
    let lastname = $('#lastnamee').val();
    let email = $('#email').val();
    let age = $('#agee').val();
    let gender = $('#gender').val();
    let status = $('#status').val();
    let cin = $('#cinn').val();
    let phoneNumber = $('#phoneNumberr').val();
    let date = $('#date').val();
    formData.append('image', $('#createinputfile').prop('files')[0]);
    formData.append('name', name);
    formData.append('lastname', lastname);
    formData.append('email', email);
    formData.append('age', age);
    formData.append('gender', gender);
    formData.append('status', status);
    formData.append('cin', cin);
    formData.append('phoneNumber', phoneNumber);
    formData.append('date', date);
    loader_start()
    $.ajax({
        url: '/editProfile',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            loader_stop(3000)
            setTimeout(function () {
                if (response.success) {
                    showValidPop("Data updated successfully");
                    removeInputs();
                }
                if (response.redirect) {
                    console.log(response.redirect);
                    window.location.href = response.redirect;
                }

                // else if((response.errors)) {
                //     let errors = response.errors;
                //     showInvalidPop("Data not updated successfully");
                // }
            }, 3000)
        },
        error: function (response) {
            loader_stop(3000)
            setTimeout(function () {
                const messagesErreur = parserMessagesErreur(response.responseText);
                    afficherMessagesErreur(messagesErreur);
                    showInvalidPop('Data not updated successfully');
                if (messagesErreur.hasOwnProperty('other')) {
                    showInvalidPop('Invalid condtienls');
                }
            }, 3000)
        },
    });
}


function addErrorMessage(message, classeStyle, inputId) {
    const conteneurErreurs = document.getElementById(inputId);
    const elementErreur = document.createElement('div');
    conteneurErreurs.classList.add('message-container');
    elementErreur.classList.add(classeStyle);
    elementErreur.textContent = message;
    conteneurErreurs.appendChild(elementErreur);
}


function editImage() {
    let formData = new FormData();
    formData.append('imagee', $('#createinputfile').prop('files')[0]);
    $.ajax({
        url: '/editImage',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            showValidPop("image updated successfully");
        },
        error: function (response) {
            showInvalidPop("image not updated successfully");
        },
    });
}


function editPassword(event) {
    event.preventDefault();
    let formData = new FormData();
    let oldPass = $('#oldPass').val();
    let NewPass = $('#NewPass').val();
    let rePass = $('#rePass').val();
    if (oldPass === '' || NewPass === '' || rePass === '') {
        showInvalidPop("password not updated successfully");
        return
    }
    $.ajax({
        url: '/changePassword',
        type: "POST",
        data: {
            oldPass: oldPass,
            NewPass: NewPass,
            rePass: rePass
        },
        async: true,
        success: function (response) {
            showValidPop("password updated successfully");
            removeInputsChangePassword();
        },

        error: function (response) {
            showInvalidPop("password not updated successfully");
            afficherMessagesErreur(messagesErreur);
        },
    });
}




function DeleteCustomer(event) {
}


function editProfileAdmin(event) {

    event.preventDefault();
    // showLoaderAndBlockUI("test");
    let formData = new FormData();
    let name = $('#firstnamee').val();
    let lastname = $('#lastnamee').val();
    let email = $('#email').val();
    let address = $('#address').val();
    let role = $('#role').val();
    let age = $('#agee').val();
    let gender = $('#gender').val();
    let status = $('#status').val();
    let cin = $('#cinn').val();
    let phoneNumber = $('#phoneNumberr').val();
    let date = $('#dob').val();
    formData.append('image', $('#upload-settings-porfile-picture').prop('files')[0]);
    formData.append('name', name);
    formData.append('lastname', lastname);
    formData.append('email', email);
    formData.append('address', address);
    formData.append('role', role);
    formData.append('age', age);
    formData.append('gender', gender);
    formData.append('status', status);
    formData.append('cin', cin);
    formData.append('phoneNumber', phoneNumber);
    formData.append('date', date);
    $.ajax({
        url: '/editProfileAdmin',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            if (response.success) {
                let user = response.user;
                console.log(user)
                removeInputs();
                addErrorMessage(" Profile edited with sucess", 'success', 'message');
            } else {
                let errors = response.errors;
                console.log(errors);
                alert('Il y a des erreurs dans le formulaire. Veuillez corriger et réessayer.');
            }
        },
        error: function (response) {
            const messagesErreur = parserMessagesErreur(response.responseText);
            console.log(messagesErreur);
            afficherMessagesErreur(messagesErreur);
            alert('Il y a des erreurs dans le formulaire. Veuillez corriger et réessayer.');

        },

    });
}

// function cacheAlerte() {
//     setTimeout(function () {
//         var alertElement = document.getElementById('notification_box');
//         if (alertElement) {
//             alertElement.style.display = 'none';
//         }
//     }, 60000);
// }
function removeInputsChangePassword() {
    $('#email').val('');
    $('#oldPass').val('');
    $('#NewPass').val('');
    $('#rePass').val('');

}

