---

# Análise Arquitetural — Módulo `creditrequest`

---

## 1. QUALIDADE DO CÓDIGO

---

### 📍 CreditRequest.java / CreditRequestItems.java
**⚠️ Modelo de domínio anêmico (Anemic Domain Model)**

Ambas as classes são apenas bags de dados com `@Getter @Setter`, sem nenhum comportamento de negócio. Regras como "apenas cancelar se o status permitir" ou "calcular `vlTotal` a partir dos itens" ficam espalhadas nos serviços, violando o princípio de encapsulamento do DDD.

**💡 Sugestão:** Mover comportamento de negócio simples para o próprio domínio.

```java
// ✅ Refatorado
public class CreditRequest {
    // ... campos
    
    public boolean isTerminal() {
        return SituationCreditRequest.ATENDIDO_TOTALMENTE.getCode().equals(codSituacao)
            || SituationCreditRequest.CANCELADO.getCode().equals(codSituacao);
    }
    
    public void transitionTo(SituationCreditRequest novaSituacao) {
        if (isTerminal()) {
            throw new DomainException("Pedido já está em status terminal");
        }
        this.codSituacao = novaSituacao.getCode();
        this.dtManutencao = LocalDateTime.now();
    }
    
    public BigDecimal calcularVlTotal() {
        return itens.stream()
            .map(CreditRequestItems::getVlItem)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

---

### 📍 CreditRequest.java / CreditRequestItems.java
**⚠️ Tipagem fraca: `codSituacao` como `String` no domínio**

O campo `codSituacao` é `String` quando deveria ser o enum `SituationCreditRequest`. Comparações com strings hardcoded (`"04"`, `"05"`) estão espalhadas por todo o módulo, criando risco de typos silenciosos.

**💡 Sugestão:** Usar o enum diretamente no domínio.

```java
// ✅ No domínio
private SituationCreditRequest situacao;

// ✅ No mapper (adapter)
cr.setSituacao(SituationCreditRequest.fromCode(entity.getCodSituacao()));
```

---



### 📍 CreditRequestService.java (método `processarItemComTryCatchSequencial`)
**⚠️ Logs de DEBUG em produção e comentários debug esquecidos**

```java
// ❌ Linhas que não deveriam estar em produção
log.info("[DEBUG] Antes de salvar item: numSolicitacao={}, idUsuarioCartao={}...");
log.info("[DEBUG] Antes de salvar item:  idUsuarioCartao={}, numLogicoCartao={}");
```

**💡 Remover logs de nível `INFO` marcados como `[DEBUG]`, ou usar `log.debug()` apropriadamente.**

---

### 📍 UpdateRequestCredit.java
**⚠️ `Double` para valores monetários**

```java
// ❌ Perigoso: ponto flutuante para moeda
private Double vlPago;
```

```java
// ✅ Correto
private BigDecimal vlPago;
```

 Isso cascateia: `PayItemEntry` no `CreditRequestManagementUseCase` também usa `Double` para `vlItem`, `vlTxadm`, `vlTxserv`.

---

## 2. ORGANIZAÇÃO E ARQUITETURA

---

### 📍 SearchModeClassifier.java (pacote `domain`)
**⚠️ Violação da arquitetura hexagonal: domínio dependendo de DTO da camada de aplicação**

```java
// ❌ Domain importando DTO da application layer
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;

