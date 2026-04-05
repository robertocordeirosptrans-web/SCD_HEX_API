
## 1. QUALIDADE DO CÓDIGO


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

| Status | Prioridade | Refatoração | Impacto |
|---|---|---|---|
| ✅ | — | **Criar `UserResolverHelper` (interface + impl) em `shared/helper/`** | DRY / manutenção |
| 🔄 | — | **Trocar injeção de `UserResolverHelperImpl` pela interface `UserResolverHelper` nos services** | Desacoplamento |
| ⚠️ | 🔴 1 | **Corrigir bug `findByCodProduto` filtrando pelo campo errado** | Funcional |
| ⚠️ | 🔴 2 | **Eliminar full table scan em `ProductChannelAdapterJpa`** — usar queries JPA por canal/produto | Performance crítica |
| ⚠️ | 🔴 3 | **Mover `ProductChannelProjection` para fora da camada de infraestrutura** — viola a hierarquia de dependências | Arquitetural |
| ⚠️ | 🟠 4 | **Renomear `*Repository` → `*PersistencePort`** nos `application/port/out` | Convenção do projeto |
| ⚠️ | 🟠 5 | **Substituir strings "A"/"I" por enum `ChannelStatus`** e adicionar `HttpStatus` ao `ChannelErrorType` | Segurança de tipo |
| ⚠️ | 🟠 6 | **Implementar paginação no banco** (`Pageable`) em vez de carregar tudo e paginar na memória | Performance |
| ⚠️ | 🟡 7 | **Extrair `resolveUserId` para `UserResolverHelper`** e remover `UserPersistencePort` dos controllers | SRP |
| ⚠️ | 🟡 8 | **Remover `@Setter` dos domínios** — expor factory method e métodos de domínio expressivos | DDD / encapsulamento |
| ⚠️ | 🟡 9 | **Corrigir `SalesChannelMapper`** para não ignorar relacionamentos silenciosamente | Correção de dados |
| ⚠️ | 🟡 10 | **Mover Request DTOs para arquivos separados** e adicionar Bean Validation | Organização / segurança |
| ⚠️ | 🟡 11 | **Corrigir `ProductChannelMapper`** — remover `UserPersistencePort` da assinatura do mapper | SRP / testabilidade |

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

---

## 🗺️ Guia de Refatoração por Fases

> **Princípio de execução:** cada fase deve ser concluída, compilada (`mvn compile`) e commitada antes de iniciar a próxima. Nenhuma fase cria regressões para a anterior.



### 🟠 Fase 2 — Integridade Arquitetural

**Objetivo:** Eliminar violações de dependência entre camadas e alinhar nomenclatura à convenção do projeto.  
**Pré-requisito:** Fase 1 completa.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 2.1 | **Mover `ProductChannelProjection` para `channel/application/port/out/query/`** | `ProductChannelProjection.java` → `application/port/out/query/` | Nenhum import em `application/` aponta para `adapter/` |
| 2.2 | **Atualizar `ProductChannelRepository` para importar de `query/` em vez de `adapter/`** | `ProductChannelRepository.java` | `mvn compile` verde |
| 2.3 | **Renomear todas as portas de saída** de `*Repository` para `*PersistencePort` nos 8 arquivos de `application/port/out/` | `*Repository.java` → `*PersistencePort.java` | Nenhum arquivo `application/port/out/` tem sufixo `Repository` |
| 2.4 | **Atualizar todas as referências** às interfaces renomeadas em services e adapters | `channel/application/service/*.java`, `channel/adapter/port/out/jpa/adapter/*.java` | `mvn compile` verde |
| 2.5 | **Remover injeção de `UserPersistencePort` dos controllers** (`SalesChannelController`, `ProductChannelController`) — delegar resolução ao `UserResolverHelper` | `SalesChannelController.java`, `ProductChannelController.java` | Sem `UserPersistencePort` em nenhum controller |

---

### 🟠 Fase 3 — Segurança de Tipo e Domínio

