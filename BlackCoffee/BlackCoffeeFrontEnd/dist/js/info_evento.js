document.addEventListener("DOMContentLoaded", function() {
    let eventId = window.location.hash.substring(1) || new URLSearchParams(window.location.search).get('id');
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    if (!eventId) {
        alert("Evento non specificato!");
        return;
    }

    const container = document.getElementById("eventContainer");

    // Carichiamo l'evento
    apiCall(`/api/event/${eventId}`)
        .then(res => {
            if (!res || !res.ok) throw new Error("Evento non trovato");
            return res.json();
        })
        .then(event => {
            if (!event) return;

            // 1. Definiamo e chiamiamo la funzione per i dettagli
            renderEventDetails(event, container, loggedUser);

            // 2. Chiamiamo i bottoni passando l'oggetto event completo
            renderButtons(loggedUser, event);
        })
        .catch(err => {
            console.error(err);
            container.innerHTML = "<p>Errore nel caricamento dell'evento</p>";
        });
});

// Funzione che scrive i dati dell'evento nel DOM
function renderEventDetails(event, container, loggedUser) {
    let eventDate = new Date(event.date);
    let formattedDate = isNaN(eventDate.getTime()) ? "N/D" : eventDate.toLocaleDateString('it-IT');

    if (event.tipo === "CLUB") {
        
            apiCall(`/api/club/${event.clubId}`)
                .then(res => {
                    if (!res || !res.ok) throw new Error("Club non trovato");
                    return res.json();
                })
                .then(club => {
                    if (!club) return;

                    if (loggedUser.role === "ORGANIZZATORE") {
                        container.innerHTML = `
                            <h2>${event.nomeEvento || "Dettagli Evento"}</h2>
                            <p><strong>Stato:</strong> ${event.stato}</p>
                            <p><strong>Data:</strong> ${formattedDate}</p>
                            <p><strong>Descrizione:</strong> ${event.descrizione || ""}</p>
                            <p><strong>Club Associato:</strong> ${club.nomeclub || ""}</p>
                        `;
                    } else {
                        container.innerHTML = `
                            <h2>${event.nomeEvento || "Dettagli Evento"}</h2>
                            <p><strong>Data:</strong> ${formattedDate}</p>
                            <p><strong>Descrizione:</strong> ${event.descrizione || ""}</p>
                            <p><strong>Club Associato:</strong> ${club.nomeclub || ""}</p>
                        `;
                    }
                })
                .catch(err => {
                    console.error(err);
                    container.innerHTML = "<p>Errore nel caricamento del club</p>";
                });
    } else {
        if (loggedUser.role === "ORGANIZZATORE") {
            container.innerHTML = `
                <h2>${event.nomeEvento || "Dettagli Evento"}</h2>
                <p><strong>Stato:</strong> ${event.stato}</p>
                <p><strong>Data:</strong> ${formattedDate}</p>
                <p><strong>Descrizione:</strong> ${event.descrizione || ""}</p>
            `;
        } else {
            container.innerHTML = `
                <h2>${event.nomeEvento || "Dettagli Evento"}</h2>
                <p><strong>Data:</strong> ${formattedDate}</p>
                <p><strong>Descrizione:</strong> ${event.descrizione || ""}</p>
            `;
        }
    }
}