@Component  // ❌ Anotação Spring no domínio
public class SearchModeClassifier {
    public SearchMode classify(CursorPageRequest request) { ... } // ❌
```

O domínio não pode conhecer `CursorPageRequest` (que é um DTO da borda HTTP). O domínio não pode ter `@Component`.

**💡 Mover para `application/service` ou criar uma interface de domínio pura:**

```java
// ✅ Na application layer
@Component
public class SearchModeClassifier {
    public SearchMode classify(
            Long numSolicitacao, String codCanal, String codSituacao,
            String numLote, String codProduto, String codLogin, ...) {
        // lógica pura sem DTOs
    }
}
```

---

### 📍 CreditRequestItemsRepository.java (output port)
**⚠️ Porto de saída vaza tipo de infraestrutura JPA**

```java
// ❌ Interface de porta retorna entidade JPA — viola o princípio de isolamento
public interface CreditRequestItemsRepository {
    List<CreditRequestItemsEJpa> findFirstBySituacaoAndDtPagtoEconomicaBetween(...);
    List<CreditRequestItemsEJpa> findProcessRechargeService(...);
```

Uma porta de saída (output port) não pode retornar `*EJpa`. A camada de aplicação não deve conhecer esse tipo.

**💡 Retornar tipos do domínio:**

```java
// ✅ Correto
public interface CreditRequestItemsRepository {
    List<CreditRequestItems> findElegiveisParaLiberacao(
        String codSituacao, LocalDateTime dtInicio, LocalDateTime dtFim, int limit);
}
```

---

### 📍 ReleaseRechargeService.java, ConfirmedRechargeService.java, ProcessRechargeService.java
**⚠️ Services da application layer importando entidades JPA**

```java
// ❌ Application service conhecendo infraestrutura
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
```

Isso é uma inversão de dependências quebrada: o núcleo da aplicação não pode depender do adaptador JPA.

---

### 📍 CursorPaginationService.java
**⚠️ Service da application importando mapper do adapter**

```java
// ❌
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper.CreditRequestMapper;
```

`CreditRequestMapper` é um bean do pacote `adapter` (infraestrutura). Serviços de aplicação não devem depender dele.

---

### 📍 HmGateway.java
**⚠️ Não segue convenção de nomenclatura de portas do projeto**

Conforme a memória do repositório, a convenção é `*Port`. O correto seria `HmPort` ou `HardwareManagerPort`.

---

## 3. REUTILIZAÇÃO DE CÓDIGO

---

### 📍 ConfirmedRechargeService.java — método `toEntity()`
**⚠️ Mapeamento manual de `CreditRequestItems` → `CreditRequestItemsEJpa` completamente duplicado**

O mesmo mapeamento já existe em `CreditRequestMapper.toEntityItem()`. O `ConfirmedRechargeService` tem uma cópia completa do método com ~45 linhas.

```java
// ❌ Duplicado em ConfirmedRechargeService
private CreditRequestItemsEJpa toEntity(CreditRequestItems item) {
    CreditRequestItemsEJpa entity = new CreditRequestItemsEJpa();
    // 45 linhas de set field a field...
}
```

O mesmo padrão aparece em `ReleaseRechargeService`. **Isso viola o DRY de forma crítica** — qualquer campo novo exige 3+ mudanças.

**💡** Injetar e usar `CreditRequestMapper.toEntityItem()` (mas resolver antes a violação arquitetural do ponto anterior).

---

### 📍 `SituationCreditRequest.fromCode()` / `SituationCreditRequestItems.fromCode()`
**⚠️ Método `fromCode()` duplicado identicamente nos dois enums**

```java
// ❌ Mesmo método em ambos os enums
public static SituationCreditRequest fromCode(String codigo) {
    for (SituationCreditRequest s : values()) {
        if (s.code.equals(codigo)) return s;
    }
    throw new IllegalArgumentException("...");
}
```

**💡 Extrair interface:**

```java
// ✅ Interface reutilizável
public interface CodedEnum {
    String getCode();
    
