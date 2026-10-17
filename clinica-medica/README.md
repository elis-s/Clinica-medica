# Sistema de Controle de Atendimento da Clínica Médica (versão mínima)

Arquitetura pedida: **Angular (front) → Java + Spring (back) → PostgreSQL**.

```
clinica-medica/
├── backend/             Java 17 + Spring Boot 3 + Spring Security (login no banco) + JPA
├── frontend/            Angular 18 (único ponto de acesso dos usuários)
└── docker-compose.yml   (opcional) sobe só o PostgreSQL
```

## Pré-requisitos
- JDK 17 ou superior e Maven 3.9+
- Node.js 20 ou 22 (com npm)
- PostgreSQL (ou Docker)

## Como rodar (3 passos)

**1) Banco de dados** — escolha uma opção:
- Docker: `docker compose up -d` (na raiz do projeto)
- Sem Docker: crie um banco chamado `clinica` no seu PostgreSQL. Usuário/senha padrão do projeto: `postgres` / `postgres`
  (se for diferente, ajuste `backend/src/main/resources/application.properties`).

**2) Back-end** (porta 8080)
```
cd backend
mvn spring-boot:run
```
As tabelas são criadas automaticamente e, no primeiro start, entram dados de teste.

**3) Front-end** (porta 4200)
```
cd frontend
npm install
npm start
```
Abra **http://localhost:4200**

## Usuários de teste
| Login | Senha | Perfil |
|---|---|---|
| admin | admin123 | ADMINISTRADOR |
| medico | medico123 | COLABORADOR (Dra. Ana Souza, médica) |
| atendente | atendente123 | USUARIO (atendente) |

Um usuário pode ter mais de um perfil (tela *Usuários*, como admin).

## Roteiro de teste (sugestão para a apresentação)
1. **admin** → *Clínicas*: cadastrar, editar e inativar (pede o motivo; não existe exclusão).
2. **admin** → *Setores*: cadastrar um setor (precisa de clínica e 1 responsável), editar e inativar.
3. **admin** → *Convênios*, *Colaboradores* (médico exige especialidade/conselho/registro) e *Usuários* (definir perfis).
4. **admin** → *Relatórios*: relatório completo com identificação de quem emitiu. *Auditoria*: logins e operações registradas.
5. **atendente** → *Pacientes*: cadastrar paciente (o **prontuário é criado junto**, com o convênio).
6. **atendente** → *Agendamentos*: criar (nasce "AGENDADO"); botão *Receber paciente* mostra o agendamento + prontuário.
7. **medico** → *Atendimentos*: novo atendimento (médico capturado pelo login), editar e **Encerrar** → o agendamento vira "FINALIZADO".
8. **medico** → *Solicitações*: pedir medicação (quantidade + justificativa).
9. **atendente** → *Solicitações*: **Entregar** (baixa o estoque; registra quem solicitou, quem entregou e data/hora).
10. **atendente** → *Medicações/Estoque*: cadastrar, ver saldo, **Repor estoque**, ver *Movimentações*. A "Insulina NPH" já vem
    com saldo 8 (abaixo do mínimo de 10) para mostrar o alerta **Repor? SIM**.
11. **medico** → *Relatórios*: relatório só com a movimentação dele.

## Testando as restrições arquiteturais
- Back-end só responde ao front: com o back rodando, `curl -i http://localhost:8080/api/clinicas` → **403** (sem a chave do front).
  Mesmo com a chave, sem login → **401**. O front envia a chave pelo proxy (`frontend/proxy.conf.json`).
  A chave está em `app.frontend-key` (`application.properties`) e no proxy do Angular — devem ser iguais.
- Spring Security usa o **banco** (tabela `usuario`, senhas com BCrypt, perfis na tabela `usuario_perfis`) — nada em memória.
- Só o back conecta no PostgreSQL (a conexão está só no `application.properties` do back).

## Onde está cada coisa
| Requisito | Onde |
|---|---|
| Clínica, Setor, Convênio, Colaborador, Usuário/Perfis | `backend/.../controller/*Controller.java` + `model/*` |
| Paciente ↔ Prontuário 1:1 | `PacienteController` (cria junto) / `Prontuario` |
| Agendamento (AGENDADO → FINALIZADO) | `AgendamentoController`, `AtendimentoController.encerrar` |
| Atendimento / orientações de enfermagem | `AtendimentoController` |
| Medicação, estoque, solicitação, entrega | `MedicacaoController`, `SolicitacaoController`, `service/EstoqueService` |
| Logs de auditoria | `config/AuditoriaFilter` |
| Relatórios | `RelatorioController` |
| Segurança (Spring Security + só front) | `security/SecurityConfig`, `security/FrontendOnlyFilter` |
| Telas | `frontend/src/app` (`recursos.ts` define cada tela) |

## Decisões simples tomadas (dúvidas do enunciado)
- Nenhum registro é excluído: só **inativado** (histórico preservado).
- Convênio fica no prontuário do paciente.
- Quem solicita medicação é o **colaborador**; quem **entrega** (baixa o estoque) é o **usuário/atendente**.
- O alerta de reposição aparece quando saldo ≤ estoque mínimo (padrão 10).
- Autenticação por HTTP Basic + Spring Security (login/senha validados no banco a cada requisição).
- O enunciado cita "espaços/aprovação" e "IES" (sobra de outro cenário) — ignorados; vale perguntar ao professor pelo Classroom.
- O enunciado pede usar os repositórios GitHub da UCSAL: suba `backend/` e `frontend/` neles.
