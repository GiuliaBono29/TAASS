document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('formLogin');

    form.addEventListener('submit', async function(event){
        event.preventDefault();

        const email = document.getElementById('emailInput').value.trim();
        const password = document.getElementById('passwordInput').value.trim();

        if (!email || !password) {
            alert("inserisci mail e password");
            return;
        }

        const userData = {email, password};

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                const user = await response.json();

                sessionStorage.setItem("loggedUser", JSON.stringify(user));
                alert(`Benvenuto, ${user.name}!`);
                window.location.href = "index.html";
            } else {
                const message = await response.text();
                alert("Errore: " + message);
            }
        } catch (error) {
            console.error("errore: ", error);
            alert("errore di rete")
        }
    });
});