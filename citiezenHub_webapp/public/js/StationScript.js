
var maVariableGlobale =1;

function addStation(event) {
    event.preventDefault();
    const TypeVehicule = document.getElementById("typeSelect").value;
    let formData = new FormData();
    let name = $('#name').val();  
    let adress = $('#adressStation').val();
    
    formData.append('image', $('#createinputfile').prop('files')[0]);
    formData.append('nomStation', name);
    formData.append('adressStation', adress);
    formData.append('type_vehicule', TypeVehicule);

    $.ajax({
        url: '/addStation',
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        beforeSend: function() {
            // You can add any pre-processing logic here
        },
        success: function(response) {
          
              if (response.message === "Station added successfully.") {

                  $('#alert').html('<div class="alert alert-subtle-success" role="alert">Station added successfully!!!</div>');
                  setTimeout(function() {
                      $('#alert').empty();
                  }, 4000);




                  // Handle success


                  $('#addDealModal').modal('hide');
                  $('#name').val('');
                  $('#adressStation').val('');
                  $('#createinputfile').val('');
                  $('#createinputfile').closest('form').get(0).reset();
                updateStationList(response.stations); 
            } 
        },
        error: function(response) {
            if (response.responseJSON && response.responseJSON.error === 'VALIDATION_ERROR') {
                const errorMessage = response.responseJSON.messages.join(', ');
                const errorMessagesArray = errorMessage.split(',');
                let errorMessagesHTML = '';
        
                errorMessagesArray.forEach((message) => {
                    errorMessagesHTML += `<div>${message.trim()}</div>`;
                });
                $('.error-label').html(errorMessagesHTML);
            }
            else if (response.responseJSON && response.responseJSON.error === 'DUPLICATE_ENTRY') {
                // Handle duplicate entry error
                alert("Error: " + response.responseJSON.message);
                console.log("Duplicate entry");
            } else {
                // Handle other errors
                $('#alert').html('        <div class="alert alert-subtle-danger" role="alert">An error while inserting  !</div>\n');
                setTimeout(function() {
                    $('#alert').empty();
                }, 4000);
            } 
            // Handle AJAX errors
        },
    });
}


    

function updateStation(event) {
    event.preventDefault();
    const TypeVehicule = document.getElementById("typeSelectUpd").value;
    let formData = new FormData();
    let name = $('#nameUpd').val();  
    let address = $('#adressStationUpd').val();
    var listStation;

    formData.append('image', $('#createinputfileUpd').prop('files')[0]);
    formData.append('nomStation', name);
    formData.append('adressStation', address);
    formData.append('type_vehicule', TypeVehicule);

    $.ajax({
        url: '/updateStation/' + maVariableGlobale,
        type: "POST",
        data: formData,
        async: true,
        processData: false,
        contentType: false,
        beforeSend: function() {
            // Add any pre-request logic here
        },
        success: function(response) {
            if (response.message == "Station updated successfully.") {
                $('#alert').html('<div class="alert alert-subtle-success" role="alert">Station Updated successfully!!!</div>');
                setTimeout(function() {
                    $('#alert').empty();
                }, 4000);                $('#updateDealModal').modal('hide');
                $('#name').val('');
                $('#adressStation').val('');
                $('#createinputfileUpd').val('');
                $('#createinputfileUpd').closest('form').get(0).reset();
                updateStationList(response.stations); 
            } else {
                alert(response.message);
            }
        },
        error: function(response) {
            if (response.responseJSON && response.responseJSON.error === 'VALIDATION_ERROR') {
                const errorMessage = response.responseJSON.messages.join(', '); // Join error messages with ','
                const errorMessagesArray = errorMessage.split(','); // Split the error messages on ','
                let errorMessagesHTML = ''; // Initialize an empty string to store HTML for error messages
        
                // Loop through each error message and create HTML for it
                errorMessagesArray.forEach((message) => {
                    errorMessagesHTML += `<div>${message.trim()}</div>`; // Trim whitespace and wrap each message in a <div>
                });
                $('.error-label').html(errorMessagesHTML);
            }
            else if (response.responseJSON && response.responseJSON.error === 'DUPLICATE_ENTRY') {
                // Handle duplicate entry error
                $('#alert').html('         <div class="alert alert-subtle-danger" role="alert">An error occured while updating!</div>\n');
                setTimeout(function() {
                    $('#alert').empty();
                }, 4000);                console.log("Duplicate entry");
            } else {
                // Handle other errors
                $('#alert').html('         <div class="alert alert-subtle-danger" role="alert">An error occured while updating!</div>\n');
                setTimeout(function() {
                    $('#alert').empty();
                }, 4000);
            } 
            // Handle AJAX errors
        },
    });
}



const inputFields = document.querySelectorAll('.input-target');
const inputFieldAddress = document.querySelectorAll('.input-Address');

