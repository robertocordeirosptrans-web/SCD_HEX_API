
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

Este padrão é extremamente frágil: qualquer adição de campo em `SalesChannel` quebra silenciosamente todos esses locais. Padrão confirmado tanto no `createAddressChannel` quanto no `updateAddressChannel`, e idem para `ContactChannelService`.

💡 Criar factory method no domínio:

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

### 📍 Services em geral

⚠️ **Services injetam `UserResolverHelperImpl` (implementação concreta) em vez da interface `UserResolverHelper`**

```java
// SalesChannelService.java
private final UserResolverHelperImpl userResolverHelper; // ← concreto
```

Os controllers já usam a interface `UserResolverHelper` corretamente, mas os services continuam acoplados à implementação.

💡 Trocar para `private final UserResolverHelper userResolverHelper;` em todos os services.

---

## 2. ORGANIZAÇÃO E ARQUITETURA

---

### 📍 `application/port/out/` — arquivos `*Repository` duplicados

⚠️ **Interfaces `*PersistencePort` foram criadas, mas as antigas `*Repository` não foram removidas**

Os 8 pares coexistem no mesmo pacote:
```
AddressChannelPersistencePort.java    + AddressChannelRepository.java
AgreementValidityPersistencePort.java + AgreementValidityRepository.java
ContactChannelPersistencePort.java    + ContactChannelRepository.java
MarketingDistribuitionChannelPersistencePort.java + MarketingDistribuitionChannelRepository.java
ProductChannelPersistencePort.java    + ProductChannelRepository.java
RechargeLimitPersistencePort.java     + RechargeLimitRepository.java
SalesChannelPersistencePort.java      + SalesChannelRepository.java
TypesActivityPersistencePort.java     + TypesActivityRepository.java
```

Os adapters já implementam `*PersistencePort`. Os arquivos `*Repository` são código morto que deve ser removido.

💡 Deletar os 8 arquivos `*Repository.java` de `application/port/out/` e remover qualquer import remanescente.

---

### 📍 Controllers com Request DTOs como inner records

⚠️ **5 controllers ainda definem Request DTOs como inner records**

O `SalesChannelController` já foi corrigido (DTOs em `adapter/port/in/rest/dto/`), mas os seguintes controllers ainda possuem inner records:

- `ProductChannelController` — `CreateProductChannelRequest`, `UpdateProductChannelRequest`
- `TypesActivityController` — `CreateTypesActivityRequest`, `UpdateTypesActivityRequest`
- `AddressChannelController` — inner records para create/update
- `ContactChannelController` — inner records para create/update
- `RechargeLimitController` — inner records para create/update

💡 Mover todos para `adapter/port/in/rest/dto/` como arquivos separados.

---

### 📍 Controllers em geral

⚠️ **Paginação em memória via `PageResponse.fromList` em todos os endpoints de listagem**

```java
List<SalesChannel> all = salesChannelUseCase.findAllSalesChannels(stCanais);
// ...paginação manual após carregar TUDO
return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
```

Todos os controllers fazem isso. O banco nunca recebe os parâmetros de paginação.

💡 Introduzir `Pageable` do Spring Data nos use cases e ports, propagando até os repositórios JPA.

---

## 3. BOAS PRÁTICAS SPRING BOOT

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

💡 Criar `TypesActivityResponseDTO` e converter no controller.

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

Nenhum DTO de request usa `@NotNull`, `@NotBlank`, `@Size`, etc. Nenhum controller usa `@Valid` nos parâmetros `@RequestBody`. Dados inválidos chegam ao use case e podem gerar erros obscuros ou NPEs.

---

## 4. PERFORMANCE E ESCALABILIDADE

| Problema | Localização | Impacto |
|---|---|---|
| Paginação na memória após `findAll()` | Todos os controllers | 🟠 Alto |
| `SalesChannelJpaRepository.findAllByStCanais` sem paginação | `SalesChannelAdapterJpa` | 🟠 Alto |

---

## 5. DOMÍNIO E SEGURANÇA DE TIPO

### 📍 Domínios usam `String` para status em vez do enum `ChannelDomainStatus`

⚠️ **O enum `ChannelDomainStatus` foi criado, mas os domínios continuam com `String`**

```java
// ProductChannel.java
private String codStatus;  // ← String, não ChannelDomainStatus

// SalesChannel.java
private String stCanais;   // ← String, não ChannelDomainStatus

// MarketingDistribuitionChannel.java
private String codStatus;  // ← String, não ChannelDomainStatus
```

A conversão é feita indiretamente via `ChannelDomainStatus.fromCode()` em métodos de negócio, mas o campo continua como String bruta.

💡 Substituir os campos `String codStatus` / `String stCanais` pelo tipo `ChannelDomainStatus` diretamente nos domínios.

