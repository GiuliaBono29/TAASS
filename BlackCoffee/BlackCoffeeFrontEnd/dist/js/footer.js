document.addEventListener("DOMContentLoaded", function() {
    const footerNav = document.getElementById("footer-nav");
    if (!footerNav) return;

    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));

    // link comuni
    const commonLinks = [
        { name: "Home", url: "index.html" },
        { name: "Eventi", url: "eventi.html" },
        { name: "Club", url: "club.html" },
        { name: "Stanze", url: "stanze.html" },
        { name: "Consigli", url: "bacheca.html" }
    ];

    let footerContent = "";

    // 3. Costruisci l'HTML per i link comuni
    commonLinks.forEach((link, index) => {
        // Aggiungiamo la classe border-end solo se non è l'ultimo o se seguiranno altri bottoni
        const isLast = (index === commonLinks.length - 1) && (!loggedUser || loggedUser.role !== "LIBRAIO");
        const borderClass = isLast ? "" : "border-end border-dark";

        footerContent += `<a href="${link.url}" class="nav-link text-dark px-5 ${borderClass}">${link.name}</a>`;
    });

    // 4. Aggiungi il bottone speciale se l'utente è LIBRAIO
    if (loggedUser && (loggedUser.role === "ROLE_LIBRAIO" || loggedUser.role === "LIBRAIO")) {
        // Rimuoviamo il bordo dall'ultimo link comune precedente e lo aggiungiamo qui
        footerContent = footerContent.replace(/px-5">Consigli<\/a>$/, 'px-5 border-end border-dark">Consigli</a>');
        footerContent += `<a href="../messages.html" class="nav-link text-dark px-5">Messaggi</a>`;
    }

    footerNav.innerHTML = footerContent;
});