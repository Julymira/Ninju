# Ninju — API de Acompanhamento Fitness e Nutricional

Projeto prático da disciplina **Programação para Web** (IFG - Campus Luziânia).  
Back-end REST construído com **Quarkus 3**, **Java 21**, **JAX-RS**, **Hibernate ORM** e banco **H2** em memória.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java JDK | 21 |
| Maven | 3.9+ |
| OpenSSL | qualquer versão recente |

> **OpenSSL no Windows:** já vem incluso no [Git for Windows](https://git-scm.com/download/win). Após instalar o Git, o `openssl` fica disponível no Git Bash e no terminal do VS Code.

> Não é necessário instalar banco de dados. O H2 sobe automaticamente em memória junto com a aplicação.

---

## Configuração inicial após clonar

As chaves RSA usadas para assinar e verificar os tokens JWT **não são versionadas** por segurança.  
É necessário gerá-las uma única vez após clonar o repositório.

Abra o terminal do VS Code (`Ctrl + J`) e rode os dois comandos abaixo:

```bash
openssl genpkey -algorithm RSA -out src/main/resources/privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/privateKey.pem -out src/main/resources/publicKey.pem
```

> O `openssl` já vem instalado junto com o [Git for Windows](https://git-scm.com/download/win). Se o comando não for reconhecido, instale o Git e reinicie o VS Code.

Os arquivos `privateKey.pem` e `publicKey.pem` serão criados em `src/main/resources/` e ignorados pelo `.gitignore`.

---

## Rodar em modo desenvolvimento (hot reload)

```bash
mvn quarkus:dev
```

A API ficará disponível em `http://localhost:8080`.  
O banco H2 é recriado do zero a cada inicialização (`drop-and-create`).  
Os dados de seed (usuários, alimentos e treinos) são inseridos automaticamente.

**Credenciais de teste:**

| E-mail | Senha | Perfil |
|---|---|---|
| admin@ninju.com | password123 | ADMIN |
| joao@ninju.com | password123 | USER |
| maria@ninju.com | password123 | USER |

---

## Documentação da API

Com a aplicação rodando, acesse o Swagger UI:

```
http://localhost:8080/q/swagger-ui
```

---

## Estrutura do projeto

```
src/main/java/com/ninju/
├── controller/   # Endpoints JAX-RS (camada Controller do MVC)
├── bo/           # Business Objects — regras de negócio
├── dao/          # Data Access Objects — acesso ao banco
├── dto/          # Data Transfer Objects — contratos da API
├── model/        # Entidades JPA
└── util/         # Utilitários (BCrypt, DataSeeder)

src/main/resources/META-INF/resources/
├── index.html        # Tela de login
├── pages/            # Demais páginas da aplicação
├── css/              # Estilos
└── js/               # Lógica do front-end
```

---

## Entidades principais

| Entidade | Descrição |
|---|---|
| `User` | Usuário com papel `ADMIN` ou `USER` |
| `Food` | Alimento com informações nutricionais (calorias, proteína, carbo, gordura) |
| `Workout` | Exercício físico com categoria, tipo (`MUSCULACAO` / `TEMPO`) e fator calórico |
| `DailyLog` | Diário diário do usuário — refeições e notas de treino |
| `DailyLogEntry` | Item de refeição vinculado a um `DailyLog` (alimento + quantidade + tipo de refeição) |
| `WorkoutLog` | Registro de um treino executado com estimativa de calorias gastas |
| `WorkoutLogExercise` | Exercício individual dentro de um `WorkoutLog` |
| `UserPlan` | Plano de treino personalizado do usuário |
| `UserPlanExercise` | Exercício dentro de um `UserPlan` com séries/reps ou duração |
| `WaterLog` | Registro diário de consumo e meta de hidratação |
| `AuditLog` | Registro de auditoria de todas as ações do sistema |

---

## Endpoints disponíveis

| Método | Endpoint | Acesso |
|---|---|---|
| POST | `/auth/login` | Público |
| GET | `/users` | ADMIN |
| POST | `/users` | ADMIN |
| PUT | `/users/{id}` | ADMIN |
| DELETE | `/users/{id}` | ADMIN |
| PUT | `/users/{id}/password` | ADMIN + USER (próprio) |
| PUT | `/users/{id}/goals` | ADMIN + USER (próprio) |
| GET | `/users/{id}/avatar` | ADMIN + USER |
| PUT | `/users/{id}/avatar` | ADMIN + USER (próprio) |
| GET | `/foods` | ADMIN + USER |
| POST | `/foods` | ADMIN + USER (global só ADMIN) |
| DELETE | `/foods/{id}` | ADMIN ou dono |
| GET | `/workouts` | ADMIN + USER |
| POST | `/workouts` | ADMIN + USER (global só ADMIN) |
| DELETE | `/workouts/{id}` | ADMIN ou dono |
| GET | `/daily-logs` | ADMIN + USER (logs próprios) |
| POST | `/daily-logs/refeicao` | ADMIN + USER |
| POST | `/daily-logs/treino` | ADMIN + USER |
| POST | `/daily-logs/entry` | ADMIN + USER |
| DELETE | `/daily-logs/entry/{id}` | ADMIN + USER (próprio) |
| GET | `/daily-logs/report` | ADMIN + USER |
| GET | `/workout-logs` | ADMIN + USER |
| POST | `/workout-logs` | ADMIN + USER |
| DELETE | `/workout-logs/{id}` | ADMIN + USER (próprio) |
| GET | `/plans` | ADMIN + USER |
| POST | `/plans` | ADMIN + USER |
| PUT | `/plans/{id}` | ADMIN + USER (próprio) |
| DELETE | `/plans/{id}` | ADMIN + USER (próprio) |
| POST | `/plans/{id}/exercises` | ADMIN + USER (próprio) |
| DELETE | `/plans/{id}/exercises/{eid}` | ADMIN + USER (próprio) |
| GET | `/water` | ADMIN + USER |
| POST | `/water/add` | ADMIN + USER |
| PUT | `/water/goal` | ADMIN + USER |
| GET | `/audit-logs` | ADMIN |

---

## Requisitos do projeto atendidos

- [x] Linguagem Java 21 + Quarkus 3 (Jakarta EE)
- [x] Arquitetura MVC em camadas
- [x] JAX-RS para endpoints REST
- [x] Padrão DAO e Entity para cada entidade (11 entidades, 10 DAOs)
- [x] Padrão BO para todas as regras de negócio (8 BOs)
- [x] DTOs para comunicação front-end ↔ back-end (19 DTOs)
- [x] Autenticação de usuário (e-mail + senha com BCrypt + JWT)
- [x] Manter usuário (CRUD completo — exclusivo ADMIN)
- [x] Controle de perfil / roles (ADMIN vs USER) com `@RolesAllowed`
- [x] Caso de uso 1: Diário nutricional (registrar refeições, adicionar alimentos, relatório)
- [x] Caso de uso 2: Registro de treinos executados com estimativa calórica
- [x] Caso de uso 3: Planos de treino personalizados
- [x] Caso de uso 4: Controle de hidratação diária
- [x] Rastreabilidade e auditoria (AuditLog em todas as ações de todos os BOs)
- [x] Front-end completo (HTML + CSS + JS — 10 páginas com navegação)