**Objetivo:** Eliminar magic strings de status e tornar o mapeamento de erro HTTP explícito e seguro.  
**Pré-requisito:** Fase 2 completa.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 3.1 | **Criar `ChannelStatus` enum** com valores `ACTIVE("A")` e `INACTIVE("I")` e método `fromCode(String)` | `channel/domain/enums/ChannelStatus.java` | Enum criado e compilando |
| 3.2 | **Substituir `codStatus` (String) por `ChannelStatus`** em todos os domínios: `SalesChannel`, `ProductChannel`, `TypesActivity`, `AddressChannel`, `ContactChannel`, `RechargeLimit`, `MarketingDistribuitionChannel`, `AgreementValidity` | `channel/domain/*.java` | Sem `String codStatus` nos domínios |
| 3.3 | **Adicionar conversor JPA** para `ChannelStatus` (implements `AttributeConverter<ChannelStatus, String>`) | `channel/adapter/port/out/jpa/converter/ChannelStatusConverter.java` | Entidades mapeiam corretamente para "A"/"I" no banco |
| 3.4 | **Ajustar constantes de status nos services** — remover `STATUS_ACTIVE = "A"` e usar `ChannelStatus.ACTIVE` | `channel/application/service/*.java` | Sem strings literais "A" ou "I" nos services |
| 3.5 | **Adicionar `HttpStatus` ao `ChannelErrorType`** e simplificar `ChannelException.getHttpStatus()` | `channel/domain/enums/ChannelErrorType.java`, `channel/domain/exception/ChannelException.java` | `getHttpStatus()` delega ao enum; sem lógica de parsing de nome |

---

### 🟠 Fase 4 — Performance de Leitura (Paginação Real)

**Objetivo:** Enviar parâmetros de paginação ao banco em vez de paginar na memória.  
**Pré-requisito:** Fase 2 completa. (Pode ser desenvolvida em paralelo com a Fase 3.)

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 4.1 | **Adicionar `Pageable` aos use cases de listagem** — `findAllSalesChannels`, `findAllProductChannels`, etc. | `channel/application/port/in/*UseCase.java` | Assinaturas aceitam `Pageable` |
| 4.2 | **Propagar `Pageable` aos ports de saída** | `channel/application/port/out/*PersistencePort.java` | Ports retornam `Page<T>` |
| 4.3 | **Implementar paginação nos adapters JPA** — usar `JpaRepository.findAll(Pageable)` ou queries derivadas com `Pageable` | `channel/adapter/port/out/jpa/adapter/*.java` | Log SQL contém `LIMIT` e `OFFSET` |
| 4.4 | **Atualizar controllers para extrair `page` e `size` e construir `PageRequest`** — remover `PageResponse.fromList` | `channel/adapter/port/in/rest/*.java` | Resposta HTTP já vem paginada do banco |
| 4.5 | **Adicionar `Page` ao `SalesChannelJpaRepository.findAllByStCanais`** | `SalesChannelJpaRepository.java`, `SalesChannelAdapterJpa.java` | Query usa `Pageable` |

---

### 🟡 Fase 5 — Organização e Contrato da API

**Objetivo:** Separar Request DTOs, validar entradas e padronizar respostas.  
**Pré-requisito:** Fase 2 completa.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 5.1 | **Separar Request DTOs do `SalesChannelController`** para `adapter/port/in/rest/dto/` | `CreateSalesChannelRequest.java`, `UpdateSalesChannelRequest.java` | Controller sem inner records |
| 5.2 | **Adicionar Bean Validation** (`@NotBlank`, `@NotNull`, `@Size`) em todos os Request DTOs | `dto/*.java` | Requests inválidos retornam `400 Bad Request` com mensagem clara |
| 5.3 | **Criar `TypesActivityResponseDTO`** e converter `TypesActivityController` para retorná-lo | `dto/TypesActivityResponseDTO.java`, `TypesActivityController.java` | Controller não expõe objeto de domínio |
| 5.4 | **Corrigir typo `SalesChannelReponseDTO` → `SalesChannelResponseDTO`** | `SalesChannelReponseDTO.java` → `SalesChannelResponseDTO.java` + todas as referências | Sem "Reponse" no codebase |
| 5.5 | **Corrigir type mismatch `CanalResponseDTO.codCanal`** — de `Long` para `String` | `CanalResponseDTO.java` | Tipo coerente com `SalesChannel.codCanal` |

