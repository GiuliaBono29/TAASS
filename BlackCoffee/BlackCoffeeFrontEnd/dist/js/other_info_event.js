document.addEventListener("DOMContentLoaded", function() {
    // assegna UUID di default ai campi hidden

    const savedTypeEvent = sessionStorage.getItem("currentTypeEvent");

    if (savedTypeEvent === "CLUB") {
        (async () => {
            try {
                const clubsResponse = await apiCall("/api/club/all");
                if (clubsResponse && clubsResponse.ok) {
                    const clubs = await clubsResponse.json();
                    // Creare la terza card per selezionare il club
                    const card = document.createElement("div");
                    card.className = "card shadow-sm p-4 mt-4";
                    card.style.backgroundColor = "#FFF5E1";
                    card.style.border = "1px solid #D9A173";
                    card.innerHTML = `
                        <h2 class="mb-4 text-center">Seleziona Club</h2>
                        <div class="mb-3">
                            <label for="club" class="form-label">Club:</label>
                            <select id="club" name="club" class="form-select" required form="spaceForm">
                                <option value="" disabled selected>Seleziona un club</option>
                                ${clubs.map(club => `<option value="${club.id}">${club.nomeclub}</option>`).join('')}
                            </select>
                        </div>
                    `;
                    // Inserire dopo la seconda card
                    const container = document.querySelector(".col-md-8");
                    const secondCard = container.querySelectorAll(".card")[1];
                    container.insertBefore(card, secondCard.nextSibling);
                }
            } catch (error) {
                console.error("Errore nel caricamento dei club:", error);
            }
        })();
    }

    const form = document.getElementById("spaceForm");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const savedEventId = sessionStorage.getItem("currentEventId");
        const savedEventDate = sessionStorage.getItem("currentEventDate");
        const savedEventName = sessionStorage.getItem("currentEventName");
        const savedManagerName = sessionStorage.getItem("currentManagerName");


        if(!savedEventId){
            alert("Errore: id non trovato");
            return;
        }

        const description = document.getElementById("description").value;

        const data = {
            date: savedEventDate,
            slot: document.getElementById("slot").value,
            seats: parseInt(document.getElementById("seats").value),
            eventId: savedEventId,
            eventName: savedEventName,
            managerName: savedManagerName
        };

        // Gestire la creazione per eventi CLUB
        if (savedTypeEvent === "CLUB") {
            const clubSelect = document.getElementById("club");
            if (clubSelect && clubSelect.value) {
                const clubId = clubSelect.value;
                const clubEndpoint = `/api/event/create/${savedEventId}/club/${clubId}`;
                const clubResponse = await apiCall(clubEndpoint, {
                    method: "POST"
                });
                if (!(clubResponse && clubResponse.ok)) {
                    alert("Errore nella creazione dell'evento club");
                    return;
                }
            }
        }

        const endpoint = "/api/event/create/other_info?description=" + encodeURIComponent(description);

        const response = await apiCall(endpoint, {
            method: "POST",
            body: JSON.stringify(data)
        });

        

        if (response && response.ok) {
            window.location.href = "eventi.html";
        }
    });
});