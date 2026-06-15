requireAuth();
initNavbar();

if (isAdmin()) {
    document.getElementById('nav-users').style.display = 'block';
    document.getElementById('nav-logs').style.display = 'block';
    document.getElementById('admin-ex-global-opt').style.display = 'block';
}

let allExercises = [];
let currentPlanId = null;
let selectedExercise = null;

// ── Abas ─────────────────────────────────────────────────────────────────
function switchTab(tab) {
    document.querySelectorAll('.workout-tab').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.workout-tab-content').forEach(c => c.classList.remove('active'));
    document.querySelector(`[data-tab="${tab}"]`).classList.add('active');
    document.getElementById('tab-' + tab).classList.add('active');
}

function showAlert(msg, type) {
    const el = document.getElementById('alert-workouts');
    el.textContent = msg;
    el.className = `alert show ${type || 'error'}`;
    setTimeout(() => { el.className = 'alert'; }, 4000);
}

// ── Catálogo ──────────────────────────────────────────────────────────────
async function loadCatalog() {
    try {
        allExercises = await apiGet('/workouts');
        renderCatalog();
    } catch (err) {
        document.getElementById('catalog-body').innerHTML =
            `<tr><td colspan="5" class="empty-state">${err.message}</td></tr>`;
    }
}

function renderCatalog() {
    const user = getUser();
    const tbody = document.getElementById('catalog-body');
    if (!allExercises.length) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Nenhum exercício cadastrado.</td></tr>';
        return;
    }
    tbody.innerHTML = allExercises.map(e => {
        const canDelete = isAdmin() || (e.ownerId && e.ownerId === user.id);
        const badge = e.ownerId
            ? '<span style="font-size:0.72rem;background:#e8f5e9;color:#2e7d32;border-radius:4px;padding:1px 6px">pessoal</span>'
            : '';
        const typeLabel = e.exerciseType === 'MUSCULACAO'
            ? '<span class="ex-type-badge musculacao">Musculação</span>'
            : '<span class="ex-type-badge tempo">Tempo</span>';
        const factorLabel = e.exerciseType === 'MUSCULACAO'
            ? `${e.calorieFactor} kcal/rep/kg`
            : `${e.calorieFactor} kcal/min`;
        const delBtn = canDelete
            ? `<button class="btn-icon-danger" onclick="deleteExercise(${e.id})" title="Excluir">&#x1F5D1;</button>`
            : '';
        return `<tr>
            <td><strong>${e.name}</strong> ${badge}</td>
            <td>${e.category}</td>
            <td>${typeLabel}</td>
            <td>${factorLabel}</td>
            <td style="text-align:right">${delBtn}</td>
        </tr>`;
    }).join('');
}

async function deleteExercise(id) {
    if (!confirm('Excluir este exercício do catálogo?')) return;
    try {
        await apiDelete(`/workouts/${id}`);
        loadCatalog();
    } catch (err) {
        showAlert(err.message);
    }
}

// ── Modal adicionar ao catálogo ───────────────────────────────────────────
function openAddExerciseModal() {
    document.getElementById('exCatName').value = '';
    document.getElementById('exCatCategory').value = '';
    document.getElementById('exCatType').value = 'MUSCULACAO';
    document.getElementById('exCatCal').value = '';
    if (isAdmin()) document.getElementById('exCatGlobal').checked = false;
    document.getElementById('alert-ex-modal').className = 'alert';
    updateCatalogFactorLabel();
    document.getElementById('addExerciseModal').classList.add('open');
}

function closeAddExerciseModal() {
    document.getElementById('addExerciseModal').classList.remove('open');
}

function updateCatalogFactorLabel() {
    const type = document.getElementById('exCatType').value;
    document.getElementById('exCatFactorLabel').textContent =
        type === 'MUSCULACAO' ? 'kcal por repetição por kg *' : 'kcal por minuto *';
}

async function saveExercise() {
    const name     = document.getElementById('exCatName').value.trim();
    const category = document.getElementById('exCatCategory').value.trim();
    const type     = document.getElementById('exCatType').value;
    const factor   = parseFloat(document.getElementById('exCatCal').value);
    const alertEl  = document.getElementById('alert-ex-modal');

    if (!name || !category || isNaN(factor) || factor <= 0) {
        alertEl.textContent = 'Preencha todos os campos obrigatórios com valores válidos.';
        alertEl.className = 'alert show error';
        return;
    }

    const global = isAdmin() && document.getElementById('exCatGlobal').checked;
    try {
        await apiPost('/workouts', { name, category, exerciseType: type, calorieFactor: factor, global });
        closeAddExerciseModal();
        loadCatalog();
    } catch (err) {
        alertEl.textContent = err.message;
        alertEl.className = 'alert show error';
    }
}

// ── Meus Treinos ──────────────────────────────────────────────────────────
async function loadPlans() {
    try {
        const plans = await apiGet('/user-plans');
        renderPlans(plans);
        document.getElementById('btnNewPlan').style.display = plans.length >= 7 ? 'none' : '';
    } catch (err) {
        document.getElementById('plansGrid').innerHTML =
            `<div class="plan-empty-state">${err.message}</div>`;
    }
}

