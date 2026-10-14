const loggedUserRaw = sessionStorage.getItem("loggedUser");

if (!loggedUserRaw) {
    console.warn("Utente non autenticato. Le notifiche WebSocket non saranno attivate.");
} else {

    const loggedUserData = JSON.parse(loggedUserRaw);
    const token = loggedUserData.accessToken;
    const currentUserId = loggedUserData.userId;
    const userRoles = loggedUserData.role;

    if (!token) {
        console.warn("Token di accesso non trovato. Le notifiche WebSocket non saranno attivate.");
    } else {

        console.log("Utente autenticato. Inizializzazione delle notifiche WebSocket per l'utente ID:", currentUserId);

        // 1. Puntiamo all'URL gestito da Nginx
        const socket = new SockJS('http://localhost/ws-notifications/');
        const stompClient = Stomp.over(socket);

        const headers = {
            'Authorization': 'Bearer ' + token
        };

        stompClient.connect(headers, (frame) => {
            console.log('Connesso al WebSocket: ' + frame);

            // 2. Iscrizione al canale BROADCAST (per tutti)
            stompClient.subscribe('/topic/all', (notification) => {
                const data = JSON.parse(notification.body);
                alert("Notifica Globale: " + data.message);
            });

            // 3. Iscrizione al canale PRIVATO (User-specific)
            // 'currentUserId' deve essere l'ID dell'utente loggato
            stompClient.subscribe(`/user/queue/notifications`, (notification) => {
                const data = JSON.parse(notification.body);
                console.log("Notifica Privata Ricevuta:", data);
                alert("Notifica Privata: " + data.message);
            });

            stompClient.subscribe(`/topic/role/${userRoles}`, (notification) => {
                const data = JSON.parse(notification.body);
                alert("Notifica per " + userRoles + ": " + data.message);
            });
        }, (error) => {
            console.error('Errore di connessione:', error);
        });
    }
}

