# Análise Completa — Módulo `product`

## 1. QUALIDADE DO CÓDIGO

---

### 📍 `FamilyService`, `ModalityService`, `SpeciesService`, `TechnologyService`, `ProductsTypeService`

⚠️ **Problema: Duplicação massiva (violação DRY / anti-pattern Copy-Paste)**

Os 5 serviços de entidades de domínio (`Family`, `Modality`, `Species`, `Technology`, `ProductsType`) têm estrutura 100% idêntica: `create`, `update`, `findById`, `findAll`, `activate`, `inactivate`, `resolveUser`. A única diferença são os nomes dos campos e os `ErrorType` específicos.

💡 **Sugestão:** Introduzir uma interface genérica `CatalogueEntity<ID>` e um serviço abstrato `AbstractCatalogueService<T, ID>` que encapsula o comportamento compartilhado. Os serviços concretos ficam com no máximo 15 linhas.

✅ **Exemplo refatorado:**

```java
// domain — interface base para todas as entidades catálogo
public interface CatalogueEntity {
    String getCodStatus();
    void setCodStatus(String status);
    boolean isActive();
    boolean isInactive();
}

// application — serviço base genérico
public abstract class AbstractCatalogueService<T extends CatalogueEntity> {

    protected final CatalogueRepository<T> repository;
    protected final UserPersistencePort userPort;

    protected AbstractCatalogueService(CatalogueRepository<T> repository,
                                       UserPersistencePort userPort) {
        this.repository = repository;
        this.userPort = userPort;
    }

    protected void activate(String code, Long userId,
                            ProductErrorType notFound,
                            ProductErrorType alreadyActive) {
        T entity = repository.findById(code)
            .orElseThrow(() -> new ProductException(notFound));
        if (entity.isActive()) throw new ProductException(alreadyActive);
        repository.updateStatus(code, DomainStatus.ACTIVE.getCode(), userId);
    }
    // ...
}

// Serviço concreto — fica com ~20 linhas
@Service @Transactional @RequiredArgsConstructor
public class FamilyService extends AbstractCatalogueService<Family>
        implements FamilyManagementUseCase {
    // apenas o que é único para Family
}
```

---

### 📍 `FamilyController`, `ModalityController`, `SpeciesController`, `TechnologyController`, `ProductController`

⚠️ **Problema: `resolveUserId()` duplicado em todos os controllers**

O método abaixo está copiado identicamente em pelo menos 6 controllers:

```java
private Long resolveUserId(Authentication authentication) {
    return userRepository.findByCodLogin(authentication.getName())
            .map(u -> u.getIdUsuario())
            .orElse(null); // ← retorna null silenciosamente
}
```

⚠️ **Segundo problema: Controllers injetando `UserPersistencePort`** — Viola a arquitetura hexagonal. Um adapter de entrada (controller) não deve depender diretamente de um output port de outro módulo.

⚠️ **Terceiro problema: retorna `null` silenciosamente** — Se o usuário não for encontrado, `idUsuario` é `null` e pode gerar `NullPointerException` ou salvar auditoria sem usuário.

💡 **Sugestão:** Criar um componente compartilhado que extrai o ID do usuário direto do `Authentication` (ex.: do JWT claim, se disponível), eliminando a dependência do output port:

```java
// shared/security/AuthenticatedUserResolver.java
@Component
public class AuthenticatedUserResolver {

    private final UserPersistencePort userPort;

    public Long resolveId(Authentication auth) {
        return userPort.findByCodLogin(auth.getName())
            .map(User::getIdUsuario)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuário autenticado não encontrado: " + auth.getName()));
    }
}

// No controller — sem injeção de UserPersistencePort
private final AuthenticatedUserResolver userResolver;

// uso
Long userId = userResolver.resolveId(authentication);
```

---

### 📍 `ProductService.resolveUser()`

⚠️ **Problema: falha silenciosa**

```java
private User resolveUser(Long idUsuario) {
    if (idUsuario == null) return null;  // ← permite null propagar
    return userRepository.findById(idUsuario).orElse(null); // ← também null
}
```

Se o usuário não existir, o objeto é salvo com `idUsuarioCadastro = null`. Auditoria corrompida.

