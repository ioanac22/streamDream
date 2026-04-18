const API_MEDIA = "http://localhost:8080/api/media";
const API_USERS = "http://localhost:8080/api/users";

// Variabilă globală pentru Editare
let editingMediaId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadUsers();
    loadLibrary();
});

// --- 1. HANDLE FORM (ADD & UPDATE & DUPLICATE CHECK) ---

async function handleFormSubmit() {
    // 1. Luăm datele din input-uri
    const title = document.getElementById('apiTitle').value;
    const genre = document.getElementById('apiGenre').value;
    const releaseYear = document.getElementById('apiYear').value;
    const posterUrl = document.getElementById('apiPoster').value;

    // 2. Validare simplă
    if (!title || !genre || !releaseYear) {
        return Swal.fire('Error', 'Please fill all fields!', 'error');
    }

    const payload = { title, genre, releaseYear, posterUrl };

    try {
        let url, method;

        // 3. Decidem: E Adăugare sau Editare?
        if (editingMediaId) {
            url = `${API_MEDIA}/update/${editingMediaId}`;
            method = 'PUT';
        } else {
            url = `${API_MEDIA}/add`;
            method = 'POST';
        }

        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            // --- SUCCES ---
            Swal.fire({
                title: editingMediaId ? 'Updated!' : 'Added!',
                text: 'Database updated successfully.',
                icon: 'success',
                background: '#240046', color: '#fff',
                timer: 1500, showConfirmButton: false
            });

            resetForm();    // Golim formularul
            loadLibrary();  // Reîncărcăm tabelul
            loadStats();    // Actualizăm numerele
        } else {
            // --- EROARE (AICI PRINDEM DUPLICATUL) ---
            const errorMessage = await res.text(); // Citim mesajul trimis de Java ("Movie already exists...")

            Swal.fire({
                title: 'Oops...',
                text: errorMessage, // Afișăm exact ce zice serverul
                icon: 'warning',
                background: '#240046', color: '#fff',
                confirmButtonColor: '#d33'
            });
        }
    } catch (e) {
        console.error(e);
        Swal.fire('Error', 'Server connection failed', 'error');
    }
}

// --- 2. EDIT LOGIC ---

