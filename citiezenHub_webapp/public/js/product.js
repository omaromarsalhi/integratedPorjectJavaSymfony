// OMAR SALHI  IS THE OWNER OF THIS PIECE OF USELESS CODE
// $(document).ready(function () {
// });


$(document).ready(function () {

    // const eventSource = new EventSource('/sse/product');
    //
    // eventSource.onmessage = (event) => {
    //     console.log('Received message:', event.data);
    // };
    //
    // eventSource.onerror = (error) => {
    //     console.error('SSE Error:', error);
    //     eventSource.close()
    // };


    regex();
    changeImageUpdate()
});

















function changeImageUpdate() {
    $('input[type="checkbox"]').click(function () {
        $('#deleteImage').prop('checked', false);
        $('#uploadImage').prop('checked', false);
        $(this).prop('checked', true);

        for (let i = 0; i < 10; i++) {
            var myElement = document.getElementById("updateImageLabel_" + i);
            if (myElement) {
                if ($(this).prop('id') === 'uploadImage') {
                    $('.updateImageLabel_' + i).attr('for', 'createinputfile')
                    $('.updateImageLabel_' + i).html('<i class="feather-upload"></i>\n' +
                        '<span class="text-center">Choose a File</span>\n' +
                        '<p class="text-center mt&#45;&#45;10">PNG, GIF, WEBP, MP4 or\n' +
                        'MP3. <br> Max\n' +
                        '1Gb.</p>')
                } else {
                    $('.updateImageLabel_' + i).attr('for', '')
                    $('.updateImageLabel_' + i).html('<button type="button"\n' +
                        'name="deleteBtns"\n' +
                        'style="border: none !important;"\n' +
                        'data-bs-toggle="modal"\n' +
                        'data-bs-target="#confirmModel"\n' +
                        'data-value="{{ index }}">\n' +
                        '<i class="fa-regular fa-trash-can"></i>\n' +
                        '</button>\n' +
                        '<span class="text-center">Delete a File</span>')
                }
            }
        }
    })

    $('Button[name="deleteBtns"]').click(function () {
        $('#modelValue').val($(this).data("value"))
        $('#confirmModel').modal("show")
    })
}

function deleteImage() {
    if ($('.splide__list')[0].children.length > 1) {
        let imageIndex = $('#modelValue').val();
        $('.slider_' + imageIndex).remove()
        $('#confirmModel').modal("hide")
        $('.splide__pagination').remove()
        new Splide('#image-slider').mount();
    } else {
        $('#confirmModel').modal("hide")
        let errors = [];
        $('#error-message-image').html('')

        if ($('#createinputfile').prop('files').length === 0)
            errors.push({text: "image", el: $('#error-message-image')});

        if (errors.length > 0) {
            handle_errors(errors);
        }
    }
}


function updateProduct(id) {
    // if(check_all_inputs()){
    loader_start()
    let name = $('#name').val();
    let description = $('#description').val();
    let price = $('#price').val();
    let quantity = $('#quantity').val();
    let category = $('#category').val();

    let form_data = new FormData();
    const list = $('#createinputfile').prop('files');

    for (let i = 0; i < list.length; i++) {
        form_data.append('file-' + (i + 1), list[i]);
    }
    let str = ''
    let state = document.getElementById('singLeImageUploaded')
    if (state) {
        str=$("#singLeImageUploaded" ).data('value')
    } else {
        for (let i = 1; i <= $('#nbrImages').val(); i++) {
            var myElement = document.getElementById("image-slider-slide0" + i);
            if (myElement) {
                str += $("#image-slider-slide0" + i).data('value');
                str += '_';
            }
        }
        str = str.replace(/_$/, '')
    }

    form_data.append('name', name);
    form_data.append('description', description);
    form_data.append('price', price);
    form_data.append('quantity', quantity);
    form_data.append('category', category);
    form_data.append('idProduct', id);
    form_data.append('savedImages', str);



    $.ajax({
        url: '/product/1/edit',
        type: "POST",
        data: form_data,
        async: true,
        processData: false,
        contentType: false,
        success: function (response) {
            loader_stop(3000)
        },
        error: function (xhr) {
            loader_stop(1000)
            const errorMessage = xhr.responseJSON.error;
            check_all_inputs_with_php(errorMessage)
        },
    });
}


