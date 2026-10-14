let currentEventId = null;

document.addEventListener("DOMContentLoaded", async function() { // Aggiunto async qui
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    const form = document.getElementById("formPrenotazione");
    const container = document.getElementById("roomsContainer");
    const createBtn = document.getElementById("createRoomBtn");
    const rejectBtn = document.getElementById("btnReject");
    const urlParams= new URLSearchParams(window.location.search);
    const requestId = urlParams.get('id'); // Recupera l'id dal link

    if (!container) return;

    if (loggedUser && loggedUser.role === "LIBRAIO"){
        createBtn.style.display = "block";
        rejectBtn.style.display = "block";
        //se la richiesta viene fatta per un messaggio

        if (!requestId) {
            rejectBtn.disabled = true;
            rejectBtn.title = "Nessuna richiesta di evento attiva";
            rejectBtn.classList.add("opacity-50"); // Feedback visivo
        } else {
            rejectBtn.addEventListener("click", () => rifiutaRichiesta(requestId));
        }
    } else {
        createBtn.style.display = "none";
        rejectBtn.style.display = "none";
    }

    if (requestId) {
        await autocompilaDaMessaggio(requestId);
    } else {
        // Se non c'è un ID, carica tutte le stanze normalmente
        caricaStanze();
    }
    // ----------------------------------------------

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        eseguiRicerca();
    });
});

/**
 * Recupera i dettagli di una richiesta specifica e popola il form
 */
async function autocompilaDaMessaggio(id) {
    try {
        // Chiamata all'API per ottenere i dettagli della singola richiesta
        // Assumo l'endpoint sia /api/space/room/message/{id}
        const res = await apiCall(`/api/space/room/message/${id}`);
        
        if (res && res.ok) {
            const data = await res.json();
            
            // Popolamento campi form
            // Assicurati che i nomi dei campi (data.date, ecc.) corrispondano al tuo DTO

            currentEventId = data.eventId;

            document.getElementById("inputGiorno").value = data.date || "";
            document.getElementById("inputFascia").value = data.slot || data.fascia || "Scegli...";
            document.getElementById("inputPartecipanti").value = data.requestedSeats || data.seats || "";

            // Esegue automaticamente la ricerca delle stanze disponibili per questa richiesta
            eseguiRicerca();
        }
    } catch (error) {
        console.error("Errore nel recupero dettagli messaggio:", error);
        caricaStanze(); // In caso di errore, carica comunque la lista base
    }
}

/**
 * Logica di ricerca estratta per essere riutilizzabile
 */
async function eseguiRicerca() {
    const container = document.getElementById("roomsContainer");
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    const params = getParams();

    if (!params.date || params.slot === "Scegli..." || isNaN(params.seats)) {
        alert("Compilare tutti i campi per la ricerca");
        return;
    }

    try {
        const res = await apiCall(`/api/space/disponibility/${loggedUser.role}`, {
            method: "POST",
            body: JSON.stringify(params)
        });

        if (res && res.ok) {
            if (res.status === 204) {
                container.innerHTML = `<div class="alert alert-info w-100">Nessuna stanza disponibile</div>`;
            } else {
                const roomDisp = await res.json();
                renderRooms(roomDisp);
            }
        }
    } catch (error) {
        console.error("Errore durante la ricerca: ", error);
    }
}

function getParams() {
    const dateValue = document.getElementById("inputGiorno").value;
    const selectedDate = new Date(dateValue);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if(!dateValue || selectedDate < today) {
        alert("La data della prenotazione non può essere precedente alla data odierna.");
        return;
    }

    return {
        date : document.getElementById("inputGiorno").value,
        slot : document.getElementById("inputFascia").value,
        seats : parseInt(document.getElementById("inputPartecipanti").value)
    }

}

