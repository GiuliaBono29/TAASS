# Black Coffee – Progetto TAAS

Progetto sviluppato per il corso di **Tecniche e Architetture Avanzate per lo Sviluppo del Software (TAAS)** della Laurea Magistrale in Informatica, Università di Torino. Il progetto è la piattaforma web di **Black Coffee**, un bar libreria in cui i clienti possono bere un caffè mentre leggono. Il negozio organizza eventi (di club e non), permette di prenotare spazi e offre una bacheca dei consigli per far comunicare i clienti. Il sistema è progettato con un'architettura a **microservizi** secondo i principi del **Domain-Driven Design**. Il progetto è stato sviluppato in gruppo per il corso di TAASS, insieme a un collaboratore.

## Tecnologie usate

* Java, Spring Boot, Spring Security
* JWT (jjwt) e OAuth2 (Google, GitHub)
* MySQL
* RabbitMQ
* WebSocket
* Nginx (API Gateway)
* Docker, Docker Compose

## Architettura

Ogni servizio gira in un container Docker isolato, collegato agli altri tramite una rete comune. Nginx è l'unico punto di accesso per il frontend e smista le richieste ai microservizi. La comunicazione tra i servizi avviene via REST e, per le operazioni asincrone, tramite RabbitMQ.

### Microservizi implementati

|Servizio|Funzione|
|-|-|
|**AuthService**|Autenticazione e autorizzazione: utenti, ruoli, JWT firmati (HMAC-SHA256), password con hash e salt, accesso con OAuth2|
|**EventService**|Creazione e gestione degli eventi e dei loro tipi|
|**ClubService**|Creazione e gestione dei club|
|**SpaceService**|Gestione di stanze, tavoli e relative prenotazioni|
|**NotificationService**|Invio di notifiche in tempo reale agli utenti online tramite WebSocket|
|**LogService**|Registrazione delle operazioni svolte dagli utenti sulla piattaforma|

### Microservizi progettati ma non implementati

KitchenService, CateringService, BoardService (bacheca dei consigli), InventoryService, PaymentService, BookService.

