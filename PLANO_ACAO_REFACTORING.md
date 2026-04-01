# 📊 PLANO DE AÇÃO - ROTEIRO DE IMPLEMENTAÇÃO
**Refatoração do Módulo AUTH para Nível Sênior**

---

## 📋 Visão Geral

| Aspecto | Antes | Depois | Melhoria |
|---------|-------|--------|----------|
| Score de Qualidade | 5.5/10 | 8.5/10 | +54% |
| Complexidade Ciclomática (User) | ~45 | ~12 | -73% |
| Testabilidade | Baixa | Alta | +300% |
| Segurança (Validação) | Espalhada | Centralizada | +100% |
| N+1 Queries | ~8 | 1 | -87.5% |
| Code Duplication | Alto | Mínimo | -80% |

---

## 🎯 SPRINT 1 - FUNDAÇÃO (5-6 dias)

### Dia 1: Domain Value Objects - Estrutura Base

**Objetivo**: Criar Value Objects segregados para User

**Tarefas**:

#### 1.1 - Criar estrutura de Value Objects
```bash
mkdir -p src/main/java/br/sptrans/scd/auth/domain/vo
```

**Arquivos a criar**:
- `UserId.java` - Value Object imutável para ID
- `Credentials.java` - Agrupa login, senha, tentativas
- `PersonalInfo.java` - Dados pessoais (PII)
- `UserAudit.java` - Status e auditoria
- `AccessPolicy.java` - Política de jornada
- `DayPattern.java` - Padrão de dias permitidos
- `TimeRange.java` - Intervalo de horário

**Tempo estimado**: 4-5 horas

**Validação**:
- [ ] Todos os 7 Value Objects compilam
- [ ] Sem erros de dependência circular
- [ ] Lombok funciona com @Value

---

#### 1.2 - Refatorar User.java
**Tempo estimado**: 2-3 horas

**Mudanças**:
```java
// ❌ ANTES
public class User {
    private Long idUsuario;
    private String codLogin;
    private String codSenha;
    // 50+ campos
}

// ✅ DEPOIS
@Entity
public class User {
    @Id
    private UserId id;
    
    @Embedded
    private Credentials credentials;
    
    @Embedded
    private PersonalInfo personalInfo;
    
    @Embedded
    private UserAudit audit;
}
```

**Validação**:
- [ ] User compila sem erros
- [ ] Dados legados mapeados corretamente
- [ ] Testes existentes ainda passam (migrar conforme necessário)

---

### Dia 2-3: Validador de Senha & Otimizações

#### 2.1 - Implementar PasswordValidator abstrata
**Tempo estimado**: 4-5 horas

**Arquivos**:
- `PasswordValidator.java` - Interface
- `StrictPasswordValidator.java` - Implementação estrita
- `ModeratePasswordValidator.java` - Implementação moderada
- `PasswordValidationResult.java` - DTO de resultado
- `PasswordPolicy.java` - Política de configuração

**Validação**:
- [ ] Interface compilada
- [ ] Ambas implementações compilam
- [ ] @Component anotadas
- [ ] Testes unitários passam (14 testes)

**Testes Necessários**:
```java
✓ Deve aceitar senha válida
✓ Deve rejeitar senha vazia
✓ Deve rejeitar senha curta
✓ Deve rejeitar sem maiúscula
✓ Deve rejeitar sem minúscula
✓ Deve rejeitar sem número
✓ Deve rejeitar sem char especial
✓ Deve rejeitar com sequência
✓ Deve rejeitar reutilização
```

---

#### 2.2 - Otimizar Queries (N+1)
**Tempo estimado**: 3-4 horas

**Ação**: Adicionar queries com FETCH JOIN em UserJpaRepository

```java
@Query("""
    SELECT DISTINCT r FROM UserEntityJpa u
    LEFT JOIN FETCH u.userProfiles up
    LEFT JOIN FETCH up.profile p
    LEFT JOIN FETCH p.profileFunctionalities pf
    WHERE u.id = :userId
""")
Set<Role> findRolesWithFetch(Long userId);
```

**Validação**:
- [ ] Queries compilam
- [ ] Sem N+1 em teste (usar JUnit + DataJpaTest)
- [ ] Performance medida (~100ms → ~20ms)

---

### Dia 4: Segregação de Repository (ISP)

