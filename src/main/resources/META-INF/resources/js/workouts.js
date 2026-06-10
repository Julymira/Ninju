requireAuth();

const user = getUser();
document.getElementById('user-name').textContent = user.name;
if (isAdmin()) document.getElementById('nav-users').style.display = 'block';

const tbody = document.getElementById('workouts-body');
const alert = document.getElementById('alert');

async function carregarTreinos() {
    try {
        const workouts = await apiGet('/workouts');
        if (!workouts.length) {
            tbody.innerHTML = '<tr><td colspan="3" class="empty-state">Nenhum treino cadastrado.</td></tr>';
            return;
        }
        tbody.innerHTML = workouts.map(w => `
            <tr>
                <td><strong>${w.name}</strong></td>
                <td>${w.category}</td>
                <td>${w.estimatedCaloriesBurned}</td>
            </tr>
        `).join('');
    } catch (err) {
        alert.textContent = err.message;
        alert.classList.add('show');
    }
}

carregarTreinos();
