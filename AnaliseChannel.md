# Análise Arquitetural — Módulo `channel`

---

## 1. QUALIDADE DO CÓDIGO

---

### 📍 Todos os Services (`AddressChannelService`, `ContactChannelService`, `MarketingDistribuitionChannelService`, `RechargeLimitService`, `ProductChannelService`)

⚠️ **Método `resolveUser(Long idUsuario)` duplicado em 5+ classes**

Cada service possui sua própria cópia idêntica do método privado. Violação direta do princípio DRY.

```java
// ContactChannelService
private User resolveUser(Long idUsuario) {
    if (idUsuario == null) return null;
    return userRepository.findById(idUsuario).orElse(null);
}

// RechargeLimitService — cópia idêntica
private User resolveUser(Long idUsuario) {
    if (idUsuario == null) return null;
    return userRepository.findById(idUsuario).orElse(null);
}
```

💡 Centralizar em um `@Component` compartilhado:

```java
// shared/helper/UserResolverHelper.java
@Component
@RequiredArgsConstructor
public class UserResolverHelper {
    private final UserPersistencePort userPort;

    public User resolve(Long userId) {
        if (userId == null) return null;
        return userPort.findById(userId).orElse(null);
    }
}
```

---

### 📍 `AddressChannelService` / `ContactChannelService`

⚠️ **Construtor com 28 argumentos nulos para criar referência por ID**

```java
// AddressChannelService.java
new SalesChannel(cmd.codCanal(), null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null)
```

Este padrão é extremamente frágil: qualquer adição de campo em `SalesChannel` quebra silenciosamente todos esses locais.

💡 Usar factory method no domínio ou um builder de referência:

```java
// No domínio SalesChannel:
public static SalesChannel ofReference(String codCanal) {
    SalesChannel ref = new SalesChannel();
    ref.setCodCanal(codCanal);
    return ref;
}

// No service:
cmd.codCanal() != null ? SalesChannel.ofReference(cmd.codCanal()) : null
```

---

### 📍 `SalesChannelService.createSalesChannel()` / `updateSalesChannel()`

⚠️ **Construtor all-args duplicado entre create e update — forte acoplamento posicional**

O update reconstrói o objeto inteiro com 28 parâmetros posicionais, misturando campos imutáveis do `existing` com novos do `cmd`. Qualquer reordenação de campo é um bug silencioso.

💡 Extrair a montagem para um método `SalesChannel.applyUpdate(UpdateSalesChannelCommand cmd, User usuario)` ou utilizar o padrão record de atualização parcial via builder.

---

### 📍 Todos os domínios (`SalesChannel`, `AddressChannel`, `ContactChannel`, etc.)

⚠️ **Domínio completamente mutável via `@Setter` — violação de encapsulamento DDD**

Os objetos de domínio expõem setters livres, permitindo que qualquer camada altere o estado arbitrariamente, tornando regras de negócio impossíveis de garantir.

💡 Remover `@Setter` do nível de classe. Expor apenas o necessário via métodos de domínio com intenção negocial. Campos imutáveis no registro histórico (`dtCadastro`, `codCanal`) devem ser `final` ou sem setter.

---

### 📍 `codStatus` em todos os domínios

⚠️ **Status representado como `String` ("A"/"I") — magic strings espalhadas**

```java
private static final String STATUS_ACTIVE   = "A";
private static final String STATUS_INACTIVE = "I";
```

As constantes ficam em cada service individualmente (duplicadas), e o domínio não tem esse conceito.

💡 Criar enum de domínio compartilhado:

```java
// channel/domain/enums/ChannelStatus.java
public enum ChannelStatus {
    ACTIVE("A"), INACTIVE("I");

    private final String code;

    public static ChannelStatus fromCode(String code) {
        return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Status inválido: " + code));
    }
}
```

---

## 2. ORGANIZAÇÃO E ARQUITETURA

---

### 📍 `application/port/out/` — todos os 8 arquivos

⚠️ **Portas de saída nomeadas com sufixo `Repository` em vez de `Port`**

