document.addEventListener('DOMContentLoaded', function() {
  const form = document.getElementById('formConsigli');
  if (!form) return console.error("Form non trovato!");

  form.addEventListener('submit', async function(event) {
    event.preventDefault();

    const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
    if (!loggedUser) {
      alert("Devi essere loggato per scrivere un consiglio");
      return;
    }

    const commentData = {
      titolo: document.getElementById('titolInput').value,
      autore: document.getElementById('autore').value,
      genere: $('#genere').val() ? $('#genere').val().join(',') : '',
      voto: parseFloat(document.getElementById('voto-input').value) || 0,
      descrizione: document.getElementById('descrizione').value,
      copertina: ''
    };

    try {
      // Sostituisci il blocco try/fetch
      const response = await apiCall(`/api/comments/add?userId=${loggedUser.id}`, {
        method: 'POST',
        body: JSON.stringify(commentData)
      });
      if (response && response.ok) {
        window.location.href = "bacheca.html";
      }
    } catch (error) {
      console.error(error);
      alert("Errore di rete");
    }
  });
});
