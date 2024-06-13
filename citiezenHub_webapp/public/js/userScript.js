


function getCinTimeInfo() {
    $.ajax({
        url: '/cinTimeInfo',
        type: "POST",
        async: true,
        success: function (response) {
            if (response.state === 'verified') {
                $('#cinInfoTime').html("you are verified put another image if you have updated your location or id card")
                $('#cinInfoModel').modal('show')
            } else {
                $('#cinInfoTime').html("you need to add your cin images ( front and back ) so that we can verify you info" +
                    " otherwise your account will be deleted within: <p style='color: red'>" + response.hours + "H and " + response.minutes + "m</p>")
                $('#cinInfoModel').modal('show')
            }
        }, error: function (response) {
            console.log("error");
        },
    });

}


function saveCin() {

    let formData = new FormData();
    formData.append('frontId', $('#createinputfile4').prop('files')[0]);
    formData.append('backId', $('#createinputfile5').prop('files')[0]);
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#cinLoading').html('<img src="' + value + '" width="30"/>')
    $('#cinLoading').addClass('btnTransparent');
    $('#cinLoading').prop('disabled', true)
    $.ajax({
        url: '/cinUpdate',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            console.log(response)
            $('#cinLoading').html('Save')
            $('#cinLoading').removeClass('btnTransparent');
            $('#cinLoading').prop('disabled', false)
            let splittedResponse = response.split('_')
            if (splittedResponse[0] === 'error' || splittedResponse[0] === 'false') {
                if (splittedResponse[1] === 'dob')
                    afficherMessage('the date of birth does not match with the id card')
                else if (splittedResponse[1] === 'cin')
                    afficherMessage('the cin id does not match with the id card')
                else if (splittedResponse[1] === 'location')
                    afficherMessage('the given location does not match with the id card location')

                showInvalidPop('please do input the right data')
            } else
                showValidPop('now your are verified')
        }, error: function (response) {
            console.log("error");
        },
    });

}


function saveUserLocation() {
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#locationBtn').html('<img src="' + value + '" width="30"/>')
    $('#locationBtn').addClass('btnTransparent');
    $('#locationBtn').prop('disabled', true)

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
            $('#locationBtn').html('Save')
            $('#locationBtn').removeClass('btnTransparent');
            $('#locationBtn').prop('disabled', false)
            let splittedResponse = response.split('_')
            if (splittedResponse[0] === 'error' || splittedResponse[0] === 'false') {
                if (splittedResponse[1] === 'dob')
                    afficherMessage('the date of birth does not match with the id card')
                else if (splittedResponse[1] === 'cin')
                    afficherMessage('the cin id does not match with the id card')
                else
                    afficherMessage('the given location does not match with the id card location')

                showInvalidPop('please do input the right data')
            } else
                showValidPop("Address updated successfully")
        },
        error: function (response) {
            $('#locationBtn').html('Save')
            $('#locationBtn').removeClass('btnTransparent');
            $('#locationBtn').prop('disabled', false)
            showValidPop("Address not updated successfully");
        },
    });

}


function afficherMessage(msg) {
    $('#notification_box').html('<div class="woocommerce-message" id="notifDiv" role="alert">\n' +
        '<i class="notifIcon mt-6 pb-0 fa-solid fa-circle-check"></i> '+ msg+ '<a href=""\n' +
        '</div>')
    $('#notifDiv').on('click', function () {
        $('#notifDiv').remove()
    });
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
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#submit_button').html('<img src="' + value + '" width="28" style="margin-left: 6px !important;"/>')
    $('#submit_button').addClass('btnTransparent');
    $('#submit_button').prop('disabled', true)
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
    $.ajax({
        url: '/editProfile',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            $('#submit_button').html('Save')
            $('#submit_button').removeClass('btnTransparent');
            $('#submit_button').prop('disabled', false)
            let splittedResponse = response.split('_')
            if (splittedResponse[0] === 'error' || splittedResponse[0] === 'false') {
                if (splittedResponse[1] === 'dob')
                    afficherMessage('the date of birth does not match with the id card')
                else if (splittedResponse[1] === 'cin')
                    afficherMessage('the cin id does not match with the id card')
                else
                    afficherMessage('the given location does not match with the id card location')

                showInvalidPop('please do input the right data')
            } else
                showValidPop("updated updated successfully")
        },
        error: function (response) {
            $('#submit_button').html('Save')
            $('#submit_button').removeClass('btnTransparent');
            $('#submit_button').prop('disabled', false)
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
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#editImg_button').html('<img src="' + value + '" width="28" style="margin-left: 6px !important;"/>')
    $('#editImg_button').addClass('btnTransparent');
    $('#editImg_button').prop('disabled', true)
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
            $('#userImageIndex').prop('src','/usersImg/'+response.image);
            showValidPop("image updated successfully");
            $('#editImg_button').html('Save')
            $('#editImg_button').removeClass('btnTransparent');
            $('#editImg_button').prop('disabled', false)
        },
        error: function (response) {
            showInvalidPop("image not updated successfully");
            $('#editImg_button').html('Save')
            $('#editImg_button').removeClass('btnTransparent');
            $('#editImg_button').prop('disabled', false)
        },
    });
}


function editPassword(event) {
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#editPwd_button').html('<img src="' + value + '" width="28" style="margin-left: 6px !important;"/>')
    $('#editPwd_button').addClass('btnTransparent');
    $('#editPwd_button').prop('disabled', true)
    event.preventDefault();
    let formData = new FormData();
    let oldPass = $('#oldPass').val();
    let NewPass = $('#NewPass').val();
    let rePass = $('#rePass').val();
    if (oldPass === '' || NewPass === '' || rePass === '') {
        showInvalidPop("password not updated successfully");
        $('#editPwd_button').html('Save')
        $('#editPwd_button').removeClass('btnTransparent');
        $('#editPwd_button').prop('disabled', false)
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
            $('#editPwd_button').html('Save')
            $('#editPwd_button').removeClass('btnTransparent');
            $('#editPwd_button').prop('disabled', false)
            showValidPop("password updated successfully");
            removeInputsChangePassword();
        },

        error: function (response) {
            $('#editPwd_button').html('Save')
            $('#editPwd_button').removeClass('btnTransparent');
            $('#editPwd_button').prop('disabled', false)
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

