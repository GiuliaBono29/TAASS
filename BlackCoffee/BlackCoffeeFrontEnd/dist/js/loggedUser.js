// Funzione di utilità per decodificare il JWT (se non l'hai già)
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(window.atob(base64));
    } catch (e) {
        return null;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    let token = urlParams.get("token") || new URLSearchParams(window.location.hash.substring(1)).get("token");

    // --- CASO 1: LOGIN TRAMITE OAUTH2 (Redirect con token nell'URL) ---
    if (token) {
        const payload = parseJwt(token);
        if (payload) {
            const oauthUser = {
                accessToken: token, // Salva il token dentro l'oggetto!
                userId: payload.sub,
                role: payload.role,
                name: payload.name
            };

            sessionStorage.setItem("loggedUser", JSON.stringify(oauthUser));
            // Pulisce l'URL dai parametri del token
            window.history.replaceState({}, document.title, window.location.pathname);

            renderLoggedButtons(oauthUser);
            return;
        }
    }

    // --- CASO 2: PAGE LOAD NORMALE (Controlla sessione esistente) ---
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    renderLoggedButtons(loggedUser);
});

function renderLoggedButtons(loggedUser) {
    const buttonsContainer = document.getElementById("out-button");
    if (!buttonsContainer) return;

    if (!loggedUser) {
        // Utente NON loggato
        buttonsContainer.innerHTML = `
            <a href="login.html" class="btn btn-outline-dark me-2">Login</a>
            <a href="sign_up.html" class="btn btn-outline-dark me-2">Sign-up</a>
        `;
    } else {
        // Utente loggato
        buttonsContainer.innerHTML = `
            <span class="fw-bold me-2">Ciao, ${loggedUser.name}</span>
            <button id="logoutBtn" class="btn btn-outline-dark ms-2">Logout</button>
        `;

        document.getElementById("logoutBtn").addEventListener("click", () => {
            sessionStorage.removeItem("loggedUser");
            // Se avevi salvato il token separatamente, rimuovi anche quello
            sessionStorage.removeItem("accessToken");
            window.location.href = "index.html"; // O reload
        });
    }
}