💡 **Sugestão:**
```java
private User resolveUserOrThrow(Long idUsuario) {
    if (idUsuario == null) throw new IllegalStateException("idUsuario obrigatório");
    return userRepository.findById(idUsuario)
        .orElseThrow(() -> new ProductException(ProductErrorType.USER_NOT_FOUND));
}
```

---

## 2. ORGANIZAÇÃO E ARQUITETURA

---

### 📍 `application/port/out/repository/` — Toda a pasta

⚠️ **Problema crítico: Violação da convenção de nomenclatura de ports**

Os output ports estão nomeados como `FamilyRepository`, `ProductRepository`, `ModalityRepository`, etc. Segundo a convenção estabelecida no projeto (arquivo `/memories/repo/hexagonal_port_naming_conventions.md`), output ports **nunca** devem usar o sufixo `Repository`.

O sufixo `Repository` é reservado exclusivamente para interfaces JPA da camada de infraestrutura (`FamilyJpaRepository`).

💡 **Renomear para:**

| Atual | Correto |
|---|---|
| `ProductRepository` | `ProductPersistencePort` |
| `FamilyRepository` | `FamilyPersistencePort` |
| `ModalityRepository` | `ModalityPersistencePort` |
| `FareRepository` | `FarePersistencePort` |
| `FeeRepository` | `FeePersistencePort` |
| `ProductVersionRepository` | `ProductVersionPersistencePort` |
| `AdministrativeFeeRepository` | `AdministrativeFeePersistencePort` |
| ... | ... |

---

### 📍 `ProductAdapterJpa.findAll()` e `ModalityAdapterJpa.findAll()`

⚠️ **Problema grave: filtragem em memória (N+1 / full table scan)**

```java
// ProductAdapterJpa — carrega TUDO e filtra em Java
return repository.findAll().stream()
    .map(ProductMapper::toDomain)
    .filter(p -> codStatus.equals(p.getCodStatus()))
    .toList();
```

Para uma tabela de produtos com 10.000 registros, isso carrega 10.000 entidades só para retornar 50.

💡 **Solução imediata — delegar o filtro ao banco:**
```java
// ProductJpaRepository
List<ProductEntityJpa> findByCodStatus(String codStatus);
List<ProductEntityJpa> findAllByOrderByCodProduto();

// ProductAdapterJpa
@Override
public List<Product> findAll(String codStatus) {
    List<ProductEntityJpa> entities = (codStatus != null && !codStatus.isBlank())
        ? repository.findByCodStatus(codStatus)
        : repository.findAllByOrderByCodProduto();
    return entities.stream().map(ProductMapper::toDomain).toList();
}
```

---

### 📍 Paginação — `PageResponse.fromList()` em todos os controllers

⚠️ **Problema: paginação em memória**

A paginação é feita carregando todos os registros e depois fatiando a lista em Java. Em produção com volume real, isso é inaceitável.

💡 **Solução:** Os output ports devem aceitar `Pageable` e retornar `Page<T>`:

```java
// output port
Page<Product> findAll(String codStatus, Pageable pageable);

// JPA repository
Page<ProductEntityJpa> findByCodStatus(String codStatus, Pageable pageable);

// controller
public ResponseEntity<Page<Product>> findAllProducts(
    @RequestParam(required = false) String codStatus,
    @PageableDefault(size = 20, sort = "codProduto") Pageable pageable) {
    return ResponseEntity.ok(productUseCase.findAllProducts(codStatus, pageable));
}
```

---

### 📍 `LiminarGateway` — colocado no módulo `product`

⚠️ **Problema: port mal posicionado**

`LiminarGateway` interface está em `product/application/port/out/gateway/`, mas por sua funcionalidade (verificação judicial de isenção de taxa no processamento de pedidos de crédito), ela pertence ao módulo `creditrequest`, não a `product`. Produtos não processam pedidos de crédito.

---

### 📍 `FamilyAdapterJpa` — `@Transactional` importado do pacote errado

⚠️ **Problema:**
```java
import jakarta.transaction.Transactional; // ← Jakarta EE
```
Os services usam `import org.springframework.transaction.annotation.Transactional`. Misturar os dois pode causar comportamentos inesperados (a anotação Spring oferece mais opções como `readOnly`, propagation, etc.).

💡 Padronizar para `org.springframework.transaction.annotation.Transactional` em toda a codebase.

---

## 3. REUTILIZAÇÃO DE CÓDIGO

---

