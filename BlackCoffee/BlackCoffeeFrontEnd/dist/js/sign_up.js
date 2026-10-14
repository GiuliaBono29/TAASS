document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('formRegistrazione');

    // Funzione di submit separata
    async function submitHandler(event) {
        event.preventDefault(); // blocca submit automatico

        let formValid = true;

        // Validazioni
        const firstName = document.getElementById('nameInput');
        const firstNameError = document.getElementById('nameInputError');
        if (!firstName.value.trim()) {
            firstNameError.textContent = "Il nome è obbligatorio";
            firstNameError.classList.remove('hidden');
            formValid = false;
        } else {
            firstNameError.classList.add('hidden');
        }

        const lastName = document.getElementById('surnameInput');
        const lastNameError = document.getElementById('surnameInputError');
        if (!lastName.value.trim()) {
            lastNameError.classList.remove('hidden');
            formValid = false;
        } else {
            lastNameError.classList.add('hidden');
        }

        const email = document.getElementById('emailInput');
        const emailError = document.getElementById('emailInputError');
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!email.value.trim() || !emailPattern.test(email.value.trim())) {
            emailError.classList.remove('hidden');
            formValid = false;
        } else {
            emailError.classList.add('hidden');
        }

        const password = document.getElementById('passwordInput');
        const passwordError = document.getElementById('passwordInputError');
        const confirmPassword = document.getElementById('passwordInputDue');
        const confirmPasswordError = document.getElementById('passwordInputDueError');

        if (!password.value.trim()) {
            passwordError.classList.remove('hidden');
            formValid = false;
        } else {
            passwordError.classList.add('hidden');
        }

        if (!confirmPassword.value.trim()) {
            confirmPasswordError.classList.remove('hidden');
            formValid = false;
        } else if (password.value !== confirmPassword.value) {
            confirmPasswordError.textContent = "Le password non corrispondono";
            confirmPasswordError.classList.remove('hidden');
            formValid = false;
        } else {
            confirmPasswordError.classList.add('hidden');
        }

        if (password.value.trim().length < 8) {
            alert("La password deve essere di almeno 8 caratteri.");
            return;
        }

        if (!formValid) return; // esce se invalidi

        // Crea oggetto utente
        const user = {
            name: firstName.value.trim(),
            surname: lastName.value.trim(),
            tel: document.getElementById('telInput').value.trim() || null,
            email: email.value.trim(),
            password: password.value.trim(),
            confPassword: confirmPassword.value.trim()
        };

        try {
            const response = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(user)
            });

            if (response.ok) {

                const savedUser = await response.json();

                sessionStorage.setItem("loggedUser", JSON.stringify(savedUser))

                alert("Registrazione completata!");
                window.location.href = "index.html"; // redirect
            } else if (response.status === 400) {

                const errorMSG = await response.text();
                alert("errore: " + errorMSG);

            } else {
                alert("Errore durante la registrazione");
            }
        } catch (error) {
            console.error('Errore:', error);
            alert("Errore di rete");
        }
    }

    // Rimuove eventuali listener precedenti
    form.removeEventListener('submit', submitHandler);
    // Aggiunge il listener corretto
    form.addEventListener('submit', submitHandler);
});

