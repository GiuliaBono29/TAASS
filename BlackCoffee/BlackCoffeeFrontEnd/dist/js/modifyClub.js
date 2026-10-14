document.addEventListener("DOMContentLoaded", function () {
    //estrazione id evento da URL
    const urlParams = new URLSearchParams(window.location.search);
    const clubId = urlParams.get('id');

    if(!clubId){
        console.error("ID del club non trovato nell'URL");
        return;
    }

    apiCall(`/api/club/${clubId}`)
        .then(response => {
            // Se apiCall fallisce per auth (es. token scaduto), gestisce lei il redirect
            if (!response) return;

            if(!response.ok) {
                throw new Error("Errore nel recupero dati");
            }
            return response.json();
        })
        .then(clubData => {
            if (clubData) {
                populateForm(clubData);
            }
        })
        .catch(e => {
            console.error("Errore: " + e);
            alert("Impossibile caricare i dati dell'evento");
        });

    function populateForm(clubData) {
        document.querySelector("h2").textContent = "Modifica informazioni Club";

        // Popola i campi input
        document.getElementById("nameClub").value = clubData.nomeclub || "";
        document.getElementById("description").value = clubData.description || "";

        // serve per fare vedere i valori caricati nuovi e non il "seleziona ..."
        $('.selectpicker').selectpicker('refresh');
    }

    const form = document.getElementById("formClub");
    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        const formData = new FormData(form);
        const updateData = Object.fromEntries(formData.entries());

        try {
            // Sostituisci la fetch esistente
            const response = await apiCall(`/api/club/modify/${clubId}`, {
                method: 'PUT',
                body: JSON.stringify(updateData)
            });

            if (response && response.ok) {
                alert("Club aggiornato!");
                window.location.href = "club.html";
            }
        } catch (error){
            console.error("Errore durante la modifica: ", error);

            if (error.message.includes("403")){
                alert("Errore, non hai i permessi di modifica");
            } else if (error.message.includes("404")){
                alert("Errore, il club non esiste più");
            } else {
                alert("Errore imprevisto");
            }
        }
    });

})