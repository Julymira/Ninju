requireAuth();
initNavbar();

if (isAdmin()) {
    document.getElementById('nav-users').style.display = 'block';
    document.getElementById('nav-logs').style.display = 'block';
}

const MEAL_LABELS = { CAFE: 'Café da Manhã', ALMOCO: 'Almoço', JANTAR: 'Jantar', LANCHE: 'Lanches/Outros' };
const MEAL_COLORS = { CAFE: '#f59e0b', ALMOCO: '#3b82f6', JANTAR: '#f97316', LANCHE: '#a855f7' };
const MAX_DAYS_BACK = 7;

let currentDate = new Date();
currentDate.setHours(0,0,0,0);

// ── Tabs ──────────────────────────────────────────────────────────────────
document.querySelectorAll('.report-tab').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.report-tab').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.report-tab-content').forEach(c => c.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
    });
});

// ── Navegação de data ─────────────────────────────────────────────────────
function changeDay(delta) {
    const next = new Date(currentDate);
    next.setDate(next.getDate() + delta);
    const today = new Date(); today.setHours(0,0,0,0);
    const minDate = new Date(today); minDate.setDate(today.getDate() - MAX_DAYS_BACK);
    if (next > today || next < minDate) return;
    currentDate = next;
    updateNavButtons();
    loadReport();
}

function updateNavButtons() {
    const today = new Date(); today.setHours(0,0,0,0);
    const minDate = new Date(today); minDate.setDate(today.getDate() - MAX_DAYS_BACK);
    document.getElementById('btnNext').disabled = currentDate >= today;
    document.getElementById('btnPrev').disabled = currentDate <= minDate;

    const diff = Math.round((today - currentDate) / 86400000);
    if (diff === 0) document.getElementById('reportDateLabel').textContent = 'Hoje';
    else if (diff === 1) document.getElementById('reportDateLabel').textContent = 'Ontem';
    else document.getElementById('reportDateLabel').textContent = formatDate(currentDate);
}

function formatDate(d) {
    return d.toLocaleDateString('pt-BR', { weekday: 'short', day: '2-digit', month: 'short' });
}

function toISOLocal(d) {
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}

// ── Load report ───────────────────────────────────────────────────────────
async function loadReport() {
    const dateStr = toISOLocal(currentDate);
    try {
        const [foodData, workoutLogs] = await Promise.all([
            apiGet(`/daily-logs/report?date=${dateStr}`),
            apiGet(`/workout-logs?date=${dateStr}`).catch(() => [])
        ]);
        renderCalorias(foodData);
        renderMacros(foodData);
        renderActivity(workoutLogs, foodData.totalCalories || 0);
    } catch (err) {
        console.error(err);
    }
}

// ── Render calorias tab ───────────────────────────────────────────────────
function renderCalorias(data) {
    const total = data.totalCalories || 0;
    const meta  = data.calorieMeta  || 0;
    const pct   = meta > 0 ? Math.min(Math.round((total / meta) * 100), 100) : 0;

    document.getElementById('calTotal').textContent = Math.round(total);
    document.getElementById('calPct').textContent   = meta > 0 ? `${pct}% da meta` : 'Meta não definida';
    document.getElementById('calMeta').textContent  = meta > 0 ? `Meta: ${meta} kcal` : '';

    drawDonut(total, meta);

    const meals = [
        { key: 'cafe',   label: 'Café da Manhã',    entries: data.cafe   || [] },
        { key: 'almoco', label: 'Almoço',            entries: data.almoco || [] },
        { key: 'jantar', label: 'Jantar',            entries: data.jantar || [] },
        { key: 'lanche', label: 'Lanches/Outros',    entries: data.lanche || [] },
    ];

    const colorMap = { cafe: '#f59e0b', almoco: '#3b82f6', jantar: '#f97316', lanche: '#a855f7' };

    document.getElementById('mealsList').innerHTML = meals.map(m => {
        const mealCal = m.entries.reduce((s, e) => s + (e.calories || 0), 0);
        const mealPct = total > 0 ? Math.round((mealCal / total) * 100) : 0;
        const items   = m.entries.map(e =>
            `<div class="meal-item">
                <span class="meal-item-name">${e.foodName}</span>
                <span class="meal-item-qty">${e.quantityGrams}g</span>
                <span class="meal-item-cal">${Math.round(e.calories)} kcal</span>
            </div>`
        ).join('');

        return `<div class="report-card meal-card">
            <div class="meal-header">
                <span class="meal-dot" style="background:${colorMap[m.key]}"></span>
                <span class="meal-label">${m.label}</span>
                <span class="meal-pct">(${mealPct}%)</span>
                <span class="meal-cal-total">${mealCal > 0 ? Math.round(mealCal) + ' kcal' : '—'}</span>
            </div>
            ${items}
        </div>`;
    }).join('');
}

