function scrollButtons(offset) {
  const container = document.getElementById('buttonScroll');
  container.scrollBy({ left: offset, behavior: 'smooth' });
}


document.addEventListener("DOMContentLoaded", function() {
    const roomId = window.location.hash.substring(1);
    if (!roomId) {
        console.error("Nessun ID stanza specificato nell'URL");
        return;
    }

    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    const createBtn = document.getElementById("createTableBtn");
    if (loggedUser && loggedUser.role === "LIBRAIO"){
        createBtn.style.display = "block";

        // AGGIUNTA: Gestione del click per passare il roomId
        createBtn.addEventListener("click", function() {
            window.location.href = `crea_tavolo.html#${roomId}`;
        });
    } else {
        createBtn.style.display = "none";
    }

    const container = document.getElementById("tablesContainer");
    if (!container) return;

    // Fetch eventi dal backend
    apiCall(`/api/space/room/${roomId}/tables`)
        .then(res => res.json())
        .then(tables => {
            container.innerHTML = "";
            if (tables.length === 0) {
                container.innerHTML = "<p class='text-center mt-5'>Nessun tavolo presente in questa stanza.</p>";
                return;
            }
            tables.forEach(table => {
                // Crea il div per l'evento
                const tableDiv = document.createElement("div");
                tableDiv.className = "col-12 col-md-4";
                tableDiv.style.flex = "0 0 30%";

                let deleteButtonHtml = "";
                if (loggedUser && loggedUser.role === "LIBRAIO") {
                    deleteButtonHtml =
                        `<button class="btn btn-danger btn-sm mt-3" onclick="deleteTable('${table.id}', '${roomId}')">
                            Cancella Tavolo
                        </button>`;
                }

                tableDiv.innerHTML = `
                    <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative" style="background-color: #FFF5E1;">
                        <div class="col p-4 d-flex flex-column position-static">
                            <strong class="d-inline-block mb-2 text-primary">Tavolo #${table.number}</strong>
                            <p class="mb-auto">Capacità: ${table.capacity} persone</p>
                            ${deleteButtonHtml}
                        </div>
                    </div>
                `;
                container.appendChild(tableDiv);
            });
        })
        .catch(err => console.error("Errore caricamento stanze:", err));
});

//Funzione per cancellare il tavolo
function deleteTable(tableId, roomId) {
    if (!confirm("Sei sicuro di voler eliminare questo tavolo?")) return;

    apiCall(`/api/space/room/${roomId}/table/delete/${tableId}`, {
        method: "DELETE"
    })
        .then(res => {
            if (res && res.ok) {
                // Ricarica la pagina per aggiornare la lista
                window.location.reload();
            } else {
                alert("Errore durante la cancellazione");
            }
        })
        .catch(err => {
            console.error("Errore:", err);
            alert("Si è verificato un errore di rete");
        });
}
