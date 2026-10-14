// apiClient.js

async function apiCall(endpoint, options = {}) {
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    const token = loggedUser ? loggedUser.accessToken : null;

    // Impostazioni predefinite degli header
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    // Aggiunge il token se presente
    if (token) {
        defaultHeaders['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };

    try {
        const response = await fetch(endpoint, config);

        // GESTIONE ERRORI GLOBALE
        if (response.status === 401) {
            // Token scaduto o non valido
            alert("Sessione scaduta. Verrai reindirizzato al login.");
            sessionStorage.removeItem("loggedUser");
            window.location.href = "login.html";
            return null;
        }

        if (response.status === 403) {
            alert("Accesso negato: non hai i permessi necessari.");
            return null;
        }

        return response;
    } catch (error) {
        console.error("Errore di rete:", error);
        throw error;
    }
}