### 📍 `FamilyMapper`, `ModalityMapper`, `SpeciesMapper`, `TechnologyMapper`

⚠️ **Problema: Mappers com eficiência N+1**

Todos os mappers de entidades de catálogo carregam o usuário via `userRepository.findById()` dentro do próprio mapper:

```java
// FamilyMapper — 2 queries para cada Family mapeada!
User usuarioCadastro = entity.getIdUsuarioCadastro() != null
        ? userRepository.findById(entity.getIdUsuarioCadastro()).orElse(null)
        : null;
User usuarioManutencao = entity.getIdUsuarioManutencao() != null
        ? userRepository.findById(entity.getIdUsuarioManutencao()).orElse(null)
        : null;
```

Para uma lista de 100 famílias, isso gera até **200 queries extras** só de usuários.

💡 **Solução 1 (melhor):** Configurar o relacionamento JPA usando `@ManyToOne @JoinColumn` nas entidades JPA, deixando o Hibernate resolver com JOIN:
```java
// FamilyEntityJpa
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "ID_USUARIO_CADASTRO")
private UserEntityJpa idUsuarioCadastro;
```

💡 **Solução 2 (alternativa):** Remover `User` do domínio das entidades catálogo e devolver apenas o `Long idUsuarioCadastro` (ID), mapeando para `UserSimpleDTO` somente na camada de resposta do controller.

---

### 📍 `FamilyMapper.toEntity()` — bug silencioso

⚠️ **Problema: IDs de usuário nunca são persistidos**

```java
static FamilyEntityJpa toEntity(Family family) {
    // ...
    // idUsuarioCadastro, idUsuarioManutencao: implementar se necessário ← BUG!
    return entity;
}
```

Quando uma `Family` é salva, `idUsuarioCadastro` e `idUsuarioManutencao` são `null` no banco. O comentário "implementar se necessário" na infraestrutura de um sistema de produção é um bug em produção.

💡 **Corrigir imediatamente:**
```java
entity.setIdUsuarioCadastro(family.getIdUsuarioCadastro() != null
    ? family.getIdUsuarioCadastro().getIdUsuario() : null);
entity.setIdUsuarioManutencao(family.getIdUsuarioManutencao() != null
    ? family.getIdUsuarioManutencao().getIdUsuario() : null);
```

---

### 📍 `ModalityAdapterJpa.updateStatus()` — code smell

⚠️ **Problema: objeto `User` criado apenas para pegar um Long**
```java
User user = new User();
user.setIdUsuario(idUsuario);
entity.setIdUsuarioManutencao(user.getIdUsuario()); // = idUsuario
```

Cria um objeto apenas para passar o próprio `idUsuario`. Desnecessário e confuso.

💡 **Simplificar:**
```java
entity.setIdUsuarioManutencao(idUsuario);
```

---

## 4. BOAS PRÁTICAS SPRING BOOT

---

### 📍 Controllers — Request bodies sem validação

⚠️ **Problema: ausência de `@Valid` e Bean Validation**

Nenhum `@RequestBody` usa `@Valid`/`@Validated`. Os records de request não possuem anotações de validação:

```java
// Atual — nenhuma validação
public record CreateProductRequest(
    String codProduto,   // pode ser null, vazio, 500 chars?
    String desProduto,
    ...
```

💡 **Adicionar anotações de validação:**
```java
public record CreateProductRequest(
    @NotBlank @Size(max = 20) String codProduto,
    @NotBlank @Size(max = 20) String desProduto,
    @Size(max = 60) String desEmissorResponsavel,
    @Size(max = 60) String desUtilizacao,
    @Pattern(regexp = "[SN]") String flgBloqFabricacao,
    // ...
) {}

// controller
public ResponseEntity<Void> createProduct(@Valid @RequestBody CreateProductRequest request, ...)
```

---

### 📍 `FeeFareController` — HTTP verbs incorretos

⚠️ **Problema: `@PostMapping` para operações de leitura**

```java
@PostMapping("/tarifas/{codProduto}/{codCanal}")   // ← deveria ser GET
public ResponseEntity<?> listFares(...)

@PostMapping("/taxas/{codProduto}/{codCanal}")     // ← deveria ser GET
public ResponseEntity<?> listFees(...)
```

Usar POST para listagem viola REST, impede cache HTTP e confunde clientes.

