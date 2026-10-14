document.addEventListener("DOMContentLoaded", function() {
    const clubId = window.location.hash.substring(1);
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    if (!clubId) {
        alert("Club non specificato!");
        return;
    }

    const container = document.getElementById("clubContainer");

    apiCall(`/api/club/${clubId}`)
        .then(res => {
            if (!res || !res.ok) throw new Error("Club non trovato");
            return res.json();
        })
        .then(club => {
            if (!club) return;

            // 1. Definiamo e chiamiamo la funzione per i dettagli
            renderClubDetails(club, container);

            // 2. Chiamiamo i bottoni passando l'oggetto event completo
            renderButtons(loggedUser, club);
        })
        .catch(err => {
            console.error(err);
            container.innerHTML = "<p>Errore nel caricamento del club</p>";
        });
});

function renderClubDetails(club, container) {

    const numIscritti = club.iscritti ? club.iscritti.length : "error null";

    container.innerHTML = `
        <h2>${club.nomeclub || "Dettagli Club"}</h2>
        <p><strong>Descrizione:</strong> ${club.description || "error null"}</p>
        <p><strong>Numero Iscritti:</strong> ${numIscritti}</p>
    `;
}

function renderButtons(loggedUser, club) {
    const buttonsContainer = document.getElementById("button_view");
    if (!buttonsContainer) return;

    const clubId = club.id;

    if (loggedUser && loggedUser.role === "ORGANIZZATORE") {
        buttonsContainer.innerHTML = `
            <a href="modifica_club.html?id=${clubId}" class="btn btn-primary">Modifica Club</a>
            <button id="deleteBtn" class="btn btn-danger">Cancella</button>
        `;
        document.getElementById("deleteBtn").onclick = () => cancellaClub(clubId);
    }

    else {

        const currentUserId = loggedUser ? (loggedUser.userId) : null;
        const isIscritto = currentUserId && club.iscritti && club.iscritti.includes(currentUserId);

        if (isIscritto) {
            buttonsContainer.innerHTML = `<button id="rmBtn" class="btn btn-outline-danger">Disiscrivimi</button>`;
            document.getElementById("rmBtn").onclick = () => gestisciIscrizione(clubId, "rmUser");
        } else {
            buttonsContainer.innerHTML = `<button id="addBtn" class="btn btn-primary">Iscrivimi!</button>`;
            document.getElementById("addBtn").onclick = () => gestisciIscrizione(clubId, "addUser");
        }
    }
}

function gestisciIscrizione(clubId, action) {
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    // Se non loggato, avvisa e reindirizza
    if (!loggedUser) {
        alert("Devi essere loggato per iscriverti ai club!");
        window.location.href = "login.html";
        return;
    }

    apiCall(`/api/club/sub/${action}/${clubId}`, { method: "PUT" })
        .then(res => {
            if (res && res.ok) {
                alert(action === "addUser" ? "Iscrizione completata!" : "Disiscrizione completata.");
                location.reload();
            } else {
                alert("Errore nell'operazione.");
            }
        })
        .catch(err => console.error(err));
}

function cancellaClub(clubId) {
    if (!confirm("Sei sicuro?")) return;
    apiCall(`/api/club/delete/${clubId}`, { method: "DELETE" })
        .then(res => (res && res.ok) ? window.location.href = "club.html" : alert("Errore"))
        .catch(err => console.error(err));
}