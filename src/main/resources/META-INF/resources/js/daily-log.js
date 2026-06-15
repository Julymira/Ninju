requireAuth();
initNavbar();

if (isAdmin()) document.getElementById('nav-users').style.display = 'block';

const MAX_DAYS_BACK = 7;
const DAY_LABELS = ['D','S','T','Q','Q','S','S'];
const MEALS = ['CAFE','ALMOCO','JANTAR','LANCHE'];
const MEAL_NAMES = { CAFE: 'Café da Manhã', ALMOCO: 'Almoço', JANTAR: 'Jantar', LANCHE: 'Lanches/Outros' };

let currentDate = new Date(); currentDate.setHours(0,0,0,0);
let currentMealType = null;
let selectedFood = null;
let allFoods = [];

// Estado do modal de treino
let wlogSelectedPlan = null;
let userPlans = [];

// ── Utilitários de data ───────────────────────────────────────────────────
function toISOLocal(d) {
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}

function isSameDay(a, b) {
    return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
}

// ── Abas do diário ────────────────────────────────────────────────────────
function switchDiaryTab(tab) {
    document.querySelectorAll('.diary-tab').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.diary-tab-content').forEach(c => c.classList.remove('active'));
    document.querySelector(`[data-tab="${tab}"]`).classList.add('active');
    document.getElementById('tab-' + tab).classList.add('active');
}

// ── Navegação de dia ──────────────────────────────────────────────────────
function changeDay(delta) {
    const next = new Date(currentDate);
    next.setDate(next.getDate() + delta);
    const today = new Date(); today.setHours(0,0,0,0);
    const minDate = new Date(today); minDate.setDate(today.getDate() - MAX_DAYS_BACK);
    if (next > today || next < minDate) return;
    currentDate = next;
    renderNav();
    renderWeek();
    loadAll();
}

function renderNav() {
    const today = new Date(); today.setHours(0,0,0,0);
    const minDate = new Date(today); minDate.setDate(today.getDate() - MAX_DAYS_BACK);
    document.getElementById('btnNext').disabled = isSameDay(currentDate, today);
    document.getElementById('btnPrev').disabled = isSameDay(currentDate, minDate);

    const diff = Math.round((today - currentDate) / 86400000);
    if (diff === 0) document.getElementById('dayLabel').textContent = 'Hoje';
    else if (diff === 1) document.getElementById('dayLabel').textContent = 'Ontem';
    else document.getElementById('dayLabel').textContent = currentDate.toLocaleDateString('pt-BR', { weekday: 'short', day: '2-digit', month: 'short' });
}

// ── Mini semana ───────────────────────────────────────────────────────────
function renderWeek() {
    const today = new Date(); today.setHours(0,0,0,0);
    const strip = document.getElementById('weekStrip');
    strip.innerHTML = '';
    for (let i = MAX_DAYS_BACK; i >= 0; i--) {
        const d = new Date(today); d.setDate(today.getDate() - i);
        const label = DAY_LABELS[d.getDay()];
        const num   = d.getDate();
        const isToday = i === 0;
        const isSel   = isSameDay(d, currentDate);

        const wrap = document.createElement('div');
        wrap.className = 'week-day';
        wrap.innerHTML = `<span class="week-day-label">${label}</span>
            <div class="week-day-circle ${isToday ? 'today' : ''} ${isSel && !isToday ? 'selected' : ''}">${num}</div>`;
        const circle = wrap.querySelector('.week-day-circle');
        const captured = new Date(d);
        circle.addEventListener('click', () => {
            currentDate = captured;
            renderNav();
            renderWeek();
            loadAll();
        });
        strip.appendChild(wrap);
    }
}

// ── Load tudo ─────────────────────────────────────────────────────────────
function loadAll() {
    loadReport();
    loadWorkoutLogs();
}