#### 4.1 - Criar interfaces segregadas
**Tempo estimado**: 3-4 horas

**Interfaces**:
- `UserReader.java` - Leitura
- `UserWriter.java` - Escrita
- `AuthenticationRepository.java` - Autenticação
- `UserStatusRepository.java` - Status
- `AuthorizationRepository.java` - Autorização

**Implementação**:
- `UserRepositoryAdapter.java` - Implementa todos

**Validação**:
- [ ] Todas as interfaces compilam
- [ ] Adapter implementa todas
- [ ] Services conseguem injetar interfaces específicas
- [ ] Testes funcionam com mocks específicos

---

#### 4.2 - Atualizar AuthService
**Tempo estimado**: 2-3 horas

**Mudanças**:
```java
// ❌ ANTES
@Service
public class AuthService {
    private final UserRepository userRepository;
    // usa tudo
}

// ✅ DEPOIS
@Service
public class AuthService {
    private final UserReader userReader;
    private final AuthenticationRepository authRepo;
    private final AuthorizationRepository authzRepo;
    // cada um sabe o que precisa
}
```

**Validação**:
- [ ] AuthService compila
- [ ] Métodos ainda trabalham
- [ ] Testes unitários passam

---

### Dia 5: Global Exception Handler

#### 5.1 - Criar GlobalExceptionHandler
**Tempo estimado**: 3-4 horas

**Arquivo**: `GlobalExceptionHandler.java`

**Exceções a tratar**:
- AuthenticationException → 401
- ResourceNotFoundException → 404
- DuplicateResourceException → 409
- BusinessException → 422
- ValidationException → 400

**Validação**:
- [ ] Classe compila
- [ ] Todos os @ExceptionHandler funcionam
- [ ] Respostas têm formato correto
- [ ] Erro genérico captura exceções inesperadas

---

#### 5.2 - Atualizar JwtAuthFilter
**Tempo estimado**: 1-2 horas

**Mudanças**:
```java
// ❌ ANTES
response.getWriter().write(
    String.format("{\"errorCode\":\"%s\",\"message\":\"%s\"}", 
        errorCode, message));

// ✅ DEPOIS
var errorResponse = new ErrorResponse(errorCode, message);
response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
```

**Validação**:
- [ ] Filter compila
- [ ] ObjectMapper injeta corretamente
- [ ] Respostas JSON válidas

---

### Dia 6: Testes Unitários

#### 6.1 - Criar testes base
**Tempo estimado**: 4-5 horas

**Arquivos**:
- `AccessPolicyTest.java` - 5-7 testes
- `PasswordValidatorTest.java` - 8-10 testes
- `AuthServiceTest.java` - 6-8 testes

**Cobertura Mínima**: 80%+

**Validação**:
- [ ] Todos os testes passam
- [ ] Coverage > 80% para classes refatoradas
- [ ] Sem skipped tests

---

## 🎯 SPRINT 2 - CONSOLIDAÇÃO (4-5 dias)

### Dia 1-2: Padronização de Nomes

#### Mudanças de Nomenclatura
**Tempo estimado**: 3-4 horas

| Campo Antigo | Novo | Arquivos Afetados |
|--------------|------|------------------|
| idUsuario | userId | User, Entity, Mapper, Tests |
| codLogin | login | User, Credentials, Entity |
| nomUsuario | username | PersonalInfo, Entity |
| nomEmail | email | PersonalInfo, Entity |
| codSenha | passwordHash | Credentials, Entity |
| codStatus | status | UserAudit, Entity |
| dtCriacao | createdAt | UserAudit, Entity |
| dtModi | modifiedAt | UserAudit, Entity |
| dtUltimoAcesso | lastAccessAt | UserAudit, Entity |
| dtExpiraSenha | passwordExpiresAt | Credentials, Entity |
| numTentativasFalha | failedAttempts | Credentials, Entity |
| numDiasSemanasPermitidos | allowedDays | AccessPolicy, Entity |
| dtJornadaIni | journeyStart | TimeRange, Entity |
| dtJornadaFim | journeyEnd | TimeRange, Entity |

**Strategy**: Usar Find & Replace com IDE + Rename Symbol
1. Refatorar Domain entities (sem dependências externas)
2. Refatorar DTOs
3. Refatorar Mappers
4. Refatorar Testes
5. Validar em staged environment