    static <E extends Enum<E> & CodedEnum> E fromCode(Class<E> type, String code) {
        for (E e : type.getEnumConstants()) {
            if (e.getCode().equals(code)) return e;
        }
        throw new IllegalArgumentException("Código inválido: " + code);
    }
}

// Uso:
SituationCreditRequest s = CodedEnum.fromCode(SituationCreditRequest.class, "04");
```

---

### 📍 `CreditRequestAdapterJpa.toEntity()` / `CreditRequestMapper.toDomain()`
**⚠️ Mapeamento manual duplicado com o que o MapStruct já deveria gerar**

`CreditRequestAdapterJpa` tem métodos `toEntity()` e `toDomain()` manuais com ~50 linhas cada, sendo que `CreditRequestMapper` (MapStruct) existe e já deveria cobrir isso.

**💡** Injetar `CreditRequestMapper` em `CreditRequestAdapterJpa` e delegar o mapeamento.

---

## 4. BOAS PRÁTICAS SPRING BOOT

---

### 📍 CreditRequestController.java
**⚠️ `IdempotencyStore` declarado como campo estático**

```java
// ❌ Campo estático — não é um bean gerenciado pelo Spring, não testável
private static final IdempotencyStore<ResponseEntity<?>> idempotencyStore 
    = new InMemoryIdempotencyStore<>();
```

**💡 Injetar via construtor (já existe `IdempotencyStore` injetado no service):**

```java
// ✅ 
@RequiredArgsConstructor
public class CreditRequestController {
    private final IdempotencyStore idempotencyStore; // injetado
```

---

### 📍 CreditRequestController.java — método `criarPedido()`
**⚠️ Lógica de negócio (busca de usuário) no Controller**

```java
// ❌ Responsabilidade que não pertence ao controller
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
var userOpt = userRepository.findByCodLogin(login);
Long userId = userOpt.get().getIdUsuario();
```

Controllers devem apenas transformar HTTP → comando → HTTP response. Resolver `userId` a partir do principal é responsabilidade da camada de aplicação ou de um `@AuthenticationPrincipal` customizado.

```java
// ✅ Usando resolver customizado
public ResponseEntity<?> criarPedido(
    @AuthenticationPrincipal UserPrincipal principal,
    @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
    @Valid @RequestBody CreateRequestCredit request) {
    
    var result = creditRequestManagementUseCase
        .createCreditRequest(request, idempotencyKey, principal.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

---

### 📍 CreditRequestValidationService.java — `validarNumLote()`
**⚠️ Repositório passado como parâmetro de método em vez de injetado**

```java
// ❌ Injeção via parâmetro de método
public void validarNumLote(String numLote, String codCanal, 
    CreditRequestRepository creditRequestRepository) { // ← ERRADO
```

```java
// ✅ Injeção por construtor
@RequiredArgsConstructor
public class CreditRequestValidationService {
    private final CreditRequestRepository creditRequestRepository;
    
    public void validarNumLote(String numLote, String codCanal) { ... }
}
```

---

### 📍 Services (`ProcessRechargeService`, `ReleaseRechargeService`, etc.)
**⚠️ `@Transactional` do pacote errado**

```java
// ❌ jakarta.transaction — não gerenciado pelo Spring AOP
import jakarta.transaction.Transactional;
```

```java
// ✅ Spring gerencia rollback, propagation, isolation corretamente
import org.springframework.transaction.annotation.Transactional;
```

---

### 📍 HistCreditRequestController.java
**⚠️ Valor padrão hardcoded no `@RequestParam`**

```java
// ❌ Magic number "152" no controller — dependência de regra de negócio no adapter
@RequestParam(value = "codCanal", defaultValue = "152") String codCanal
```

**💡** Extrair para configuração (`application.properties`) ou remover o default e exigir o parâmetro.

---

### 📍 Logging inconsistente
**⚠️ Mistura de `@Slf4j` (Lombok) e `LoggerFactory.getLogger()` manual**

- `CreditRequestService` → `LoggerFactory.getLogger()` manual
- `ReleaseRechargeScheduler` → `@Slf4j`
- `ProcessRechargeService` → `LoggerFactory.getLogger()` manual

**💡 Padronizar com `@Slf4j` em toda a base.**

---

## 5. PERFORMANCE E ESCALABILIDADE

---

### 📍 ProcessRechargeSheduler.java
**⚠️ N+1 queries no scheduler — busca itens um a um**

```java
// ❌ Chama o banco tamanhoLote vezes em loop
for (int i = 0; i < tamanhoLote; i++) {
    CreditRequest solicitacao = creditRequestRepository
        .findElegiveisParaProcessamento(codSituacao); // ← 1 query por iteração
    ...
}
```

**💡 Buscar todas as solicitações elegíveis de uma vez:**

```java
// ✅ Uma única query
List<CreditRequest> elegíveis = creditRequestRepository
    .findElegiveisParaProcessamento(codSituacao, tamanhoLote);

elegíveis.forEach(sol -> {
    try {
        processRechargeUseCase.processarRecarga(new ProcessRechargeCommand(...));
    } catch (Exception e) {
        log.error("Erro...", e);
    }
});
```

---

### 📍 ConfirmedRechargeService.java / ProcessRechargeService.java
**⚠️ N+1 dentro do processamento de itens**

```java
// ❌ findById em loop = N queries
for (Long numSolicitacaoItem : numSolicitacaoItens) {
    var optItem = itemRepository.findById(key); // ← 1 query por item
```

**💡** Adicionar método `findAllByIds(List<CreditRequestItemsKey>)` ao port e buscar em lote.

---

### 📍 CursorPaginationService.java
**⚠️ Cache key frágil baseado em `toString()`**

```java
// ❌ @Cacheable com toString() de objeto mutable (@Data)
@Cacheable(value = "pedidos", key = "#request.toString()")
```

`CursorPageRequest` usa `@Data` (Lombok), portanto `toString()` inclui todos os campos. Se qualquer campo for `null`, a key pode colidir ou ser ineficiente. Use uma key composta e determinística.

---

### 📍 ReleaseRechargeService.java
**⚠️ Mistura de `java.sql.Timestamp` com `java.time.LocalDateTime`**

```java
// ❌ Conversão desnecessária para tipo legado
java.sql.Timestamp dtInicio = java.sql.Timestamp.valueOf(solicitacao.getDtPagtoEconomica());
```

O port deveria receber `LocalDateTime` diretamente.

---

## 6. TESTABILIDADE

---

### 📍 CreditRequestServiceTest.java
**⚠️ Único teste é `@SpringBootTest` — sem testes unitários reais**

O módulo tem apenas 2 arquivos de teste para ~88 classes. O único teste de service levanta todo o contexto Spring com `@ActiveProfiles("local")`, o que requer banco de dados real. Isso torna o CI frágil e lento.

**💡 Estrutura de testes recomendada:**

```java
// ✅ Teste unitário — sem Spring context
@ExtendWith(MockitoExtension.class)
class CreditRequestValidationServiceTest {

    @Mock private SalesChannelRepository salesChannelRepository;
    @InjectMocks private CreditRequestValidationService service;

    @Test
    void validarCanal_quandoCanalInativo_deveLancarExcecao() {
        var canal = new SalesChannel();
        canal.setStCanais("I");
        when(salesChannelRepository.findById("10")).thenReturn(Optional.of(canal));
        
        assertThrows(IllegalStateException.class, 
            () -> service.validarCanal("10"));
    }
}

// ✅ Teste unitário do domínio de situações
class SituationAscertainedServiceTest {
    private final SituationAscertainedService service = new SituationAscertainedService();

    @Test
    void deveDeterminarAtendidoParcialmente_quandoRecarregadoMaisCancelados() {
        var situacoes = List.of("07", "07", "13");
        assertEquals("07", service.apurarSituacaoPedido(situacoes)); // parcial
    }
}
```

---

### 📍 CreditRequestController.java
**⚠️ Lógica no controller dificulta testes**

A busca de `userId` via `SecurityContextHolder` + `userRepository` dentro do método HTTP impossibilita testar a lógica do controller sem mockar infraestrutura de segurança.

---

## 7. PADRONIZAÇÃO

---

### 📍 ProcessRechargeSheduler.java
**⚠️ Typo no nome da classe**

```
❌ ProcessRechargeSheduler   (faltando 'c')
✅ ProcessRechargeScheduler
```

---

### 📍 CardsTypeProjection.JAVA
**⚠️ Extensão de arquivo em maiúscula**

```
❌ CardsTypeProjection.JAVA
✅ CardsTypeProjection.java
```

---

### 📍 Portas de saída com nome `*Repository` ao invés de `*Port`
**⚠️ Inconsistente com a convenção do projeto**

Conforme documentado nas convenções do projeto, portas de saída devem usar o sufixo `*Port`. Os arquivos em `application/port/out/repository/` (ex: `CreditRequestRepository`, `CreditRequestItemsRepository`) deveriam ser:

```
❌ CreditRequestRepository        → ✅ CreditRequestPersistencePort
❌ CreditRequestItemsRepository   → ✅ CreditRequestItemsPersistencePort
❌ HmGateway                      → ✅ HardwareManagerPort
```

---

## 8. PERFORMANCE NA INSERÇÃO DE PEDIDOS (`createCreditRequest`)

> Análise focada no caminho crítico de escrita: `createCreditRequest` → `processarItemComTryCatchSequencial` → `validarItem`.

---

### 📍 `CreditRequestService.createCreditRequest` — loop de pedidos
**⚠️ Query duplicada: `findByNumSolicitacaoAndCodCanal` executada 2x por pedido**

No loop de pedidos, o código primeiro verifica a existência do pedido:
```java
// ❌ Verificação 1 — feita no loop externo
boolean solicitacaoExiste = creditRequestRepository
    .findByNumSolicitacaoAndCodCanal(pedido.numSolicitacao(), request.codCanal()).isPresent();
```
E depois, dentro de `processarItemComTryCatchSequencial`, para **cada item** do mesmo pedido, faz a mesma query novamente:
```java
// ❌ Verificação 2 — repetida por ITEM dentro do mesmo pedido
Optional<CreditRequest> existingRequest = creditRequestRepository
    .findByNumSolicitacaoAndCodCanal(numSolicitacao, request.codCanal());
```
Para um pedido com 50 itens isso gera **51 queries idênticas** — 1 no loop externo + 50 dentro do loop de itens.

**💡 Sugestão:** Salvar o resultado da primeira verificação e passá-lo para o método de processamento do item.

```java
// ✅ Refatorado
for (CreateRequestCredit.CreditRequest pedido : request.pedidos()) {
    validationService.validarNumLote(pedido.numLote(), request.codCanal());

    // Uma única consulta por pedido
    Optional<CreditRequest> existingRequest = creditRequestRepository
        .findByNumSolicitacaoAndCodCanal(pedido.numSolicitacao(), request.codCanal());
    if (existingRequest.isPresent()) {
        throw new IllegalStateException("Já existe solicitação: " + pedido.numSolicitacao());
    }

    // Cria o CreditRequest uma vez, antes do loop de itens
    CreditRequest creditRequest = mapToCreditRequest(pedido, request, userId);
    creditRequestRepository.save(creditRequest);
    historyService.saveRequestStatusHistory(creditRequest, pedido.numSolicitacao(), ...);

    long seq = 1L;
    for (ItemRequest item : pedido.itens()) {
        // Passa o creditRequest já criado, sem nova query
        processarItem(canal, pedido, item, request, creditRequest, processados, rejeitados, seq++);
    }
}
```

---

### 📍 `CreditRequestService.validarItem` — validações repetidas sem cache
**⚠️ 4–5 queries ao banco por item, com dados invariantes para o mesmo `canal + produto`**

Para cada item, `validarItem` dispara as seguintes consultas:

| Chamada | Query | Repete-se? |
|---|---|---|
| `validarProdutoNoCanal` | `SELECT` em `CANAL_PRODUTOS` | ✅ Mesmo par canal+produto por todo o lote |
| `validarLimites` | `SELECT` em `LIMITES_RECARGA` | ✅ Mesmo par canal+produto por todo o lote |
| `validarVigenciadoCanal` (assoc.) | `SELECT` em canal de distribuição | ✅ Mesmo par por todo o lote |
| `validarVigenciadoCanal` (convênio) | `SELECT` em `VIGENCIA_CONVENIO` | ✅ Mesmo par canal+produto |
| `feeFareService.findByCanalProduto` | `SELECT` em tarifas | ✅ Mesmo par canal+produto |

Para um lote de 100 itens com o mesmo canal e produto, isso gera **~500 queries desnecessárias**.

**💡 Sugestão:** Pré-carregar os dados invariantes **antes** do loop de itens e repassá-los como parâmetro (ou usar um objeto `ItemValidationContext`):

```java
// ✅ Pré-carga única por par canal+produto
record ItemValidationContext(
    ProductChannel produtoCanal,
    RechargeLimit limite,
    Fee taxaVigente
) {}

private ItemValidationContext prepararContextoValidacao(String codCanal, String codProduto) {
    ProductChannel produtoCanal = validationService.buscarProdutoNoCanal(codCanal, codProduto);
    RechargeLimit limite = validationService.buscarLimite(codCanal, codProduto);
    Fee taxaVigente = feeFareService.findTaxaVigente(codCanal, codProduto);
    return new ItemValidationContext(produtoCanal, limite, taxaVigente);
}

// No loop externo (por par canal+produto único do lote):
Map<String, ItemValidationContext> contextos = new HashMap<>();

for (ItemRequest item : pedido.itens()) {
    String chave = codCanal + ":" + item.codProduto();
    ItemValidationContext ctx = contextos.computeIfAbsent(
        chave, k -> prepararContextoValidacao(codCanal, item.codProduto()));
    validarItemComContexto(ctx, pedido, item, request, rejeitados);
}
```

---

### 📍 `CreditRequestService.consolidarStatusSolicitacao`
**⚠️ Loop infinito com `findById` — N+1 crítico encadeado**

```java
// ❌ CRÍTICO: N+1 para descobrir todos os itens de um pedido
for (long numSolicitacaoItem = 1;; numSolicitacaoItem++) {
    CreditRequestItemsKey key = ...;
    Optional<CreditRequestItems> opt = itemRepository.findById(key); // ← 1 query por iteração
    if (opt.isPresent()) {
        itens.add(creditRequestMapper.toEntityItem(opt.get()));
    } else {
        break; // Quebra ao achar o primeiro "buraco" na sequência
    }
}
```

Para um pedido com 50 itens, este loop faz **51 queries** (50 hits + 1 miss para sair). Este método é chamado toda vez que um item tem seu status alterado.

Além disso, o loop **assume que os IDs são sequenciais sem gaps**. Se houver qualquer item cancelado ou gap na sequência, o loop quebra prematuramente e a consolidação usa dados incompletos.

**💡 Sugestão:** Adicionar método dedicado no port e buscar todos os itens em uma única query:

```java
// ✅ No port CreditRequestItemsRepository (renomear para Port)
List<CreditRequestItems> findAllBySolicitacao(Long numSolicitacao, String codCanal);

// ✅ Na implementação JPA
@Query("SELECT i FROM CreditRequestItemsEJpa i WHERE i.id.numSolicitacao = :num AND i.id.codCanal = :canal")
List<CreditRequestItemsEJpa> findAllBySolicitacao(@Param("num") Long num, @Param("canal") String canal);

// ✅ Uso no service — 1 query no lugar de N+1
private void consolidarStatusSolicitacao(Long numSolicitacao, String codCanal, ...) {
    List<CreditRequestItems> itens = itemRepository.findAllBySolicitacao(numSolicitacao, codCanal);
    List<String> statusItens = itens.stream().map(CreditRequestItems::getCodSituacao).toList();
    String novoStatus = aplicarRegrasConsolidacao(statusItens);
    // ...
}
```

---

### 📍 `CreditRequestService.validarTransicoes` — Phase 1
**⚠️ Carrega os itens do banco apenas para validar e descarta; os mesmos itens são carregados novamente na Phase 2**

```java
// ❌ Phase 1 — busca cada item individualmente (N queries)
private void validarTransicoes(ActionStatus acao, OrderItemEntry entry) {
    for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
        Optional<CreditRequestItems> itemOpt = itemRepository.findById(itemId); // 1 query/item
        transitionValidator.validarTransicaoItem(acao, itemOpt.get().getCodSituacao());
    }
}

// ❌ Phase 2 — carrega OS MESMOS itens novamente
for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
    Optional<CreditRequestItems> itemOpt = itemRepository.findById(itemId); // 1 query/item de novo
    // ...processa
}
```

Para 100 itens = **200 queries** quando deveria ser **100**.

**💡 Sugestão:** Buscar todos os itens uma vez, validar e processar sobre o mesmo mapa:

```java
// ✅ Uma única leitura
Map<CreditRequestItemsKey, CreditRequestItems> itensPorKey = itemRepository
    .findAllById(entry.numSolicitacaoItems().stream().map(...).toList())
    .stream()
    .collect(toMap(i -> i.getId(), identity()));

// Validar
itensPorKey.values().forEach(item ->
    transitionValidator.validarTransicaoItem(acao, item.getCodSituacao()));

// Processar sobre o mesmo mapa — sem nova query
itensPorKey.values().forEach(item -> {
    item.setCodSituacao(determinarNovoStatus(acao));
    itemRepository.save(item);
    // ...
});
```

---

### 📍 `RechargeLogService.upsertLogRecarga` — chamado dentro do loop de itens
**⚠️ 2 queries por item para operação de sequência que poderia ser centralizada**

```java
// ❌ Para cada item no loop:
int novoSeq = rechargeLogRepository.findMaxSeqRecarga()      // query 1 por item
    .map(maxSeq -> maxSeq + 1).orElse(1);
Optional<RechargeLog> existingOpt =
    rechargeLogRepository.findByNumLogicoCartao(numLogicoCartao); // query 2 por item
```

Para um lote com 50 cartões distintos: **100 queries** só para log de recarga. `findMaxSeqRecarga()` é chamado uma vez por item, mas retorna o mesmo resultado dentro de uma mesma transação — o valor não muda entre as chamadas.

**💡 Sugestão:** Buscar o `maxSeq` uma única vez antes do loop e injetar:

```java
// ✅ Antes do loop de itens
int baseSeq = rechargeLogRepository.findMaxSeqRecarga().orElse(0);
AtomicInteger seqCounter = new AtomicInteger(baseSeq);

// No loop
int seqRecarga = rechargeLogService.upsertLogRecargaComSeq(
    item.numLogicoCartao(), userId, seqCounter.incrementAndGet());
```

---

### 📍 `HistCreditRequestService.saveItemStatusHistory` — propagação `REQUIRES_NEW`
**⚠️ Cada registro de histórico abre e fecha uma transação própria**

```java
// ❌ REQUIRES_NEW por item = N transações separadas para N itens
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveItemStatusHistory(CreditRequestItems item, String origemTransicao) { ... }
```

Para 50 itens em um lote = **50 commits de transação separados**, além de 50 consultas de deduplicação (`findLatestByItem`). O objetivo de `REQUIRES_NEW` (garantir o log mesmo em rollback da transação pai) é válido, mas a granularidade por item é excessiva.

**💡 Sugestão:** Criar método de batch que persiste todos os históricos de um lote em uma única transação `REQUIRES_NEW`:

```java
// ✅ Uma transação para todos os históricos do lote
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveItemStatusHistoryBatch(List<CreditRequestItems> itens, String origemTransicao) {
    List<HistCreditRequestItems> registros = itens.stream()
        .map(item -> montarHistorico(item, origemTransicao))
        .toList();
    itemHistoryRepository.saveAll(registros);
}
```

---

### 📍 `CreditRequestService.validarAcaoPago`
**⚠️ N+1 queries para validação do valor de pagamento**

```java
// ❌ findById em loop na validação do pagamento
for (PayItemEntry payItem : entry.getValue()) {
    Optional<CreditRequestItems> opt = itemRepository.findById(key); // ← N queries
    opt.ifPresent(domainItem -> itens.add(...));
}
```

Este loop já busca individualmente cada item — o mesmo padrão descrito na `validarTransicoes` mas em outro método.

---

## 9. BUGS DE LÓGICA E VALIDAÇÃO NA INSERÇÃO

---

### 📍 `CreditRequestValidationService.validarVigenciadoCanal`
**⚠️ CRÍTICO: Condição lógica invertida — rejeita convênios ativos**

```java
// ❌ Bug: isBefore(now) é TRUE para qualquer convênio já iniciado (ativo)
Optional<AgreementValidity> convOpt = agreeValidRepository.findById(key);
if (convOpt.isEmpty() || convOpt.get().getDtInicioValidade().isBefore(LocalDateTime.now())) {
    rejeitados.add(new ItemRejeitado(..., "Convênio não vigente..."));
}
```

`dtInicioValidade.isBefore(now)` retorna `true` para qualquer convênio cuja data de início já passou — ou seja, para **todos os convênios ativos normalmente**. Isso significa que **todos os pedidos serão rejeitados** por esta regra.

A verificação correta de vigência deve checar a data de **término** (`dtFimValidade`), não a de início.

```java
// ✅ Correto
Optional<AgreementValidity> convOpt = agreeValidRepository.findById(key);
if (convOpt.isEmpty()) {
    rejeitados.add(...);
    return;
}
AgreementValidity conv = convOpt.get();
LocalDateTime agora = LocalDateTime.now();
boolean fimExpirado = conv.getDtFimValidade() != null && conv.getDtFimValidade().isBefore(agora);
boolean naoIniciado = conv.getDtInicioValidade() != null && conv.getDtInicioValidade().isAfter(agora);
if (fimExpirado || naoIniciado) {
    rejeitados.add(..., "Convênio não vigente...");
}
```

---

### 📍 `CreditRequestService.mapToCreditRequest`
**⚠️ `vlTotal` calculado apenas com o valor do primeiro item do pedido**

```java
// ❌ vlTotal do CreditRequest é setado com o valorTotal de UM ÚNICO item
private CreditRequest mapToCreditRequest(
        CreateRequestCredit.CreditRequest pedido, ItemRequest item, ...) {
    cr.setVlTotal(item.valorTotal()); // ← valor de apenas 1 dos N itens
    ...
}
```

O `CreditRequest` é criado na primeira iteração do loop de itens, usando o valor do primeiro item. Itens subsequentes do mesmo pedido não atualizam o `vlTotal`. O valor total do pedido deveria ser a soma de todos os itens.

**💡 Sugestão:** Calcular o total antes do loop e passar para o método de mapeamento:

```java
// ✅ Calcular total do pedido antes do loop de itens
BigDecimal vlTotalPedido = pedido.itens().stream()
    .map(ItemRequest::valorTotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

CreditRequest creditRequest = mapToCreditRequest(pedido, request, userId, vlTotalPedido);
creditRequestRepository.save(creditRequest);
```

---

### 📍 `CreditRequestService.mapToCreditRequest`
**⚠️ `codSituacao` inicial hardcoded como `"03"` (ACEITO_PENDENTE_LIQUIDACAO)**

```java
// ❌ String mágica com status errado
cr.setCodSituacao("03"); // ACEITO_PENDENTE_LIQUIDACAO
```

Segundo o enum `SituationCreditRequest`, um novo pedido deveria iniciar em `"01"` (CRIADO) ou `"02"` (CONSISTIDO_OK), não diretamente em `"03"`. O mesmo problema ocorre em `mapToCreditRequestItemSequencial`.

```java
// ✅ Usando enum tipado
cr.setCodSituacao(SituationCreditRequest.CRIADO.getCode());
```

---

### 📍 `CreditRequestService.mapToCreditRequestItemSequencial`
**⚠️ `qtdItem` sempre zero**

```java
// ❌ Hardcoded como comentário indica "Ajuste se necessário"
cri.setQtdItem(0); // Ajuste se necessário
```

`qtdItem` é um campo da entidade que deveria refletir a quantidade de itens da recarga. Deixá-lo sempre zero pode impactar relatórios, auditorias e lógica de negócio downstream.

```java
// ✅ Usar 1 como padrão para recarga unitária, ou receber do request
cri.setQtdItem(1);
```

---

### 📍 `CreditRequestService.createCreditRequest`
**⚠️ Idempotência implementada mas sem efeito — `idempotencyStore` nunca é atualizado**

```java
// ✅ Verificação correta
Optional<CreateRequestResponse> cached = idempotencyStore.get(idempotencyKey);
if (cached.isPresent()) {
    return cached.get();
}

// ... toda a lógica de criação ...

CreateRequestResponse response = new CreateRequestResponse(...);

// ❌ idempotencyStore.put() NUNCA É CHAMADO
// A próxima request com a mesma key vai reprocessar tudo novamente
return response;
```

A idempotência foi iniciada mas não foi concluída. A segunda chamada com o mesmo `idempotencyKey` processará o pedido novamente, podendo criar duplicatas.

```java
// ✅ Salvar no cache antes de retornar
idempotencyStore.put(idempotencyKey, response);
return response;
```

---

### 📍 `CreditRequestService.processarItemComTryCatchSequencial`
**⚠️ `catch (Exception e)` engole erros de infraestrutura como rejeições de negócio**

```java
// ❌ Falha de conexão com banco, OutOfMemoryError, etc.,
// são tratados como uma rejeição de item normal
} catch (Exception e) {
    log.error("Erro ao processar pedido {}: {}", numSolicitacao, e.getMessage(), e);
    rejeitados.add(new ItemRejeitado(numSolicitacao, ..., "Erro interno: " + e.getMessage()));
}
```

Erros de infraestrutura críticos (timeout de banco, `DataAccessException`, etc.) não devem ser silenciados como rejeições de item — eles devem propagar e causar rollback da transação inteira.

**💡 Sugestão:** Capturar somente `ValidationException` e deixar erros de infraestrutura propagar:

```java
// ✅ Apenas erros de negócio são rejeições
} catch (ValidationException e) {
    rejeitados.add(new ItemRejeitado(numSolicitacao, ..., e.getMessage()));
} catch (DataAccessException e) {
    // Infra falhou — propagar para rollback da transação
    throw e;
}
```

---

### 📍 `CreditRequestService.validarItem`
**⚠️ `validarVigenciaVersaoProduto` comentada — validação de negócio desabilitada silenciosamente**

```java
// ❌ Bloco comentado sem registro de why/ticket
// validationService.validarVigenciaVersaoProduto(
//     pedido.numSolicitacao(), item.numLogicoCartao(), ...);
```

Manter código de validação comentado em produção é perigoso: pedidos com versões de produto expiradas são aceitos sem qualquer verificação, e futuros desenvolvedores não têm como saber se foi removido intencionalmente.

**💡** Ou remover o método completamente (com registro no git/ticket), ou reativar a validação, ou substituir por um feature flag explícito (`@ConditionalOnProperty`).

---

## 🔥 Lista de Refatorações Prioritárias *(atualizada)*

| Prioridade | Item | Impacto |
|---|---|---|
| 🔴 P1 | **BUG:** `validarVigenciadoCanal` — condição lógica invertida, rejeita todos os convênios ativos | Funcional crítico |
| 🔴 P1 | **BUG:** `idempotencyStore.put()` nunca é chamado — idempotência sem efeito | Duplicação de dados |
| 🔴 P1 | **BUG:** `vlTotal` do pedido setado com valor de apenas um item | Dados incorretos |
| 🔴 P1 | `consolidarStatusSolicitacao` — loop infinito N+1 (`findById` sequencial) | Performance crítica |
| 🔴 P1 | Remover tipos JPA (`*EJpa`) das interfaces de porto de saída | Arquitetural crítico |
| 🔴 P1 | Eliminar dependências de `adapter.*` em services de `application.*` | Arquitetural crítico |
| 🔴 P1 | Remover mapeamento duplicado em `ConfirmedRechargeService.toEntity()` | DRY / bugs em cascata |
| 🟠 P2 | Query duplicada `findByNumSolicitacaoAndCodCanal` por item no `createCreditRequest` | Performance (N+1) |
| 🟠 P2 | Pré-carregar `ProductChannel`, `RechargeLimit`, `Fee` antes do loop de itens | Performance (5x N queries) |
| 🟠 P2 | `validarTransicoes` + Phase 2 carregam os mesmos itens 2x | Performance (2x N queries) |
| 🟠 P2 | `catch (Exception e)` engole erros de infraestrutura como rejeições | Confiabilidade |
| 🟠 P2 | `codSituacao` inicial `"03"` deveria ser `"01"` (CRIADO) | Lógica de negócio |
| 🟠 P2 | Corrigir `@Transactional` para `org.springframework.transaction.annotation` | Comportamento transacional |
| 🟠 P2 | Mover lógica de autenticação/usuário para fora do `CreditRequestController` | SRP / testabilidade |
| 🟠 P2 | Injetar `CreditRequestRepository` no `CreditRequestValidationService` | Clean Code |
| 🟡 P3 | `saveItemStatusHistory` com `REQUIRES_NEW` por item — criar método batch | Performance (N commits) |
| 🟡 P3 | `upsertLogRecarga` com `findMaxSeqRecarga` repetido por item | Performance |
| 🟡 P3 | `qtdItem = 0` hardcoded | Dados incorretos |
| 🟡 P3 | Validação `validarVigenciaVersaoProduto` comentada silenciosamente | Negócio / manutenção |
| 🟡 P3 | Padronizar logging para `@Slf4j` | Consistência |
| 🟡 P3 | Typo `ProcessRechargeSheduler` → `ProcessRechargeScheduler` | Padronização |
| 🟡 P3 | Remover `Double` para valores monetários, usar `BigDecimal` | Precisão |
| 🟢 P4 | Pré-carregar contexto de validação com `Map<canal:produto, ctx>` para lotes grandes | Performance |
| 🟢 P4 | Adicionar testes unitários com Mockito (sem `@SpringBootTest`) | Testabilidade |
| 🟢 P4 | Mover `SearchModeClassifier` para `application.service` | Arquitetural |

---

## 🧠 Recomendações para Elevar ao Nível Sênior

1. **Enriquecer o domínio**: Classes de domínio devem encapsular suas regras. `CreditRequest` deve saber quando pode ser cancelado, bloqueado ou transicionar. O `TransitionSituationValidator` poderia ser integrado ao próprio aggregate.

2. **Separar ports por responsabilidade (ISP)**: Em vez de um único `CreditRequestPersistencePort` com 12 métodos, considere `CreditRequestQueryPort` (leitura) e `CreditRequestCommandPort` (escrita), conforme a convenção do projeto.

3. **Usar `record` para commands e queries**: As queries (como `SearchCommand`) e comandos (como `CreateRequestCredit`) já usam `record` — estender isso para os demais commands internos que ainda são classes.

4. **Proteger fronteiras de arquitetura com ArchUnit**: Adicionar testes de arquitetura para garantir que `domain` não importa `application`, que `application` não importa `adapter`, etc.
   ```java
   // Exemplo com ArchUnit
   @AnalyzeClasses(packages = "br.sptrans.scd.creditrequest")
   class CreditRequestArchTest {
       @ArchTest
       ArchRule domainShouldNotDependOnApplication = noClasses()
           .that().resideInAPackage("..domain..")
           .should().dependOnClassesThat().resideInAPackage("..application..");
   }
   ```

5. **Eliminar Magic Strings completamente**: Todas as comparações com `"04"`, `"05"`, `"A"`, `"S"`, `"N"` devem usar enums ou constantes tipadas.

6. **Testes de contrato para schedulers**: Os schedulers devem ter testes de comportamento com `@MockBean` e sem necessidade de banco.

7. **Seguir Result Pattern em validações**: Em vez de acumular em `List<ItemRejeitado>` e depois lançar exceção, considerar um `Either<ValidationError, ValidatedItem>` para tornar o fluxo explícito e composável.