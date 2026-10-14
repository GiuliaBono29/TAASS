document.addEventListener("DOMContentLoaded", function() {
    // RECUPERO DEL ROOM ID DALL'URL
    const URLparam = new URLSearchParams(window.location.search)
    const roomId = URLparam.get('roomId');

    if (!roomId) {
        console.error("Nessun ID stanza specificato");
        alert("Errore: ID stanza mancante. Torna alla pagina precedente.");
        return;
    }

    const form = document.getElementById("formTable");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            number: form.numberTable.value,
            capacity: form.capacityTable.value
        };

        try {
            // USIAMO IL roomId RECUPERATO SOPRA
            const response = await apiCall(`/api/space/room/${roomId}/table/create`, {
                method: "POST",
                body: JSON.stringify(data)
            });

            if (response && response.ok) {
                // Reindirizziamo l'utente alla visualizzazione dei tavoli di quella stanza
                window.location.href = `stanze.html`;
            } else {
                alert("Errore durante la creazione del tavolo");
            }
        } catch (error) {
            console.error("Errore critico: " + error);
        }
    });
});