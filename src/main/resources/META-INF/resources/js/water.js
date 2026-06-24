requireAuth();
initNavbar();

if (isAdmin()) {
    document.getElementById('nav-users').style.display = 'block';
    document.getElementById('nav-logs').style.display = 'block';
}

const alertEl = document.getElementById('alert');

function showError(msg) {
    alertEl.textContent = msg;
    alertEl.classList.add('show');
    setTimeout(() => alertEl.classList.remove('show'), 4000);
}

async function carregarAgua() {
    try {
        const log = await apiGet('/water');

        if (!log) {
            document.getElementById('total-bebido').textContent = '0 ml';
            document.getElementById('meta').textContent = '2000 ml';
            document.getElementById('progresso-bar').style.width = '0%';
            document.getElementById('progresso-pct').textContent = '0%';
            return;
        }

        document.getElementById('total-bebido').textContent = log.amountMl + ' ml';
        document.getElementById('meta').textContent = log.goalMl + ' ml';

        const pct = Math.min(log.percentageAchieved, 100).toFixed(0);
        document.getElementById('progresso-bar').style.width = pct + '%';
        document.getElementById('progresso-pct').textContent = pct + '%';
    } catch (err) {
        showError(err.message);
    }
}

async function adicionarAgua(quantidade) {
    const ml = parseInt(quantidade);
    if (!ml || ml < 50) {
        showError('Quantidade mínima é 50 ml.');
        return;
    }
    if (ml > 1000) {
        showError('Quantidade máxima por vez é 1000 ml. Beber mais que 1 litro de uma só vez não é saudável.');
        return;
    }
    try {
        await apiPost(`/water/add?amount=${ml}`, {});
        carregarAgua();
    } catch (err) {
        showError(err.message);
    }
}

async function salvarMeta() {
    const goalMl = parseInt(document.getElementById('input-meta').value);
    if (!goalMl) {
        showError('Informe uma meta em ml.');
        return;
    }
    if (goalMl < 2000) {
        showError('Meta mínima é 2000 ml. Consumir menos que 2 litros por dia pode causar desidratação.');
        return;
    }
    if (goalMl > 3500) {
        showError('Meta máxima é 3500 ml. Consumir mais que 3,5 litros por dia pode ser prejudicial à saúde.');
        return;
    }
    try {
        await apiPut(`/water/goal?goal=${goalMl}`, {});
        carregarAgua();
    } catch (err) {
        showError(err.message);
    }
}

carregarAgua();
