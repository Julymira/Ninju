if (getToken()) window.location.href = '/pages/dashboard.html';

const reason = sessionStorage.getItem('logout_reason');
if (reason) {
    sessionStorage.removeItem('logout_reason');
    const alert = document.getElementById('alert');
    alert.textContent = reason;
    alert.classList.add('show');
    clearTimeout(alert._timer);
    alert._timer = setTimeout(() => alert.classList.remove('show'), 6000);
}

const form  = document.getElementById('loginForm');
const alert = document.getElementById('alert');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    alert.classList.remove('show');

    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    try {
        const data = await apiPost('/auth/login', { email, password });
        saveSession(data.token, data.name, data.role, data.id, data.email, data.avatar, {
            weight: data.weight, calorieMeta: data.calorieMeta,
            carbsMetaPct: data.carbsMetaPct, proteinMetaPct: data.proteinMetaPct,
            fatMetaPct: data.fatMetaPct
        });
        window.location.href = '/pages/dashboard.html';
    } catch (err) {
        alert.textContent = err.message || 'E-mail ou senha inválidos.';
        alert.classList.add('show');
        clearTimeout(alert._timer);
        alert._timer = setTimeout(() => alert.classList.remove('show'), 4000);
    }
});
