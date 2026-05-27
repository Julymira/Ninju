-- Foods (valores nutricionais por 100g)
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Frango Grelhado', 165, 31.0, 0.0, 3.6);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Arroz Branco Cozido', 130, 2.7, 28.2, 0.3);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Feijão Carioca Cozido', 77, 4.8, 13.6, 0.5);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Ovo Inteiro', 155, 13.0, 1.1, 11.0);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Banana', 89, 1.1, 22.8, 0.3);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Batata Doce Cozida', 86, 1.6, 20.1, 0.1);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Salmão Assado', 208, 20.0, 0.0, 13.0);
INSERT INTO foods (name, calories, protein, carbohydrates, fat) VALUES ('Aveia em Flocos', 389, 17.0, 66.0, 7.0);

-- Workouts
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Corrida Leve', 'Cardio', 300);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Musculação - Peito', 'Força', 250);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Musculação - Costas', 'Força', 260);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('HIIT 20 min', 'Cardio', 400);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Yoga', 'Flexibilidade', 150);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Ciclismo', 'Cardio', 350);
INSERT INTO workouts (name, category, estimatedCaloriesBurned) VALUES ('Natação', 'Cardio', 450);

-- Audit Logs
INSERT INTO audit_logs (action_executed, executed_by, execution_time) VALUES ('SISTEMA_INICIADO', 'system', '2026-05-25T08:00:00');
