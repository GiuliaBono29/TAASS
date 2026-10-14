document.addEventListener("DOMContentLoaded", function() {
    // assegna UUID di default ai campi hidden

    const form = document.getElementById("formClub");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            nameClub: form.nameClub.value,
            description: form.description.value,
            managerId: form.managerId.value
        };

        const response = await apiCall("/api/club/create", {
            method: "POST",
            body: JSON.stringify(data)
        });

        if (response && response.ok) {
            window.location.href = "club.html";
        }
    });
});