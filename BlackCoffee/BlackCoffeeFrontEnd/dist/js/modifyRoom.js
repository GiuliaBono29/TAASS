document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const roomId = urlParams.get('id');

    if (!roomId) {
        console.error("ID stanza non trovato nell'URL");
        return;
    }

    // 1. Caricamento dati iniziali per popolare il form
    apiCall(`/api/space/room/${roomId}`)
        .then(response => {
            if (!response) return;
            if (!response.ok) throw new Error("Errore nel recupero dati");
            return response.json();
        })
        .then(roomData => {
            if (roomData) {
                populateForm(roomData);
            }
        })
        .catch(e => {
            console.error("Errore: " + e);
            alert("Impossibile caricare i dati della stanza");
        });

    function populateForm(roomData) {
        document.querySelector("h2").textContent = "Modifica Stanza";

        // Assicurati che questi ID corrispondano ai tuoi input HTML
        const nameInput = document.getElementById("nameRoom");
        const capacityInput = document.getElementById("capacityRoom");

        if (nameInput) nameInput.value = roomData.name || "";
        if (capacityInput) capacityInput.value = roomData.capacity || "";

        // Se usi bootstrap-select
        if (typeof $('.selectpicker').selectpicker === 'function') {
            $('.selectpicker').selectpicker('refresh');
        }
    }

    // 2. Gestione dell'invio (Modifica)
    const form = document.getElementById("formRoom");
    if (form) {
        form.addEventListener("submit", async function (e) {
            e.preventDefault();

            // COSTRUZIONE OGGETTO PER IL DTO JAVA
            // Invece di Object.fromEntries, mappiamo manualmente i campi
            const updateData = {
                name: document.getElementById("nameRoom").value,
                capacity: parseInt(document.getElementById("capacityRoom").value)
            };

            // Debug per vedere cosa stiamo mandando
            console.log("Invio dati modifica:", updateData);

            try {
                const response = await apiCall(`/api/space/room/modify/${roomId}`, {
                    method: 'PUT',
                    body: JSON.stringify(updateData) // Invia JSON pulito
                });

                if (response && response.ok) {
                    alert("Stanza aggiornata con successo!");
                    window.location.href = "stanze.html";
                } else {
                    const errorData = await response.json().catch(() => ({}));
                    console.error("Dettagli errore server:", errorData);
                    alert("Errore durante l'aggiornamento. Controlla i dati inseriti.");
                }
            } catch (error) {
                console.error("Errore di rete:", error);
                alert("Errore di connessione al server.");
            }
        });
    }
});