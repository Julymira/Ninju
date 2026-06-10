requireAuth();

const user = getUser();
document.getElementById('user-name').textContent = user.name;
if (isAdmin()) document.getElementById('nav-users').style.display = 'block';

const alertEl   = document.getElementById('alert');
const successEl = document.getElementById('alert-success');
const tbody     = document.getElementById('logs-body');

function hoje() {
    return new Date().toISOString().split('T')[0];
}

function showError(msg) {
    successEl.classList.remove('show');
    alertEl.textContent = msg;
    alertEl.classList.add('show');
}

function showSuccess(msg) {
    alertEl.classList.remove('show');
    successEl.textContent = msg;
    successEl.classList.add('show');
    setTimeout(() => successEl.classList.remove('show'), 3000);
}

// Preenche data de hoje nos campos
document.getElementById('data-refeicao').value = hoje();
document.getElementById('data-treino').value   = hoje();

// Caso de uso 1 — Registrar refeição
document.getElementById('form-refeicao').addEventListener('submit', async (e) => {
    e.preventDefault();
    alertEl.classList.remove('show');
    try {
        await apiPost('/daily-logs/refeicao', {
            logDate:    document.getElementById('data-refeicao').value,
            mealsNotes: document.getElementById('notas-refeicao').value
        });
        showSuccess('Refeição registrada com sucesso!');
        document.getElementById('notas-refeicao').value = '';
        carregarHistorico();
    } catch (err) {
        showError(err.message);
    }
});

// Caso de uso 2 — Registrar treino
document.getElementById('form-treino').addEventListener('submit', async (e) => {
    e.preventDefault();
    alertEl.classList.remove('show');
    try {
        await apiPost('/daily-logs/treino', {
            logDate:      document.getElementById('data-treino').value,
            workoutNotes: document.getElementById('notas-treino').value
        });
        showSuccess('Treino registrado com sucesso!');
        document.getElementById('notas-treino').value = '';
        carregarHistorico();
    } catch (err) {
        showError(err.message);
    }
});

async function carregarHistorico() {
    try {
        const logs = await apiGet('/daily-logs');
        if (!logs.length) {
            tbody.innerHTML = '<tr><td colspan="3" class="empty-state">Nenhum registro encontrado.</td></tr>';
            return;
        }
        tbody.innerHTML = logs.map(l => `
            <tr>
                <td>${l.logDate}</td>
                <td>${l.mealsNotes || '<span style="color:#aaa">—</span>'}</td>
                <td>${l.workoutNotes || '<span style="color:#aaa">—</span>'}</td>
            </tr>
        `).join('');
    } catch (err) {
        showError(err.message);
    }
}

carregarHistorico();
