# 🗂️ Mapeamento Detalhado - Renomeação de Portos AUTH

**Documento de Referência Rápida para Refatoração**

---

## 📋 TABELA DE REFATORAÇÃO - Todos os Portos

### Domain Layer Ports (domain/port/out/)

| Arquivo Atual | Status | Ação | Novo Nome (se aplicável) |
|---|---|---|---|
| `TokenGeneratorPort.java` | ✅ OK | Manter | - |
| `TokenValidatorPort.java` | ✅ OK | Manter | - |

**Total Domain**: ✅ 2 portos corretos

---

### Application Layer Ports (application/port/out/)

#### 🔴 GRUPO 1: Persistence Ports - DEVEM SER RENOMEADOS

| Arquivo Atual | Tipo | ✅ Novo Nome | Responsabilidade |
|---|---|---|---|
| `UserRepository.java` | Agregador | `UserPersistencePort` | CRUD User + Auth/Authz |
| `ProfileRepository.java` | Port | `ProfilePersistencePort` | CRUD Profile |
| `GroupRepository.java` | Port | `GroupPersistencePort` | CRUD Group |
| `GroupUserRepository.java` | Port | `GroupUserPersistencePort` | CRUD GroupUser (relacionamentos) |
| `GroupProfileRepository.java` | Port | `GroupProfilePersistencePort` | CRUD GroupProfile (relacionamentos) |
| `FunctionalityRepository.java` | Port | `FunctionalityPersistencePort` | CRUD Functionality |
| `ProfileFunctionalityRepository.java` | Port | `ProfileFunctionalityPersistencePort` | CRUD ProfileFunctionality (relacionamentos) |
| `PasswordTokenRepository.java` | Port | `PasswordTokenPersistencePort` | Gerenciar tokens de reset |
| `UserSessionRepository.java` | Port | `UserSessionPersistencePort` | Gerenciar sessões |
| `AuditLogRepository.java` | Port | `AuditLogPort` | Registrar eventos de auditoria |

**Total Persistence**: 10 portos a renomear

---

#### 🟡 GRUPO 2: Segregated Query/Command Ports - RENOMEAR PARA CLAREZA

| Arquivo Atual | Tipo | ✅ Novo Nome | Responsabilidade |
|---|---|---|---|
| `UserReader.java` | Query Port | `UserQueryPort` | Leitura de User |
| `UserWriter.java` | Command Port | `UserCommandPort` | Escrita de User |
| `AuthenticationRepository.java` | Auth Port | `AuthenticationPort` | Validar credenciais |
| `AuthorizationRepository.java` | Auth Port | `AuthorizationPort` | Verificar permissões |
| `UserStatusRepository.java` | Status Port | `UserStatusPort` | Gerenciar status de User |

**Total Segregated**: 5 portos a renomear

---

#### 🔵 GRUPO 3: External Service Ports - RENOMEAR PARA CLAREZA

| Arquivo Atual | Tipo | ✅ Novo Nome | Responsabilidade |
|---|---|---|---|
| `GatewayEmail.java` | Email Service | `EmailSendingPort` | Enviar e-mails (reset pwd, notificações) |

**Total External Services**: 1 porto a renomear

---

## 📊 RESUMO CONSOLIDADO

```
┌─────────────────────────────────────────────────────────────┐
│ ESTATÍSTICAS DE REFATORAÇÃO                                 │
├─────────────────────────────────────────────────────────────┤
│ Domain Layer Ports          │  2  │ ✅ OK                    │
│ Application Persistence     │ 10  │ 🔴 RENOMEAR               │
│ Application Segregated      │  5  │ 🟡 RENOMEAR               │
│ External Services           │  1  │ 🔵 RENOMEAR               │
├─────────────────────────────────────────────────────────────┤
│ TOTAL PORTOS AUTH           │ 18  │                           │
│ TOTAL A REFATORAR           │ 16  │ 89% dos portos            │
│ JÁ CORRETOS                 │  2  │ 11% dos portos            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔀 IMPACTO EM OUTRAS CAMADAS

### 1️⃣ Adapters Afetados (adapter/port/out/)

**Arquivos de implementação que precisam update:**

```
adapter/port/out/
├── persistence/
│   ├── UserPersistenceAdapter.java          (impl UserPersistencePort)
│   ├── ProfilePersistenceAdapter.java       (impl ProfilePersistencePort)
│   └── ... (14 implementações no total)
│
├── email/
│   └── EmailSendingAdapter.java             (impl EmailSendingPort)
│
└── jpa/repository/
    └── UserRepositoryJpa.java               (⚠️ MANTER - é infraestrutura JPA)
