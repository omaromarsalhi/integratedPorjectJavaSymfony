$(document).ready(function () {
    // $('#nav-cin-tab').prop('disabled',true)
    // $('#nav-contact-tab').prop('disabled',true)
    // countBasket()
    new Splide('#image-slider').mount();
    launchSwiper()

});



    const socket = new WebSocket("ws://192.168.152.239:8091?userId=" + currentUser + "&app=symfony");
// const socket = new WebSocket("ws://192.168.1.7:8090?userId=" + currentUser);
    socket.addEventListener("open", function () {
        console.log("CONNECTED");
    });

    socket.addEventListener('message', (event) => {
        const messageData = JSON.parse(event.data);
        console.log(messageData)
        if (messageData.action === 'chat') {
            receiveMsg(messageData)
        } else if (messageData.action === 'productEvent') {
            if (messageData.subAction === 'ADD')
                filterByPrice();
            else if (messageData.subAction === 'UPDATE') {
                updateProductOfOtherUser(messageData.Data.idProduct)
            } else if (messageData.subAction === 'DELETE') {
                filterByPrice();
            }
        } else if (messageData.action === 'postEvent') {
            if (messageData.subAction === 'ADD')
                showInRealTimePost(messageData.Data.idPost);
        } else if (messageData.action === 'accountDeletion') {
            if (currentUser === messageData.recipientId) {
                $('#btnPopUp').attr('href', '/logout')
                $('#btnPopUp').removeAttr('data-bs-dismiss')
                $('#error-message').html(messageData.message);
                $('#statusErrorsModal').modal('show')
            }
        }
    });


function updateProductOfOtherUser(idProduct) {
    if (document.getElementById('container_product_' + idProduct)) {
        $('#container_product_' + idProduct).addClass('blur')
        $.ajax({
            url: '/market/place/renderSingleProduct',
            type: "POST",
            data: {
                idProduct: idProduct,
                index: 100
            },
            async: true,
            success: function (response) {
                $('#container_product_' + idProduct).removeClass('blur')
                $('#container_product_' + idProduct).html(response)
                launchSwiper()

                let out = false
                let count = 1
                while (!out) {
                    let product = document.getElementById("product_" + count)
                    if (!product) {
                        let product2Update = document.getElementById("product_100")
                        product2Update.setAttribute('id', 'product_' + count)
                        product2Update.setAttribute('onclick', 'add2Card(' + idProduct + ',' + count + ')')
                        out = true;
                        console.log('count ' + count)
                    }
                    if (count > 12)
                        out = true;
                    count++
                }
            },
            error: function (response) {
                alert(response)
            },
        })
        launchSwiper()
    }

}


function countBasket() {
    $.ajax({
        url: '/basket/count',
        type: "POST",
        async: true,
        success: function (response) {
            $('#itmesNumber').html(response)
        },
        error: function (response) {
            alert(response)
        },
    })
}


function showInvalidPop(msg) {
    $('#error-message').html(msg);
    $('#statusErrorsModal').modal('show')
}

function hideInvalidPop() {
    $('#statusErrorsModal').modal('hide')
}

function showValidPop(msg) {
    $('#success-message').html(msg);
    $('#statusSuccessModal').modal('show')
}


function hideValidPop() {
    $('#statusSuccessModal').modal('hide')
}

function webNotif(message){
    $('#notification_box').html('<div class="woocommerce-message notifDiv" id="notifDiv" role="alert">\n' +
        '<i class="notifIcon mt-6 pb-0 fa-solid fa-circle-check"></i>  “ '+message+' ”\n' +
        '</div>')
    $('#notifDiv').on('click', function () {
        $('#notifDiv').remove()
    });
}