async function caricaStanze() {
    const res = await apiCall("/api/space/rooms");
    if (res && res.ok) {
        const rooms = await res.json();
        renderRooms(rooms);
    }
}
function renderRooms(rooms) {
    const container = document.getElementById("roomsContainer");
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    const role = loggedUser ? loggedUser.role : null;

    container.innerHTML = "";

    rooms.forEach(room => {
        // Gestione nomi campi (compatibilità tra Entity Room e DTO ReservationResponse)
        const roomId = room.id || room.roomId;
        const roomName = room.name || room.roomName;
        const roomCapacity = room.capacity || room.roomCapacity;
        const tables = room.tables || room.availableTables || [];

        console.log(`room name: ${roomName}, room capacity: ${roomCapacity}, tables ${tables}`);

        const roomDiv = document.createElement("div");
        roomDiv.className = "col-12 col-md-6 mb-4";

        let tableOptions = tables.length > 0
            ? `<option value="null" selected disabled>-- seleziona tavolo --</option>` +
            tables.map(t => `<option value="${t.id}">Tavolo ${t.number} (${t.capacity} posti)</option>`).join('')
            : `<option value="null" disabled selected>Nessun tavolo disponibile</option>` ;

        let actionButtons = "";

        if (role === "LIBRAIO") {
            actionButtons += `
                <div class="row g-2 mt-2">
                    <div class="col-6"><button class="btn btn-sm btn-outline-dark w-100" onclick="location.href='modifica_stanza.html?id=${roomId}'">Modifica</button></div>
                    <div class="col-6"><button class="btn btn-sm btn-outline-danger w-100" onclick="deleteRoom('${roomId}')">Elimina</button></div>
                </div>
                <button class="btn btn-sm btn-primary w-100 mt-2" onclick="aggiungiTavolo('${roomId}')">Aggiungi Tavolo</button>
                <div class="input-group mt-2">
                    <select class="form-select form-select-sm" id="select-${roomId}">${tableOptions}</select>
                    <button class="btn btn-sm btn-danger" onclick="cancellaTavolo('${roomId}')">Elimina Tavolo</button>
                </div>`;
        }

        if (role === "LIBRAIO") {
            actionButtons += `<button class="btn btn-sm btn-success w-100 mt-2" onclick="prenotaStanza('${roomId}')">Prenota Intera Stanza</button>`;
        }

        if (role === "ORGANIZZATORE" || role === "CLIENTE") {
            actionButtons += `
                <div class="input-group mt-2">
                    <select class="form-select form-select-sm" id="select-${roomId}">${tableOptions}</select>
                    <button class="btn btn-sm btn-warning" onclick="prenotaTavolo('${roomId}')">Prenota Tavolo</button>
                </div>`;
        }

        roomDiv.innerHTML = `
            <div class="card shadow-sm h-100" style="background-color: #FFF5E1; border: 1px solid #D9A173;">
                <div class="card-body d-flex flex-column">
                    <h4 class="card-title text-center">${roomName}</h4>
                    <p class="card-text text-muted mb-auto">Capacità: ${roomCapacity} persone</p>
                    <hr>
                    <div class="mt-auto">${actionButtons}</div>
                </div>
            </div>`;
        container.appendChild(roomDiv);
    });
}

// Esempio di funzioni per i nuovi bottoni
function aggiungiTavolo(roomId) {
    window.location.href = `crea_tavolo.html?roomId=${roomId}`;
}

