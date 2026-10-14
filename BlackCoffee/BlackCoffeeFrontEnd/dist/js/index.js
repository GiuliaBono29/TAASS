document.addEventListener("DOMContentLoaded", () => {

    const user = JSON.parse(sessionStorage.getItem("loggedUser"));

    const loginBtn = document.getElementById("loginBtn");
    const signupBtn = document.getElementById("signupBtn");
    const logoutBtn = document.getElementById("logoutBtn");

    if (user) {
    // utente loggato
        loginBtn.classList.add("d-none");
        signupBtn.classList.add("d-none");
        logoutBtn.classList.remove("d-none");
    } else {
    // utente non loggato
        loginBtn.classList.remove("d-none");
        signupBtn.classList.remove("d-none");
        logoutBtn.classList.add("d-none");
    }

    logoutBtn.addEventListener("click", () => {
    sessionStorage.removeItem("loggedUser");
    window.location.reload();
    });
});