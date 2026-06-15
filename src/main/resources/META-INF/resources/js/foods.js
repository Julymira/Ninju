requireAuth();
initNavbar();

if (isAdmin()) {
    document.getElementById('nav-users').style.display = 'block';
    document.getElementById('nav-logs').style.display = 'block';
    document.getElementById('admin-global-opt').style.display = 'block';
}

const tbody = document.getElementById('foods-body');
const alertEl = document.getElementById('alert');

function showError(msg) {
    alertEl.textContent = msg;
    alertEl.classList.add('show');
    setTimeout(() => alertEl.classList.remove('show'), 4000);
}

async function carregarAlimentos() {
    try {
        const foods = await apiGet('/foods');
        if (!foods.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Nenhum alimento cadastrado.</td></tr>';
            return;
        }
        const user = getUser();
        tbody.innerHTML = foods.map(f => {
            const canDelete = isAdmin() || (f.ownerId && f.ownerId === user.id);
            const badge = f.ownerId
                ? '<span style="font-size:0.72rem;background:#e8f5e9;color:#2e7d32;border-radius:4px;padding:1px 6px">pessoal</span>'
                : '';
            const delBtn = canDelete
                ? `<button class="btn-icon-danger" onclick="excluirAlimento(${f.id})" title="Excluir">&#x1F5D1;</button>`
                : '';
            return `<tr>
                <td><strong>${f.name}</strong> ${badge}</td>
                <td>${f.calories}</td>
                <td>${f.protein ?? '—'}</td>
                <td>${f.carbohydrates ?? '—'}</td>
                <td>${f.fat ?? '—'}</td>
                <td style="text-align:right">${delBtn}</td>
            </tr>`;
        }).join('');
    } catch (err) {
        showError(err.message);
    }
}

async function excluirAlimento(id) {
    if (!confirm('Excluir este alimento?')) return;
    try {
        await apiDelete(`/foods/${id}`);
        carregarAlimentos();
    } catch (err) {
        showError(err.message);
    }
}

// ── Modal ────────────────────────────────────────────────────────────────
function openAddModal() {
    document.getElementById('food-name').value = '';
    document.getElementById('food-calories').value = '';
    document.getElementById('food-protein').value = '';
    document.getElementById('food-carbs').value = '';
    document.getElementById('food-fat').value = '';
    if (isAdmin()) document.getElementById('food-global').checked = false;
    document.getElementById('alert-modal').classList.remove('show');
    document.getElementById('addFoodModal').classList.add('open');
}

function closeAddModal() {
    document.getElementById('addFoodModal').classList.remove('open');
}

async function saveFood() {
    const name  = document.getElementById('food-name').value.trim();
    const cal   = parseFloat(document.getElementById('food-calories').value);
    const prot  = parseFloat(document.getElementById('food-protein').value);
    const carbs = parseFloat(document.getElementById('food-carbs').value);
    const fat   = parseFloat(document.getElementById('food-fat').value);

    const alertModal = document.getElementById('alert-modal');
    if (!name || isNaN(cal) || isNaN(prot) || isNaN(carbs) || isNaN(fat)) {
        alertModal.textContent = 'Preencha todos os campos.';
        alertModal.classList.add('show');
        return;
    }

    try {
        const global = isAdmin() && document.getElementById('food-global').checked;
        await apiPost('/foods', { name, calories: cal, protein: prot, carbohydrates: carbs, fat, global });
        closeAddModal();
        carregarAlimentos();
    } catch (err) {
        alertModal.textContent = err.message;
        alertModal.classList.add('show');
    }
}

document.getElementById('addFoodModal').addEventListener('click', function(e) {
    if (e.target === this) closeAddModal();
});

carregarAlimentos();
