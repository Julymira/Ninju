# Ninju — API de Acompanhamento Fitness e Nutricional

Projeto prático da disciplina **Programação para Web** (IFG - Campus Luziânia).  
Back-end REST construído com **Quarkus 3**, **Java 21**, **JAX-RS**, **Hibernate ORM** e banco **H2** em memória.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java JDK | 21 |
| Maven | 3.9+ |

> Não é necessário instalar banco de dados. O H2 sobe automaticamente em memória junto com a aplicação.

---

## Clonar o repositório

```bash
git clone <URL-do-repositório>
cd Ninju
```

---

## Rodar em modo desenvolvimento (hot reload)

```bash
mvn quarkus:dev
```

A API ficará disponível em `http://localhost:8080`.  
O banco H2 é recriado do zero a cada inicialização (`drop-and-create`).

---

## Rodar os testes

```bash
mvn test
```

---

## Gerar o JAR e rodar em produção

```bash
mvn package
java -jar target/quarkus-app/quarkus-run.jar
```

---

## Estrutura do projeto

```
src/main/java/com/ninju/
├── controller/   # Endpoints JAX-RS (camada View/Controller do MVC)
├── bo/           # Business Objects — regras de negócio
├── dao/          # Data Access Objects — acesso ao banco
├── dto/          # Data Transfer Objects — contratos da API
└── model/        # Entidades JPA (User, Food, Workout, DailyLog, AuditLog)
```

---

## Entidades principais

| Entidade | Descrição |
|---|---|
| `User` | Usuário da aplicação com papel `ADMIN` ou `USER` |
| `Food` | Alimento com informações nutricionais (calorias, proteína, carboidrato, gordura) |
| `Workout` | Exercício físico com categoria e estimativa calórica |
| `DailyLog` | Diário diário vinculando refeições e treinos do usuário |
| `AuditLog` | Registro de auditoria de todas as ações executadas no sistema |

---

## Requisitos do projeto atendidos

- [x] Linguagem Java 21 + Quarkus 3 (Java EE / Jakarta EE)
- [x] Arquitetura MVC em camadas
- [x] JAX-RS para endpoints REST
- [x] Padrão DAO e Entity para cada entidade
- [x] Banco H2 com Hibernate ORM
- [ ] Padrão BO (Business Objects)
- [ ] DTOs para comunicação front-end ↔ back-end
- [ ] Autenticação de usuário (e-mail + senha)
- [ ] Controle de perfil / roles (ADMIN vs USER)
- [ ] Casos de uso de domínio completos (registro de refeição e treino)
- [ ] Rastreabilidade e auditoria (AuditLog em todas as ações)
- [ ] Front-end (HTML, CSS, JS)

---

## Endpoint de teste atual

```
GET /teste
```

Retorna uma lista de usuários cadastrados no banco (usado para validar a conexão com H2).