💡 Usar `@GetMapping` para listar, `@PutMapping` para atualizar:
```java
@GetMapping("/tarifas/{codProduto}/{codCanal}")
public ResponseEntity<List<FareResponseDTO>> listFares(...)

@PutMapping("/tarifa/{codTarifa}")
public ResponseEntity<FareResponseDTO> updateFare(...)
```

---

### 📍 `FeeFareController` — `ResponseEntity<?>` wildcard

⚠️ **Problema:** Retornar `ResponseEntity<?>` em vez de tipos concretos elimina a documentação Swagger automática e prejudica a segurança de tipo.

```java
// ruim
public ResponseEntity<?> createFare(...)

// correto
public ResponseEntity<FareResponseDTO> createFare(...)
```

---

### 📍 `ProductException.getHttpStatus()` e `getErrorCode()` — lógica frágil

⚠️ **Problema: determinação de HTTP status por string matching no nome do enum**

```java
if (name.contains("NOT_FOUND")) return HttpStatus.NOT_FOUND;
if (name.contains("ALREADY_EXISTS") || name.contains("CODE_ALREADY_EXISTS")) return HttpStatus.CONFLICT;
```

Se um desenvolvedor renomear `FAMILY_CODE_ALREADY_EXISTS` para `FAMILY_DUPLICATE_CODE`, o HTTP status muda silenciosamente para `400` em vez de `409`.

💡 **Melhor: colocar o HTTP status no próprio enum:**

```java
@Getter
@AllArgsConstructor
public enum ProductErrorType {
    PRODUCT_NOT_FOUND("Produto não encontrado.", HttpStatus.NOT_FOUND),
    CODE_ALREADY_EXISTS("Código de produto já cadastrado.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_ACTIVE("Produto já está ativo.", HttpStatus.UNPROCESSABLE_ENTITY),
    // ...

    private final String description;
    private final HttpStatus httpStatus;
}

// ProductException simplificada
@Override
public HttpStatus getHttpStatus() {
    return errorType.getHttpStatus();
}
```

---

### 📍 Controllers — `@SecurityRequirement` duplicado

⚠️ **Problema:** `ProductController` tem `@SecurityRequirement(name = "bearerAuth")` tanto na classe quanto em cada método individualmente. A anotação de classe já abrange todos os métodos.

---

### 📍 Domínio — Flags como `String` em vez de tipos adequados

⚠️ **Problema:** Todos os flags booleanos (`flgBloqFabricacao`, `flgBloqVenda`, etc.) são `String`. Os campos de código referencial (`codTecnologia`, `codModalidade`, `codFamilia`, `codEspecie`) são `String` puros, sem validação ou relação explícita de domínio.

💡 Se o banco armazena `"S"/"N"`, considerar um conversor JPA que mapeie `Boolean ↔ "S"/"N"`, mantendo o domínio tipado:

```java
// domain
private boolean bloqFabricacao;

// JPA entity com conversor
@Convert(converter = BooleanToSNConverter.class)
@Column(name = "FLG_BLOQ_FABRICACAO")
private boolean flgBloqFabricacao;
```

---

## 5. PERFORMANCE E ESCALABILIDADE

---

### 📍 `ProductService.createProduct()` — construtor com 26 argumentos posicionados

⚠️ **Problema:** Construtores altamente posicionais com muitos argumentos são extremamente frágeis. Trocar a ordem de dois `String` é um bug silencioso em tempo de compilação.

```java
Product product = new Product(
    cmd.codProduto(),
    cmd.desProduto(),
    // ... 24 argumentos mais
    null    // ← o que é esse null?
);
```

💡 **Usar Builder pattern via Lombok `@Builder`:**
```java
Product product = Product.builder()
    .codProduto(cmd.codProduto())
    .desProduto(cmd.desProduto())
    .codStatus(ProductStatus.INACTIVE.getCode())
    .dtCadastro(LocalDateTime.now())
    .idUsuarioCadastro(usuario.getIdUsuario())
    // apenas o que interessa
    .build();
```

O mesmo se aplica a `ProductVersion`, `Fare`, `Fee` e outros com muitos campos.

---

### 📍 `generateNextVersionCode()` — race condition

⚠️ **Problema:** A geração do próximo código de versão busca o último e incrementa em memória. Em ambiente com múltiplas instâncias ou requisições concorrentes, dois requests podem obter o mesmo `lastVersion` e gerar `codVersao` duplicado.

