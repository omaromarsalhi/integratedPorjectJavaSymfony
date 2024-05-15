// Ensure stationArray is defined and initialized properly before calling initMap()

// Initialize the map variable in the global scope
let map;

// Initialization function
function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 0, lng: 0 }, // Initialize with a default center
        zoom: 355,
        mapTypeControl: true,
    });

    // Call function to display all stations (assuming stationArray is properly initialized)
    displayAllStations(map, nomstationArray,stationArray);

    // Calculate the map bounds based on marker positions
    const bounds = new google.maps.LatLngBounds();
    nomstationArray.forEach(function (station) {
        const [lat, lng] = station.split(',').map(parseFloat);
        bounds.extend(new google.maps.LatLng(lat,lng));
    });

    // Fit the map to the calculated bounds
    map.fitBounds(bounds);

    // Set a maximum zoom level to prevent zooming in too close
    const maxZoom = 70;
    google.maps.event.addListenerOnce(map, 'bounds_changed', function () {
        if (this.getZoom() > maxZoom) {
            this.setZoom(maxZoom);
        }
    });
}

// // Function to display all stations
// function displayAllStations(map, stationArray) {
//     stationArray.forEach(function (station) {
//         // Split the address string into latitude and longitude
//         const [lat, lng] = station.split(',').map(parseFloat);
//
//         // Create a marker for each station
//         const marker = new google.maps.Marker({
//             position: { lat: lat, lng: lng },
//             map: map,
//             title: station.id,
//         });
//         marker.stationId = 64;
//
//         // Add click listener to each marker
//         marker.addListener('click', function () {
//             const stationId = this.stationId;
//
//             // Construct URL with station ID as query parameter
//             const transportUrl = `/transportClientFilter/${stationId}`;
//
//             // Redirect to the transport page
//             window.location.href = transportUrl;
//         });
//     });
// }

function displayAllStations(map, addressArray, idArray) {
    // Ensure both arrays have the same length
    if (addressArray.length !== idArray.length) {
        console.error("The address array and ID array must have the same length.");
        return;
    }

    addressArray.forEach(function (address, index) {
        // Split the address string into latitude and longitude
        const [lat, lng] = address.split(',').map(parseFloat);

        // Create a marker for each station
        const marker = new google.maps.Marker({
            position: { lat: lat, lng: lng },
            map: map,
            title: idArray[index],
            icon: {
                url: "images/transport/sex.png", // Replace with the URL to your image
                scaledSize: new google.maps.Size(80, 80) // Adjust the size to fit your needs
            }
        });

        // Add click listener to each marker
        marker.addListener('click', function () {
            const stationId = idArray[index]; // Accessing the corresponding ID
            // Construct URL with station ID as query parameter
            const transportUrl = `/transportClientFilter/${stationId}`;
            // Redirect to the transport page
            window.location.href = transportUrl;
        });
    });
}

// Example usage:
// Assuming you have two arrays: addressArray and idArray
// displayAllStations(map, addressArray, idArray);

// Rest of your code remains unchanged...
