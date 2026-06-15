requireAuth();
initNavbar();

const user = getUser();
if (isAdmin()) {
    document.getElementById('nav-users').style.display = 'block';
    document.getElementById('nav-logs').style.display = 'block';
}

// ── Tabs ──────────────────────────────────────────────────────────────────
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
    });
});

// ── Populate perfil tab ───────────────────────────────────────────────────
function loadProfile() {
    const avatarSrc = user.avatar ? `../images/avatars/${user.avatar}` : '../images/avatars/default.svg';
    document.getElementById('profileAvatarPreview').src = avatarSrc;
    document.getElementById('reviewName').textContent   = user.name;
    document.getElementById('reviewEmail').textContent  = user.email;
    document.getElementById('pName').value  = user.name;
    document.getElementById('pEmail').value = user.email;
}
loadProfile();

// ── Populate metas tab ────────────────────────────────────────────────────
function loadGoals() {
    const g = user.goals || {};
    if (g.weight)        document.getElementById('gWeight').value   = g.weight;
    if (g.calorieMeta)   document.getElementById('gCalories').value = g.calorieMeta;
    if (g.carbsMetaPct)  document.getElementById('gCarbs').value    = g.carbsMetaPct;
    if (g.proteinMetaPct) document.getElementById('gProtein').value = g.proteinMetaPct;
    if (g.fatMetaPct)    document.getElementById('gFat').value      = g.fatMetaPct;
    updateMacroSum();
}
loadGoals();

function updateMacroSum() {
    const c = parseInt(document.getElementById('gCarbs').value) || 0;
    const p = parseInt(document.getElementById('gProtein').value) || 0;
    const f = parseInt(document.getElementById('gFat').value) || 0;
    const sum = c + p + f;
    const el = document.getElementById('macroSum');
    el.textContent = sum > 0 ? `(${sum}%)` : '';
    el.style.color = sum === 100 ? 'var(--primary)' : sum > 100 ? 'var(--danger)' : 'var(--text-muted)';
}

// ── Perfil form ───────────────────────────────────────────────────────────
document.getElementById('profileForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alert = document.getElementById('alert-perfil');
    alert.className = 'alert';

    const body = {
        name:   document.getElementById('pName').value.trim(),
        email:  document.getElementById('pEmail').value.trim(),
        avatar: pendingAvatar ?? (user.avatar || '')
    };

    try {
        const updated = await apiPut('/auth/me', body);
        saveSession(getToken(), updated.name, updated.role, updated.id, updated.email,
            updated.avatar, {
                weight: updated.weight, calorieMeta: updated.calorieMeta,
                carbsMetaPct: updated.carbsMetaPct, proteinMetaPct: updated.proteinMetaPct,
                fatMetaPct: updated.fatMetaPct
            });
        pendingAvatar = null;
        Object.assign(user, getUser());
        initNavbar();
        loadProfile();
        showAlert(alert, 'Perfil atualizado!', true);
    } catch (err) {
        showAlert(alert, err.message || 'Erro ao salvar.', false);
    }
});

// ── Segurança form ────────────────────────────────────────────────────────
let passwordAttempts = 0;
const MAX_PASSWORD_ATTEMPTS = 3;

document.getElementById('passwordForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById('alert-seguranca');
    alertEl.className = 'alert';

    const newPwd     = document.getElementById('newPassword').value;
    const confirmPwd = document.getElementById('confirmNewPassword').value;

    if (newPwd !== confirmPwd) {
        showAlert(alertEl, 'As senhas não coincidem.', false);
        return;
    }

    try {
        await apiPut('/auth/me/password', {
            currentPassword: document.getElementById('currentPassword').value,
            newPassword: newPwd
        });
        passwordAttempts = 0;
        document.getElementById('passwordForm').reset();
        showAlert(alertEl, 'Senha alterada com sucesso!', true);
    } catch (err) {
        const isWrongPassword = err.status === 401;

        if (isWrongPassword) {
            passwordAttempts++;

            if (passwordAttempts >= MAX_PASSWORD_ATTEMPTS) {
                showAlert(alertEl, 'Você errou a senha atual 3 vezes. Por segurança, você será desconectado.', false);
                setTimeout(() => {
                    sessionStorage.setItem('logout_reason', 'Sessão encerrada por segurança: 3 tentativas de senha incorretas.');
                    logout();
                }, 3000);
                return;
            }

            const restantes = MAX_PASSWORD_ATTEMPTS - passwordAttempts;
            showAlert(alertEl, `Senha atual incorreta. ${restantes} tentativa${restantes > 1 ? 's' : ''} restante${restantes > 1 ? 's' : ''}.`, false);
        } else {
            showAlert(alertEl, err.message || 'Erro ao alterar senha.', false);
        }

        document.getElementById('currentPassword').value = '';
        document.getElementById('currentPassword').focus();
    }
});