```java
private String generateNextVersionCode(String codProduto) {
    return productVersionRepository.findLastVersion(codProduto)
            .map(last -> {
                int num = Integer.parseInt(last.getCodVersao());
                return String.valueOf(num + 1); // ← race condition aqui
            })
            .orElse("1");
}
```

💡 **Solução:** Usar uma sequência do banco de dados ou uma constraint de banco + retry com `@Retryable`, ou usar `SELECT MAX(cod_versao) + 1 ... FOR UPDATE`.

---

### 📍 `ProductService.activateProduct()` — lógica de domínio ignorada

⚠️ **Problema:** O método `activate()` existe no domínio `Product`, mas o service o ignora, invocando `productRepository.updateStatus()` diretamente:

```java
// Existe no domínio:
public void activate(Long idUsuario) {
    this.codStatus = ProductStatus.ACTIVE.getCode();
    this.idUsuarioManutencao = idUsuario;
    this.dtManutencao = LocalDateTime.now();
}

// Mas o service faz:
productRepository.updateStatus(productCode, ProductStatus.ACTIVE.getCode(), idUsuario);
// Não usa product.activate(idUsuario) + productRepository.save(product)
```

Isso bypassa o modelo de domínio rico. Qualquer lógica futura dentro de `activate()` nunca será executada.

💡 **Usar o método de domínio:**
```java
product.activate(idUsuario);
productRepository.save(product);
```

---

## 6. TESTABILIDADE

---

### 📍 Services — todos dependem de `UserPersistencePort`

⚠️ **Problema:** Todos os services de catálogo têm dependência de `UserPersistencePort` apenas para o `resolveUser()`. Nos testes unitários, você precisa mockar o repositório de usuário em todos eles, mesmo que o teste seja sobre lógica de produto.

💡 **Se a resolução de usuário for extraída para `AuthenticatedUserResolver` (proposta acima), os services se tornam mais puros e fáceis de testar.**

---

### 📍 `FeeFareService.TaxaCalculada` — inner class pública em service

⚠️ **Problema:** Um DTO/value object aninhado dentro de um service mistura responsabilidades e dificulta testes isolados:
```java
public class FeeFareService implements FeeFareManagementUseCase {
    // ...
    public static class TaxaCalculada { ... }
}
```

💡 **Mover para o domínio ou para DTO da camada de aplicação:**
```java
// product/domain/TaxCalculation.java (value object)
public record TaxCalculation(BigDecimal adminFee, BigDecimal serviceFee) {
    public static TaxCalculation exempt() {
        return new TaxCalculation(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
```

---

## 7. PADRONIZAÇÃO

---

### 📍 Inconsistências de nomenclatura no domínio

| Inconsistência | Onde |
|---|---|
| `Family` tem `idUsuarioCadastro` como `User` | `Family`, `Modality`, `ProductVersion` |
| `Product` tem `idUsuarioCadastro` como `Long` | `Product` |
| `ProductVersion` usa `dtUsoInicio` | domínio |
| `CreateVersionCommand` usa `dtUsoIni` | use case |
| Controller usa `dtUsoIni` | request record |
| Adaptador JPA usa `dtUsoIni` | entity? |

O nome `dtUsoIni` vs `dtUsoInicio` é inconsistente entre camadas. O domínio deve ser a fonte da verdade.

---

### 📍 Mix de idiomas nos nomes

Os campos usam abreviações em português (`cod`, `des`, `flg`, `dt`) que são legadas (provavelmente herdadas do schema Oracle). No entanto, métodos, classes e pacotes usam inglês (`ProductService`, `FamilyMapper`, `activate()`). Essa mistura é aceitável dado o contexto legado, mas convém documentar a decisão explicitamente.

---

### 📍 `ProductErrorType.fromCode()` — nomenclatura enganosa

```java
public static ProductErrorType fromCode(String description) {
    for (ProductErrorType type : values()) {
        if (type.description.equals(description)) { // busca por description, não por code!
```

O método se chama `fromCode` mas recebe e compara `description`. Nome completamente enganoso.

---

## 🔥 REFATORAÇÕES PRIORITÁRIAS

