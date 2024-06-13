// OMAR SALHI  IS THE OWNER OF THIS PIECE OF USELESS CODE


$(document).ready(function () {
    $('input[type="checkbox"]').click(function () {
        let currentState = this.checked
        let keyValue = this.id.split('_')
        Object.keys(filterBy[keyValue[0]]).forEach(function (key) {
                let id = keyValue[0] + '_' + key
                console.log(id)
                $('#' + id).prop('checked', false)
                filterBy[keyValue[0]][key] = false
            }
        )

        $(this).prop('checked', currentState)
        filterBy[keyValue[0]][keyValue[1]] = currentState
        console.log(filterBy);
        filterByPrice()
    })
});


function startAiImageSearch(file) {
    $('#rbtinputForSearch').prop('src', '/marketPlaceImages/t9gQ2ptYYn.gif')

    let formData = new FormData();
    formData.append('image', $('#nipaForSearch').prop('files')[0]);

    $('#nipaForSearch').prop('disabled', true)
    $('#iconForNipa').html('<i class="fa-solid fa-xmark"></i>')
    $('#nipaForSearch').attr('type', 'text');

    $('#nipaForSearch').on('click', function () {
            $('#rbtinputForSearch').prop('src', '/assets/images/portfolio/portfolio-055.png')
            $('#iconForNipa').html('<i class="feather-edit">')
            $('#nipaForSearch').val(null)
            DisplayListProducts(-100)
            setTimeout(function () {
                imageSearchState = false
                $('#nipaForSearch').off('click')
                $('#nipaForSearch').attr('type', 'file');
                $('#iconForNipa').attr('data-value', 'edit');
            }, 1000)
        }
    )


    if ($('#iconForNipa').data('value') === 'edit')
        $.ajax({
            url: "/market/place/searChByImage",
            type: "POST",
            data: formData,
            async: true,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log(response.idlist)

                $("#sub-market-block").html(response.subMarket);
                $("#navPages").html(response.nav);

                setTimeout(function () {
                    showProducts();
                    launchSwiper();
                }, 1000);

                var page_index_to_enable = $('#currentPage').val();
                var page_index_to_disable = $('#previousPage').val();

                if (document.getElementById("page-" + page_index_to_enable) && document.getElementById("page-" + page_index_to_disable)) {
                    document.getElementById("page-" + page_index_to_enable).classList.add("active")
                    document.getElementById("page-" + page_index_to_disable).classList.remove("active")
                }
                // setTimeout(function (){
                $('#rbtinputForSearch').prop('src', URL.createObjectURL(file))
                $('#nipaForSearch').prop('disabled', false)
                $('#iconForNipa').attr('data-value', 'close');
                // },4000)
            },
        });
}


function filterByPrice() {
    jQuery('html, body').animate({scrollTop: 10}, 550);
    $("#sub-market-block").html('<div style="max-width: 150px !important; max-height: 150px !important;margin-top: 60px;"><img src="../../marketPlaceImages/basketAnimation.gif" alt="nft-logo"></div>');
    $("#navPages").html('');
    $.ajax({
        url: '/market/place/filtered',
        type: "post",
        data: {
            filterBy: filterBy,
        },
        async: true,
        success: function (response) {
            console.log(response.idlist);
            $("#sub-market-block").html(response.subMarket);
            $("#navPages").html(response.nav);

            setTimeout(function () {
                showProducts();
                launchSwiper();
            }, 1000);

            var page_index_to_enable = $('#currentPage').val();
            var page_index_to_disable = $('#previousPage').val();

            if (document.getElementById("page-" + page_index_to_enable)) {
                document.getElementById("page-" + page_index_to_enable).classList.add("active")
                document.getElementById("page-" + page_index_to_disable).classList.remove("active")
            }
        },
        error: function (response) {
        },
    });
}


function DisplayListProducts(movement_direction) {
    $("#sub-market-block").html('<div style="max-width: 150px !important; max-height: 150px !important;margin-top: 60px;"><img src="../../marketPlaceImages/basketAnimation.gif" alt="nft-logo"></div>');
    $("#navPages").html('');
    $.ajax({
        url: '/market/place/',
        type: "post",
        data: {
            movement_direction: movement_direction,
        },
        async: true,
        success: function (response) {

            $("#sub-market-block").html(response.subMarket);
            $("#navPages").html(response.nav);
            $("#formsPages").html(response.forms);

            setTimeout(function () {
                showProducts();
                launchSwiper();
            }, 1000);

            jQuery('html, body').animate({scrollTop: 10}, 550);

            var page_index_to_enable = $('#currentPage').val();
            var page_index_to_disable = $('#previousPage').val();
            if (document.getElementById("page-" + page_index_to_enable)) {
                document.getElementById("page-" + page_index_to_enable).classList.add("active")
                document.getElementById("page-" + page_index_to_disable).classList.remove("active")
            }
        },
        error: function (response) {
            console.log(response);
        },
    });
}

function showProducts() {
    var children = document.getElementById("sub-market-block").children;
    for (var i = 0; i < children.length; i++) {
        var child = children[i];
        child.classList.add("sal-animate");
    }
}

// function executeTasks() {
//     var currentTask = tasks.shift(); // Get the first function
//
//     if (currentTask) {
//         currentTask(); // Execute the current task
//
//         setTimeout(function() {
//             executeTasks(); // Call itself recursively after delay
//         }, 2000); // Delay in milliseconds (adjust as needed)
//     }
// }

function deletUser(id) {
    $.ajax({
        url: "/user/delete",
        type: "POST",
        data: {
            id: id,
        },
        async: true,
        success: function (response) {
            if (response == "success") {
                DisplayTableMenu();
            } else {
                alert("something went wrong");
            }
        },
    });
}

// function createUser() {
//   $.ajax({
//     url: "/user/create",
//     type: "POST",
//     data: {
//       id: id,
//     },
//     success: function (response) {
//       if (response == "success") {
//         DisplayTableMenu();
//       } else {
//         alert("something went wrong");
//       }
//     },
//   });
// }