```
SalesChannelRepository.java       ← deveria ser SalesChannelPersistencePort
TypesActivityRepository.java      ← deveria ser TypesActivityPersistencePort
ProductChannelRepository.java     ← deveria ser ProductChannelPersistencePort
...
```

O projeto possui uma convenção documentada e bem definida (`*Port`), violada sistematicamente neste módulo. O sufixo `Repository` é reservado para as interfaces JPA da camada de infraestrutura.

💡 Renomear para `*PersistencePort` conforme o padrão da memória do repositório.

---

### 📍 `application/port/out/ProductChannelRepository.java`

⚠️ **Violação arquitetural grave: porta da camada de aplicação depende da camada de infraestrutura**

```java
import br.sptrans.scd.channel.adapter.port.out.jpa.projection.ProductChannelProjection;

public interface ProductChannelRepository {
    List<ProductChannelProjection> findCompletoByCanal(String codCanal); // ← import da infra
}
```

A interface de porta da aplicação importa um tipo da camada `adapter`. Isso inverte a direção de dependência — a camada de domínio/aplicação não pode depender da infraestrutura.

💡 Mover `ProductChannelProjection` para `channel/application/port/out/query/` ou criar um DTO de domínio equivalente em `channel/domain/`.

---

### 📍 `adapter/port/in/rest/SalesChannelController.java`

⚠️ **Controller injeta `UserPersistencePort` diretamente — lógica de aplicação no adapter**

```java
private final UserPersistencePort userRepository; // infraestrutura no controller

private Long resolveUserId(Authentication authentication) {
    return userRepository.findByCodLogin(authentication.getName())
            .map(u -> u.getIdUsuario())
            .orElse(null);
}
```

O `resolveUserId` é repetido em `SalesChannelController` e `ProductChannelController`. A responsabilidade de resolver usuário a partir do token pertence à camada de aplicação. Além disso, retornar `null` silenciosamente quando o usuário não é encontrado pode causar `NullPointerException` downstream.

💡 O `idUsuario` deve ser extraído no controller via `SecurityContextHelper` e passado apenas quando necessário, ou o use case deve aceitar `String codLogin` e resolver internamente.

---

### 📍 `application/service/ProductChannelService.java`

⚠️ **Dois campos do mesmo tipo injetados — código morto visível**

```java
private final ProductChannelRepository repository;
private final ProductChannelRepository productChannelJpaRepository; // campo duplicado!
// private final ProductChannelJpaRepository productChannelJpaRepository; // código comentado
```

Existe uma injeção duplicada e código comentado que indica uma refatoração incompleta.

---

### 📍 `adapter/port/out/jpa/mapper/ProductChannelMapper.java`

⚠️ **Mapper recebe `UserPersistencePort` como parâmetro — violação de SRP**

```java
public ProductChannel toDomain(ProductChannelEntityJpa entity, UserPersistencePort userR) {
    // ...realiza queries de banco dentro do mapper
}
```

O mapper não deve fazer consultas ao banco. Isso acumula responsabilidades e cria problema de N+1.

💡 O adapter deve resolver os usuários antes de passar para o mapper:

```java
// No ProductChannelAdapterJpa:
public Optional<ProductChannel> findById(ProductChannelKey id) {
    return jpaRepository.findById(entityKey)
        .map(entity -> {
            User userCad = resolveUser(entity.getIdUsuarioCadastro());
            User userMan = resolveUser(entity.getIdUsuarioManutencao());
            return mapper.toDomain(entity, userCad, userMan);
        });
}
```

---

### 📍 `adapter/port/in/rest/SalesChannelController.java`

⚠️ **Request DTOs definidos como inner records dentro do Controller**

```java
public record CreateSalesChannelRequest(...) {} // 23 campos definidos dentro do controller
public record UpdateSalesChannelRequest(...) {}
```

Isso polui o controller, dificulta testes e impossibilita reutilização.

💡 Mover para `adapter/port/in/rest/dto/` como arquivos separados.

---

## 3. REUTILIZAÇÃO DE CÓDIGO

