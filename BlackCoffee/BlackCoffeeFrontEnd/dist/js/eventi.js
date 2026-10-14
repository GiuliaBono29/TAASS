function scrollButtons(offset) {
  const container = document.getElementById('buttonScroll');
  container.scrollBy({ left: offset, behavior: 'smooth' });
}


document.addEventListener("DOMContentLoaded", function() {
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    const createBtn = document.getElementById("createEventBtn");

    if (createBtn) {
        if (loggedUser && loggedUser.role === "ORGANIZZATORE") {
            createBtn.style.display = "block";
        } else {
            createBtn.style.display = "none";
        }
    }

    const container = document.getElementById("eventsContainer");

    if (!container) return;

    // Fetch eventi dal backend
    apiCall("/api/event/all")
        .then(res => res.json())
        .then(events => {
            container.innerHTML = ""; // pulisce prima
            events.forEach(event => {


                if (event.stato === "PUBBLICATO" && (!loggedUser ||loggedUser.role !== "ORGANIZZATORE")) {
                    // Converte la data dal backend in formato leggibile
                    eventView(event, container, loggedUser);
                
                } else if (loggedUser && loggedUser.role === "ORGANIZZATORE") {
                    // Mostra tutti gli eventi agli organizzatori
                    eventView(event, container, loggedUser);
                }
            });
        })
        .catch(err => console.error("Errore caricamento eventi:", err));
});

function eventView(event, container, loggedUser) {
    let eventDate = new Date(event.date);
    let formattedDate = isNaN(eventDate.getTime())
        ? "Data non disponibile"
        : eventDate.toLocaleDateString('it-IT');

    // Crea il div per l'evento
    const eventDiv = document.createElement("div");
    eventDiv.className = "col-12 col-md-4";
    eventDiv.style.flex = "0 0 30%";

    // Imposta il colore di sfondo in base al tipo di evento e stato
    let backgroundColor = "#FFF5E1"; // Default color

    if (loggedUser && loggedUser.role === "ORGANIZZATORE") {
        switch (event.stato) {
            case "CANCELLATO":
                backgroundColor = "#FFCCCC"; // Rosso
                break;
            case "PUBBLICATO":
                backgroundColor = "#CCFFCC"; // Verde
                break;
            case "ATTESA_PUBB":
                backgroundColor = "#CCE5FF"; // Azzurro
                break;
            case "IN_CREAZIONE":
                backgroundColor = "#D3D3D3"; // Grigio
                break;
            case "ATTESA_CATERING":
            case "ATTESA_STANZA":
                backgroundColor = "#FFFFCC"; // Giallo
                break;
        }
    } else if (event.tipo === "CLUB") {
        backgroundColor = "#EED9C4"; // Slightly darker color for CLUB events
    }

    // Listener per il click
    eventDiv.style.cursor = "pointer";
    eventDiv.addEventListener("click", () => {
        // reindirizza alla pagina info_evento.html passando l'id dell'evento
        console.log("ID evento cliccato:", event.id);

        if (loggedUser){
            window.location.href = `info_evento.html#${event.id}`;
        } else {
            alert("Devi essere loggato per accedere all'evento.");
        }
        
    });

    eventDiv.innerHTML = `
                        <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative" style="background-color: ${backgroundColor};">
                            <div class="col p-4 d-flex flex-column position-static">
                                <p class="mb-auto"><strong>${event.nomeEvento || ""}</strong></p>
                                <p class="mb-auto">Data: ${formattedDate}</p>
                                <p class="mb-auto">${event.descrizione || ""}</p>
                            </div>
                        </div>
                    `;
    container.appendChild(eventDiv);
}

