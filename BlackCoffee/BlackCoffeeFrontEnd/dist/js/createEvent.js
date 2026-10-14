document.addEventListener("DOMContentLoaded", function() {
    // assegna UUID di default ai campi hidden

    const form = document.getElementById("formEvent");
    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));


    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const dateInput = form.dateEvent.value;

        const now = new Date();
        const day = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');

        if(!dateInput || dateInput < day) { 
            alert("La data dell'evento non può essere precedente alla data odierna.");
            return;
        }

        const data = {
            nameEvent: form.nameEvent.value,
            dateEvent: dateInput,
            typeEvent: form.typeEvent.value.toUpperCase(),
            description: form.description.value,
            spaceId: null

        };

        const response = await apiCall("/api/event/create", {
            method: "POST",
            body: JSON.stringify(data)
        });

        if (response && response.ok) {
            const createdEvent = await response.json();
            if (createdEvent && createdEvent.id){
                sessionStorage.setItem("currentEventId", createdEvent.id);
                sessionStorage.setItem("currentEventDate", createdEvent.date);
                sessionStorage.setItem("currentEventName", createdEvent.nomeEvento);
                sessionStorage.setItem("currentManagerName", loggedUser.name);
                sessionStorage.setItem("currentTypeEvent", createdEvent.tipo);
                window.location.href = "other_info_event.html";
            } else {
                alert("Errore durante la creazione dell'evento");
            }
        }
    });
});