function deleteProduct(id, index, type) {
    console.log(id)
    $.ajax({
        url: '/product/delete',
        type: "POST",
        data: {
            id: id,
            type: type
        },
        async: true,
        success: function (response) {

            DisplayListProducts4Owner(response.page, type);

            $('#notification_box').html('<div class="woocommerce-message notifDiv" id="notifDiv" role="alert">\n' +
                '<i class="notifIcon mt-6 pb-0 fa-solid fa-circle-check"></i>  “ Product has been Deleted ”\n' +
                '<a href=""\n' +
                '   class="restore-item">Undo?</a>\n' +
                '</div>')

            $('#notifDiv').on('click', function () {
                $('#notifDiv').remove()
            });
        }
    });
}



function createProduct(e) {
    if (check_all_inputs()) {
        loader_start()
        let name = $('#name').val();
        let description = $('#description').val();
        let price = $('#price').val();
        let quantity = $('#quantity').val();
        let category = $('#category').val();

        let form_data = new FormData();
        const list = $('#createinputfile').prop('files');

        for (let i = 0; i < list.length; i++) {
            form_data.append('file-' + (i + 1), list[i]);
        }
        form_data.append('name', name);
        form_data.append('description', description);
        form_data.append('price', price);
        form_data.append('quantity', quantity);
        form_data.append('category', category);

        $.ajax({
            url: '/product/new',
            type: "POST",
            data: form_data,
            async: true,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log(response.desc)
                loader_stop(4000)
                setTimeout(function () {
                    handle_success('the product has been added successfully')
                    $('#createinputfile').val(null)
                    $('#createfileImage').attr('src', '/assets/images/portfolio/portfolio-05.jpg')
                    $('#name').val('');
                    $('#description').val('');
                    $('#price').val('');
                    $('#quantity').val('');
                    $('#category').val('');
                }, 4100)
            },
            error: function (xhr) {
                loader_stop(1000)
                const errorMessage = xhr.responseJSON.error;
                check_all_inputs_with_php(errorMessage)
            },
        });
    }
}

function consultProd() {

    let name = $('#name').val();
    let price = $('#price').val();
    let quantity = $('#quantity').val();
    let category = $('#category').val();

    $("#name_model").html(name.charAt(0).toUpperCase() + name.slice(1).toLowerCase())
    $("#price_model").html(price + ' DT')
    $("#quantity_model").html(quantity + ' Pieces/ ')
    $("#category_model").html(category.charAt(0).toUpperCase() + category.slice(1).toLowerCase())

    const files = $('#createinputfile').prop('files');
    let html = '<div class="swiper-wrapper" >';
    for (let i = 0; i < files.length; i++) {
        console.log(i)
        html += '<div class="swiper-slide">\n' +
            '<img src="' + URL.createObjectURL(files[i]) + '"' +
            ' alt="" class="fixedImagesSize"/>\n' +
            '</div>'
    }
    html += '<div class="swiper-pagination"></div></div>'
    $("#slider").html(html);
    $("#uploadModal").modal("show");

    launchSwiper()
}

function check_all_inputs() {
    let errors = [];
    $('#error-message-image').html('')

    if (!$('#name').val().match(/^[a-zA-Z][a-zA-Z0-9\s]*$/))
        errors.push({text: "name", el: $('#error-message')});
    if (!$('#price').val().match(/^[1-9]\d{0,10}(,\d{3})*(\.\d{1,2})?$/))
        errors.push({text: "price", el: $('#error-message-price')});
    if (!$('#quantity').val().match(/^[1-9]\d{0,10}(,\d{3})*(\.\d{1,2})?$/))
        errors.push({text: "quantity", el: $('#error-message-quantity')});
    if ($('#description').val().trim() === '')
        errors.push({text: "description", el: $('#error-message-desc')});
    if ($('#createinputfile').prop('files').length === 0)
        errors.push({text: "image", el: $('#error-message-image')});

    if (errors.length > 0) {
        handle_errors(errors);
        return false
    }
    return true
}

