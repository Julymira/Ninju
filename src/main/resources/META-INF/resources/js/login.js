if (getToken()) window.location.href = '/pages/dashboard.html';

const form  = document.getElementById('loginForm');
const alert = document.getElementById('alert');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    alert.classList.remove('show');

    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    try {
        const data = await apiPost('/auth/login', { email, password });
        saveSession(data.token, data.name, data.role);
        window.location.href = '/pages/dashboard.html';
    } catch (err) {
        alert.textContent = err.message || 'E-mail ou senha inválidos.';
        alert.classList.add('show');
    }
});