```

**Total de adapters a atualizar**: ~16 classes

---

### 2️⃣ Use Cases / Application Services (application/service/)

**Exemplos de refatoração necessária:**

```java
// ❌ ANTES
@Service
public class LoginUseCase {
    @Autowired
    private UserRepository userRepository;           // ❌ Antigo
    
    @Autowired
    private GatewayEmail gatewayEmail;              // ❌ Antigo
    
    @Autowired
    private PasswordTokenRepository tokenRepository; // ❌ Antigo
}

// ✅ DEPOIS
@Service
public class LoginUseCase {
    private final UserPersistencePort userPort;                  // ✓
    private final EmailSendingPort emailPort;                    // ✓
    private final PasswordTokenPersistencePort tokenPort;        // ✓
}
```

**Classes de use case afetadas** (estimado): 8-12 classes

---

### 3️⃣ Controllers / REST Adapters (adapter/in/)

**Impacto**: NENHUM (controllers não conhecem diretamente os ports)

---

### 4️⃣ Tests (test/)

**Arquivos de teste que precisam update:**

```
test/java/br/sptrans/scd/auth/
├── application/
│   └── service/
│       ├── LoginUseCaseTest.java             (refatorar mocks/stubs)
│       └── ... (outros tests de use case)
│
└── adapter/
    └── persistence/
        └── UserPersistenceAdapterTest.java  (refatorar mocks)
```

**Total de tests a atualizar**: ~8-10 classes de teste

---

## 🎯 CHECKLIST COMPLETO DE ARQUIVOS

### Fase 1: Criar Novas Interfaces (application/port/out/)

```
✅ Criar UserPersistencePort.java
✅ Criar ProfilePersistencePort.java
✅ Criar GroupPersistencePort.java
✅ Criar GroupUserPersistencePort.java
✅ Criar GroupProfilePersistencePort.java
✅ Criar FunctionalityPersistencePort.java
✅ Criar ProfileFunctionalityPersistencePort.java
✅ Criar PasswordTokenPersistencePort.java
✅ Criar UserSessionPersistencePort.java
✅ Criar AuditLogPort.java
✅ Renomear UserReader → UserQueryPort
✅ Renomear UserWriter → UserCommandPort
✅ Renomear AuthenticationRepository → AuthenticationPort
✅ Renomear AuthorizationRepository → AuthorizationPort
✅ Renomear UserStatusRepository → UserStatusPort
✅ Renomear GatewayEmail → EmailSendingPort
```

### Fase 2: Atualizar Implementações (adapter/)

```
✅ Atualizar 16 adapters de implementação
✅ Atualizar 8-12 use cases
✅ Atualizar 8-10 testes
```

### Fase 3: Cleanup

```
❌ Deletar UserRepository.java (transferir conteúdo para UserPersistencePort)
❌ Deletar ProfileRepository.java
❌ ... (deletar 16 interfaces antigas)
```

---

## ⚠️ CUIDADOS

1. **Manter backward compatibility temporariamente**:
   - Interfaces antigas podem ser mantidas como `@Deprecated` durante transição
   - Adapters gradualmente refatorados

2. **JPA Repositories**:
   - Nunca renomear `UserRepositoryJpa` etc
   - Essas NÃO SÃO domain ports, são artefatos de infraestrutura

3. **Git/Version Control**:
   - Fazer commit atômico por fase
   - Testar após cada rename

4. **IDE Refactoring**:
   - Usar "Rename" do IDE (IntelliJ/Eclipse) com "Find usages"
   - Garante que nenhuma referência é perdida

---

## 📈 Ordem Recomendada de Execução

```
1º PASSO: Criar novas interfaces (não quebra nada - são novas)
2º PASSO: Criar novos adapters/implementações
3º PASSO: Atualizar use cases com injeção dos novos ports
4º PASSO: Atualizar testes
5º PASSO: Executar suite de testes (Maven: mvn test)
6º PASSO: Deletar interfaces antigas e adapters antigos
7º PASSO: Cleanup final (remover imports não utilizados)
```

---

## 🧪 Validação Pós-Refatoração

```bash
# Compilar
$ mvn clean compile

# Executar testes
$ mvn test

# Análise de código
$ mvn sonar:sonar

# Verificar se NENHUMA referência a "Repository", "Gateway" persiste
$ grep -r "implements.*Repository" src/
$ grep -r "implements.*Gateway" src/
```

✅ Sucesso: Nenhum resultado acima, apenas em adapter/jpa/

---