---

### 📍 Ausência de conversor JPA para `ChannelDomainStatus`

⚠️ **Sem `AttributeConverter<ChannelDomainStatus, String>` para persistir o enum no banco**

Quando os domínios migrarem para usar o enum diretamente, será necessário um conversor JPA para manter compatibilidade com as colunas "A"/"I" no banco.

💡 Criar `ChannelStatusConverter` em `channel/adapter/port/out/jpa/converter/`.

---

## 6. TESTABILIDADE

⚠️ **Services com `LocalDateTime.now()` embutido no código** — dificultam testes determinísticos. Ideal seria injetar um `Clock` do Java Time.

⚠️ **`findProjections` no `ProductChannelService` lança `UnsupportedOperationException`** quando parâmetros não correspondem ao filtro por canal — impossível testar o caminho alternativo.

⚠️ **`findByCodCanalSuperior` no `SalesChannelAdapterJpa` lança `UnsupportedOperationException`** — método não implementado em código de produção.

---

## 7. PADRONIZAÇÃO

⚠️ **Typo no nome de classe propagado por todo o módulo**  
`MarketingDistribuitionChannel` → `MarketingDistributionChannel` ("Distribuition" não existe em inglês). Afeta: domínio, key, mapper, service, repository/port, adapter.

⚠️ **`CanalResponseDTO.codCanal` é `Long`, mas `SalesChannel.codCanal` é `String` — type mismatch**

⚠️ **Nomenclatura inconsistente de métodos `findBy*`**  
`findBySalesChannel(String codCanal)` e `findByTypesActivity(String codAtividade)` — o prefixo `findBy` implica filtro por propriedade (estilo Spring Data), mas o parâmetro é o ID. O correto seria `findSalesChannelById` ou simplesmente `findSalesChannel`.

⚠️ **`SalesChannelReponseDTO.java`** — typo no nome do arquivo ("Reponse" faltando "s").

⚠️ **`UnsupportedOperationException` em código de produção:**
```java
// ProductChannelService.java
throw new UnsupportedOperationException("Filtro de projections não implementado...");

// SalesChannelAdapterJpa.java
throw new UnsupportedOperationException("Unimplemented method 'findByCodCanalSuperior'");
```

---

## 🔥 Lista das Refatorações Pendentes

| Prioridade | Refatoração | Impacto |
|---|---|---|
| 🟠 1 | **Remover os 8 arquivos `*Repository.java` duplicados** de `application/port/out/` (os `*PersistencePort` já existem e são usados) | Limpeza / convenção |
| 🟠 2 | **Trocar injeção de `UserResolverHelperImpl` pela interface `UserResolverHelper` nos services** | Desacoplamento |
| 🟠 3 | **Substituir `String codStatus/stCanais` por `ChannelDomainStatus`** nos domínios + criar `ChannelStatusConverter` JPA | Segurança de tipo |
| 🟠 4 | **Implementar paginação no banco** (`Pageable`) em vez de carregar tudo e paginar na memória | Performance |
| 🟡 5 | **Mover Request DTOs dos 5 controllers restantes** para `adapter/port/in/rest/dto/` | Organização |
| 🟡 6 | **Adicionar Bean Validation** (`@NotNull`, `@NotBlank`, `@Size`, `@Valid`) nos Request DTOs e controllers | Segurança de entrada |
| 🟡 7 | **Criar `SalesChannel.ofReference(String codCanal)`** e eliminar o padrão de 28 argumentos nulos | Fragilidade |
| 🟡 8 | **Criar `SalesChannel.applyUpdate(...)`** para substituir o construtor all-args posicional no update | Manutenibilidade |
| 🟡 9 | **Corrigir `SalesChannelMapper`** para mapear `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro`, `idUsuarioManutencao` | Correção de dados |
| 🟡 10 | **Criar `TypesActivityResponseDTO`** e parar de expor domínio no `TypesActivityController` | Encapsulamento |
| 🟡 11 | **Corrigir typo `SalesChannelReponseDTO` → `SalesChannelResponseDTO`** | Padronização |
| 🟡 12 | **Corrigir type mismatch `CanalResponseDTO.codCanal`** — de `Long` para `String` | Consistência |
| 🟡 13 | **Corrigir typo `MarketingDistribuitionChannel` → `MarketingDistributionChannel`** em todo o módulo | Padronização |
| 🟡 14 | **Padronizar nomenclatura** — `findBySalesChannel()` → `findSalesChannel()` | Clareza |
| 🟡 15 | **Implementar ou remover métodos com `UnsupportedOperationException`** (`findByCodCanalSuperior`, `findProjections` parcial) | Código morto |
| 🟡 16 | **Injetar `Clock` nos services** em vez de `LocalDateTime.now()` direto | Testabilidade |

---

