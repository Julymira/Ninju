requireAdmin();
initNavbar();

const alertEl   = document.getElementById('alert');
const successEl = document.getElementById('alert-success');
const tbody     = document.getElementById('users-body');
const modal     = document.getElementById('modal-overlay');
const modalAlert = document.getElementById('modal-alert');

function showError(msg) {
    alertEl.textContent = msg;
    alertEl.classList.add('show');
}

function showSuccess(msg) {
    alertEl.classList.remove('show');
    successEl.textContent = msg;
    successEl.classList.add('show');
    setTimeout(() => successEl.classList.remove('show'), 3000);
}

function abrirModalCriar() {
    document.getElementById('modal-title').textContent = 'Novo Usuário';
    document.getElementById('form-usuario').reset();
    document.getElementById('user-id').value = '';
    document.getElementById('campo-senha').style.display = 'block';
    document.querySelector('#campo-senha input').required = true;
    modalAlert.classList.remove('show');
    modal.classList.add('show');
}

function abrirModalEditar(id, nome, email, role) {
    document.getElementById('modal-title').textContent = 'Editar Usuário';
    document.getElementById('user-id').value    = id;
    document.getElementById('user-nome').value  = nome;
    document.getElementById('user-email').value = email;
    document.getElementById('user-role').value  = role;
    document.getElementById('user-senha').value = '';
    document.getElementById('campo-senha').style.display = 'block';
    document.querySelector('#campo-senha input').required = false;
    modalAlert.classList.remove('show');
    modal.classList.add('show');
}

function fecharModal() {
    modal.classList.remove('show');
}

modal.addEventListener('click', (e) => {
    if (e.target === modal) fecharModal();
});

document.getElementById('form-usuario').addEventListener('submit', async (e) => {
    e.preventDefault();
    modalAlert.classList.remove('show');

    const id    = document.getElementById('user-id').value;
    const body  = {
        name:     document.getElementById('user-nome').value,
        email:    document.getElementById('user-email').value,
        password: document.getElementById('user-senha').value || undefined,
        role:     document.getElementById('user-role').value
    };

    try {
        if (id) {
            await apiPut(`/users/${id}`, body);
            showSuccess('Usuário atualizado com sucesso!');
        } else {
            await apiPost('/users', body);
            showSuccess('Usuário criado com sucesso!');
        }
        fecharModal();
        carregarUsuarios();
    } catch (err) {
        modalAlert.textContent = err.message;
        modalAlert.classList.add('show');
    }
});

async function deletarUsuario(id, nome) {
    if (!confirm(`Deseja excluir o usuário "${nome}"?`)) return;
    try {
        await apiDelete(`/users/${id}`);
        showSuccess('Usuário excluído com sucesso!');
        carregarUsuarios();
    } catch (err) {
        showError(err.message);
    }
}

async function carregarUsuarios() {
    try {
        const users = await apiGet('/users');
        if (!users.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhum usuário encontrado.</td></tr>';
            return;
        }
        tbody.innerHTML = users.map(u => `
            <tr>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td><span class="badge badge-${u.role.toLowerCase()}">${u.role}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="abrirModalEditar(${u.id}, '${u.name}', '${u.email}', '${u.role}')">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deletarUsuario(${u.id}, '${u.name}')">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        showError(err.message);
    }
}

carregarUsuarios();
