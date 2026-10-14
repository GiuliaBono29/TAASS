document.addEventListener("DOMContentLoaded", function () {
    //estrazione id evento da URL
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('id');

    if(!eventId){
        console.error("ID evento non trovato nell'URL");
        return;
    }

    apiCall(`/api/event/${eventId}`)
        .then(response => {
            // Se apiCall fallisce per auth (es. token scaduto), gestisce lei il redirect
            if (!response) return;

            if(!response.ok) {
                throw new Error("Errore nel recupero dati");
            }
            return response.json();
        })
        .then(eventData => {
            if (eventData) {
                populateForm(eventData);
            }
        })
        .catch(e => {
            console.error("Errore: " + e);
            alert("Impossibile caricare i dati dell'evento");
        });

    function populateForm(eventData) {
        document.querySelector("h2").textContent = "Modifica Evento";

        // Popola i campi input
        document.getElementById("nameEvent").value = eventData.nomeEvento || "";
        document.getElementById("dateEvent").value = eventData.date || "";
        document.getElementById("typeEvent").value = eventData.tipo || "";
        document.getElementById("description").value = eventData.descrizione || "";

        // serve per fare vedere i valori caricati nuovi e non il "seleziona ..."
        $('.selectpicker').selectpicker('refresh');
    }

    const form = document.getElementById("formEvent");
    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        const formData = new FormData(form);
        const updateData = Object.fromEntries(formData.entries());

        // Sostituisci la fetch esistente
        const response = await apiCall(`/api/event/modify/${eventId}`, {
            method: 'PUT',
            body: JSON.stringify(updateData)
        });

        if (response && response.ok) {
            alert("Evento aggiornato!");
            window.location.href = "eventi.html";
        }
    });

})