// ── Donut chart (canvas) ──────────────────────────────────────────────────
function drawDonut(consumed, meta) {
    const canvas = document.getElementById('donutCanvas');
    const ctx    = canvas.getContext('2d');
    const cx = canvas.width / 2, cy = canvas.height / 2;
    const r = 72, lineW = 18;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Track background
    ctx.beginPath();
    ctx.arc(cx, cy, r, 0, Math.PI * 2);
    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = lineW;
    ctx.stroke();

    if (meta > 0 && consumed > 0) {
        const fraction = Math.min(consumed / meta, 1);
        const endAngle = -Math.PI / 2 + fraction * Math.PI * 2;
        const color    = fraction >= 1 ? '#dc3545' : '#28a745';

        ctx.beginPath();
        ctx.arc(cx, cy, r, -Math.PI / 2, endAngle);
        ctx.strokeStyle = color;
        ctx.lineWidth = lineW;
        ctx.lineCap = 'round';
        ctx.stroke();
    }
}

// ── Render macros tab ─────────────────────────────────────────────────────
function renderMacros(data) {
    const macros = [
        { label: 'Proteínas',     value: data.totalProtein || 0, color: '#ef4444', unit: 'g' },
        { label: 'Carboidratos',  value: data.totalCarbs   || 0, color: '#f59e0b', unit: 'g' },
        { label: 'Gorduras',      value: data.totalFat     || 0, color: '#3b82f6', unit: 'g' },
    ];

    const maxVal = Math.max(...macros.map(m => m.value), 1);
    const user   = getUser();
    const goals  = user.goals || {};
    const calMeta = goals.calorieMeta || 0;

    document.getElementById('macroBars').innerHTML = macros.map(m => {
        const pct = Math.min((m.value / maxVal) * 100, 100);
        return `<div class="macro-bar-row">
            <span class="macro-bar-label">${m.label}</span>
            <div class="macro-bar-track">
                <div class="macro-bar-fill" style="width:${pct}%;background:${m.color}"></div>
            </div>
            <span class="macro-bar-value">${m.value}${m.unit}</span>
        </div>`;
    }).join('');
}

// ── Render atividade física ───────────────────────────────────────────────
function renderActivity(logs, totalConsumed) {
    const section = document.getElementById('activitySection');
    const saldoCard = document.getElementById('saldoCard');

    const totalBurned = logs.reduce((s, l) => s + (l.totalCalories || 0), 0);

    if (!logs.length) {
        section.innerHTML = `
            <div class="report-card activity-empty">
                <span class="activity-empty-icon">🏋️</span>
                <div>
                    <p class="activity-empty-title">Atividade Física</p>
                    <p class="activity-empty-sub">Nenhum treino registrado neste dia.<br>
                    Acesse o <a href="daily-log.html">Diário</a> para registrar.</p>
                </div>
            </div>`;
        saldoCard.style.display = 'none';
        return;
    }

    section.innerHTML = `
        <div class="report-card activity-header-card">
            <div class="activity-header">
                <span class="activity-header-icon">🏋️</span>
                <span class="activity-header-title">Atividade Física</span>
                <span class="activity-header-total">~${Math.round(totalBurned)} kcal queimadas</span>
            </div>
        </div>
        ${logs.map(log => `
            <div class="report-card workout-log-card">
                <div class="wlog-report-header">
                    <span class="wlog-report-name">${log.planName}</span>
                    <span class="wlog-report-cal">~${Math.round(log.totalCalories)} kcal</span>
                </div>
                ${log.exercises.map(e => {
                    const detail = e.exerciseType === 'MUSCULACAO'
                        ? `${e.sets} séries × ${e.reps} reps @ ${e.weightKg}kg`
                        : `${e.durationMinutes} min`;
                    const badge = e.exerciseType === 'MUSCULACAO'
                        ? '<span class="ex-badge musc">Musculação</span>'
                        : '<span class="ex-badge tempo">Tempo</span>';
                    return `<div class="wlog-report-ex">
                        <span class="wlog-report-ex-name">${e.workoutName} ${badge}</span>
                        <span class="wlog-report-ex-detail">${detail}</span>
                        <span class="wlog-report-ex-cal">${e.estimatedCalories.toFixed(1)} kcal</span>
                    </div>`;
                }).join('')}
            </div>
        `).join('')}`;

    // Saldo
    const liquido = totalConsumed - totalBurned;
    document.getElementById('saldoConsumidas').textContent = `${Math.round(totalConsumed)} kcal`;
    document.getElementById('saldoQueimadas').textContent  = `−${Math.round(totalBurned)} kcal`;
    document.getElementById('saldoLiquido').textContent    = `${Math.round(liquido)} kcal`;
    document.getElementById('saldoLiquido').style.color    = liquido <= 0 ? '#28a745' : '#dc3545';
    saldoCard.style.display = 'block';
}

// ── Init ──────────────────────────────────────────────────────────────────
updateNavButtons();
loadReport();