function startEdit(id, title, genre, year, poster) {
    editingMediaId = id; // Ținem minte ID-ul

    // Punem datele în formular
    document.getElementById('apiTitle').value = title;
    document.getElementById('apiGenre').value = genre;
    document.getElementById('apiYear').value = year;
    document.getElementById('apiPoster').value = poster;

    // Arătăm poza
    const img = document.getElementById('previewImg');
    img.src = poster;
    img.style.display = 'block';

    // Schimbăm butonul în GALBEN (Update)
    const btn = document.getElementById('actionBtn');
    btn.innerText = "💾 Save Changes";
    btn.className = "btn btn-warning w-100 fw-bold";

    // Mergem sus la formular
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetForm() {
    // Golim tot
    document.getElementById('apiTitle').value = '';
    document.getElementById('apiGenre').value = '';
    document.getElementById('apiYear').value = '2000';
    document.getElementById('apiPoster').value = '';
    document.getElementById('previewImg').style.display = 'none';

    editingMediaId = null;

    // Revenim la butonul VERDE (Add)
    const btn = document.getElementById('actionBtn');
    btn.innerText = "Add to database";
    btn.className = "btn btn-success w-100 fw-bold";
}

// --- 3. LOADERS (Library, Users, Stats) ---

async function loadLibrary() {
    const res = await fetch(`${API_MEDIA}/all`);
    const media = await res.json();
    const tbody = document.getElementById('library-table-body');

    // Inversăm ca să vedem ultimele adăugate sus
    media.reverse();

    tbody.innerHTML = media.map(m => `
        <tr>
            <td class="ps-4">
                <img src="${m.posterUrl}" style="height: 40px; width: 30px; object-fit: cover; margin-right: 10px; border-radius: 4px;">
                <span class="text-white fw-bold">${m.title}</span>
            </td>
            <td class="small text-white-50">${m.genre}</td>
            <td class="text-end pe-4">
                 <button onclick="startEdit(${m.mediaId}, '${escapeStr(m.title)}', '${m.genre}', ${m.releaseYear}, '${m.posterUrl}')"
                         class="btn btn-sm btn-outline-warning me-1">✏️</button>

                 <button onclick="deleteMovie(${m.mediaId})" class="btn btn-sm btn-outline-danger">🗑️</button>
            </td>
        </tr>
    `).join('');
}

// Funcție ajutătoare pt titluri cu apostrof (ex: Schindler's List)
function escapeStr(str) {
    return str ? str.replace(/'/g, "\\'") : "";
}

async function loadUsers() {
    const res = await fetch(`${API_USERS}/all`);
    const users = await res.json();
    const tbody = document.getElementById('user-table-body');

    tbody.innerHTML = users.map(u => {
        const isBanned = u.status && u.status.toLowerCase() === 'banned';
        return `
        <tr>
            <td class="ps-4 fw-bold text-white">
                ${u.username}
                ${u.role === 'admin' ? '<span class="badge bg-danger ms-1">ADMIN</span>' : ''}
            </td>
            <td>${u.email}</td>
            <td>
                ${isBanned
                    ? '<span class="badge bg-danger">BANNED</span>'
                    : '<span class="badge bg-success text-dark">Active</span>'}
            </td>
            <td class="text-end pe-4">
                ${u.role !== 'admin' ? `
                    ${isBanned
                        ? `<button onclick="unbanUser(${u.userId})" class="btn btn-sm btn-outline-success py-0 fw-bold">Unban</button>`
                        : `<button onclick="banUser(${u.userId})" class="btn btn-sm btn-outline-warning py-0">Ban</button>`
                    }
                    <button onclick="deleteUser(${u.userId})" class="btn btn-sm btn-outline-danger py-0 ms-1">Delete</button>
                ` : '<small class="text-muted">Protected</small>'}
            </td>
        </tr>
        `;
    }).join('');
}

async function loadStats() {
    try {
        const resMedia = await fetch(`${API_MEDIA}/all`);
        const media = await resMedia.json();
        document.getElementById('count-movies').innerText = media.length;

        const resUsers = await fetch(`${API_USERS}/all`);
        const users = await resUsers.json();
        document.getElementById('count-users').innerText = users.length;

        const resWatchlist = await fetch('http://localhost:8080/api/watchlist/count');
        const count = await resWatchlist.json();
        document.getElementById('count-watchlist').innerText = count;
    } catch (e) { console.error(e); }
}

// --- 4. AUTOFILL & ACTIONS ---

async function autofill() {
    const title = document.getElementById('apiTitle').value;
    if(!title) return Swal.fire('Oops', 'Please write a title!', 'warning');

    const btn = document.querySelector('.btn-magic');
    const originalText = btn.innerText;
    btn.innerText = "⌛";

    try {
        const res = await fetch(`${API_MEDIA}/fetch-external?title=${encodeURIComponent(title)}`);
        if(res.ok) {
            const data = await res.json();
            document.getElementById('apiTitle').value = data.title; // Update title to exact match
            document.getElementById('apiYear').value = data.releaseYear;
            document.getElementById('apiGenre').value = data.genre;
            document.getElementById('apiPoster').value = data.posterUrl;

            const img = document.getElementById('previewImg');
            img.src = data.posterUrl;
            img.style.display = 'block';

            const Toast = Swal.mixin({toast: true, position: 'top-end', showConfirmButton: false, timer: 3000, background:'#240046', color:'#fff'});
            Toast.fire({icon: 'success', title: 'Found!'});
        } else {
            Swal.fire('Error', 'Movie not found', 'error');
        }
    } catch(err) {
        console.error(err);
    } finally {
        btn.innerText = originalText;
    }
}

async function banUser(id) {
    const res = await fetch(`${API_USERS}/ban/${id}`, { method: 'PUT' });
    if(res.ok) { loadUsers(); }
}

async function unbanUser(id) {
    const res = await fetch(`${API_USERS}/unban/${id}`, { method: 'PUT' });
    if(res.ok) { loadUsers(); }
}

async function deleteUser(id) {
    if(await confirmAction("Delete user?")) {
        await fetch(`${API_USERS}/delete/${id}`, { method: 'DELETE' });
        loadUsers();
        loadStats();
    }
}

async function deleteMovie(id) {
    if(await confirmAction("Delete movie?")) {
        await fetch(`${API_MEDIA}/delete/${id}`, { method: 'DELETE' });
        loadLibrary();
        loadStats();
    }
}

async function confirmAction(text) {
    const res = await Swal.fire({
        title: text, icon: 'warning', showCancelButton: true,
        confirmButtonColor: '#d33', cancelButtonColor: '#3085d6',
        background: '#240046', color: '#fff'
    });
    return res.isConfirmed;
}