inputFields.forEach(inputField => {
  inputField.addEventListener('keyup', function() {
    // Check for letters, commas and spaces
    if (/^[A-Za-z ,]+$/.test(this.value)) {
      this.classList.add('input-modified');
      this.classList.add('input-valid');
    } else {
      this.classList.remove('input-modified');
      this.classList.remove('input-invalid');
      this.classList.add('input-typing');
    }
  });
});

inputFieldAddress.forEach(inputField => {
  inputField.addEventListener('keyup', function() {
    // Check for valid geolocation format
    if (/^(\-?\d+\.\d+),(\-?\d+\.\d+)$/.test(this.value)) {
      this.classList.add('input-modified');
      this.classList.add('input-valid');
    } else {
      this.classList.remove('input-modified');
      this.classList.remove('input-invalid');
      this.classList.add('input-typing');
    }
  });
});

$('#name').change(function() {
    var stationName = $(this).val();

    $.ajax({
        url: 'https://api.opencagedata.com/geocode/v1/json',
        data: {
            q: stationName, // Use the value of the input field
            key: '53ee85fa919942ebb5df4021833590b4' // replace with your actual OpenCage API key
        },
        success: function(response) {
            if (response.results.length > 0) {
                var lat = response.results[0].geometry.lat;
                var lng = response.results[0].geometry.lng;
                var address = lat + ', ' + lng;
                $('#adressStation').val(address);
            } else {
                $('#adressStation').val('').prop('readonly', false);
            }
        }
    });
});



    const fileInputs = document.querySelectorAll('input[type="file"]');

    fileInputs.forEach(fileInput => {
        fileInput.addEventListener('change', function() {
            // Check if files have been selected
            if (this.files.length > 0) {
                console.log('No file has been selected.');
                this.classList.remove('input-modified');
                this.classList.remove('input-invalid');
                this.classList.add('input-typing');
            } else {
                console.log('A file has been selected.');
                this.classList.add('input-modified');
                this.classList.add('input-invalid');
            }
        });
    });

    document.getElementById('createDealBtn').addEventListener('click', function() {
        // Validate all fields
        if (validateFields()) {
            // All fields are valid, proceed with the action
            console.log('All fields are valid. Proceeding with the action...');
            // Add your code to perform the action (e.g., submit the form)
        } else {
            // Some fields are not valid, display an error message
            alert('Error: Please fill out all fields correctly.');
        }
    });

    function validateFields() {
        // Check if all fields are valid
        const inputFields = document.querySelectorAll('.input-target');
        let allFieldsValid = true;

        inputFields.forEach(inputField => {
            if (inputField.value.trim() === '') {
                // Field is empty
                allFieldsValid = false;
                return;
            }

            // Check if the field color is #50C878
            if (getComputedStyle(inputField).border-color !== 'green') {
                // Field color is not #50C878
                allFieldsValid = false;
                return;
            }
                if (getComputedStyle(inputField).border-color == 'red') {
                // Field color is not #50C878
                allFieldsValid = true;
                return;
            }
        });

        // Return whether all fields are valid
        return allFieldsValid;
    }



 



   $('#search-input').change( function(event) {
        console.log("bbbb");

        var searchQuery = $(this).val();

        // Send the AJAX request only if the search query is not empty
        if (searchQuery.trim() !== '') {
            $.ajax({
                url: '/searchStation/' + searchQuery,
                method: 'GET',
                success: function(response) {
                    // Update the table with search results
                    $('#table-latest-review-body').html(response);
                },
                error: function(xhr, status, error) {
                    console.error(error);
                }
            });
        } else {
            console.log("aaaa");
            // If the search query is empty, remove the search results
            // and revert to the original content
                $('#table-latest-review-body').append(`
                 
                `);
        }
    });


    $('.search-input').on('input', function(event) {
        console.log("Event listener triggered."); // Add this line
        var searchQuery = $(this).val();
        console.log("Search query:", searchQuery); // Add this line
        // Rest of your code
    });// 


var itemId;
function handleDelete(value) {
    // Do something with the value (e.g., log it)
    itemId=value;
}




function deleteStation() {
    $.ajax({
        url: '/station/' + itemId,
        type: 'DELETE',
        success: function(response) {
            $('#verticallyCentered').modal('hide');

            updateStationList(response.stations);


            $('#alert').html('         <div class="alert alert-subtle-success" role="alert">Station deleted succefully !!!</div>\n');
            setTimeout(function() {
                $('#alert').empty();
            }, 4000);

        },
        error: function(xhr, status, error) {
            $('#verticallyCentered').modal('hide');
            $('#alert').html('         <div class="alert alert-subtle-danger" role="alert">An error occured while Deleting the station!</div>\n');
            setTimeout(function() {
                $('#alert').empty();
            }, 4000);        }
    });
}



function fetchUpdatedStationList() {
            updateStationList(listStation); 
}