---

### 🟡 Fase 6 — Encapsulamento e Domínio Rico

**Objetivo:** Remover setters livres dos domínios e introduzir factory methods expressivos.  
**Pré-requisito:** Fases 3 e 5 completas.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 6.1 | **Adicionar `SalesChannel.ofReference(String codCanal)`** factory method | `SalesChannel.java` | `AddressChannelService` e `ContactChannelService` usam `ofReference` em vez do construtor de 28 args |
| 6.2 | **Remover `@Setter` de classe** nos domínios — manter apenas setters necessários para JPA e services | `channel/domain/*.java` | `@Setter` removido do nível de classe; campos `codCanal`, `dtCadastro` sem setter público |
| 6.3 | **Adicionar `SalesChannel.applyUpdate(UpdateSalesChannelCommand, User)`** para centralizar a montagem do update | `SalesChannel.java`, `SalesChannelService.java` | `updateSalesChannel` sem construtor all-args de 28 parâmetros |
| 6.4 | **Unificar modelo de usuário nos domínios** — `AgreementValidity` e `RechargeLimit` passam de `Long` para `User` | `AgreementValidity.java`, `RechargeLimit.java`, mappers correspondentes | Sem `Long idUsuario` raw nos domínios de `channel` |
| 6.5 | **Corrigir `SalesChannelMapper`** — mapear `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro`, `idUsuarioManutencao` em vez de ignorar | `SalesChannelMapper.java` | Query de leitura retorna valores não-nulos para esses campos quando presentes no banco |

---

### 🟡 Fase 7 — Testabilidade e Padronização Final

**Objetivo:** Cobrir os componentes com testes e corrigir issues de padronização remanescentes.  
**Pré-requisito:** Todas as fases anteriores completas.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 7.1 | **Corrigir typo `MarketingDistribuitionChannel` → `MarketingDistributionChannel`** em todo o módulo | Renomear classes, arquivos e pacotes | Sem "Distribuition" no codebase |
| 7.2 | **Implementar `findByIdOtimized` ou remover o método** do port e adapter | `ProductChannelAdapterJpa.java`, `ProductChannelPersistencePort.java` | Sem `UnsupportedOperationException` em código de produção |
| 7.3 | **Adicionar `@DataJpaTest` para os adapters JPA críticos** | `test/channel/adapter/ProductChannelAdapterJpaTest.java`, `SalesChannelAdapterJpaTest.java` | Queries derivadas testadas contra schema real |
| 7.4 | **Adicionar `@WebMvcTest` para os controllers principais** | `test/channel/adapter/SalesChannelControllerTest.java` | Validação de request DTO coberta por testes |
| 7.5 | **Injetar `Clock` nos services** em vez de usar `LocalDateTime.now()` diretamente | `channel/application/service/*.java` | Testes de unidade passam com `Clock.fixed(...)` |
| 7.6 | **Padronizar nomenclatura dos métodos de busca** — `findBySalesChannel(codCanal)` → `findSalesChannel(codCanal)` | `SalesChannelUseCase.java`, `SalesChannelService.java`, `SalesChannelPersistencePort.java`, controller | Métodos `findBy*` usados apenas com semântica Spring Data |

---

### Visão Geral do Roadmap

```
Fase 0 ── CONCLUÍDA ──► Fase 0 (residual 0.3) ─┐
                                                  ▼
                          Fase 1 (Bugs críticos + performance ProductChannel)
                                                  │
                                                  ▼
                          Fase 2 (Arquitetura + nomenclatura)
                                    │                    │
                          ┌─────────┴──────┐   ┌─────────┴─────────┐
                          ▼                ▼   ▼                   ▼
                        Fase 3           Fase 4                  Fase 5
                    (ChannelStatus    (Paginação real)       (DTOs + validação)
                     + ErrorType)
                          │                │                       │
                          └────────────────┴───────────────────────┘
                                                  │
                                                  ▼
                                    Fase 6 (Domínio rico + encapsulamento)
                                                  │
                                                  ▼
                                    Fase 7 (Testes + padronização final)
```