// ── Metas form ────────────────────────────────────────────────────────────
document.getElementById('goalsForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alert = document.getElementById('alert-metas');
    alert.className = 'alert';

    const c = parseInt(document.getElementById('gCarbs').value) || 0;
    const p = parseInt(document.getElementById('gProtein').value) || 0;
    const f = parseInt(document.getElementById('gFat').value) || 0;
    if ((c + p + f) > 100) {
        showAlert(alert, 'A soma dos macronutrientes não pode ultrapassar 100%.', false);
        return;
    }

    const body = {
        weight:       parseFloat(document.getElementById('gWeight').value) || null,
        calorieMeta:  parseInt(document.getElementById('gCalories').value) || null,
        carbsMetaPct: c || null,
        proteinMetaPct: p || null,
        fatMetaPct:   f || null
    };

    try {
        const updated = await apiPut('/auth/me/goals', body);
        saveSession(getToken(), user.name, user.role, user.id, user.email,
            user.avatar, {
                weight: updated.weight, calorieMeta: updated.calorieMeta,
                carbsMetaPct: updated.carbsMetaPct, proteinMetaPct: updated.proteinMetaPct,
                fatMetaPct: updated.fatMetaPct
            });
        Object.assign(user, getUser());
        showAlert(alert, 'Metas salvas com sucesso!', true);
    } catch (err) {
        showAlert(alert, err.message || 'Erro ao salvar metas.', false);
    }
});

// ── Avatar modal ──────────────────────────────────────────────────────────
let pendingAvatar = null;

async function openAvatarModal() {
    const current = user.avatar ? `../images/avatars/${user.avatar}` : '../images/avatars/default.svg';
    document.getElementById('avatarModalPreview').src = current;
    pendingAvatar = null;

    const grid = document.getElementById('avatarPresets');
    grid.innerHTML = '<span style="color:var(--text-muted);font-size:0.85rem">Carregando...</span>';

    try {
        const avatars = await apiGet('/avatars');
        grid.innerHTML = '';
        avatars.forEach(name => {
            const img = document.createElement('img');
            img.src = `../images/avatars/${name}`;
            img.className = 'preset-avatar' + (user.avatar === name ? ' selected' : '');
            img.title = name;
            img.onclick = () => {
                document.querySelectorAll('.preset-avatar').forEach(i => i.classList.remove('selected'));
                img.classList.add('selected');
                document.getElementById('avatarModalPreview').src = img.src;
                pendingAvatar = name;
            };
            grid.appendChild(img);
        });
        if (!avatars.length) grid.innerHTML = '<span style="color:var(--text-muted);font-size:0.85rem">Nenhum avatar encontrado.</span>';
    } catch {
        grid.innerHTML = '<span style="color:var(--danger);font-size:0.85rem">Erro ao carregar avatares.</span>';
    }

    document.getElementById('avatarModal').classList.add('show');
}

function closeAvatarModal() {
    document.getElementById('avatarModal').classList.remove('show');
}

function confirmAvatar() {
    if (pendingAvatar) {
        document.getElementById('profileAvatarPreview').src = `../images/avatars/${pendingAvatar}`;
    }
    closeAvatarModal();
}

// ── IDR Calculator ────────────────────────────────────────────────────────
let lastIDR = null;

function openIdrModal() {
    const w = document.getElementById('gWeight').value;
    if (w) document.getElementById('idrWeight').value = w;
    document.getElementById('idrResult').style.display = 'none';
    document.getElementById('idrApplyBtn').style.display = 'none';
    document.getElementById('idrModal').classList.add('show');
}

function closeIdrModal() {
    document.getElementById('idrModal').classList.remove('show');
}

function calcularIDR() {
    const age      = parseInt(document.getElementById('idrAge').value);
    const weight   = parseFloat(document.getElementById('idrWeight').value);
    const height   = parseFloat(document.getElementById('idrHeight').value);
    const sex      = document.querySelector('input[name="idrSex"]:checked').value;
    const activity = parseFloat(document.getElementById('idrActivity').value);
    const goalAdj  = parseInt(document.getElementById('idrGoal').value);

    if (!age || !weight || !height) {
        alert('Preencha idade, peso e altura.');
        return;
    }

    // Mifflin-St Jeor
    let bmr = 10 * weight + 6.25 * height - 5 * age;
    bmr += sex === 'M' ? 5 : -161;
    const tdee = Math.round(bmr * activity) + goalAdj;
    lastIDR = tdee;

    const el = document.getElementById('idrResult');
    el.innerHTML = `<strong>Seu IDR calculado: ${tdee} kcal/dia</strong>`;
    el.style.display = 'block';
    document.getElementById('idrApplyBtn').style.display = 'inline-block';
}

function applyIDR() {
    if (lastIDR) document.getElementById('gCalories').value = lastIDR;
    closeIdrModal();
}

// ── Helper ────────────────────────────────────────────────────────────────
function showAlert(el, msg, success) {
    el.textContent = msg;
    el.classList.add('show', success ? 'alert-success' : 'alert-error');
}