| # | Prioridade | Ação | Impacto |
|---|---|---|---|
| 1 | 🔴 Crítico | **Corrigir `FamilyMapper.toEntity()`** — IDs de usuário não são salvos. Bug em produção. | Bugs de auditoria |
| 2 | 🔴 Crítico | **Renomear ports** `*Repository` → `*PersistencePort` para conformidade com convenção do projeto | Arquitetura |
| 3 | 🔴 Crítico | **Substituir filtragem em memória** em `ProductAdapterJpa.findAll()` e `ModalityAdapterJpa.findAll()` por query JPA com filtro | Performance |
| 4 | 🟠 Alto | **Mover `resolveUserId()` para componente compartilhado** e remover `UserPersistencePort` dos controllers | DRY + Arquitetura |
| 5 | 🟠 Alto | **Colocar `HttpStatus` no enum `ProductErrorType`** em vez de string matching na exceção | Robustez |
| 6 | 🟠 Alto | **Usar `product.activate()` / `product.deactivate()`** no service em vez de `updateStatus()` direto | Rich Domain Model |
| 7 | 🟠 Alto | **Adicionar `@Valid` + Bean Validation** em todos os request bodies | Segurança / Qualidade |
| 8 | 🟡 Médio | **Abstrair os 5 serviços de catálogo** em `AbstractCatalogueService` | DRY |
| 9 | 🟡 Médio | **Paginação real** via `Pageable` nos ports e queries JPA | Escalabilidade |
| 10 | 🟡 Médio | **Builder pattern** nos construtores com muitos argumentos | Manutenibilidade |
| 11 | 🟡 Médio | **Corrigir HTTP verbs** em `FeeFareController` (`POST → GET` para listagens) | REST compliance |
| 12 | 🟡 Médio | **Resolver race condition** em `generateNextVersionCode()` | Confiabilidade |
| 13 | 🟢 Baixo | **Padronizar `@Transactional`** para `org.springframework.*` em toda a codebase | Consistência |
| 14 | 🟢 Baixo | **Mover `TaxaCalculada`** para value object de domínio | Clean Architecture |
| 15 | 🟢 Baixo | **Remover `@SecurityRequirement` redundante** nos métodos dos controllers | Limpeza |

---

## 🧠 RECOMENDAÇÕES PARA NÍVEL SÊNIOR

**1. Adotar Rich Domain Model de forma consistente**
As entidades de domínio (`Product`, `Family`, `Modality`) já têm métodos de transição de estado (`activate`, `deactivate`). Mas os services não os usam. A regra é: **toda lógica de negócio vive no domínio**, o service apenas orquestra. Hoje, o service contém lógica que deveria estar no domínio.

**2. Separar Use Cases por responsabilidade (CQRS lite)**
`ProductUseCase` mistura comandos e queries. Considere `ProductCommandUseCase` e `ProductQueryUseCase`. Facilita controle de transação (`readOnly=true`) e diferentes SLAs de performance.

**3. Response DTOs para todos os endpoints**
`Product`, `Family`, `Modality` (entidades de domínio) estão sendo serializados diretamente como resposta HTTP. Isso acopla o contrato da API ao modelo interno. Qualquer refatoração no domínio quebra clientes. Criar `ProductResponseDTO`, `FamilyResponseDTO` (já existe para Family mas inconsistente) para _todos_ os endpoints.

**4. Estrutura de pacotes sugerida para ports**
Hoje há confusão entre `adapter/port/out/` e `application/port/out/`. A estrutura recomendada para hexagonal é:
```
product/
  domain/             ← entidades, value objects, enums, exceptions
  application/
    port/
      in/             ← use cases (interfaces)
      out/            ← *PersistencePort, *GatewayPort (interfaces)
    service/          ← implementações dos use cases
  adapter/
    in/
      rest/           ← controllers, request/response DTOs
    out/
      jpa/            ← adapters JPA, entidades JPA, mappers, JpaRepositories
      gateway/        ← implementações de gateways externos
```
Hoje o projeto tem `adapter/port/out/persistence/entity/` — a pasta `port` dentro de `adapter` é redundante. Entidades JPA não são ports; são detalhes de implementação do adapter.

**5. Observabilidade e rastreabilidade**
Nenhum service possui logging estruturado. Em produção, sem logs de auditoria de operações críticas (criação de produto, ativação), depuração de incidentes é impossível. Adicionar `log.info("Produto {} ativado pelo usuário {}", productCode, idUsuario)` nos pontos críticos.