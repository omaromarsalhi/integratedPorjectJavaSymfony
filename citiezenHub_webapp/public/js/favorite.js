const datePicker1 = document.getElementById('fromDateFavorite');
const datePicker2 = document.getElementById('toDateFavorite');
let test = false;
// Événement sur le changement de date du deuxième champ de saisie
datePicker2.addEventListener('change', function () {
    const date1 = new Date(datePicker1.value);
    const date2 = new Date(datePicker2.value);

    // Vérifier si la date2 est inférieure à la date1
    if (date2 < date1 || date2 === date1) {
        test = false
        $('#fromDateFavorite').removeClass('name_regex_t');
        $('#toDateFavorite').removeClass('name_regex_t');
        $('#fromDateFavorite').addClass('name_regex_f');
        $('#toDateFavorite').addClass('name_regex_f');
    } else {
        test = true
        $('#fromDateFavorite').removeClass('name_regex_f');
        $('#toDateFavorite').removeClass('name_regex_f');
        $('#fromDateFavorite').addClass('name_regex_t');
        $('#toDateFavorite').addClass('name_regex_t');
    }


});


function deleteFavorite(id) {

    $.ajax({
        url: "/favorite/delete",
        type: "POST",
        data: {
            id: id,
        },
        async: true,
        success: function (response) {
            webNotif('Favorite has been Deleted')
            $('#favoriteContainer').html(response)
        },
    });

}


function saveFavorite() {
    let category = $('#categoryFavorite').val()
    let quantityFavorite = $('#quantityFavorite').val()
    let fromDateFavorite = $('#fromDateFavorite').val()
    let toDateFavorite = $('#toDateFavorite').val()

    if (!test&&fromDateFavorite&&toDateFavorite) {
        showInvalidPop("dates are wrong")
        return
    }

    $.ajax({
        url: "/favorite/new",
        type: "POST",
        data: {
            category: category,
            quantityFavorite: quantityFavorite,
            fromDateFavorite: fromDateFavorite,
            toDateFavorite: toDateFavorite,
            priceMin: filterBy.priceIntervale.min,
            priceMax: filterBy.priceIntervale.max,
        },
        async: true,
        success: function (response) {
            webNotif('New Favorite has been Added')

            $('#favoriteContainer').html(response)
        },
    });

}