## ✅ Itens Concluídos (removidos desta análise)

| Refatoração | Status |
|---|---|
| Criar `UserResolverHelper` (interface + impl) em `shared/helper/` | ✅ Concluído |
| Corrigir bug `findByCodProduto` filtrando pelo campo errado (`getCodCanal` → `getCodProduto`) | ✅ Concluído |
| Eliminar full table scan em `ProductChannelAdapterJpa` — usar queries JPA derivadas | ✅ Concluído |
| Mover `ProductChannelProjection` para `application/port/out/query/` | ✅ Concluído |
| Criar interfaces `*PersistencePort` nos 8 arquivos de `application/port/out/` | ✅ Concluído |
| Remover `UserPersistencePort` dos controllers — delegam ao `UserResolverHelper` | ✅ Concluído |
| Criar enum `ChannelDomainStatus` com valores `ACTIVE("A")` e `INACTIVE("I")` | ✅ Concluído |
| Adicionar `HttpStatus` ao `ChannelErrorType` | ✅ Concluído |
| Remover constantes `STATUS_ACTIVE = "A"` dos services — usam `ChannelDomainStatus.ACTIVE.getCode()` | ✅ Concluído |
| Corrigir `ProductChannelMapper` — recebe `User userCad, User userMan` em vez de `UserPersistencePort` | ✅ Concluído |
| Unificar modelo de usuário — `AgreementValidity` e `RechargeLimit` agora usam `User` | ✅ Concluído |
| Corrigir `TypesActivityService.updateTypesActivity` — preserva `dtManutencao` do objeto existente | ✅ Concluído |
| Mover DTOs do `SalesChannelController` para `adapter/port/in/rest/dto/` | ✅ Concluído |
| Remover `@Setter` do nível de classe — agora é por campo individual | ✅ Concluído |

---

## 🧠 Recomendações para Elevar o Projeto ao Nível Sênior

**1. Domínio rico, não anêmico**  
O `SalesChannel` já possui métodos de negócio (`activate()`, `inactivate()`, `isAtivo()`, etc.), o que é um bom progresso. Próximo passo: migrar a criação/atualização para métodos de domínio em vez de construtores posicionais.

**2. Separar portas de leitura e escrita (CQRS light)**  
`ProductChannelPersistencePort` mistura queries simples, queries otimizadas e projeções complexas. Separar em `ProductChannelQueryPort` e `ProductChannelCommandPort` deixa cada contrato claro.

**3. Adicionar testes de integração por slice (`@DataJpaTest`, `@WebMvcTest`)**  
Nenhum teste de integração existe para os adapters JPA do módulo. Os testes de unidade para os services ficam facilmente implementáveis após a troca do `UserResolverHelperImpl` pela interface.

---

## 🗺️ Guia de Refatoração por Fases

> **Princípio de execução:** cada fase deve ser concluída, compilada (`mvn compile`) e commitada antes de iniciar a próxima. Nenhuma fase cria regressões para a anterior.

### 🟠 Fase 2 — Limpeza Arquitetural (Residual)

**Objetivo:** Remover código morto duplicado e alinhar injeção de dependências.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 2.1 | **Deletar os 8 arquivos `*Repository.java`** de `application/port/out/` | `*Repository.java` | Apenas `*PersistencePort.java` no pacote |
| 2.2 | **Remover qualquer import** remanescente para as interfaces deletadas | Services, adapters | `mvn compile` verde |
| 2.3 | **Trocar `UserResolverHelperImpl` → `UserResolverHelper`** (interface) em todos os services | `channel/application/service/*.java` | Sem referência a `UserResolverHelperImpl` nos services |

---

### 🟠 Fase 3 — Segurança de Tipo no Domínio

**Objetivo:** Substituir strings de status pelo enum `ChannelDomainStatus` no campo dos domínios.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 3.1 | **Substituir `String codStatus/stCanais` por `ChannelDomainStatus`** em todos os domínios | `channel/domain/*.java` | Sem `String codStatus` ou `String stCanais` nos domínios |
| 3.2 | **Criar `ChannelStatusConverter`** (implements `AttributeConverter<ChannelDomainStatus, String>`) | `channel/adapter/port/out/jpa/converter/` | Entidades mapeiam corretamente para "A"/"I" no banco |
| 3.3 | **Atualizar mappers e services** para usar o enum diretamente | `channel/adapter/port/out/jpa/mapper/*.java`, `channel/application/service/*.java` | `mvn compile` verde |

---

### 🟠 Fase 4 — Performance de Leitura (Paginação Real)