function updateStationList(stationList) {
    console.log(stationList);
    // Clear the existing station list
    $('#table-latest-review-body').empty();

    // Append the new station list
    stationList.forEach(function(station) {
        // Generate HTML for each station and append it to the list
        let stationHTML = `   
 

    <tr class="hover-actions-trigger btn-reveal-trigger position-static">
                                            <td class="fs-9 align-middle ps-0">
                                                <div class="form-check mb-0 fs-8"><input class="form-check-input"
                                                                                         type="checkbox"
                                                                                         data-bulk-select-row='{"product":"Apple MacBook Pro 13 inch-M1-8/256GB-space","productImage":"/products/60x60/3.png","customer":{"name":"Woodrow Burton","avatar":"/team/40x40/58.webp"},"rating":4.5,"review":"It&#39;s a Mac, after all. Once you&#39;ve gone Mac, there&#39;s no going back. My first Mac lasted over nine years, and this is my second.","status":{"title":"Pending","badge":"warning","icon":"clock"},"time":"Just now"}'/>
                                                </div>
                                            </td>

                                            <td class="align-middle product white-space-nowrap"><a class="fw-semibold"
                                                                                                   href="">${station.nomstation}</a>
                                            </td>
                                            <td class="align-middle customer white-space-nowrap"><a
                                                        class="d-flex align-items-center text-body"
                                                        href="apps/e-commerce/landing/profile.html">
                                                    <div class="avatar avatar-l"><img class="rounded-circle"
                                                                                      src="/usersImg/${station.image_station}"
                                                                                      alt=""/></div>
                                                </a></td>
                                            <td class="align-middle rating white-space-nowrap fs-10">
                                                <h6 class="fs-9 fw-semibold text-body-highlight mb-0">  ${station.Type_Vehicule}</h6>
                                            </td>
                                            <td class="align-middle review" style="min-width:350px;">
                                                <h6 class="fs-9 fw-semibold text-body-highlight mb-0">${station.addressstation}</h6>
                                            </td>
                                         
                                            <td class="align-middle text-end time white-space-nowrap">
                                                <div class="hover-hide">
                                                    <h6 class="text-body-highlight mb-0">Old data</h6>
                                                </div>
                                            </td>
                                            <td class="align-middle white-space-nowrap text-end pe-0">
                                                <div class="position-relative">
                                                    <div class="hover-actions">
                                                        <button class="btn btn-sm btn-phoenix-secondary me-1 fs-10"
                                                                onclick=" showModifierPopup('${ station.id }','${ station.nomstation }','${ station.addressstation }','${ station.image_station }','${ station.Type_Vehicule }')">
                                                            <span class=" fas fa-check"></span>
                                                        </button>
                                                        <button class="btn btn-sm btn-phoenix-secondary fs-10 open-DeleteModal"
                                                                 onclick="handleDelete('${ station.id }')"
                                                                 data-bs-toggle="modal" data-bs-target="#verticallyCentered">
                                                            <span class="fas fa-trash"></span>
                                                        </button>
                                                    </div>
                                                </div>
                                                <div class="btn-reveal-trigger position-static">
                                                    <button class="btn btn-sm dropdown-toggle dropdown-caret-none transition-none btn-reveal fs-10"
                                                            type="button" data-bs-toggle="dropdown"
                                                            data-boundary="window" aria-haspopup="true"
                                                            aria-expanded="false" data-bs-reference="parent"><span
                                                                class="fas fa-ellipsis-h fs-10"></span></button>
                                                    <div class="dropdown-menu dropdown-menu-end py-2"><a
                                                                class="dropdown-item" href="#!">View</a><a
                                                                class="dropdown-item" href="#!">Update</a>
                                                        <div class="dropdown-divider"></div>
                                                        <a class="dropdown-item text-danger" href="#!">Remove</a>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
        `;
        $('#table-latest-review-body').append(stationHTML);
    });
}


function showModifierPopup(id, name, address,image,type) {
    maVariableGlobale=id;
    var modal = document.getElementById("updateDealModal");
    let stationNameInput = document.getElementById("nameUpd");
    let stationLocationInput = document.getElementById("adressStationUpd");
    let stationImageInput =document.getElementById("createinputfileUpd");

    console.log(name);
    // Assuming stationData is an object with properties like name, location, capacity
    stationNameInput.value = name;
    stationLocationInput.value = address;
   // stationImageInput.value=image;
    $('#updateDealModal').modal('show');


    
}



// assets/js/map.js
// assets/js/map.js

function initMap() {
    // Coordinates to center the map
    var myLatLng = {lat: 34.397, lng: 9.644};

    // Create the map
    var map = new google.maps.Map(document.getElementById('map'), {
        center: myLatLng,
        zoom: 8
    });

    // Create a marker on the map
    var marker = new google.maps.Marker({
        position: myLatLng,
        map: map,
        title: 'Hello World!'
    });
}