function check_all_inputs_with_php(test_result) {
    let errors = [];
    $('#error-message-image').html('')

    if (test_result.includes('name'))
        errors.push({text: "name", el: $('#error-message')});
    if (test_result.includes('price'))
        errors.push({text: "price", el: $('#error-message-price')});
    if (test_result.includes('quantity'))
        errors.push({text: "quantity", el: $('#error-message-quantity')});
    if (test_result.includes('name'))
        errors.push({text: "description", el: $('#error-message-desc')});
    if (test_result.includes('image'))
        errors.push({text: "image", el: $('#error-message-image')});

    if (errors.length > 0) {
        handle_errors(errors);
        return false
    }
    return true
}


function regex() {

    const all_inputs = {
        name: {
            input_name: 'name',
            regex: /^[a-zA-Z][a-zA-Z0-9\s]*$/,
            error_div: 'error-message',
            error_text: 'Please enter a valid name (letters and numbers only).'
        },
        price: {
            input_name: 'price',
            regex: /^[1-9]\d{0,10}(,\d{3})*(\.\d{1,2})?$/,
            error_div: 'error-message-price',
            error_text: 'Please enter a valid Price (numbers only).'
        },
        quantity: {
            input_name: 'quantity',
            regex: /^[1-9]\d{0,10}(,\d{3})*(\.\d{1,2})?$/,
            error_div: 'error-message-quantity',
            error_text: 'Please enter a valid Quantity (numbers only).'
        }
    }

    for (const key in all_inputs) {
        document.getElementById(all_inputs[key].input_name).addEventListener('input', function () {
            const errorMessageElement = document.getElementById(all_inputs[key].error_div);
            if (this.value.match(all_inputs[key].regex)) {
                this.classList.remove('name_regex_f');
                this.classList.add('name_regex_t');
                errorMessageElement.textContent = '';
            } else {
                this.classList.remove('name_regex_t');
                this.classList.add('name_regex_f');
                errorMessageElement.textContent = all_inputs[key].error_text;
            }
            if (this.value.trim().length === 0) {
                this.classList.remove('name_regex_t');
                this.classList.remove('name_regex_f');
                errorMessageElement.textContent = "";
            }
        })
    }
}


function generateDescreption() {
    const errorMessageElement = document.getElementById('error-message-desc');
    let title = $('#name').val();
    console.log(title)
    if (title === '') {
        errorMessageElement.textContent = 'Please enter a valid product name so that Ai can help you generating a description';
    } else {
        loader_start_desc()
        errorMessageElement.textContent = ''
        $.ajax({
            url: '/product/generate_description',
            type: "POST",
            data: {
                title: title,
            },
            async: true,
            success: function (response) {
                $('#description').val(response.description)
                loader_stop_desc()
            },
            error: function (response) {
                console.log("error")
            },
        });
    }
}


function DisplayListProducts4Owner(movement_direction, page) {
    $.ajax({
        url: '/user/dashboard/',
        type: "post",
        data: {
            movement_direction: movement_direction,
            page: page
        },
        async: true,
        success: function (response) {

            $("#sub-" + page + "-block").html(response.template);
            setTimeout(function () {
                launchSwiper();
                count();
            }, 1000);


            $("#" + page + "-page-" + response.previousPage).removeClass("active")
            $("#" + page + "-page-" + response.currentPage).addClass("active")
        },
        error: function (response) {
            console.log(response);
        },
    });
}






function deleteProductAdmin(id) {
    console.log(id)
    $.ajax({
        url: '/product/deleteproductAdmin/'+id,
        type: "DELETE",
        success: function (response) {
            console.log(response.list[0])
            updateTransportList(response.list);

            $('#alert').html('        ' +
                ' <div class="alert alert-subtle-success" role="alert">Transport deleted succefully !!!</div>\n');
            setTimeout(function() {
                $('#alert').empty();
            }, 4000);
        },
        error: function(xhr, status, error) {
            $('#alert').html('         <div class="alert alert-subtle-danger" role="alert">An error occured while Deleting the Transport!</div>\n');
            setTimeout(function() {
                $('#alert').empty();
            }, 4000);        }
    });
}