// ── Barra de calorias ─────────────────────────────────────────────────────
function renderCalBar(report) {
    const consumed = Math.round(report.totalCalories || 0);
    const meta     = report.calorieMeta || 0;
    const rest     = meta > 0 ? meta - consumed : null;

    document.getElementById('calConsumidas').textContent = consumed;
    document.getElementById('calMeta').textContent = meta > 0 ? meta : '—';
    document.getElementById('calRestantes').textContent = rest !== null ? rest : '—';
    document.getElementById('sumConsumidas').textContent = consumed;
    document.getElementById('sumRestantes').textContent  = rest !== null ? rest : '—';
    document.getElementById('sumPct').textContent = meta > 0 ? `${Math.round((consumed/meta)*100)}% da IDR` : '';
}

// ── Refeições ─────────────────────────────────────────────────────────────
function renderMeals(report) {
    MEALS.forEach(key => {
        const entries = report[key.toLowerCase()] || [];
        const kcalTotal = entries.reduce((s, e) => s + (e.calories || 0), 0);
        document.getElementById(`kcal-${key}`).textContent = Math.round(kcalTotal) + ' kcal';

        const container = document.getElementById(`items-${key}`);
        if (entries.length === 0) {
            container.innerHTML = '<div class="meal-empty">Nenhum item registrado</div>';
        } else {
            container.innerHTML = entries.map(e => `
                <div class="meal-item-row" data-id="${e.id}">
                    <span class="meal-item-name">${e.foodName}</span>
                    <span class="meal-item-qty">${e.quantityGrams}g</span>
                    <span class="meal-item-cal">${Math.round(e.calories)} kcal</span>
                    <button class="meal-item-del" onclick="removeEntry(${e.id})" title="Remover">&#x2715;</button>
                </div>
            `).join('');
        }
    });
}

function toggleMeal(key) {
    document.getElementById(`items-${key}`).classList.toggle('hidden');
}

async function loadReport() {
    try {
        const data = await apiGet(`/daily-logs/report?date=${toISOLocal(currentDate)}`);
        renderCalBar(data);
        renderMeals(data);
    } catch (err) {
        showAlert('Erro ao carregar diário: ' + err.message, 'error');
    }
}

async function removeEntry(id) {
    try {
        await apiDelete(`/daily-logs/entry/${id}`);
        loadReport();
    } catch (err) {
        showAlert('Erro ao remover: ' + err.message, 'error');
    }
}

// ── Modal adicionar alimento ──────────────────────────────────────────────
function openAddModal(mealType) {
    currentMealType = mealType;
    selectedFood = null;
    document.getElementById('foodSearch').value = '';
    document.getElementById('foodResults').innerHTML = '';
    document.getElementById('addFoodForm').style.display = 'none';
    document.getElementById('confirmAddBtn').style.display = 'none';
    document.getElementById('foodQty').value = '100';
    document.getElementById('nutritionPreview').innerHTML = '';
    document.getElementById('selectedFoodInfo').innerHTML = '';
    document.getElementById('addModalTitle').textContent = `Adicionar em ${MEAL_NAMES[mealType]}`;
    document.getElementById('addFoodModal').classList.add('open');

    if (allFoods.length === 0) loadFoods();
    else renderFoodResults(allFoods.slice(0, 20));
}

function closeAddModal() {
    document.getElementById('addFoodModal').classList.remove('open');
}

async function loadFoods() {
    try {
        allFoods = await apiGet('/foods');
        renderFoodResults(allFoods.slice(0, 20));
    } catch (err) {
        document.getElementById('foodResults').innerHTML = '<div style="padding:10px;font-size:0.85rem;color:var(--text-muted)">Erro ao carregar alimentos</div>';
    }
}

function searchFoods() {
    const q = document.getElementById('foodSearch').value.trim().toLowerCase();
    if (!q) { renderFoodResults(allFoods.slice(0, 20)); return; }
    renderFoodResults(allFoods.filter(f => f.name.toLowerCase().includes(q)).slice(0, 30));
}

