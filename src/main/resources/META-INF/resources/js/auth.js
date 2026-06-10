const TOKEN_KEY = 'ninju_token';
const USER_KEY  = 'ninju_user';

function saveSession(token, name, role, id, email, avatar, goals) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify({ id, name, email, role, avatar, goals: goals || {} }));
}

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function getUser() {
    const u = localStorage.getItem(USER_KEY);
    return u ? JSON.parse(u) : null;
}

function isAdmin() {
    const u = getUser();
    return u && u.role === 'ADMIN';
}

function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '/index.html';
}

function initNavbar() {
    const user = getUser();
    if (!user) return;
    const avatar = user.avatar ? `../images/avatars/${user.avatar}` : '../images/avatars/default.svg';

    const navAvatar      = document.getElementById('navAvatar');
    const dropdownAvatar = document.getElementById('dropdownAvatar');
    const dropdownName   = document.getElementById('dropdownName');
    const dropdownRole   = document.getElementById('dropdownRole');
    if (navAvatar)      navAvatar.src      = avatar;
    if (dropdownAvatar) dropdownAvatar.src = avatar;
    if (dropdownName)   dropdownName.textContent = user.name;
    if (dropdownRole)   dropdownRole.textContent = user.role === 'ADMIN' ? 'Administrador' : 'Usuário';

    document.addEventListener('click', (e) => {
        const btn      = document.getElementById('avatarBtn');
        const dropdown = document.getElementById('profileDropdown');
        if (dropdown && btn && !btn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.classList.remove('open');
            btn.classList.remove('open');
        }
    });
}

function toggleDropdown() {
    const btn      = document.getElementById('avatarBtn');
    const dropdown = document.getElementById('profileDropdown');
    if (!dropdown) return;
    dropdown.classList.toggle('open');
    btn.classList.toggle('open');
}

// Redireciona para login se não autenticado
function requireAuth() {
    if (!getToken()) {
        window.location.href = '/index.html';
    }
}

// Redireciona para dashboard se não for ADMIN
function requireAdmin() {
    requireAuth();
    if (!isAdmin()) {
        window.location.href = '/pages/dashboard.html';
    }
}
