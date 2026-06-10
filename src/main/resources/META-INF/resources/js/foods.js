requireAuth();

const user = getUser();
document.getElementById('user-name').textContent = user.name;
if (isAdmin()) document.getElementById('nav-users').style.display = 'block';

const tbody = document.getElementById('foods-body');
const alert = document.getElementById('alert');

async function carregarAlimentos() {
    try {
        const foods = await apiGet('/foods');
        if (!foods.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Nenhum alimento cadastrado.</td></tr>';
            return;
        }
        tbody.innerHTML = foods.map(f => `
            <tr>
                <td><strong>${f.name}</strong></td>
                <td>${f.calories}</td>
                <td>${f.protein}</td>
                <td>${f.carbohydrates}</td>
                <td>${f.fat}</td>
            </tr>
        `).join('');
    } catch (err) {
        alert.textContent = err.message;
        alert.classList.add('show');
    }
}

carregarAlimentos();
