function scrollButtons(offset) {
  const container = document.getElementById('buttonScroll');
  container.scrollBy({ left: offset, behavior: 'smooth' });
}

/**
 * Genera stelle in base al voto e le inserisce nell'elemento HTML con l'ID specificato.
 * @param {number} voto - Il numero di stelle piene (es. 4).
 * @param {number} max - Il numero massimo di stelle (default: 5).
 * @param {string} targetId - L'ID dell'elemento HTML dove inserire le stelle.
 */
function convertiVotiInStelle() {
  const elementiVoto = document.querySelectorAll('.voto');
  const fullStar = '★';
  const emptyStar = '☆';
  const halfStar = String.fromCodePoint(0x2BEA); // '\u{2BEA}'

  const re = /Voto:\s*(\d+)(?:\.(\d+))?\/(\d+)/;

  elementiVoto.forEach(el => {
    const match = el.textContent.match(re);
    if (!match) return;

    const intPart = parseInt(match[1], 10);
    const fracPart = match[2] ? parseInt(match[2], 10) : 0;
    const max = parseInt(match[3], 10);

    const votoFloat = match[2] ? parseFloat(`${match[1]}.${match[2]}`) : intPart;
    const full = Math.floor(votoFloat);
    const hasHalf = (votoFloat - full) >= 0.5;
    const empty = Math.max(0, max - full - (hasHalf ? 1 : 0));

    let stelle = fullStar.repeat(full) + (hasHalf ? halfStar : '') + emptyStar.repeat(empty);
    el.textContent = stelle;
    el.classList.add('stelle-grandi');
  });
}

// Esegui la conversione quando la pagina è pronta
document.addEventListener('DOMContentLoaded', convertiVotiInStelle);

