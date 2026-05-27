const TOKEN_KEY = 'ninju_token';
const USER_KEY  = 'ninju_user';

function saveSession(token, name, role) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify({ name, role }));
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
