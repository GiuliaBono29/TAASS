document.addEventListener("DOMContentLoaded", function() {
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    const btnMiePrenotazioni = document.getElementById("btnLeMiePrenotazioni");
    const btnCaricaStanze = document.getElementById("btnCaricaStanze");


    if (loggedUser){
        btnMiePrenotazioni.style.display = "block";
        btnCaricaStanze.style.display = "none";
    } else {
        btnMiePrenotazioni.style.display = "none";
        btnCaricaStanze.style.display = "none";
    }

    // Click su "Le mie prenotazioni"
    if (btnMiePrenotazioni) {
        btnMiePrenotazioni.addEventListener("click", async function() {
            await caricaLeMiePrenotazioni();

            btnMiePrenotazioni.style.display = "none";
            btnCaricaStanze.style.display = "block";
        });
    }

    // Click su "Torna alle stanze"
    if (btnCaricaStanze) {
        btnCaricaStanze.addEventListener("click", async function() {
            if (typeof caricaStanze === "function") {
                await caricaStanze();

                btnCaricaStanze.style.display = "none";
                btnMiePrenotazioni.style.display = "block";
            } else {
                console.error("Errore: la funzione caricaStanze non è stata trovata.");
            }
        });
    }

    // Controlla se dobbiamo mostrare le prenotazioni automaticamente
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('view') === 'prenotazioni' && loggedUser) {
        btnLeMiePrenotazioni.click();
    }
});

async function caricaLeMiePrenotazioni() {
    const container = document.getElementById("roomsContainer");

    try {
        const res = await apiCall("/api/space/reservation", { method: "GET" });

        if (!res || !res.ok) {
            alert("Errore nel recupero delle prenotazioni.");
            return;
        }

        if (res.status === 204){
            container.innerHTML = `
            <div class="alert alert-info w-100 text-center">
            Non hai prenotazioni attive
            </div>
            `;

            return;
        }

        if (res.ok) {
            const prenotazioni = await res.json();
            container.innerHTML = ""; // Puliamo la griglia

            prenotazioni.forEach(p => {
                const card = createReservationCard(p);
                container.appendChild(card);
            });
        } else {
            alert("Errore nel caricamento delle prenotazioni");
        }

    } catch (err) {
        console.error("Errore:", err);
    }
}

function createReservationCard(p) {
    const div = document.createElement("div");
    div.className = "col-12 col-md-6 mb-4";

    div.innerHTML = `
        <div class="card shadow-sm h-100" style="background-color: #E3F2FD; border: 1px solid #90CAF9;">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <h5 class="card-title text-primary">${p.roomName}</h5>
                    <span class="badge bg-primary">${p.slot}</span>
                </div>
                <p class="mb-1"><strong>Tavolo:</strong> ${p.table.number}</p>
                <p class="mb-1"><strong>Capacità tavolo:</strong> ${p.table.capacity} persone</p>
                <p class="mb-1"><strong>Posti prenotati:</strong> ${p.seats} persone</p>
                <p class="mb-1"><strong>Data:</strong> ${p.date}</p>
                <hr>
                <div class="d-flex gap-2">
                    <button class="btn btn-sm btn-warning w-100" onclick="modificaPrenotazione('${p.reservationId}')">
                        Modifica
                    </button>
                    <button class="btn btn-sm btn-danger w-100" onclick="cancellaPrenotazione('${p.reservationId}')">
                        Cancella
                    </button>
                </div>
            </div>
        </div>
    `;
    return div;
}

// --- AZIONI SULLE PRENOTAZIONI ---

async function cancellaPrenotazione(reservationId) {
    if (!confirm("Sei sicuro di voler eliminare questa prenotazione?")) return;

    try {
        const res = await apiCall(`/api/space/reservation/delete/${reservationId}`, {
            method: "DELETE"
        });

        if (res && res.ok) {
            alert("Prenotazione cancellata.");
            await caricaLeMiePrenotazioni(); // Ricarica la lista aggiornata
        } else {
            alert("Impossibile cancellare la prenotazione.");
        }
    } catch (err) {
        console.error("Errore:", err);
    }
}

function modificaPrenotazione(reservationId) {
    // Reindirizza a una pagina di modifica o apre un modal
    window.location.href = `modifica_prenotazione.html?id=${reservationId}`;
}