function renderFoodResults(foods) {
    const el = document.getElementById('foodResults');
    if (!foods.length) {
        el.innerHTML = '<div style="padding:10px;font-size:0.85rem;color:var(--text-muted)">Nenhum alimento encontrado</div>';
        return;
    }
    el.innerHTML = foods.map(f => `
        <div class="food-result-item" onclick="selectFood(${f.id})">
            <span class="food-result-name">${f.name}</span>
            <span class="food-result-kcal">${f.calories} kcal/100g</span>
        </div>
    `).join('');
}

function selectFood(id) {
    selectedFood = allFoods.find(f => f.id === id);
    if (!selectedFood) return;
    document.getElementById('selectedFoodInfo').textContent = selectedFood.name;
    document.getElementById('addFoodForm').style.display = 'block';
    document.getElementById('confirmAddBtn').style.display = 'inline-block';
    updateNutritionPreview();
}

document.getElementById('foodQty').addEventListener('input', updateNutritionPreview);

function updateNutritionPreview() {
    if (!selectedFood) return;
    const qty = parseFloat(document.getElementById('foodQty').value) || 0;
    const f   = qty / 100;
    const cal  = (selectedFood.calories * f).toFixed(1);
    const prot = selectedFood.protein        ? (selectedFood.protein        * f).toFixed(1) : null;
    const carbs = selectedFood.carbohydrates ? (selectedFood.carbohydrates  * f).toFixed(1) : null;
    const fat  = selectedFood.fat            ? (selectedFood.fat            * f).toFixed(1) : null;
    let html = `<span>${cal} kcal</span>`;
    if (prot)  html += `<span>P: ${prot}g</span>`;
    if (carbs) html += `<span>C: ${carbs}g</span>`;
    if (fat)   html += `<span>G: ${fat}g</span>`;
    document.getElementById('nutritionPreview').innerHTML = html;
}

async function confirmAdd() {
    if (!selectedFood || !currentMealType) return;
    const qty = parseFloat(document.getElementById('foodQty').value);
    if (!qty || qty <= 0) { showAlert('Informe uma quantidade válida.', 'error'); return; }
    try {
        await apiPost('/daily-logs/entry', {
            logDate: toISOLocal(currentDate),
            foodId: selectedFood.id,
            quantityGrams: qty,
            mealType: currentMealType
        });
        closeAddModal();
        loadReport();
    } catch (err) {
        showAlert('Erro ao adicionar: ' + err.message, 'error');
    }
}

// ── Acompanhamento Físico ─────────────────────────────────────────────────
async function loadWorkoutLogs() {
    try {
        const logs = await apiGet(`/workout-logs?date=${toISOLocal(currentDate)}`);
        renderWorkoutLogs(logs);
    } catch (err) {
        document.getElementById('workoutLogsList').innerHTML =
            `<div class="wlog-empty">Erro ao carregar treinos.</div>`;
    }
}

function renderWorkoutLogs(logs) {
    const container = document.getElementById('workoutLogsList');
    const summary   = document.getElementById('wlogSummary');

    if (!logs.length) {
        container.innerHTML = '<div class="wlog-empty">Nenhum treino registrado para este dia.</div>';
        summary.style.display = 'none';
        return;
    }

    container.innerHTML = logs.map(log => {
        const exRows = log.exercises.map(e => {
            const detail = e.exerciseType === 'MUSCULACAO'
                ? `${e.sets} séries × ${e.reps} reps @ ${e.weightKg}kg`
                : `${e.durationMinutes} min`;
            return `<div class="wlog-ex-row">
                <span class="wlog-ex-name">${e.workoutName}</span>
                <span class="wlog-ex-detail">${detail}</span>
                <span class="wlog-ex-cal">${e.estimatedCalories.toFixed(1)} kcal</span>
            </div>`;
        }).join('');
        return `<div class="wlog-card">
            <div class="wlog-card-header">
                <span class="wlog-plan-icon">🏋️</span>
                <span class="wlog-plan-name">${log.planName}</span>
                <span class="wlog-total-cal">~${log.totalCalories.toFixed(0)} kcal</span>
                <button class="wlog-del-btn" onclick="removeWorkoutLog(${log.id})" title="Remover">&#x2715;</button>
            </div>
            <div class="wlog-exercises">${exRows}</div>
        </div>`;
    }).join('');

    const totalBurned = logs.reduce((s, l) => s + l.totalCalories, 0);
    document.getElementById('sumCalQueimadas').textContent = totalBurned.toFixed(0);
    summary.style.display = 'block';
}

