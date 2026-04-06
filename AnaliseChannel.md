
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

O `createSalesChannel` e `updateSalesChannel` constroem o objeto com 28 parâmetros posicionais, misturando campos imutáveis do `existing` com novos do `cmd`. Qualquer reordenação de campo é um bug silencioso.

💡 Extrair a montagem para um método `SalesChannel.applyUpdate(UpdateSalesChannelCommand cmd, User usuario)` ou utilizar o padrão record de atualização parcial via builder.

---

## 2. ORGANIZAÇÃO E ARQUITETURA

---

### 📍 Controllers com Request DTOs como inner records

⚠️ **4 controllers ainda definem Request DTOs como inner records**

Os controllers `SalesChannelController`, `ProductChannelController`, `AddressChannelController` e `ContactChannelController` já foram corrigidos (DTOs em `adapter/port/in/rest/dto/`), mas os seguintes controllers ainda possuem inner records:

- `TypesActivityController` — `CreateTypesActivityRequest`, `UpdateTypesActivityRequest`
- `RechargeLimitController` — inner records para create/update
- `AgreementValidityController` — inner records para create/update
- `MarketingDistribuitionChannelController` — inner records para create/update

💡 Mover todos para `adapter/port/in/rest/dto/` como arquivos separados.

---

### 📍 `ProductChannelController`

⚠️ **Paginação em memória via `PageResponse.fromList` no endpoint de projections**

```java
List<ProductChannelProjection> projections = productChannelUseCase.findProjections(codCanal, codProduto);
List<ProductChResponseDTO> dtos = productChannelMapper.toResponseDTOList(projections);
return ResponseEntity.ok(PageResponse.fromList(dtos, page, size));
```

Os demais controllers já utilizam `Pageable` + `PageResponse.fromPage()` corretamente, mas o `ProductChannelController` ainda carrega tudo na memória e pagina manualmente com `@RequestParam int page, int size`.

💡 Propagar `Pageable` ao use case e port de `findProjections`, fazendo o banco aplicar `LIMIT`/`OFFSET`.

---

## 3. BOAS PRÁTICAS SPRING BOOT

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

Nenhum DTO de request do módulo Channel usa `@NotNull`, `@NotBlank`, `@Size`, etc. Nenhum controller do módulo usa `@Valid` nos parâmetros `@RequestBody`. Dados inválidos chegam ao use case e podem gerar erros obscuros ou NPEs.

💡 Adicionar Bean Validation nos DTOs e `@Valid` nos controllers. (Nota: o módulo `creditrequest` já usa corretamente.)

---

## 4. PERFORMANCE E ESCALABILIDADE

| Problema | Localização | Impacto |
|---|---|---|
| Paginação na memória após `findAll()` no endpoint de projections | `ProductChannelController` | 🟠 Alto |

---

## 5. TESTABILIDADE

⚠️ **Services com `LocalDateTime.now()` embutido no código** — 14+ ocorrências distribuídas em `SalesChannelService`, `ProductChannelService`, `RechargeLimitService`, `AddressChannelService`, `ContactChannelService`, `AgreementValidityService`. Dificultam testes determinísticos. Ideal seria injetar um `Clock` do Java Time.

⚠️ **`findProjections` no `ProductChannelService` lança `UnsupportedOperationException`** quando parâmetros não correspondem ao filtro por canal — impossível testar o caminho alternativo.

⚠️ **`findByCodCanalSuperior` no `SalesChannelAdapterJpa` lança `UnsupportedOperationException`** — método não implementado em código de produção.

---

## 6. PADRONIZAÇÃO

⚠️ **Typo no nome de classe propagado por todo o módulo**  
`MarketingDistribuitionChannel` → `MarketingDistributionChannel` ("Distribuition" não existe em inglês). Afeta: domínio, key, mapper, service, persistence port, adapter, controller.