**Validação**:
- [ ] Projeto compila sem erros
- [ ] Testes passam 100%
- [ ] Sem "codLogin" references restantes (busca global)

---

### Dia 3: Refatoração de Mappers

#### 3.1 - Configurar MapStruct
**Tempo estimado**: 2-3 horas

**Tarefas**:
1. MapStruct já está no pom.xml
2. Criar `UserEntityMapper.java` com @Mapper
3. Atualizar UserMapperTest

**Validação**:
- [ ] Mapper compila
- [ ] Generated classes criadas
- [ ] Testes de mapeamento passam

---

#### 3.2 - Remover UserMapper manual
**Tempo estimado**: 1 hora

**Ação**: Deletar `UserMapper.java` antigo

---

### Dia 4: DTOs e Builders

#### 4.1 - Implementar DTOs com Builders
**Tempo estimado**: 2-3 horas

**Arquivos**:
- `CreateUserRequest.java` (Builder)
- `UpdateUserRequest.java` (Builder)
- `UserResponseDto.java` (Builder)
- `UserFilterDto.java`

**Validação**:
- [ ] DTOs compilam
- [ ] Builders funcionam
- [ ] Controllers compila com novos DTOs

---

### Dia 5: Cache e Preparação para Sprint 3

#### 5.1 - Revisar Configuração de Cache
**Tempo estimado**: 2 horas

**Checklist**:
- [ ] `@Cacheable` aplicado em queries ativas
- [ ] `@CacheEvict` em todas operações de escrita
- [ ] Cache Redis/Caffeine configurado
- [ ] TTL apropriado
- [ ] Teste de cache bypass

---

## 🎯 SPRINT 3 - FINALIZAÇÃO (2-3 dias)

### Dia 1: Reorganização de Diretórios

#### Estrutura Nova
```
auth/
├── adapter/
│   ├── in/
│   │   ├── rest/
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   └── dto/
│   │   └── web/
│   │       └── filter/
│   │           ├── JwtAuthFilter.java
│   │           └── AuthorityBuilderAdapter.java
│   └── out/
│       ├── persistence/
│       │   ├── entity/
│       │   ├── repository/
│       │   └── mapper/
│       ├── email/
│       ├── security/
│       └── logging/
├── application/
│   ├── port/
│   │   ├── in/
│   │   └── out/
│   └── service/
├── domain/
│   ├── vo/         ← NEW
│   ├── policy/     ← NEW
│   ├── exception/  ← NEW
│   └── enums/
├── config/         ← NEW
└── shared/         ← NEW
    ├── validator/
    └── util/
```

**Ação**: Mover arquivos usando IDE

**Validação**:
- [ ] Projeto compila após movimento
- [ ] Imports atualizados automaticamente
- [ ] Testes passam

---

### Dia 2: Documentação e Code Review

#### 2.1 - Adicionar JavaDoc
**Tempo estimado**: 3-4 horas

**Classes críticas**:
- User.java
- AuthService.java
- PasswordValidator.java
- UserRepositoryAdapter.java

**Template**:
```java
/**
 * [DESCRIÇÃO BREVE]
 * 
 * Responsabilidades:
 * - [Responsabilidade 1]
 * - [Responsabilidade 2]
 * 
 * Design Patterns:
 * - [Pattern 1]
 * 
 * @see RelatedClass
 */
```

---

#### 2.2 - Code Review Interno
**Tempo estimado**: 2-3 horas

**Checklist de Review**:
- [ ] Sem God Objects
- [ ] SRP respeitado
- [ ] ISP respeitado
- [ ] DRY respeitado
- [ ] Testes cobrem casos críticos
- [ ] Sem hardcoded strings/numbers
- [ ] Performance aceitável
- [ ] Segurança verificada

---

### Dia 3: Validação em Staging

#### 3.1 - Deploy em Staging
**Time**: 2-3 horas

**Testes**:
- [ ] Build Maven passa
- [ ] Application inicia
- [ ] Health check responde
- [ ] Database migrations OK
- [ ] Cache funciona
- [ ] Autenticação funciona
- [ ] Queries otimizadas

---

#### 3.2 - Performance Testing
**Time**: 2-3 horas

