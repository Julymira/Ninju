const API_BASE = 'http://localhost:8080';

async function apiFetch(path, options = {}) {
    const token = getToken();

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
            ...(options.headers || {})
        }
    });

    if (response.status === 401 && getToken() && !path.includes('/me/password')) {
        logout();
        return;
    }

    if (!response.ok) {
        const text = await response.text();
        let msg = `Erro ${response.status}`;
        try { msg = JSON.parse(text).erro || msg; } catch { msg = text || msg; }
        const err = new Error(msg);
        err.status = response.status;
        throw err;
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function apiGet(path)         { return apiFetch(path, { method: 'GET' }); }
function apiPost(path, body)  { return apiFetch(path, { method: 'POST',   body: JSON.stringify(body) }); }
function apiPut(path, body)   { return apiFetch(path, { method: 'PUT',    body: JSON.stringify(body) }); }
function apiDelete(path)      { return apiFetch(path, { method: 'DELETE' }); }
