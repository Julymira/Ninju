requireAuth();
initNavbar();

if (!isAdmin()) {
    window.location.href = 'dashboard.html';
}

document.getElementById('nav-users').style.display = 'block';
document.getElementById('nav-logs').style.display = 'block';

let allLogs = [];

async function loadLogs() {
    document.getElementById('logsTbody').innerHTML =
        '<tr><td colspan="4" class="logs-loading">Carregando...</td></tr>';

    try {
        allLogs = await apiGet('/audit-logs');
        renderLogs(allLogs);
    } catch (err) {
        document.getElementById('logsTbody').innerHTML =
            '<tr><td colspan="4" class="logs-loading">Erro ao carregar logs.</td></tr>';
    }
}

function renderLogs(logs) {
    if (!logs.length) {
        document.getElementById('logsTbody').innerHTML =
            '<tr><td colspan="4" class="logs-loading">Nenhum registro encontrado.</td></tr>';
        return;
    }

    document.getElementById('logsTbody').innerHTML = logs.map((l, i) => {
        const dt = new Date(l.executionTime);
        const data = dt.toLocaleDateString('pt-BR');
        const hora = dt.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        const category = logCategory(l.actionExecuted);
        return `<tr>
            <td class="logs-id">${l.id}</td>
            <td class="logs-dt"><span class="logs-date">${data}</span><span class="logs-time">${hora}</span></td>
            <td class="logs-user">${l.executedBy}</td>
            <td><span class="logs-action-badge ${category.cls}">${category.label}</span> ${escapeHtml(actionText(l.actionExecuted))}</td>
        </tr>`;
    }).join('');
}

function logCategory(action) {
    if (action.startsWith('LOGIN'))       return { cls: 'badge-auth',    label: 'Auth' };
    if (action.startsWith('CRIAR'))       return { cls: 'badge-create',  label: 'Criação' };
    if (action.startsWith('EXCLUIR') || action.startsWith('DELETAR') || action.startsWith('REMOVER'))
                                          return { cls: 'badge-delete',  label: 'Exclusão' };
    if (action.startsWith('ATUALIZAR') || action.startsWith('RENOMEAR') || action.startsWith('ALTERAR'))
                                          return { cls: 'badge-update',  label: 'Atualização' };
    if (action.startsWith('REGISTRAR'))   return { cls: 'badge-create',  label: 'Registro' };
    if (action.startsWith('LISTAR') || action.startsWith('BUSCAR') || action.startsWith('VISUALIZAR'))
                                          return { cls: 'badge-read',    label: 'Leitura' };
    return { cls: 'badge-other', label: 'Sistema' };
}

function actionText(raw) {
    return raw.replace(/_/g, ' ').replace(/:/g, ': ');
}

function escapeHtml(s) {
    return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

loadLogs();