async function prenotaStanza(roomId) {
    const room = document.getElementById(`select-${roomId}`);
    const params = getParams();

    const payload = {
        date: params.date,
        slot: params.slot,
        seats: params.seats,
        eventId: currentEventId // Aggiunta dell'eventId al payload
    };

    if (!params.date || params.slot === "Scegli..." || isNaN(params.seats)) {
        alert("Compilare tutti i campi");
        return;
    }

    // Controllo fondamentale: deve esserci un tavolo selezionato
    if (!room || room === "null") {
        alert("Attenzione: devi selezionare una stanza valida");
        return;
    }

    if (!confirm("Confermi la prenotazione dell'intera stanza?")) return;

    try {
        const res = await apiCall(`/api/space/room/${roomId}/prenota`, {
            method: "POST",
            body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (res.ok) {
            alert(data.message);
            window.location.reload();
        } else {
            alert("Attenzione: " + (data.error || "Stanza non disponibile"));
        }
    } catch (error) {
        console.error("Errore di rete:", error);
        alert("Errore di connessione, risposta non valida");
    }
}

async function prenotaTavolo(roomId) {
    //questo serve per recuperare il tavolo
    const select = document.getElementById(`select-${roomId}`);
    const tableId = select.value;

    const params = getParams();

    if (!params.date || params.slot === "Scegli..." || isNaN(params.seats)) {
        alert("Compilare tutti i campi");
        return;
    }

    // Controllo fondamentale: deve esserci un tavolo selezionato
    if (!tableId || tableId === "null" || tableId.includes("Nessun tavolo")) {
        alert("Attenzione: devi selezionare un tavolo valido dal menù a tendina prima di prenotare.");
        return;
    }

    if (!confirm(`Vuoi prenotare il tavolo?`)){
        return;
    }

    try {
        const res = await apiCall(`/api/space/room/tavolo/${tableId}/prenota`, {
            method: "POST",
            body: JSON.stringify(params)
        });

        if (res && res.ok) {
            alert("Tavolo prenotato con successo!");
            window.location.reload();
        } else {
            alert("Errore durante la prenotazione del tavolo.");
        }
    } catch (error) {
        console.error("Errore di rete:", error);
    }
}

async function cancellaTavolo(roomId) {
    const select = document.getElementById(`select-${roomId}`);
    const tableId = select.value;

    if (!tableId || tableId === "null") {
        alert("Seleziona un tavolo dalla lista prima di cliccare su Elimina.");
        return;
    }

    if (!confirm("Sei sicuro di voler eliminare definitivamente questo tavolo?")) return;

    try {
        // Chiamata all'API che abbiamo sistemato prima
        const res = await apiCall(`/api/space/room/${roomId}/table/delete/${tableId}`, {
            method: "DELETE"
        });

        if (res && res.ok) {
            alert("Tavolo eliminato con successo.");
            window.location.reload();
        } else {
            alert("Errore durante l'eliminazione del tavolo.");
        }
    } catch (error) {
        console.error("Errore di rete:", error);
    }
}

async function deleteRoom(id) {
    // 1. Chiediamo conferma all'utente per evitare cancellazioni accidentali
    if (!confirm("Sei sicuro di voler eliminare questa stanza? Verranno eliminati anche tutti i tavoli al suo interno.")) {
        return;
    }

    try {
        // 2. Eseguiamo la chiamata DELETE al backend
        // L'URL deve corrispondere a quello definito nel tuo @DeleteMapping del Controller
        const res = await apiCall(`/api/space/room/delete/${id}`, {
            method: "DELETE"
        });

        // 3. Gestiamo la risposta
        if (res && res.ok) {
            alert("Stanza eliminata con successo!");
            // Ricarichiamo la pagina per aggiornare la lista delle stanze visibili
            window.location.reload();
        } else {
            // Se il server risponde con un errore (es. 403 o 404)
            const errorData = await res.json().catch(() => ({}));
            alert("Errore durante la cancellazione: " + (errorData.message || "Permessi insufficienti o stanza non trovata."));
        }
    } catch (err) {
        // Gestione errori di rete (es. server spento)
        console.error("Errore durante la chiamata API:", err);
        alert("Si è verificato un errore di rete. Riprova più tardi.");
    }
}

async function rifiutaRichiesta(requestId) {
    // Verifichiamo di avere l'ID dell'evento recuperato da autocompilaDaMessaggio
    if (!currentEventId) {
        alert("Errore: ID evento non trovato. Impossibile rifiutare la richiesta.");
        return;
    }

    const confermato = confirm("Sei sicuro di voler rifiutare questa richiesta? L'organizzatore riceverà una notifica.");
    if (!confermato) return;

    const params = getParams();
    const fullRequest = {
        ...params,
        eventId: currentEventId
    };

    try {
        const res = await apiCall("/api/space/room/reject", {
            method: "POST",
            body: JSON.stringify(fullRequest)
        });

        if (res && res.ok) {
            alert("Richiesta rifiutata con successo.");
            // Dopo il rifiuto, è meglio tornare alla dashboard dei messaggi
            window.location.href = "dashboard_libraio.html";
        } else {
            const errorMsg = await res.text();
            alert("Errore durante il rifiuto: " + errorMsg);
        }
    } catch (error) {
        console.error("Errore di rete durante il reject:", error);
        alert("Errore di connessione. Riprova più tardi.");
    }
}