function renderPlans(plans) {
    const grid = document.getElementById('plansGrid');
    if (!plans.length) {
        grid.innerHTML = '<div class="plan-empty-state">Nenhum treino criado ainda. Crie seu primeiro treino!</div>';
        return;
    }
    grid.innerHTML = plans.map(p => {
        const preview = p.exercises.slice(0, 3).map(e => {
            const detail = e.exerciseType === 'MUSCULACAO'
                ? `${e.sets}×${e.reps} @ ${e.weightKg}kg`
                : `${e.durationMinutes} min`;
            return `<li class="plan-ex-item">${e.workoutName} — ${detail}</li>`;
        }).join('');
        const more = p.exercises.length > 3
            ? `<li class="plan-ex-more">+${p.exercises.length - 3} exercício(s)...</li>` : '';
        return `<div class="plan-card">
            <div class="plan-card-header">
                <span class="plan-card-name">${p.name}</span>
                <div class="plan-card-actions">
                    <button class="btn-icon-danger" onclick="deletePlan(${p.id})" title="Excluir treino">&#x1F5D1;</button>
                </div>
            </div>
            <div class="plan-card-meta">${p.exercises.length} exercício(s) · ~${p.totalCalories} kcal estimado</div>
            <ul class="plan-ex-list">${preview}${more}</ul>
            <button class="btn-open-plan" onclick="openPlanBuilder(${p.id}, '${p.name.replace(/'/g,"\\'")}')">Editar</button>
        </div>`;
    }).join('');
}

async function deletePlan(id) {
    if (!confirm('Excluir este treino?')) return;
    try {
        await apiDelete(`/user-plans/${id}`);
        loadPlans();
    } catch (err) {
        showAlert(err.message);
    }
}

// ── Novo plano ────────────────────────────────────────────────────────────
function openNewPlanModal() {
    document.getElementById('newPlanName').value = '';
    document.getElementById('alert-new-plan').className = 'alert';
    document.getElementById('newPlanModal').classList.add('open');
}

function closeNewPlanModal() {
    document.getElementById('newPlanModal').classList.remove('open');
}

async function createPlan() {
    const name    = document.getElementById('newPlanName').value.trim();
    const alertEl = document.getElementById('alert-new-plan');
    if (!name) {
        alertEl.textContent = 'Informe um nome para o treino.';
        alertEl.className = 'alert show error';
        return;
    }
    try {
        await apiPost('/user-plans', { name });
        closeNewPlanModal();
        loadPlans();
    } catch (err) {
        alertEl.textContent = err.message;
        alertEl.className = 'alert show error';
    }
}

// ── Builder do plano ──────────────────────────────────────────────────────
async function openPlanBuilder(planId, planName) {
    currentPlanId = planId;
    document.getElementById('builderTitle').textContent = planName;
    document.getElementById('alert-builder').className = 'alert';
    document.getElementById('planBuilderModal').classList.add('open');
    await refreshBuilder();
}

function closePlanBuilder() {
    document.getElementById('planBuilderModal').classList.remove('open');
    currentPlanId = null;
    loadPlans();
}

async function refreshBuilder() {
    try {
        const plans = await apiGet('/user-plans');
        const plan  = plans.find(p => p.id === currentPlanId);
        if (!plan) return;
        const container = document.getElementById('planExercises');
        if (!plan.exercises.length) {
            container.innerHTML = '<div class="builder-empty">Nenhum exercício adicionado ainda.</div>';
            return;
        }
        container.innerHTML = plan.exercises.map(e => {
            const detail = e.exerciseType === 'MUSCULACAO'
                ? `${e.sets} séries × ${e.reps} reps @ ${e.weightKg}kg`
                : `${e.durationMinutes} min`;
            return `<div class="builder-ex-row">
                <span class="builder-ex-name">${e.workoutName}</span>
                <span class="builder-ex-detail">${detail}</span>
                <span class="builder-ex-cal">${e.estimatedCalories} kcal</span>
                <button class="btn-icon-danger" onclick="removeExerciseFromPlan(${e.id})" title="Remover">&#x2715;</button>
            </div>`;
        }).join('');
    } catch (err) {
        document.getElementById('alert-builder').textContent = err.message;
        document.getElementById('alert-builder').className = 'alert show error';
    }
}

async function removeExerciseFromPlan(exId) {
    try {
        await apiDelete(`/user-plans/${currentPlanId}/exercises/${exId}`);
        refreshBuilder();
    } catch (err) {
        document.getElementById('alert-builder').textContent = err.message;
        document.getElementById('alert-builder').className = 'alert show error';
    }
}

// ── Picker de exercício para o plano ─────────────────────────────────────
function openPickExercise() {
    selectedExercise = null;
    document.getElementById('exSearch').value = '';
    document.getElementById('exResults').innerHTML = '';
    document.getElementById('exForm').style.display = 'none';
    document.getElementById('confirmExBtn').style.display = 'none';
    document.getElementById('exPreview').innerHTML = '';
    document.getElementById('pickExerciseModal').classList.add('open');
    renderExResults(allExercises.slice(0, 20));
}

