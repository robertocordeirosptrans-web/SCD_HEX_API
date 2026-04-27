# SCD_HEX_API

**Monolito Modular com Arquitetura Hexagonal** — API REST para o Sistema de Central de Distribuição (SCD) da SPTrans.

---

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Configuração](#configuração)
- [Módulos e Funcionalidades](#módulos-e-funcionalidades)
- [Endpoints da API](#endpoints-da-api)
- [Documentação Swagger](#documentação-swagger)

---

## Visão Geral

O **SCD_HEX_API** é uma API RESTful desenvolvida em Spring Boot que gerencia o sistema de cartões e distribuição da SPTrans. O projeto segue a Arquitetura Hexagonal (Ports & Adapters) organizada em módulos independentes: autenticação, canais de venda, produtos, pedidos de crédito e cartões inicializados.

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.11 |
| Spring Security | 6.x |
| Spring Data JPA | (starter) |
| Oracle Database | (ojdbc11) |
| JWT (java-jwt + jjwt) | 4.4.0 / 0.12.5 |
| MapStruct | 1.5.5.Final |
| Lombok | (latest) |
| Springdoc OpenAPI (Swagger) | 2.8.4 |
| Bucket4j (Rate Limiting) | 8.10.1 |
| Jasypt (Criptografia) | 1.9.3 |
| Thymeleaf | (starter) |
| Maven | Wrapper incluído |

## Arquitetura

O projeto utiliza **Arquitetura Hexagonal** com a seguinte estrutura por módulo:

```
módulo/
├── adapter/          # Adaptadores (entrada REST, saída para banco)
│   ├── in/rest/      # Controllers REST (Driving Adapters)
│   └── port/out/     # Implementações de repositório (Driven Adapters)
├── application/      # Casos de uso e portas
│   ├── port/in/      # Portas de entrada (Use Cases)
│   ├── port/out/     # Portas de saída (Repository interfaces)
│   └── service/      # Implementações dos casos de uso
└── domain/           # Entidades de domínio e enums
```

### Módulos

```
br.sptrans.scd
├── auth/                 # Autenticação e autorização
├── channel/              # Canais de venda e distribuição
├── creditrequest/        # Pedidos de crédito
├── initializedcards/     # Cartões inicializados
├── product/              # Produtos e catálogo
└── shared/               # Infraestrutura compartilhada
    ├── config/           # Configurações (CORS, Rate Limit, Security, Swagger, Cache)
    ├── exception/        # Tratamento global de exceções
    ├── security/         # Criptografia e configuração Oracle
    └── version/          # Versionamento centralizado da API
```

## Pré-requisitos

- **Java 17** (JDK)
- **Maven 3.8+** (ou use o wrapper `mvnw` incluído)
- **Oracle Database** acessível (configurado no `application.properties`)
- **Servidor SMTP** para funcionalidade de e-mail (recuperação de senha)

## Instalação e Execução

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd SCD_HEX_API
```

### 2. Configurar o banco de dados

Edite o arquivo `src/main/resources/application.properties` com as credenciais do seu ambiente:

```properties
spring.datasource.url=jdbc:oracle:thin:@<host>:<porta>/<serviço>
spring.datasource.username=<usuario>
spring.datasource.password=<senha>
```

### 3. Configurar SMTP (e-mail)

```properties
spring.mail.host=smtp.seudominio.com
spring.mail.port=587
spring.mail.username=usuario@seudominio.com
spring.mail.password=senha
```

### 4. Configurar segurança JWT

```properties
api.security.token.secret=<seu-segredo-seguro>
api.security.token.expiration-hours=2
```

### 5. Compilar o projeto

```bash
# Linux/Mac
./mvnw clean install

# Windows
mvnw.cmd clean install
```

### 6. Executar a aplicação

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080` (ou na porta configurada em `server.port`).

## Configuração

| Propriedade | Descrição |
|---|---|
| `spring.profiles.active` | Perfil ativo (`local` por padrão) |
| `api.security.token.secret` | Segredo para geração de tokens JWT |
| `api.security.token.expiration-hours` | Tempo de expiração do token (horas) |
| `cors.allowed-origins` | Origens permitidas para CORS |
| `rate-limiting.enabled` | Habilita/desabilita rate limiting |

## Módulos e Funcionalidades

### Autenticação (`auth`)

Gerenciamento completo de autenticação e autorização baseado em JWT.

- **Login e geração de token JWT** com payload contendo perfis e permissões
- **Dados do usuário autenticado** (`/me`) com roles e permissions
- **Alteração e recuperação de senha** via e-mail SMTP
- **Gestão de usuários** — cadastro, atualização e desativação
- **Gestão de perfis** — CRUD completo de perfis de acesso
- **Gestão de grupos** — criação e associação de grupos a usuários
- **Associação usuário-perfil** — vínculo e alteração de status
- **Associação grupo-usuário** — vínculo e alteração de status
- **Associação perfil-funcionalidade** — vínculo de permissões a perfis
- **Controle de acesso** com `@PreAuthorize` baseado em roles

### Canais de Venda (`channel`)

Gestão dos canais de venda e distribuição de cartões.

- **Canais de venda** — CRUD completo com ativação/inativação e filtros por status
- **Endereços de canal** — cadastro, atualização, consulta e remoção de endereços
- **Contatos de canal** — gestão de informações de contato dos canais
- **Produtos por canal** — associação de produtos a canais de venda
- **Vigência de convênio** — gerenciamento de vigências de convênios canal/produto
- **Limites de recarga** — configuração de limites de recarga por canal/produto
- **Canais de comercialização/distribuição** — associação entre canais
- **Tipos de atividade** — CRUD com ativação/inativação

### Produtos (`product`)

Catálogo completo de produtos do sistema de cartões.

- **Produtos** — cadastro, atualização, consulta, ativação/inativação e versionamento
- **Versões de produto** — criação e consulta de versões
- **Famílias** — CRUD com ativação/inativação
- **Modalidades** — CRUD com ativação/inativação
- **Espécies** — CRUD com ativação/inativação
- **Tecnologias** — CRUD com ativação/inativação
- **Tipos de produto** — CRUD com ativação/inativação


### Pedidos de Crédito (`creditrequest`)

Gestão completa de pedidos de crédito.

- **Pedidos** — API para cadastro, atualização, busca paginada, pagamento, bloqueio/desbloqueio e cancelamento de pedidos de crédito
- **Histórico de pedidos** — consulta detalhada do histórico de status dos itens do pedido
- Entidades: `CreditRequest`, `CreditRequestItems`, `HistCreditRequest`, `PaymentMethod`, `Situation`, `RechargeLog`, `DocumentsType`

### Cartões Inicializados (`initializedcards`)

Gestão de solicitações de cartões inicializados.

- **Solicitações** — cadastro, consulta e atualização de solicitações de cartões inicializados
- **Histórico de solicitações** — consulta do histórico de movimentações das solicitações
- Entidades: `RequestInitializedCards`, `HistRequestInitializedCards`, `TbLotSCD`, `RequestLotSCP`

### Infraestrutura Compartilhada (`shared`)

- **CORS** — configuração de origens, métodos e headers permitidos
- **Rate Limiting** — controle de taxa de requisições com Bucket4j
- **Segurança** — configuração do Spring Security com filtro JWT
- **Swagger/OpenAPI** — documentação interativa da API
- **Cache** — configuração de cache
- **Exceções globais** — tratamento centralizado com `@RestControllerAdvice`
- **Versionamento** — todas as rotas sob `/api/v1`

## Endpoints da API

Base path: `/api/v1`

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/auth/login` | Login e obtenção de token JWT |
| GET | `/api/v1/auth/me` | Dados do usuário autenticado |
| POST | `/api/v1/auth/change-password` | Alteração de senha |
| POST | `/api/v1/auth/recovery-password` | Recuperação de senha via e-mail |
| POST | `/users` | Cadastrar usuário |
| PUT | `/users/{idUsuario}` | Atualizar usuário |
| PATCH | `/users/{idUsuario}/deactivate` | Desativar usuário |
| POST | `/api/v1/perfis` | Criar perfil |
| GET | `/api/v1/perfis` | Listar perfis |
| GET | `/api/v1/perfis/{codPerfil}` | Buscar perfil por código |
| PUT | `/api/v1/perfis/{codPerfil}` | Atualizar perfil |
| POST | `/api/v1/grupos` | Criar grupo |
| GET | `/api/v1/grupos` | Listar grupos |
| GET | `/api/v1/grupos/{codGrupo}` | Buscar grupo por código |
| PUT | `/api/v1/grupos/{codGrupo}` | Atualizar grupo |
| POST | `/api/v1/grupo-usuario` | Vincular usuário a grupo |
| PUT | `/api/v1/grupo-usuario` | Atualizar vínculo |
| PATCH | `/api/v1/grupo-usuario/status` | Alterar status do vínculo |
| POST | `/api/v1/usuario-perfil` | Vincular perfil a usuário |
| PUT | `/api/v1/usuario-perfil` | Atualizar vínculo |
| PATCH | `/api/v1/usuario-perfil/status` | Alterar status do vínculo |
| POST | `/api/v1/perfil-funcionalidade` | Vincular funcionalidade a perfil |
| PUT | `/api/v1/perfil-funcionalidade` | Atualizar vínculo |
| PATCH | `/api/v1/perfil-funcionalidade/status` | Alterar status do vínculo |

### Canais de Venda

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/sales-channels` | Criar canal de venda |
| PUT | `/api/v1/sales-channels/{codCanal}` | Atualizar canal |
| GET | `/api/v1/sales-channels/{codCanal}` | Buscar canal por código |
| GET | `/api/v1/sales-channels` | Listar canais |
| PATCH | `/api/v1/sales-channels/{codCanal}/activate` | Ativar canal |
| PATCH | `/api/v1/sales-channels/{codCanal}/inactivate` | Inativar canal |
| DELETE | `/api/v1/sales-channels/{codCanal}` | Remover canal |
| POST | `/api/v1/address-channels` | Criar endereço de canal |
| PUT | `/api/v1/address-channels/{codEndereco}` | Atualizar endereço |
| GET | `/api/v1/address-channels/{codEndereco}` | Buscar endereço |
| GET | `/api/v1/address-channels` | Listar endereços |
| DELETE | `/api/v1/address-channels/{codEndereco}` | Remover endereço |
| POST | `/api/v1/contact-channels` | Criar contato de canal |
| PUT | `/api/v1/contact-channels/{codContato}` | Atualizar contato |
| GET | `/api/v1/contact-channels/{codContato}` | Buscar contato |
| GET | `/api/v1/contact-channels` | Listar contatos |
| DELETE | `/api/v1/contact-channels/{codContato}` | Remover contato |
| POST | `/api/v1/product-channels` | Associar produto a canal |
| PUT | `/api/v1/product-channels/{codCanal}/{codProduto}` | Atualizar associação |
| GET | `/api/v1/product-channels/{codCanal}/{codProduto}` | Buscar associação |
| GET | `/api/v1/product-channels` | Listar associações |
| DELETE | `/api/v1/product-channels/{codCanal}/{codProduto}` | Remover associação |
| POST | `/api/v1/agreement-validities` | Criar vigência de convênio |
| PUT | `/api/v1/agreement-validities/{codCanal}/{codProduto}` | Atualizar vigência |
| GET | `/api/v1/agreement-validities/{codCanal}/{codProduto}` | Buscar vigência |
| GET | `/api/v1/agreement-validities` | Listar vigências |
| DELETE | `/api/v1/agreement-validities/{codCanal}/{codProduto}` | Remover vigência |
| POST | `/api/v1/recharge-limits` | Criar limite de recarga |
| PUT | `/api/v1/recharge-limits/{codCanal}/{codProduto}` | Atualizar limite |
| GET | `/api/v1/recharge-limits/{codCanal}/{codProduto}` | Buscar limite |
| GET | `/api/v1/recharge-limits` | Listar limites |
| DELETE | `/api/v1/recharge-limits/{codCanal}/{codProduto}` | Remover limite |
| POST | `/api/v1/marketing-distribuition-channels` | Criar associação de canais |
| PUT | `/api/v1/marketing-distribuition-channels/{codComercializacao}/{codDistribuicao}` | Atualizar |
| GET | `/api/v1/marketing-distribuition-channels/{codComercializacao}/{codDistribuicao}` | Buscar |
| GET | `/api/v1/marketing-distribuition-channels` | Listar |
| DELETE | `/api/v1/marketing-distribuition-channels/{codComercializacao}/{codDistribuicao}` | Remover |
| POST | `/api/v1/types-activities` | Criar tipo de atividade |
| PUT | `/api/v1/types-activities/{codAtividade}` | Atualizar |
| GET | `/api/v1/types-activities/{codAtividade}` | Buscar |
| GET | `/api/v1/types-activities` | Listar |
| PATCH | `/api/v1/types-activities/{codAtividade}/activate` | Ativar |
| PATCH | `/api/v1/types-activities/{codAtividade}/inactivate` | Inativar |
| DELETE | `/api/v1/types-activities/{codAtividade}` | Remover |

### Produtos

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/product` | Cadastrar produto |
| GET | `/api/v1/product` | Listar produtos (filtro por status) |
| GET | `/api/v1/product/{codProduto}` | Buscar produto |
| PUT | `/api/v1/product/{codProduto}` | Atualizar produto |
| PATCH | `/api/v1/product/{codProduto}/activate` | Ativar produto |
| PATCH | `/api/v1/product/{codProduto}/inactivate` | Inativar produto |
| POST | `/api/v1/product/{codProduto}/versions` | Criar versão do produto |
| GET | `/api/v1/product/versions/{codVersao}` | Buscar versão |
| POST | `/api/v1/families` | Criar família |
| PUT | `/api/v1/families/{codFamilia}` | Atualizar família |
| GET | `/api/v1/families/{codFamilia}` | Buscar família |
| GET | `/api/v1/families` | Listar famílias |
| PATCH | `/api/v1/families/{codFamilia}/activate` | Ativar |
| PATCH | `/api/v1/families/{codFamilia}/inactivate` | Inativar |
| DELETE | `/api/v1/families/{codFamilia}` | Remover |
| POST | `/api/v1/modalities` | Criar modalidade |
| PUT | `/api/v1/modalities/{codModalidade}` | Atualizar |
| GET | `/api/v1/modalities/{codModalidade}` | Buscar |
| GET | `/api/v1/modalities` | Listar |
| PATCH | `/api/v1/modalities/{codModalidade}/activate` | Ativar |
| PATCH | `/api/v1/modalities/{codModalidade}/inactivate` | Inativar |
| DELETE | `/api/v1/modalities/{codModalidade}` | Remover |
| POST | `/api/v1/species` | Criar espécie |
| PUT | `/api/v1/species/{codEspecie}` | Atualizar |
| GET | `/api/v1/species/{codEspecie}` | Buscar |
| GET | `/api/v1/species` | Listar |
| PATCH | `/api/v1/species/{codEspecie}/activate` | Ativar |
| PATCH | `/api/v1/species/{codEspecie}/inactivate` | Inativar |
| DELETE | `/api/v1/species/{codEspecie}` | Remover |
| POST | `/api/v1/technologies` | Criar tecnologia |
| PUT | `/api/v1/technologies/{codTecnologia}` | Atualizar |
| GET | `/api/v1/technologies/{codTecnologia}` | Buscar |
| GET | `/api/v1/technologies` | Listar |
| PATCH | `/api/v1/technologies/{codTecnologia}/activate` | Ativar |
| PATCH | `/api/v1/technologies/{codTecnologia}/inactivate` | Inativar |
| DELETE | `/api/v1/technologies/{codTecnologia}` | Remover |
| POST | `/api/v1/products-types` | Criar tipo de produto |
| PUT | `/api/v1/products-types/{codTipoProduto}` | Atualizar |
| GET | `/api/v1/products-types/{codTipoProduto}` | Buscar |
| GET | `/api/v1/products-types` | Listar |
| PATCH | `/api/v1/products-types/{codTipoProduto}/activate` | Ativar |
| PATCH | `/api/v1/products-types/{codTipoProduto}/inactivate` | Inativar |
| DELETE | `/api/v1/products-types/{codTipoProduto}` | Remover |


### Pedidos de Crédito

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/pedidos` | Criar pedido de crédito |
| PUT | `/api/v1/pedidos/{id}` | Atualizar pedido de crédito |
| POST | `/api/v1/pedidos/buscar` | Buscar pedidos (paginação/cursor) |
| POST | `/api/v1/pedidos/pagar` | Realizar pagamento de pedido |
| POST | `/api/v1/pedidos/bloquear` | Bloquear pedido |
| POST | `/api/v1/pedidos/desbloquear` | Desbloquear pedido |
| POST | `/api/v1/pedidos/cancelar` | Cancelar pedido |

### Histórico de Pedidos de Crédito

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/hist/pedidos` | Consultar histórico de status dos itens do pedido |
### Cartões Inicializados

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/initialized-cards` | Criar solicitação de cartões inicializados |
| GET | `/api/v1/initialized-cards/{id}` | Consultar solicitação de cartões inicializados |
| GET | `/api/v1/initialized-cards` | Listar solicitações de cartões inicializados |
| GET | `/api/v1/hist/initialized-cards` | Consultar histórico de solicitações |

## Documentação Swagger

Com a aplicação em execução, acesse a documentação interativa:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`