---

### 📍 `adapter/port/out/jpa/adapter/ProductChannelAdapterJpa.java`

⚠️ **`findByCodCanal` e `findByCodProduto` carregam a tabela INTEIRA em memória**

```java
public List<ProductChannel> findByCodCanal(String codCanal) {
    return productChannelJpaRepository.findAll().stream() // FULL TABLE SCAN
        .map(entity -> productChannelMapper.toDomain(entity, userRepository))
        .filter(e -> e.getId().getCodCanal().equals(codCanal))
        .toList();
}
```

Para cada registro mapeado, são disparadas até 2 queries adicionais (usuários). Com N registros, o resultado é N*2 + 1 queries. Uma tabela com 10.000 registros executaria até 20.001 queries.

⚠️ **Bug: `findByCodProduto` filtra pelo campo errado**

```java
public List<ProductChannel> findByCodProduto(String codProduto) {
    return productChannelJpaRepository.findAll().stream()
        .filter(e -> e.getId().getCodCanal().equals(codProduto)) // BUG: deveria ser getCodProduto()
        .toList();
}
```

💡 Usar queries nativas no `ProductChannelJpaRepository`:

```java
// ProductChannelJpaRepository
List<ProductChannelEntityJpa> findByIdCodCanal(String codCanal);
List<ProductChannelEntityJpa> findByIdCodProduto(String codProduto);
```

---

### 📍 `SalesChannelController`, `ProductChannelController`

⚠️ **`resolveUserId(Authentication)` duplicado nos controllers**

💡 Extrair para um `SecurityContextHelper`:

```java
// shared/security/SecurityContextHelper.java
@Component
@RequiredArgsConstructor
public class SecurityContextHelper {
    private final UserPersistencePort userPort;

    public Long resolveUserId(Authentication authentication) {
        if (authentication == null) return null;
        return userPort.findByCodLogin(authentication.getName())
            .map(User::getIdUsuario)
            .orElse(null);
    }
}
```

---

### 📍 Controllers em geral

⚠️ **Paginação em memória via `PageResponse.fromList` em todos os endpoints de listagem**

```java
List<SalesChannel> all = salesChannelUseCase.findAllSalesChannels(stCanais);
// ...paginação manual após carregar TUDO
return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
```

Todos os 8 controllers fazem isso. O banco nunca recebe os parâmetros de paginação.

💡 Introduzir `Pageable` do Spring Data nos use cases e ports, propagando até os repositórios JPA.

---

## 4. BOAS PRÁTICAS SPRING BOOT

---

### 📍 AgreementValidity.java / RechargeLimit.java

⚠️ **Inconsistência no modelo de usuário — `Long idUsuario` vs. `User idUsuarioCadastro`**

```java
// AgreementValidity
private Long idUsuario; // Raw Long

// RechargeLimit
private Long idUsuarioCadastro; // Raw Long

// ProductChannel
private User idUsuarioCadastro; // User completo
```

Três padrões diferentes para o mesmo conceito dentro do mesmo módulo.

---

### 📍 `TypesActivityController`

⚠️ **Retorna domínio (`TypesActivity`) diretamente em vez de DTO de resposta**

```java
public ResponseEntity<TypesActivity> createTypesActivity(...) {
    TypesActivity result = typesActivityUseCase.createTypesActivity(...);
    return ResponseEntity.status(HttpStatus.CREATED).body(result); // domínio exposto
}
```

O domínio de negócio é exposto diretamente para o cliente HTTP. Isso acopla a API ao modelo interno.

---

### 📍 `domain/exception/ChannelException.java`

⚠️ **Mapeamento de HTTP status via parsing de nome do enum — frágil e não-expressivo**

```java
@Override
public HttpStatus getHttpStatus() {
    String name = errorType.name();
    if (name.contains("NOT_FOUND")) {
        return HttpStatus.NOT_FOUND;
    } else if (name.contains("ALREADY_EXISTS") || name.contains("CODE_ALREADY_EXISTS")) {
        return HttpStatus.CONFLICT;
    }
    ...
}
```

