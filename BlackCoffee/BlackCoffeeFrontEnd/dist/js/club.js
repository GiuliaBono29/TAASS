document.addEventListener("DOMContentLoaded", function() {
    
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    const container = document.getElementById("clubsContainer"); // usa lo stesso div del tuo HTML
    const btnClubCreate = document.getElementById("createClubButton");
    if (!container) return;

    if (loggedUser && loggedUser.role === "ORGANIZZATORE"){
        btnClubCreate.style.display = "block"
    } else {
        btnClubCreate.style.display = "none"
    }

    // Fetch di tutti i club dal backend
    apiCall("/api/club/all")
        .then(res => res.json())
        .then(clubs => {
            container.innerHTML = ""; // pulisce il contenitore
            clubs.forEach(club => {
                // Crea un div per ogni club
                const clubDiv = document.createElement("div");
                clubDiv.className = "col-12 col-md-4";
                clubDiv.style.flex = "0 0 30%";

                const numIscritti = club.iscritti ? club.iscritti.length : "error null";

                clubDiv.innerHTML = `
                    <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative" style="background-color: #FFF5E1; cursor: pointer;">
                        <div class="col p-4 d-flex flex-column position-static">
                            <p><strong>${club.nomeclub}</strong></p>
                            <p><strong>Iscritti:</strong> ${numIscritti}</p>
                            <p>${club.description || "N/D"}</p>
                        </div>
                    </div>
                `;

                // Rende il div cliccabile per andare alla pagina info_club.html
                clubDiv.addEventListener("click", () => {
                    window.location.href = `info_club.html#${club.id}`;
                });

                container.appendChild(clubDiv);
            });
        })
        .catch(err => {
            console.error("Errore caricamento club:", err);
            container.innerHTML = "<p>Errore nel caricamento dei club</p>";
        });
});
