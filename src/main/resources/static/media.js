const API_BASE = "http://localhost:8080/api/media";
const USER_ID = 1; // Temporar, pana faci sistemul de Login

document.addEventListener('DOMContentLoaded', () => {
    fetchMedia();

    document.getElementById('genreFilter').addEventListener('change', (e) => {
        if(e.target.value === 'all') fetchMedia();
        else fetchByGenre(e.target.value);
    });
});

async function fetchMedia() {
    const res = await fetch(`${API_BASE}/all`);
    const data = await res.json();
    renderCards(data);
}

async function fetchByGenre(genre) {
    const res = await fetch(`${API_BASE}/search?genre=${genre}`);
    const data = await res.json();
    renderCards(data);
}

// În media.js
function renderCards(items) {
    const container = document.getElementById('mediaContainer'); // Verifică dacă ID-ul din HTML e 'mediaContainer' sau 'media-list'
    if (!container) return;

    container.innerHTML = items.map(m => `
        <div class="col-md-3 mb-4">
            <div class="card h-100 card-dreamy text-white">
                <img src="${m.posterUrl}" class="card-img-top" style="height: 300px; object-fit: cover;">
                <div class="card-body d-flex flex-column">
                    <h5 class="fw-bold text-truncate">${m.title}</h5>
                    <div class="mb-3">
                         <span class="badge" style="background-color: #7b2cbf;">${m.genre || 'Unknown'}</span>
                         <small class="text-white-50 ms-2">${m.releaseYear || '????'}</small>
                    </div>

                    <button onclick="addToWatchlist(${m.mediaId})" class="btn btn-dreamy w-100 mt-auto">Add to Watchlist</button>
                </div>
            </div>
        </div>
    `).join('');
}

async function addToWatchlist(mediaId) {
    const res = await fetch(`http://localhost:8080/api/watchlist/add?userId=${USER_ID}&mediaId=${mediaId}`, {
        method: 'POST'
    });
    if(res.ok) alert("Added to your dream list! ✨");
}
async function removeFromWatchlist(id) {
    if(confirm('Are you sure you want to remove this?')) { // Fallback simplu dacă nu merge SweetAlert
        try {
            const res = await fetch(`http://localhost:8080/api/watchlist/remove/${id}`, { method: 'DELETE' });
            if (res.ok) {
                location.reload();
            } else {
                alert("Error removing item.");
            }
        } catch (error) {
            console.error(error);
        }
    }
}