function closePickExercise() {
    document.getElementById('pickExerciseModal').classList.remove('open');
}

function searchExercises() {
    const q = document.getElementById('exSearch').value.trim().toLowerCase();
    const list = q
        ? allExercises.filter(e => e.name.toLowerCase().includes(q) || e.category.toLowerCase().includes(q)).slice(0, 30)
        : allExercises.slice(0, 20);
    renderExResults(list);
}

function renderExResults(list) {
    const el = document.getElementById('exResults');
    if (!list.length) {
        el.innerHTML = '<div style="padding:10px;font-size:0.85rem;color:var(--text-muted)">Nenhum exercício encontrado</div>';
        return;
    }
    el.innerHTML = list.map(e => {
        const badge = e.exerciseType === 'MUSCULACAO'
            ? '<span class="ex-type-badge musculacao">Musculação</span>'
            : '<span class="ex-type-badge tempo">Tempo</span>';
        const factorInfo = e.exerciseType === 'MUSCULACAO'
            ? `${e.calorieFactor} kcal/rep/kg`
            : `${e.calorieFactor} kcal/min`;
        return `<div class="ex-result-item" onclick="selectExercise(${e.id})">
            <div class="ex-result-main">
                <span class="ex-result-name">${e.name}</span>
                ${badge}
            </div>
            <div class="ex-result-sub">${e.category} · ${factorInfo}</div>
        </div>`;
    }).join('');
}

function selectExercise(id) {
    selectedExercise = allExercises.find(e => e.id === id);
    if (!selectedExercise) return;
    const badge = selectedExercise.exerciseType === 'MUSCULACAO'
        ? '<span class="ex-type-badge musculacao">Musculação</span>'
        : '<span class="ex-type-badge tempo">Tempo</span>';
    document.getElementById('exSelectedInfo').innerHTML =
        `<strong>${selectedExercise.name}</strong> ${badge}`;

    const musculacaoFields = document.getElementById('exFieldsMusculacao');
    const tempoFields = document.getElementById('exFieldsTempo');

    if (selectedExercise.exerciseType === 'MUSCULACAO') {
        musculacaoFields.style.display = 'grid';
        tempoFields.style.display = 'none';
    } else {
        musculacaoFields.style.display = 'none';
        tempoFields.style.display = 'block';
    }

    document.getElementById('exForm').style.display = 'block';
    document.getElementById('confirmExBtn').style.display = 'inline-block';
    updateExPreview();
}

function updateExPreview() {
    if (!selectedExercise) return;
    let cal = 0;
    let desc = '';

    if (selectedExercise.exerciseType === 'MUSCULACAO') {
        const sets = parseInt(document.getElementById('exSets').value) || 0;
        const reps = parseInt(document.getElementById('exReps').value) || 0;
        const kg   = parseFloat(document.getElementById('exWeight').value) || 0;
        cal = sets * reps * kg * selectedExercise.calorieFactor;
        desc = `${sets} séries × ${reps} reps @ ${kg}kg`;
    } else {
        const min = parseFloat(document.getElementById('exDuration').value) || 0;
        cal = min * selectedExercise.calorieFactor;
        desc = `${min} min`;
    }

    document.getElementById('exPreview').innerHTML =
        `<span>${desc}</span><span>~${cal.toFixed(1)} kcal estimado</span>`;
}

async function confirmAddExercise() {
    if (!selectedExercise || !currentPlanId) return;

    let body = { workoutId: selectedExercise.id };

    if (selectedExercise.exerciseType === 'MUSCULACAO') {
        const sets = parseInt(document.getElementById('exSets').value);
        const reps = parseInt(document.getElementById('exReps').value);
        const kg   = parseFloat(document.getElementById('exWeight').value);
        if (!sets || !reps || !kg) {
            document.getElementById('alert-builder').textContent = 'Preencha séries, reps e carga.';
            document.getElementById('alert-builder').className = 'alert show error';
            closePickExercise();
            return;
        }
        body = { ...body, sets, reps, weightKg: kg };
    } else {
        const min = parseFloat(document.getElementById('exDuration').value);
        if (!min) {
            document.getElementById('alert-builder').textContent = 'Informe a duração em minutos.';
            document.getElementById('alert-builder').className = 'alert show error';
            closePickExercise();
            return;
        }
        body = { ...body, durationMinutes: min };
    }

    try {
        await apiPost(`/user-plans/${currentPlanId}/exercises`, body);
        closePickExercise();
        refreshBuilder();
    } catch (err) {
        document.getElementById('alert-builder').textContent = err.message;
        document.getElementById('alert-builder').className = 'alert show error';
        closePickExercise();
    }
}

// ── Fechar modais clicando fora ───────────────────────────────────────────
['newPlanModal','planBuilderModal','pickExerciseModal','addExerciseModal'].forEach(id => {
    document.getElementById(id).addEventListener('click', function(e) {
        if (e.target === this) this.classList.remove('open');
    });
});

// ── Init ──────────────────────────────────────────────────────────────────
loadPlans();
loadCatalog();