**Métricas**:
- Query time: ~ 50ms (antes: ~450ms)
- Memory usage: monitorar
- Cache hit rate: > 80%
- Login latency: < 200ms

---

## 📊 Matriz de Risco

| Risco | Probabilidade | Impacto | Mitigação |
|-------|--------------|---------|-----------|
| Regressão em autenticação | MÉDIA | CRÍTICA | Testes 100%, staging validation |
| Performance degradation | BAIXA | ALTA | Load testing, monitoring |
| Dados não migram | BAIXA | ALTÍSSIMA | Backup antes, reverse migration plan |
| Integração com sistema legado quebra | ALTO | ALTA | Compatibilidade layer, integration tests |

---

## 📈 Métricas de Sucesso

### Code Quality
- ✅ SonarQube: Grade A
- ✅ Complexity: < 10 avg
- ✅ Coverage: > 85%
- ✅ Duplicação: < 5%

### Performance
- ✅ Auth latency: < 200ms
- ✅ Query N+1: Eliminado
- ✅ Cache hit rate: > 80%
- ✅ Memory usage: Estável

### Maintainability
- ✅ Classes bem segregadas
- ✅ Interfaces segregadas (ISP)
- ✅ Sem God Objects
- ✅ JavaDoc completo

---

## 📋 Checklist de Conclusão

### Código
- [ ] Todos os Value Objects criados
- [ ] User simplificada
- [ ] PasswordValidator funcionando
- [ ] Queries otimizadas
- [ ] Repository segregado
- [ ] GlobalExceptionHandler funciona
- [ ] JwtAuthFilter sem hardcoded JSON

### Testes
- [ ] 100+ testes unitários
- [ ] Coverage > 85%
- [ ] Testes de integração passam
- [ ] Performance tests OK

### Documentação
- [ ] JavaDoc completo
- [ ] README atualizado
- [ ] Decisões arquitetônicas documentadas
- [ ] Diagrama de classes atualizado

### Deployment
- [ ] Build Maven limpo
- [ ] Staging validation OK
- [ ] Rollback plan pronto
- [ ] Database migration script OK

### Comunicação
- [ ] Equipe alinhada
- [ ] Know-how transferido
- [ ] Code review completo
- [ ] Retrospective agendada

---

## 🎓 Knowledge Transfer

### Sessões de Treinamento

**Session 1: Value Objects & DDD** (1h)
- O que é un Value Object
- Imutabilidade
- Por que separar Users em VOs

**Session 2: Clean Architecture** (1h)
- Portos & Adaptadores
- Segregação de interface
- Dependency Inversion

**Session 3: Best Practices** (1h)
- Factory methods
- Builder pattern
- Strategy pattern

---

## 📞 Suporte & Escalação

**Problemas Comuns**:

### "Quero comparar dois usuários, como faço?"
```java
// Antes: Compare todos os 50 campos
// Depo is: Compare Value Objects
if (user1.getCredentials().getLogin()
    .equals(user2.getCredentials().getLogin())) { ... }
```

### "Como testo AccessPolicy?"
```java
// Sem mockito, sem static fields
var policy = new AccessPolicy(
    DayPattern.businessDays(),
    TimeRange.businessHours()
);
assertTrue(policy.isAccessAllowedAt(
    LocalDateTime.of(2026, 4, 1, 10, 0)
));
```

### "Quebrei algo, como rollback?"
```
1. Git revert --no-edit <commit>
2. Revert last migration: ./mvnw flyway:undo
3. Redeploy anterior version
```

---

## 📞 Contato & Escalação

**Arquiteto Sênior**: [Seu Nome]  
**Tech Lead**: [Nome]  
**DevOps**: [Nome]

---

## Próximos Passos

1. **Aprovação do Plano** - Revisar com Tech Lead
2. **Backlog Refinement** - Preparar tasks para Sprint 1
3. **Setup de Ambiente** - Branches, CI/CD, staging
4. **Sprint Planning** - Kickoff Sprint 1
5. **Execução** - Seguir roteiro dia-a-dia
6. **Validação** - Testes, staging, metrics
7. **Deployment** - Production rollout
8. **Retrospective** - Lições aprendidas

---

**Documento Versionado**: v1.0  
**Data**: 01 de Abril de 2026  
**Status**: ✅ Pronto para Execução