async function removeWorkoutLog(id) {
    if (!confirm('Remover este registro de treino?')) return;
    try {
        await apiDelete(`/workout-logs/${id}`);
        loadWorkoutLogs();
    } catch (err) {
        showAlert('Erro ao remover treino: ' + err.message, 'error');
    }
}

// ── Modal registrar treino ────────────────────────────────────────────────
async function openLogWorkoutModal() {
    wlogSelectedPlan = null;
    document.getElementById('wlogStep1').style.display = 'block';
    document.getElementById('wlogStep2').style.display = 'none';
    document.getElementById('wlogBtnBack').style.display = 'none';
    document.getElementById('wlogBtnConfirm').style.display = 'none';
    document.getElementById('alert-log-workout').className = 'alert';
    document.getElementById('logWorkoutTitle').textContent = 'Registrar Treino';
    document.getElementById('logWorkoutModal').classList.add('open');

    try {
        userPlans = await apiGet('/user-plans');
        renderPlanList();
    } catch (err) {
        document.getElementById('wlogPlanList').innerHTML =
            '<div style="color:var(--text-muted);font-size:0.85rem">Erro ao carregar treinos.</div>';
    }
}

function closeLogWorkoutModal() {
    document.getElementById('logWorkoutModal').classList.remove('open');
}

function renderPlanList() {
    const el = document.getElementById('wlogPlanList');
    if (!userPlans.length) {
        el.innerHTML = '<div style="color:var(--text-muted);font-size:0.85rem;text-align:center;padding:20px 0">Você não tem treinos montados.<br>Crie um treino na página <a href="workouts.html">Treinos</a>.</div>';
        return;
    }
    el.innerHTML = userPlans.map(p => `
        <div class="wlog-plan-item" onclick="selectPlanForLog(${p.id})">
            <span class="wlog-plan-item-name">${p.name}</span>
            <span class="wlog-plan-item-meta">${p.exercises.length} exercício(s) · ~${p.totalCalories.toFixed(0)} kcal</span>
            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><polyline points="9 18 15 12 9 6"/></svg>
        </div>
    `).join('');
}

function selectPlanForLog(planId) {
    wlogSelectedPlan = userPlans.find(p => p.id === planId);
    if (!wlogSelectedPlan) return;

    document.getElementById('logWorkoutTitle').textContent = wlogSelectedPlan.name;
    document.getElementById('wlogStep1').style.display = 'none';
    document.getElementById('wlogStep2').style.display = 'block';
    document.getElementById('wlogBtnBack').style.display = 'inline-block';
    document.getElementById('wlogBtnConfirm').style.display = 'inline-block';

    renderAdjustExercises(wlogSelectedPlan.exercises);
}

function wlogGoBack() {
    wlogSelectedPlan = null;
    document.getElementById('logWorkoutTitle').textContent = 'Registrar Treino';
    document.getElementById('wlogStep1').style.display = 'block';
    document.getElementById('wlogStep2').style.display = 'none';
    document.getElementById('wlogBtnBack').style.display = 'none';
    document.getElementById('wlogBtnConfirm').style.display = 'none';
}