**Objetivo:** Enviar parâmetros de paginação ao banco em vez de paginar na memória.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 4.1 | **Adicionar `Pageable` aos use cases de listagem** | `channel/application/port/in/*UseCase.java` | Assinaturas aceitam `Pageable` |
| 4.2 | **Propagar `Pageable` aos ports de saída** | `channel/application/port/out/*PersistencePort.java` | Ports retornam `Page<T>` |
| 4.3 | **Implementar paginação nos adapters JPA** | `channel/adapter/port/out/jpa/adapter/*.java` | SQL contém `LIMIT` e `OFFSET` |
| 4.4 | **Atualizar controllers — remover `PageResponse.fromList`** | `channel/adapter/port/in/rest/*.java` | Resposta paginada direto do banco |

---

### 🟡 Fase 5 — Organização e Contrato da API

**Objetivo:** Separar Request DTOs remanescentes, validar entradas e padronizar respostas.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 5.1 | **Mover inner record DTOs** dos 5 controllers restantes para `adapter/port/in/rest/dto/` | `ProductChannelController`, `TypesActivityController`, `AddressChannelController`, `ContactChannelController`, `RechargeLimitController` | Sem inner records nos controllers |
| 5.2 | **Adicionar Bean Validation** (`@NotBlank`, `@NotNull`, `@Size`) em todos os Request DTOs e `@Valid` nos controllers | `dto/*.java`, controllers | Requests inválidos retornam `400 Bad Request` |
| 5.3 | **Criar `TypesActivityResponseDTO`** e converter `TypesActivityController` para retorná-lo | `dto/TypesActivityResponseDTO.java`, `TypesActivityController.java` | Controller não expõe objeto de domínio |
| 5.4 | **Corrigir typo `SalesChannelReponseDTO` → `SalesChannelResponseDTO`** | Renomear arquivo + atualizar referências | Sem "Reponse" no codebase |
| 5.5 | **Corrigir type mismatch `CanalResponseDTO.codCanal`** — de `Long` para `String` | `CanalResponseDTO.java` | Tipo coerente com `SalesChannel.codCanal` |

---

### 🟡 Fase 6 — Encapsulamento e Domínio Rico

**Objetivo:** Introduzir factory methods expressivos e eliminar antipadrões de construção.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 6.1 | **Adicionar `SalesChannel.ofReference(String codCanal)`** factory method | `SalesChannel.java` | `AddressChannelService` e `ContactChannelService` usam `ofReference` em vez do construtor de 28 args |
| 6.2 | **Adicionar `SalesChannel.applyUpdate(UpdateSalesChannelCommand, User)`** | `SalesChannel.java`, `SalesChannelService.java` | `updateSalesChannel` sem construtor all-args de 28 parâmetros |
| 6.3 | **Corrigir `SalesChannelMapper`** — mapear `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro`, `idUsuarioManutencao` em vez de ignorar | `SalesChannelMapper.java` | Query de leitura retorna valores não-nulos para esses campos |

---

### 🟡 Fase 7 — Testabilidade e Padronização Final

**Objetivo:** Cobrir os componentes com testes e corrigir issues de padronização remanescentes.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 7.1 | **Corrigir typo `MarketingDistribuitionChannel` → `MarketingDistributionChannel`** em todo o módulo | Renomear classes, arquivos e pacotes | Sem "Distribuition" no codebase |
| 7.2 | **Implementar ou remover métodos com `UnsupportedOperationException`** | `ProductChannelService.findProjections`, `SalesChannelAdapterJpa.findByCodCanalSuperior` | Sem `UnsupportedOperationException` em produção |
| 7.3 | **Adicionar `@DataJpaTest` para os adapters JPA críticos** | `test/channel/adapter/` | Queries testadas contra schema real |
| 7.4 | **Adicionar `@WebMvcTest` para os controllers principais** | `test/channel/adapter/` | Validação de request DTO coberta |
| 7.5 | **Injetar `Clock` nos services** em vez de usar `LocalDateTime.now()` diretamente | `channel/application/service/*.java` | Testes passam com `Clock.fixed(...)` |
| 7.6 | **Padronizar nomenclatura dos métodos de busca** — `findBySalesChannel(codCanal)` → `findSalesChannel(codCanal)` | Use cases, services, ports, controllers | Métodos `findBy*` usados apenas com semântica Spring Data |

---

### Visão Geral do Roadmap

```
                          Fase 2 (Limpeza: remover *Repository duplicados + interface nos services)
                                    │                    │
                          ┌─────────┴──────┐   ┌─────────┴─────────┐
                          ▼                ▼   ▼                   ▼
                        Fase 3           Fase 4                  Fase 5
                    (ChannelDomainStatus (Paginação real)     (DTOs + validação)
                     nos campos)
                          │                │                       │
                          └────────────────┴───────────────────────┘
                                                  │
                                                  ▼
                                    Fase 6 (Domínio rico + encapsulamento)
                                                  │
                                                  ▼
                                    Fase 7 (Testes + padronização final)
```