⚠️ **Nomenclatura inconsistente de métodos `findBy*`**  
`findBySalesChannel(String codCanal)` e `findByTypesActivity(String codAtividade)` — o prefixo `findBy` implica filtro por propriedade (estilo Spring Data), mas o parâmetro é o ID. O correto seria `findSalesChannelById` ou simplesmente `findSalesChannel`.

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
| 🟠 1 | **Implementar paginação no banco para `ProductChannelController.findProjections`** — último endpoint usando `PageResponse.fromList` | Performance |
| 🟡 2 | **Mover Request DTOs dos 4 controllers restantes** (`TypesActivityController`, `RechargeLimitController`, `AgreementValidityController`, `MarketingDistribuitionChannelController`) para `adapter/port/in/rest/dto/` | Organização |
| 🟡 3 | **Adicionar Bean Validation** (`@NotNull`, `@NotBlank`, `@Size`, `@Valid`) nos Request DTOs e controllers do módulo Channel | Segurança de entrada |
| 🟡 4 | **Criar `SalesChannel.ofReference(String codCanal)`** e eliminar o padrão de 28 argumentos nulos em `AddressChannelService` e `ContactChannelService` | Fragilidade |
| 🟡 5 | **Criar `SalesChannel.applyUpdate(...)`** para substituir o construtor all-args posicional de 28 parâmetros no update | Manutenibilidade |
| 🟡 6 | **Corrigir `SalesChannelMapper`** para mapear `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro`, `idUsuarioManutencao` em vez de ignorar | Correção de dados |
| 🟡 7 | **Corrigir typo `MarketingDistribuitionChannel` → `MarketingDistributionChannel`** em todo o módulo | Padronização |
| 🟡 8 | **Padronizar nomenclatura** — `findBySalesChannel()` → `findSalesChannel()` | Clareza |
| 🟡 9 | **Implementar ou remover métodos com `UnsupportedOperationException`** (`findByCodCanalSuperior`, `findProjections` parcial) | Código morto |
| 🟡 10 | **Injetar `Clock` nos services** em vez de `LocalDateTime.now()` direto (14+ ocorrências em 6 services) | Testabilidade |

---

## ✅ Itens Concluídos

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
| Remover os 8 arquivos `*Repository.java` duplicados de `application/port/out/` | ✅ Concluído |
| Trocar injeção de `UserResolverHelperImpl` pela interface `UserResolverHelper` nos services | ✅ Concluído |
| Substituir `String codStatus/stCanais` por `ChannelDomainStatus` nos domínios | ✅ Concluído |
| Criar `ChannelStatusConverter` (JPA `AttributeConverter<ChannelDomainStatus, String>`) | ✅ Concluído |
| Criar `TypesActivityResponseDTO` e converter no `TypesActivityController` | ✅ Concluído |
| Corrigir typo `SalesChannelReponseDTO` → `SalesChannelResponseDTO` | ✅ Concluído |
| Corrigir type mismatch `CanalResponseDTO.codCanal` (Long → String) | ✅ Concluído (DTO removido) |
| Implementar paginação no banco (`Pageable`) na maioria dos controllers | ✅ Concluído (exceto `ProductChannelController.findProjections`) |
| Mover DTOs de `ProductChannelController`, `AddressChannelController`, `ContactChannelController` para `adapter/port/in/rest/dto/` | ✅ Concluído |

---

## 🧠 Recomendações para Elevar o Projeto ao Nível Sênior

**1. Domínio rico, não anêmico**  
O `SalesChannel` já possui métodos de negócio (`activate()`, `inactivate()`, `isAtivo()`, etc.) e os domínios já usam `ChannelDomainStatus` como tipo enum. Próximo passo: migrar a criação/atualização para métodos de domínio (`ofReference`, `applyUpdate`) em vez de construtores posicionais de 28 argumentos.

**2. Separar portas de leitura e escrita (CQRS light)**  
`ProductChannelPersistencePort` mistura queries simples, queries otimizadas e projeções complexas. Separar em `ProductChannelQueryPort` e `ProductChannelCommandPort` deixa cada contrato claro.

**3. Adicionar testes de integração por slice (`@DataJpaTest`, `@WebMvcTest`)**  
Nenhum teste de integração existe para os adapters JPA do módulo. Os testes de unidade para os services ficam facilmente implementáveis agora que os services injetam a interface `UserResolverHelper`.

---

## 🗺️ Guia de Refatoração por Fases

> **Princípio de execução:** cada fase deve ser concluída, compilada (`mvn compile`) e commitada antes de iniciar a próxima. Nenhuma fase cria regressões para a anterior.

