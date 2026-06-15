requireAuth();
initNavbar();

const user = getUser();
document.getElementById('welcome-name').textContent = user.name ? user.name.split(' ')[0] : '';

if (isAdmin()) {
    document.getElementById('nav-users').style.display  = 'block';
    document.getElementById('card-users').style.display = 'flex';
}

// Data de hoje
const today = new Date();
document.getElementById('dash-date').textContent = today.toLocaleDateString('pt-BR', {
    weekday: 'long', day: '2-digit', month: 'long', year: 'numeric'
});

function toISOLocal(d) {
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}

// ── Carregar dados do dia ─────────────────────────────────────────────────
async function loadDashboard() {
    const date = toISOLocal(today);
    try {
        const [foodData, workoutLogs] = await Promise.all([
            apiGet(`/daily-logs/report?date=${date}`),
            apiGet(`/workout-logs?date=${date}`).catch(() => [])
        ]);
        renderStats(foodData, workoutLogs);
        renderMacros(foodData);
        renderMeals(foodData);
        renderActivity(workoutLogs);
    } catch (err) {
        console.error('Erro ao carregar dashboard:', err);
    }
}

// ── Stats ─────────────────────────────────────────────────────────────────
function renderStats(foodData, workoutLogs) {
    const consumed = Math.round(foodData.totalCalories || 0);
    const meta     = foodData.calorieMeta || 0;
    const burned   = Math.round(workoutLogs.reduce((s, l) => s + (l.totalCalories || 0), 0));
    const net      = consumed - burned;

    document.getElementById('statConsumed').textContent = consumed + ' kcal';
    document.getElementById('statBurned').textContent   = burned > 0 ? burned + ' kcal' : '—';
    document.getElementById('statNet').textContent      = consumed > 0 || burned > 0 ? net + ' kcal' : '—';
    document.getElementById('statWorkoutCount').textContent =
        workoutLogs.length === 0 ? 'Nenhum treino hoje'
        : workoutLogs.length === 1 ? '1 treino registrado'
        : `${workoutLogs.length} treinos registrados`;

    const netEl = document.getElementById('statNet');
    if (consumed > 0 || burned > 0) {
        netEl.style.color = net <= 0 ? 'var(--primary)' : '#dc3545';
    }

    if (meta > 0) {
        const pct = Math.min(Math.round((consumed / meta) * 100), 100);
        document.getElementById('statBar').style.width = pct + '%';
        document.getElementById('statBar').style.background = pct >= 100 ? '#dc3545' : 'var(--primary)';
        document.getElementById('statMetaLabel').textContent = `${pct}% da meta (${meta} kcal)`;
    }
}

// ── Macros ────────────────────────────────────────────────────────────────
function renderMacros(data) {
    const prot = +(data.totalProtein || 0).toFixed(1);
    const carb = +(data.totalCarbs   || 0).toFixed(1);
    const fat  = +(data.totalFat     || 0).toFixed(1);
    const max  = Math.max(prot, carb, fat, 1);

    document.getElementById('macroProtVal').textContent = prot + 'g';
    document.getElementById('macroCarbVal').textContent = carb + 'g';
    document.getElementById('macroFatVal').textContent  = fat  + 'g';
    document.getElementById('macroProt').style.width = (prot / max * 100) + '%';
    document.getElementById('macroCarb').style.width = (carb / max * 100) + '%';
    document.getElementById('macroFat').style.width  = (fat  / max * 100) + '%';
}

// ── Refeições ─────────────────────────────────────────────────────────────
function renderMeals(data) {
    const meals = [
        { key: 'cafe',   icon: '🌅', label: 'Café da Manhã',  entries: data.cafe   || [] },
        { key: 'almoco', icon: '☀️', label: 'Almoço',          entries: data.almoco || [] },
        { key: 'jantar', icon: '🌆', label: 'Jantar',          entries: data.jantar || [] },
        { key: 'lanche', icon: '🌙', label: 'Lanches/Outros',  entries: data.lanche || [] },
    ];

    const total = meals.reduce((s, m) => s + m.entries.reduce((a, e) => a + (e.calories || 0), 0), 0);

    if (total === 0) {
        document.getElementById('dashMeals').innerHTML =
            '<div class="dash-empty">Nenhum alimento registrado hoje.<br><a href="daily-log.html">Registrar agora →</a></div>';
        return;
    }

    document.getElementById('dashMeals').innerHTML = meals
        .filter(m => m.entries.length > 0)
        .map(m => {
            const mCal = Math.round(m.entries.reduce((s, e) => s + (e.calories || 0), 0));
            const items = m.entries.map(e =>
                `<div class="dash-meal-item">
                    <span>${e.foodName}</span>
                    <span class="dash-item-detail">${e.quantityGrams}g · ${Math.round(e.calories)} kcal</span>
                </div>`
            ).join('');
            return `<div class="dash-meal-group">
                <div class="dash-meal-header">
                    <span>${m.icon} ${m.label}</span>
                    <span class="dash-meal-cal">${mCal} kcal</span>
                </div>
                ${items}
            </div>`;
        }).join('');
}

// ── Atividade física ──────────────────────────────────────────────────────
function renderActivity(logs) {
    const el = document.getElementById('dashActivity');

    if (!logs.length) {
        el.innerHTML = '<div class="dash-empty">Nenhum treino registrado hoje.<br><a href="daily-log.html">Registrar agora →</a></div>';
        return;
    }

    el.innerHTML = logs.map(log => {
        const exList = log.exercises.slice(0, 4).map(e => {
            const detail = e.exerciseType === 'MUSCULACAO'
                ? `${e.sets}×${e.reps} @ ${e.weightKg}kg`
                : `${e.durationMinutes} min`;
            return `<div class="dash-meal-item">
                <span>${e.workoutName}</span>
                <span class="dash-item-detail">${detail} · ${e.estimatedCalories.toFixed(0)} kcal</span>
            </div>`;
        }).join('');
        const more = log.exercises.length > 4
            ? `<div class="dash-meal-item" style="color:var(--text-muted);font-style:italic">+${log.exercises.length - 4} exercício(s)...</div>` : '';
        return `<div class="dash-meal-group">
            <div class="dash-meal-header">
                <span>🏋️ ${log.planName}</span>
                <span class="dash-meal-cal">~${Math.round(log.totalCalories)} kcal</span>
            </div>
            ${exList}${more}
        </div>`;
    }).join('');
}

loadDashboard();
