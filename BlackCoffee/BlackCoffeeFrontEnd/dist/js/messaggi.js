// messaggi.js

document.addEventListener('DOMContentLoaded', () => {
    const messagesContainer = document.getElementById('messagesContainer');
    const filterButtons = document.querySelectorAll('input[name="filterStatus"]');
    const deleteMessagesBtn = document.getElementById('btnDeleteMessages');

    // Gestione del click sul pulsante "Cancella Messaggi"
    deleteMessagesBtn.addEventListener('click', async () => {
        const confermato = confirm("Sei sicuro di voler cancellare tutti i messaggi delle richieste di prenotazione delle stanze?");
        if (!confermato) return;

        try {
            const response = await apiCall('/api/space/room/message/delete', { method: 'DELETE' });
            if (response && response.ok) {
                alert("Messaggi cancellati con successo.");
                loadMessages(); // Ricarica i messaggi dopo la cancellazione
            } else {
                alert("Errore durante la cancellazione dei messaggi.");
            }
        } catch (error) {
            console.error("Errore nella cancellazione dei messaggi:", error);
            alert("Errore di rete o del server.");
        }
    });

    // 1. Caricamento iniziale (Default: Tutti i messaggi)
    loadMessages();

    // 2. Gestione dei filtri (Bottoni PENDING, ACCEPTED, REJECTED)
    filterButtons.forEach(radio => {
        radio.addEventListener('change', (e) => {
            const id = e.target.id;
            if (id === 'btnAll') {
                loadMessages(); // Carica tutti i messaggi
            } else {
                // Otteniamo il valore del filtro (es. "PENDING")
                const status = id.replace('btn', '').toUpperCase(); 
                loadMessages(status, deleteMessagesBtn);
            }
        });
    });

    /**
     * Funzione principale per recuperare i messaggi
     * @param {string|null} action - Il filtro da applicare (opzionale)
     */
    async function loadMessages(action = null, btn = null) {
        let endpoint = '/api/space/room/request';
        if (action) {
            endpoint += `/${action}`;
        }

        try {
            const response = await apiCall(endpoint, { method: 'GET' });

            if (response) {
                // Gestione specifica per il 204 No Content
                if (response.status === 204) {
                    renderMessages([]); // Passiamo una lista vuota
                    return;
                }

                if (response.ok) {
                    const data = await response.json();
                    if (action === 'REJECTED') {
                        deleteMessagesBtn.style.display = 'inline-block';
                    } else {
                        deleteMessagesBtn.style.display = 'none';
                    }
                    renderMessages(data);
                } else {
                    messagesContainer.innerHTML = `
                        <div class="alert alert-warning text-center">
                            Errore nel caricamento: ${response.status}
                        </div>`;
                }
            }
        } catch (error) {
            console.error("Errore nel caricamento messaggi:", error);
            messagesContainer.innerHTML = `<div class="alert alert-danger">Errore di rete o del server.</div>`;
        }
    }

    /**
     * Renderizza i messaggi nel container HTML
     * @param {Array} messages - Lista di oggetti messaggio
     */
    function renderMessages(messages) {
        messagesContainer.innerHTML = '';

        if (!messages || messages.length === 0) {
            messagesContainer.innerHTML = '<p class="text-center mt-5">Nessun messaggio presente in questa categoria.</p>';
            return;
        }

        messages.forEach(msg => {
            // Creazione dinamica del div messaggio
            const messageRow = document.createElement('div');
            messageRow.className = "card mb-3 shadow-sm border-0";
            
            // Rende cliccabile solo se PENDING
            if (msg.state === 'PENDING') {
                messageRow.style.cursor = "pointer";
                messageRow.onclick = () => {
                    // Reindirizza a stanze.html passando l'ID come parametro query
                    window.location.href = `stanze.html?id=${msg.id}`;
                };
            }
            
            // Definiamo il contenuto del messaggio
            // Nota: Adattare le chiavi (msg.nomeEvento, ecc.) ai nomi reali che arrivano dal tuo DB/API
            messageRow.innerHTML = `
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="card-title mb-1">Evento: <strong>${msg.eventName || 'N/A'}</strong></h5>
                        <p class="mb-0 text-muted">Manager: ${msg.managerName || 'N/A'}</p>
                    </div>
                    <div class="text-center">
                        <p class="mb-0 fw-bold">Data: ${formatDate(msg.date)}</p>
                        <p class="mb-0">Slot: ${msg.slot || 'N/A'}</p>
                    </div>
                    <div class="text-end">
                        <span class="badge bg-dark px-3 py-2">Posti richiesti: ${msg.seats || 0}</span>
                        <p class="mb-0 mt-2">Stato: ${msg.state || 'N/A'}</p>
                    </div>
                </div>
            `;

            messagesContainer.appendChild(messageRow);
        });
    }

    /**
     * Helper per formattare la data in modo leggibile
     */
    function formatDate(dateString) {
        if (!dateString) return "Data non disponibile";
        const date = new Date(dateString);
        return date.toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }
});