### 🟠 Fase 2 — Performance Residual

**Objetivo:** Resolver o último endpoint sem paginação real no banco.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 2.1 | **Adicionar `Pageable` ao método `findProjections`** no use case e port | `ProductChannelUseCase.java`, `ProductChannelPersistencePort.java` | Assinatura aceita `Pageable`, retorna `Page<T>` |
| 2.2 | **Implementar paginação no adapter JPA** para projections | `ProductChannelAdapterJpa.java` | SQL contém `LIMIT` e `OFFSET` |
| 2.3 | **Atualizar `ProductChannelController` — remover `PageResponse.fromList`** | `ProductChannelController.java` | Usa `Pageable` e `PageResponse.fromPage()` |

---

### 🟡 Fase 3 — Organização e Contrato da API

**Objetivo:** Separar Request DTOs remanescentes, validar entradas e padronizar respostas.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 3.1 | **Mover inner record DTOs** dos 4 controllers restantes para `adapter/port/in/rest/dto/` | `TypesActivityController`, `RechargeLimitController`, `AgreementValidityController`, `MarketingDistribuitionChannelController` | Sem inner records nos controllers |
| 3.2 | **Adicionar Bean Validation** (`@NotBlank`, `@NotNull`, `@Size`) em todos os Request DTOs e `@Valid` nos controllers | `dto/*.java`, controllers | Requests inválidos retornam `400 Bad Request` |

---

### 🟡 Fase 4 — Encapsulamento e Domínio Rico

**Objetivo:** Introduzir factory methods expressivos e eliminar antipadrões de construção.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 4.1 | **Adicionar `SalesChannel.ofReference(String codCanal)`** factory method | `SalesChannel.java` | `AddressChannelService` e `ContactChannelService` usam `ofReference` em vez do construtor de 28 args |
| 4.2 | **Adicionar `SalesChannel.applyUpdate(UpdateSalesChannelCommand, User)`** | `SalesChannel.java`, `SalesChannelService.java` | `updateSalesChannel` sem construtor all-args de 28 parâmetros |
| 4.3 | **Corrigir `SalesChannelMapper`** — mapear `codAtividade`, `codClassificacaoPessoa`, `idUsuarioCadastro`, `idUsuarioManutencao` em vez de ignorar | `SalesChannelMapper.java` | Query de leitura retorna valores não-nulos para esses campos |

---

### 🟡 Fase 5 — Testabilidade e Padronização Final

**Objetivo:** Cobrir os componentes com testes e corrigir issues de padronização remanescentes.

| # | Tarefa | Arquivo(s) | Critério de aceite |
|---|---|---|---|
| 5.1 | **Corrigir typo `MarketingDistribuitionChannel` → `MarketingDistributionChannel`** em todo o módulo | Renomear classes, arquivos e pacotes | Sem "Distribuition" no codebase |
| 5.2 | **Implementar ou remover métodos com `UnsupportedOperationException`** | `ProductChannelService.findProjections`, `SalesChannelAdapterJpa.findByCodCanalSuperior` | Sem `UnsupportedOperationException` em produção |
| 5.3 | **Adicionar `@DataJpaTest` para os adapters JPA críticos** | `test/channel/adapter/` | Queries testadas contra schema real |
| 5.4 | **Adicionar `@WebMvcTest` para os controllers principais** | `test/channel/adapter/` | Validação de request DTO coberta |
| 5.5 | **Injetar `Clock` nos services** em vez de usar `LocalDateTime.now()` diretamente | `channel/application/service/*.java` | Testes passam com `Clock.fixed(...)` |
| 5.6 | **Padronizar nomenclatura dos métodos de busca** — `findBySalesChannel(codCanal)` → `findSalesChannel(codCanal)` | Use cases, services, ports, controllers | Métodos `findBy*` usados apenas com semântica Spring Data |

---

### Visão Geral do Roadmap

```
              Fase 2 (Paginação real no ProductChannelController)
                                    │
                          ┌─────────┴─────────┐
                          ▼                   ▼
                        Fase 3              Fase 4
                    (DTOs + validação)   (Domínio rico + encapsulamento)
                          │                   │
                          └─────────┬─────────┘
                                    ▼
                    Fase 5 (Testes + padronização final)
```