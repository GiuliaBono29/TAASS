document.addEventListener("DOMContentLoaded", function() {


    const form = document.getElementById("formRoom");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

        const data = {
            name: form.nameRoom.value,
            capacity: form.capacityRoom.value
        };

        try {
            const response = await apiCall("/api/space/create", {
                method: "POST",
                body: JSON.stringify(data)
            });


            if (response && response.ok) {
                window.location.href = "stanze.html";
            } else if (response) {
                alert("Errore durante la creazione della stanza");
            }
        } catch (error) {
            console.error("Errore critico: " + error);
        }

    });
});