function renderButtons(loggedUser, event) {
    const buttonsContainer = document.getElementById("button_view");
    if (!buttonsContainer) return;

    const eventId = event.id;
    const statoEvento = event.stato; // Recuperiamo lo stato dell'evento

    // CASO 1: ORGANIZZATORE
    // L'organizzatore deve sempre vedere i suoi tasti di gestione, a prescindere dallo stato
    if (loggedUser && loggedUser.role === "ORGANIZZATORE") {
        buttonsContainer.innerHTML = `
            <button id="pubblishBtn" class="btn btn-success">Pubblica</button>
            <button id="procideBtn" class="btn btn-warning">Procedi con la creazione dell'evento</button>
            <button id="cancelBtn" class="btn btn-secondary">Annulla Evento</button>
            <button id="deleteBtn" class="btn btn-danger">Cancella</button>
        `;

        
        document.getElementById("deleteBtn").onclick = () => cancellaEvento(eventId);
        document.getElementById("pubblishBtn").onclick = () => modificaStato(eventId, "pubblicato");
        document.getElementById("cancelBtn").onclick = () => modificaStato(eventId, "cancellato");
        document.getElementById("procideBtn").onclick = () => procediPrenotazione(event, loggedUser);

        if (statoEvento === "ATTESA_PUBB") {
            document.getElementById("pubblishBtn").style.display = "inline-block";
            document.getElementById("cancelBtn").style.display = "none";
            document.getElementById("deleteBtn").style.display = "none";
            document.getElementById("procideBtn").style.display = "none";
        } else if (statoEvento === "PUBBLICATO") {
            document.getElementById("pubblishBtn").style.display = "none";
            document.getElementById("cancelBtn").style.display = "inline-block";
            document.getElementById("deleteBtn").style.display = "none";
            document.getElementById("procideBtn").style.display = "none";
        } else if (statoEvento === "CANCELLATO") {
            document.getElementById("pubblishBtn").style.display = "none";
            document.getElementById("cancelBtn").style.display = "none";
            document.getElementById("deleteBtn").style.display = "inline-block";
            document.getElementById("procideBtn").style.display = "none";
        } else if (statoEvento === "IN_CREAZIONE") {
            document.getElementById("pubblishBtn").style.display = "none";
            document.getElementById("cancelBtn").style.display = "inline-block";
            document.getElementById("procideBtn").style.display = "inline-block";
            document.getElementById("deleteBtn").style.display = "none";
        } else {
            // Nascondi tutti i bottoni se lo stato non è riconosciuto
            document.getElementById("pubblishBtn").style.display = "none";
            document.getElementById("cancelBtn").style.display = "none";
            document.getElementById("deleteBtn").style.display = "none";
            document.getElementById("procideBtn").style.display = "none";
        }
    }
    // CASO 2: CLIENTE O NON LOGGATO
    else {
        // I pulsanti iscrivimi/disiscrivimi appaiono SOLO se lo stato è PUBBLICATO
        if (statoEvento === "PUBBLICATO") {
            // Logica alternata (usiamo userId o id a seconda di come lo salvi)
            const currentUserId = loggedUser ? (loggedUser.userId) : null;
            const isIscritto = currentUserId && event.iscritti && event.iscritti.includes(currentUserId);

            if (isIscritto) {
                buttonsContainer.innerHTML = `<button id="rmBtn" class="btn btn-outline-danger">Disiscrivimi</button>`;
                document.getElementById("rmBtn").onclick = () => gestisciIscrizione(eventId, "rmUser");
            } else {
                buttonsContainer.innerHTML = `<button id="addBtn" class="btn btn-primary">Iscrivimi!</button>`;
                document.getElementById("addBtn").onclick = () => gestisciIscrizione(eventId, "addUser");
            }
        } else {
            // Se non è pubblicato, possiamo mostrare un messaggio o lasciare il contenitore vuoto
            buttonsContainer.innerHTML = `<span class="badge bg-info text-dark">Iscrizioni non disponibili</span>`;
        }
    }
}

function gestisciIscrizione(eventId, action) {
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    // Se non loggato, avvisa e reindirizza
    if (!loggedUser) {
        alert("Devi essere loggato per iscriverti agli eventi!");
        window.location.href = "login.html";
        return;
    }

    apiCall(`/api/event/event_sub/${action}/${eventId}`, { method: "PUT" })
        .then(async res => {
            if (res && res.ok) {
                alert(action === "addUser" ? "Iscrizione completata!" : "Disiscrizione completata.");
                location.reload();
            } else if (res && res.status === 400) {
                const message = await res.text();
                alert(message);
            } else {
                alert("Errore imprevisto"); // Errore generico di rete
            }
        })
        .catch(err => {
            console.error(err);
            alert("Errore interno, impossibile contattare il server");
        });
}

// Funzioni di supporto (modificaStato, cancellaEvento)
function modificaStato(eventId, stato) {
    apiCall(`/api/event/modifyState/${eventId}/${stato}`, { method: "PUT" })
        .then(res => (res && res.ok) ? location.reload() : alert("Errore modifica stato"))
        .catch(err => console.error(err));
}

function cancellaEvento(eventId) {
    if (!confirm("Sei sicuro?")) return;
    apiCall(`/api/event/delete/${eventId}`, { method: "DELETE" })
        .then(res => (res && res.ok) ? window.location.href = "eventi.html" : alert("Errore"))
        .catch(err => console.error(err));
}

function procediPrenotazione(event, loggedUser) {
        sessionStorage.setItem("currentEventId", event.id);
        sessionStorage.setItem("currentEventDate", event.date);
        sessionStorage.setItem("currentEventName", event.nomeEvento);
        sessionStorage.setItem("currentManagerName", loggedUser.name);
        sessionStorage.setItem("currentTypeEvent", event.tipo);
        window.location.href = "other_info_event.html";
}