O comportamento HTTP está acoplado à convenção de nomeação do enum. Se alguém renomear um valor, o status HTTP muda silenciosamente.

💡 Adicionar o `HttpStatus` diretamente no `ChannelErrorType`:

```java
@Getter
@AllArgsConstructor
public enum ChannelErrorType {
    TYPES_ACTIVITY_NOT_FOUND("Tipo de atividade não encontrado.", HttpStatus.NOT_FOUND),
    TYPES_ACTIVITY_CODE_ALREADY_EXISTS("Código já cadastrado.", HttpStatus.CONFLICT),
    TYPES_ACTIVITY_ALREADY_ACTIVE("Já está ativo.", HttpStatus.UNPROCESSABLE_ENTITY),
    // ...
    ;

    private final String description;
    private final HttpStatus httpStatus;
}
```

---

### 📍 SalesChannelMapper.java

⚠️ **`toDomain` e `toEntity` ignoram todos os relacionamentos**

```java
@Mappings({
    @Mapping(target = "codClassificacaoPessoa", ignore = true),
    @Mapping(target = "codAtividade", ignore = true),
    @Mapping(target = "idUsuarioCadastro", ignore = true),
    @Mapping(target = "idUsuarioManutencao", ignore = true)
})
SalesChannel toDomain(SalesChannelEntityJpa entity);
```

O domínio retornado por uma busca terá `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro` sempre `null`. Dados são perdidos silenciosamente na leitura.

---

### 📍 Request DTOs em geral

⚠️ **Ausência de validação de entrada (`@Valid`, Bean Validation)**

Nenhum DTO de request usa `@NotNull`, `@NotBlank`, `@Size`, etc. Dados inválidos chegam ao use case e podem gerar erros obscuros ou NPEs.

---

## 5. PERFORMANCE E ESCALABILIDADE

| Problema | Localização | Impacto |
|---|---|---|
| Full table scan + filter em memória | `ProductChannelAdapterJpa.findByCodCanal` | 🔴 Crítico |
| N+1 por resolução de usuário no mapper | `ProductChannelMapper.toDomain` | 🔴 Crítico |
| Paginação na memória após `findAll()` | Todos os 8 controllers | 🟠 Alto |
| `SalesChannelJpaRepository.findAllByStCanais` sem paginação | `SalesChannelAdapterJpa` | 🟠 Alto |
| `TypesActivityService.updateTypesActivity` cria novo objeto sem `dtManutencao` | `TypesActivityService` | 🟡 Médio |

---

## 6. TESTABILIDADE

⚠️ **`ProductChannelMapper.toDomain(entity, userPersistencePort)` — impossível testar sem banco**

A assinatura atual obriga o teste a mockar um port de persistência só para converter tipos.

⚠️ **Services com `resolveUser` privado e `LocalDateTime.now()` embutido no código** — dificultam testes determinísticos. Ideal seria injetar um `Clock` do Java Time.

⚠️ **`findProjections` no `ProductChannelService` lança `UnsupportedOperationException` e tem lógica condicional não coberta** — impossível testar o caminho feliz.

---

## 7. PADRONIZAÇÃO

⚠️ **Typo no nome de classe propagado por todo o módulo**  
`MarketingDistribuitionChannel` → `MarketingDistributionChannel` ("Distribuition" não existe em inglês)

⚠️ **`CanalResponseDTO.codCanal` é `Long`, mas `SalesChannel.codCanal` é `String` — type mismatch**

⚠️ **Nomenclatura inconsistente de métodos `findBy*`**  
`findBySalesChannel(String codCanal)` e `findByTypesActivity(String codAtividade)` — o prefixo `findBy` implica filtro por propriedade (estilo Spring Data), mas o parâmetro é o ID. O correto seria `findSalesChannelById` ou simplesmente `findSalesChannel`.

⚠️ **`SalesChannelReponseDTO.java`** — typo no nome do arquivo ("Reponse" faltando "s").

