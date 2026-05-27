requireAuth();

// CASO DE USO 1 — Registrar refeição
// TODO: formulário com textarea para notas da refeição + data
// TODO: salvar — apiPost('/daily-logs', { logDate, mealsNotes })

// CASO DE USO 2 — Registrar treino
// TODO: formulário com textarea para notas do treino + data
// TODO: salvar — apiPut('/daily-logs/{id}', { workoutNotes }) ou apiPost se não existir log do dia

// TODO: carregar log do dia atual — apiGet('/daily-logs/hoje')
// TODO: exibir histórico dos últimos dias