function updateTransportList(listProd) {
    // Clear the existing station list
    $('#products-table-body').empty();

    // Append the new station list
    listProd.forEach(function(product) {
        let productHTML = `   
    
                      <tr class="position-static">
                                    <td class="fs-9 align-middle">







                                        <div class="form-check mb-0 fs-8">
                                            <input class="form-check-input" type="checkbox" data-bulk-select-row='{"product":"Fitbit Sense Advanced Smartwatch with Tools for Heart Health, Stress Management & Skin Temperature Trends, Carbon/Graphite, One Size (S & L Bands...","productImage":"/products/1.png","price":"$39","category":"Plants","tags":["Health","Exercise","Discipline","Lifestyle","Fitness"],"star":false,"vendor":"Blue Olive Plant sellers. Inc","publishedOn":"Nov 12, 10:45 PM"}' /></div>
                                    </td>
                                    {#                      ${ asset(product.image) }#}
                                    <td class="align-middle white-space-nowrap py-0">


                                        <div class="avatar-group avatar-group-dense" style="font-size: 20px;">
                                            {% set image_count = 0 %}
                                            {% for images in product.images %}
                                                {% if image_count < 3 %}
                                                    <a class="dropdown-toggle dropdown-caret-none d-inline-block" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" data-bs-auto-close="outside">
                                                        <div class="avatar avatar-m rounded-circle"> <!-- Adjusted size to avatar-m -->
                                                            <img class="rounded-circle" src="${ asset(images.path) }" alt="" />
                                                        </div>
                                                    </a>
                                                    <div class="dropdown-menu avatar-dropdown-menu p-0 overflow-hidden" style="width: 420px;"> <!-- Adjusted width -->
                                                        <!-- Dropdown menu content -->
                                                        <!-- This part remains the same as in your original code -->
                                                    </div>
                                                    {% set image_count = image_count + 1 %}
                                                {% endif %}
                                            {% endfor %}
                                            {% if product.images|length > 3 %}
                                                <div class="avatar avatar-m rounded-circle"> <!-- Adjusted size to avatar-m -->
                                                    <div class="avatar-name rounded-circle"><span>+${ product.images|length - 3 }</span></div>
                                                </div>
                                            {% endif %}
                                        </div>
-->



                                    </td>
                                    <td class="product align-middle ps-4"><a class="fw-semibold line-clamp-3 mb-0" href="../landing/product-details.html">${ product.name }</a></td>
                                    <td class="price align-middle white-space-nowrap text-end fw-bold text-body-tertiary ps-4">${ product.price }</td>
                                    <td class="category align-middle white-space-nowrap text-body-quaternary fs-9 ps-4 fw-semibold">${ product.category }</td>
                                    <td class="tags align-middle review pb-2 ps-3" style="min-width:225px;"><a class="text-decoration-none" href="#!"><span class="badge badge-tag me-2 mb-2">${ product.state }</span>
                                        </a>

                                    </td>
                                    <td class="vendor align-middle text-start fw-semibold ps-4"><a href="#!">${ product.quantity }$</a></td>
                                    <td class="time align-middle white-space-nowrap text-body-tertiary text-opacity-85 ps-4">${ product.timestamp | date('Y-m-d H:i:s') }</td>
                                    <td class="align-middle white-space-nowrap text-end pe-0 ps-4 btn-reveal-trigger">
                                        <div class="btn-reveal-trigger position-static"><button class="btn btn-sm dropdown-toggle dropdown-caret-none transition-none btn-reveal fs-10" type="button" data-bs-toggle="dropdown" data-boundary="window" aria-haspopup="true" aria-expanded="false" data-bs-reference="parent"><span class="fas fa-ellipsis-h fs-10"></span></button>
                                            <div class="dropdown-menu dropdown-menu-end py-2"><a class="dropdown-item" href="#!">View</a><a class="dropdown-item" href="#!">Export</a>
                                                <div class="dropdown-divider"></div><a class="dropdown-item text-danger" onclick="deleteProductAdmin(${ product.idProduct })" href="#!">Remove</a>
                                            </div>
                                        </div>
                                    </td>
                                </tr>

        `;
        $('#products-table-body').append(productHTML);
    });
}