⚠️ **Código comentado e `UnsupportedOperationException` em código de produção:**
```java
// private final ProductChannelJpaRepository productChannelJpaRepository; // morto
throw new UnsupportedOperationException("Not supported yet."); // findByIdOtimized
```

---

## 🔥 Lista das Principais Refatorações Prioritárias

| Prioridade | Refatoração | Impacto |
|---|---|---|
| 🔴 1 | **Corrigir bug `findByCodProduto` filtrando pelo campo errado** | Funcional |
| 🔴 2 | **Eliminar full table scan em `ProductChannelAdapterJpa`** — usar queries JPA por canal/produto | Performance crítica |
| 🔴 3 | **Mover `ProductChannelProjection` para fora da camada de infraestrutura** — viola a hierarquia de dependências | Arquitetural |
| 🟠 4 | **Renomear `*Repository` → `*PersistencePort`** nos `application/port/out` | Convenção do projeto |
| 🟠 5 | **Centralizar `resolveUser` em `UserResolverHelper`** e remover código duplicado de 5 services | DRY / manutenção |
| 🟠 6 | **Substituir strings "A"/"I" por enum `ChannelStatus`** e adicionar status ao `ChannelErrorType` | Segurança de tipo |
| 🟠 7 | **Implementar paginação no banco** (`Pageable`) em vez de carregar tudo e paginar na memória | Performance |
| 🟡 8 | **Remover `@Setter` dos domínios** — expor factory method e métodos de domínio expressivos | DDD / encapsulamento |
| 🟡 9 | **Corrigir mapeadores (`SalesChannelMapper`)** para não ignorar relacionamentos silenciosamente | Correção de dados |
| 🟡 10 | **Mover Request DTOs para arquivos separados** e adicionar Bean Validation | Organização / segurança |
| 🟡 11 | **Extrair `resolveUserId` para `SecurityContextHelper`** e remover `UserPersistencePort` dos controllers | SRP |

---

## 🧠 Recomendações para Elevar o Projeto ao Nível Sênior

**1. Domínio rico, não anêmico**  
Os objetos de domínio atuais são apenas data bags com getters/setters. Um domínio rico encapsularia regras:
```java
public class SalesChannel {
    public void activate(User operator) {
        if (this.status == ChannelStatus.ACTIVE) throw new ChannelException(...);
        this.status = ChannelStatus.ACTIVE;
        this.updatedBy = operator;
        this.updatedAt = LocalDateTime.now();
    }
}
```

**2. Separar portas de leitura e escrita (CQRS light)**  
`ProductChannelRepository` mistura queries simples, queries otimizadas e projeções complexas. Separar em `ProductChannelQueryPort` e `ProductChannelCommandPort` deixa cada contrato claro.

**3. Unificar o modelo de usuário nos domínios**  
`AgreementValidity` usa `Long`, `RechargeLimit` usa `Long`, `ProductChannel` usa `User`. Definir um padrão único — e manter.

**4. Adicionar testes de integração por slice (`@DataJpaTest`, `@WebMvcTest`)**  
Nenhum teste de integração existe para os adapters JPA do módulo. Os testes de unidade para os services ficam facilmente implementáveis após a extração do `UserResolverHelper` e a separação do mapper do port.

**5. Estrutura de pacotes sugerida para novos sub-módulos:**
```
channel/
  domain/
    model/          # SalesChannel, ProductChannel, ...
    valueobject/    # ProductChannelKey, ChannelStatus (enum), ...
    exception/      # ChannelException + ChannelErrorType (com HttpStatus)
    port/
      out/          # ProductChannelPersistencePort, ...
  application/
    port/
      in/           # SalesChannelUseCase (Commands como inner records estão OK)
      out/          # *PersistencePort apenas (sem tipos de infra importados)
    service/        # SalesChannelService, ...
  adapter/
    in/
      rest/
        dto/        # Todos os Request/Response DTOs separados
    out/
      jpa/
        entity/     # *EntityJpa
        repository/ # *JpaRepository
        mapper/     # *Mapper
        adapter/    # *AdapterJpa (implementações dos ports)
```