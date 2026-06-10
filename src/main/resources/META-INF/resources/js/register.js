if (getToken()) window.location.href = '/pages/dashboard.html';

const form  = document.getElementById('registerForm');
const alert = document.getElementById('alert');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    alert.className = 'alert';

    const name            = document.getElementById('name').value.trim();
    const email           = document.getElementById('email').value.trim();
    const password        = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        alert.textContent = 'As senhas não coincidem.';
        alert.classList.add('show', 'alert-error');
        return;
    }

    try {
        await apiPost('/auth/register', { name, email, password });
        alert.textContent = 'Conta criada com sucesso! Redirecionando...';
        alert.classList.add('show', 'alert-success');
        setTimeout(() => window.location.href = '/', 1500);
    } catch (err) {
        alert.textContent = err.message || 'Erro ao criar conta.';
        alert.classList.add('show', 'alert-error');
    }
});
