requireAuth();

const user = getUser();

document.getElementById('user-name').textContent   = user.name;
document.getElementById('welcome-name').textContent = user.name.split(' ')[0];

if (isAdmin()) {
    document.getElementById('nav-users').style.display  = 'block';
    document.getElementById('card-users').style.display = 'block';
}