function renderAdjustExercises(exercises) {
    const container = document.getElementById('wlogExercises');
    container.innerHTML = exercises.map((e, i) => {
        const badge = e.exerciseType === 'MUSCULACAO'
            ? '<span class="ex-type-badge musculacao">Musculação</span>'
            : '<span class="ex-type-badge tempo">Tempo</span>';

        let fields;
        if (e.exerciseType === 'MUSCULACAO') {
            fields = `<div class="wlog-adj-fields musculacao">
                <div class="form-group">
                    <label>Séries</label>
                    <input type="number" class="form-control" id="wex-sets-${i}" min="1" value="${e.sets || 3}">
                </div>
                <div class="form-group">
                    <label>Reps</label>
                    <input type="number" class="form-control" id="wex-reps-${i}" min="1" value="${e.reps || 10}">
                </div>
                <div class="form-group">
                    <label>Carga (kg)</label>
                    <input type="number" class="form-control" id="wex-kg-${i}" min="0" step="0.5" value="${e.weightKg || ''}">
                </div>
            </div>`;
        } else {
            fields = `<div class="wlog-adj-fields tempo">
                <div class="form-group">
                    <label>Duração (min)</label>
                    <input type="number" class="form-control" id="wex-min-${i}" min="1" value="${e.durationMinutes || ''}">
                </div>
            </div>`;
        }

        return `<div class="wlog-adj-row" data-index="${i}" data-workout-id="${e.workoutId}" data-type="${e.exerciseType}">
            <div class="wlog-adj-name">${e.workoutName} ${badge}</div>
            ${fields}
        </div>`;
    }).join('');
}

async function confirmLogWorkout() {
    if (!wlogSelectedPlan) return;

    const rows = document.querySelectorAll('#wlogExercises .wlog-adj-row');
    const exercises = [];
    let valid = true;

    rows.forEach((row, i) => {
        const workoutId   = parseInt(row.dataset.workoutId);
        const exerciseType = row.dataset.type;

        if (exerciseType === 'MUSCULACAO') {
            const sets = parseInt(document.getElementById(`wex-sets-${i}`).value);
            const reps = parseInt(document.getElementById(`wex-reps-${i}`).value);
            const kg   = parseFloat(document.getElementById(`wex-kg-${i}`).value);
            if (!sets || !reps || !kg) { valid = false; return; }
            exercises.push({ workoutId, sets, reps, weightKg: kg });
        } else {
            const min = parseFloat(document.getElementById(`wex-min-${i}`).value);
            if (!min) { valid = false; return; }
            exercises.push({ workoutId, durationMinutes: min });
        }
    });

    if (!valid) {
        const alertEl = document.getElementById('alert-log-workout');
        alertEl.textContent = 'Preencha todos os campos de cada exercício.';
        alertEl.className = 'alert show error';
        return;
    }

    try {
        await apiPost('/workout-logs', {
            planName:  wlogSelectedPlan.name,
            date:      toISOLocal(currentDate),
            exercises
        });
        closeLogWorkoutModal();
        loadWorkoutLogs();
    } catch (err) {
        const alertEl = document.getElementById('alert-log-workout');
        alertEl.textContent = err.message;
        alertEl.className = 'alert show error';
    }
}

// ── Alerta ────────────────────────────────────────────────────────────────
function showAlert(msg, type) {
    const el = document.getElementById('alert-diary');
    el.textContent = msg;
    el.className = `alert show ${type || 'error'}`;
    setTimeout(() => { el.className = 'alert'; }, 4000);
}

// ── Fechar modais clicando fora ───────────────────────────────────────────
document.getElementById('addFoodModal').addEventListener('click', function(e) {
    if (e.target === this) closeAddModal();
});
document.getElementById('logWorkoutModal').addEventListener('click', function(e) {
    if (e.target === this) closeLogWorkoutModal();
});

// ── Init ──────────────────────────────────────────────────────────────────
renderNav();
renderWeek();
loadAll();
