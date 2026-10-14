$(document).ready(function(){
    flatpickr("#inputGiorno", {
        dateFormat: "Y-m-d",
        minDate: "today",
        locale: "it"
    });
});

document.addEventListener("DOMContentLoaded", function () {
    //estrazione id evento da URL
    const urlParams = new URLSearchParams(window.location.search);
    const reservation = urlParams.get('id');

    if(!reservation){
        console.error("ID prenotazione non trovato nell'URL");
        return;
    }

    apiCall(`/api/space/reservation/${reservation}`)
        .then(response => {
            // Se apiCall fallisce per auth (es. token scaduto), gestisce lei il redirect
            if (!response) return;

            if(!response.ok) {
                throw new Error("Errore nel recupero dati");
            }
            return response.json();
        })
        .then(reservationData => {
            if (reservationData) {
                populateForm(reservationData);
            }
        })
        .catch(e => {
            console.error("Errore: " + e);
            alert("Impossibile caricare i dati della prenotazione.");
        });

    function populateForm(reservationData) {

        // Popola la data
        if (reservationData.date) {
            $("#inputGiorno").val(reservationData.date);
        }

        // Popola la fascia oraria (radio button)
        if (reservationData.slot) {
            $(`input[name="slot"][value="${reservationData.slot}"]`).prop('checked', true);
        }

        // Popola il numero di partecipanti
        if (reservationData.seats !== undefined) {
            $("#inputPartecipanti").val(reservationData.seats);
        }
    }

    const form = document.getElementById("formModificaPrenotazione");
    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        const updateData = {
            date: document.getElementById("inputGiorno").value,
            slot: document.querySelector('input[name="slot"]:checked') ?.value,
            seats: parseInt(document.getElementById("inputPartecipanti").value)
        }

        // Invia la richiesta PUT per aggiornare la prenotazione
        const response = await apiCall(`/api/space/reservation/modify/${reservation}`, {
            method: 'PUT',
            body: JSON.stringify(updateData)
        });

        if (response && response.ok) {
            alert("Prenotazione aggiornata!");
            window.location.href = "stanze.html?view=prenotazioni";
        } else {
            alert("Errore nell'aggiornamento della prenotazione.");
        }
    });

})