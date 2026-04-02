# 🛠️ Guia Prático de Implementação - Refatoração de Portos

**Documento com exemplos de código real para cada passo**

---

## 📋 PASSO 1: Criar Novas Interfaces

### 1.1 UserPersistencePort (Agregador)

Substitui `UserRepository` com conteúdo completo.

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/UserPersistencePort.java`

```java
package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — Agregador de persistência de User.
 * 
 * Combina todas as operações de User em um único contrato segregado por ISP:
 * - Leitura de dados (queries)
 * - Escrita de dados (commands)
 * - Autenticação (validação de credenciais)
 * - Autorização (verificação de permissões)
 * - Status (gerenciamento de estado)
 */
public interface UserPersistencePort extends
        UserQueryPort,
        UserCommandPort,
        AuthenticationPort,
        UserStatusPort,
        AuthorizationPort {
    
    // Nenhum método adicional
    // Todos os métodos estão nas interfaces segregadas acima
}
```

---

### 1.2 UserQueryPort (Nova - Antes era UserReader)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/UserQueryPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.application.dto.UserOutputDto;
import br.sptrans.scd.auth.domain.User;
import java.util.Optional;

/**
 * Porta de Saída — Queries de User (responsabilidade: LEITURA).
 * Contém todas as operações de consulta sem efeitos colaterais.
 */
public interface UserQueryPort {
    
    Optional<User> findById(Long userId);
    
    Optional<User> findByLogin(String login);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCpf(String cpf);
    
    UserOutputDto getUserProfile(Long userId);
}
```

---

### 1.3 UserCommandPort (Nova - Antes era UserWriter)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/UserCommandPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.User;

/**
 * Porta de Saída — Commands de User (responsabilidade: ESCRITA).
 * Contém todas as operações que modificam estado.
 */
public interface UserCommandPort {
    
    void save(User user);
    
    void delete(Long userId);
    
    void update(User user);
    
    void updatePassword(Long userId, String hashedPassword);
}
```

---

### 1.4 AuthenticationPort (Padrão: renomear AuthenticationRepository)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/AuthenticationPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.User;
import java.util.Optional;

/**
 * Porta de Saída — Autenticação (responsabilidade: VALIDAR CREDENCIAIS).
 */
public interface AuthenticationPort {
    
    /**
     * Busca User por login e retorna para validação de senha.
     */
    Optional<User> findByLoginForAuthentication(String login);
    
    /**
     * Registra tentativa de login (para auditoria).
     */
    void recordLoginAttempt(String login, boolean success);
}
```

---

### 1.5 AuthorizationPort (Padrão: renomear AuthorizationRepository)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/AuthorizationPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import java.util.Set;

/**
 * Porta de Saída — Autorização (responsabilidade: VERIFICAR PERMISSÕES).
 */
public interface AuthorizationPort {
    
    /**
     * Retorna todas as funcionalidades do usuário
     */
    Set<String> getUserFunctionalities(Long userId);
    
    /**
     * Verifica se usuário tem permissão específica
     */
    boolean hasPermission(Long userId, String functionalityKey);
    
    /**
     * Retorna perfis do usuário
     */
    Set<String> getUserProfiles(Long userId);
}
```

---

### 1.6 UserStatusPort (Padrão: renomear UserStatusRepository)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/UserStatusPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — Status de User (responsabilidade: GERENCIAR ESTADO).
 */
public interface UserStatusPort {
    
    void enableUser(Long userId);
    
    void disableUser(Long userId);
    
    void lockUser(Long userId);
    
    void unlockUser(Long userId);
    
    boolean isUserActive(Long userId);
    
    boolean isUserLocked(Long userId);
}
```

---

### 1.7 EmailSendingPort (Substitui GatewayEmail)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/EmailSendingPort.java`

```java
package br.sptrans.scd.auth.application.port.out;

/**
 * Porta de Saída — Serviço de Envio de E-mail (responsabilidade: COMUNICAÇÃO EXTERNA).
 * 
 * Implementação pode ser SMTP, SendGrid, AWS SES ou qualquer provedor.
 */
public interface EmailSendingPort {
    
    /**
     * Envia e-mail de redefinição de senha.
     */
    void sendPasswordResetEmail(String destinatario, String nomeUsuario, String token);
    
    /**
     * Envia confirmação de usuário criado.
     */
    void sendUserCreatedEmail(String destinatario, String nomeUsuario);
    
    /**
     * Envia notificação de acesso incomum.
     */
    void sendUnusualAccessEmail(String destinatario, String nomeUsuario, String details);
}
```

---

### 1.8 ProfilePersistencePort (Conciso - sem segregação necessária)

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/ProfilePersistencePort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.Profile;
import java.util.List;
import java.util.Optional;

/**
 * Porta de Saída — Persistência de Profile.
 */
public interface ProfilePersistencePort {
    
    Optional<Profile> findById(String profileId);
    
    Optional<Profile> findByCode(String code);
    
    List<Profile> findAll();
    
    void save(Profile profile);
    
    void delete(String profileId);
    
    boolean exists(String profileId);
}
```

---

### 1.9 PasswordTokenPersistencePort

**Arquivo**: `src/main/java/br/sptrans/scd/auth/application/port/out/PasswordTokenPersistencePort.java`

```java
package br.sptrans.scd.auth.application.port.out;

import br.sptrans.scd.auth.domain.PasswordResetToken;
import java.util.Optional;

/**
 * Porta de Saída — Persistência de Password Reset Token.
 */
public interface PasswordTokenPersistencePort {
    
    void save(PasswordResetToken token);
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByUserId(Long userId);
    
    void delete(Long tokenId);
    
    void deleteExpiredTokens();
}
```

---

### 1.10 Outros Portos (Padrão Similar)

Seguindo o mesmo padrão para:
- `GroupPersistencePort`
- `GroupUserPersistencePort`
- `GroupProfilePersistencePort`
- `FunctionalityPersistencePort`
- `ProfileFunctionalityPersistencePort`
- `UserSessionPersistencePort`
- `AuditLogPort`

---

## 🔧 PASSO 2: Atualizar Implementações (Adapters)

### 2.1 UserPersistenceAdapter

**Arquivo**: `src/main/java/br/sptrans/scd/auth/adapter/port/out/persistence/UserPersistenceAdapter.java`

```java
package br.sptrans.scd.auth.adapter.port.out.persistence;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.adapter.port.out.mapper.UserMapper;
import br.sptrans.scd.auth.application.dto.UserOutputDto;
import br.sptrans.scd.auth.application.port.out.*;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Adaptador de Persistência — Implementa todas as portas de User.
 * 
 * ✅ Antes: UserRepositoryAdapter impl UserRepository
 * ✅ Depois: UserPersistenceAdapter impl UserPersistencePort
 *           (que estende UserQueryPort, UserCommandPort, etc)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {
    
    private final UserRepositoryJpa userRepositoryJpa;
    private final UserMapper mapper;
    
    // ============================================================
    // UserQueryPort: Implementações de Leitura
    // ============================================================
    
    @Override
    public Optional<User> findById(Long userId) {
        log.debug("Buscando usuário por ID: {}", userId);
        return userRepositoryJpa.findById(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByLogin(String login) {
        log.debug("Buscando usuário por login: {}", login);
        return userRepositoryJpa.findByCodLogin(login)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);
        return userRepositoryJpa.findByNomEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByCpf(String cpf) {
        log.debug("Buscando usuário por CPF: {}", cpf);
        return userRepositoryJpa.findByCodCpf(cpf)
                .map(mapper::toDomain);
    }
    
    @Override
    public UserOutputDto getUserProfile(Long userId) {
        log.debug("Buscando perfil completo do usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapper.toOutputDto(entity);
    }
    
    // ============================================================
    // UserCommandPort: Implementações de Escrita
    // ============================================================
    
    @Override
    public void save(User user) {
        log.info("Salvando novo usuário: {}", user.getLogin());
        UserEntityJpa entity = mapper.toJpaEntity(user);
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public void delete(Long userId) {
        log.warn("Deletando usuário: {}", userId);
        userRepositoryJpa.deleteById(userId);
    }
    
    @Override
    public void update(User user) {
        log.info("Atualizando usuário: {}", user.getId());
        UserEntityJpa entity = mapper.toJpaEntity(user);
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public void updatePassword(Long userId, String hashedPassword) {
        log.info("Atualizando senha do usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setCodSenha(hashedPassword);
        userRepositoryJpa.save(entity);
    }
    
    // ============================================================
    // AuthenticationPort: Implementações de Autenticação
    // ============================================================
    
    @Override
    public Optional<User> findByLoginForAuthentication(String login) {
        log.debug("Autenticação: buscando usuário por login: {}", login);
        return userRepositoryJpa.findByCodLogin(login)
                .map(mapper::toDomain);
    }
    
    @Override
    public void recordLoginAttempt(String login, boolean success) {
        log.info("Tentativa de login ({}): {}", login, success ? "SUCESSO" : "FALHA");
        // Futura: integrar com AuditLogPort
    }
    
    // ============================================================
    // AuthorizationPort: Implementações de Autorização
    // ============================================================
    
    @Override
    public Set<String> getUserFunctionalities(Long userId) {
        log.debug("Recuperando funcionalidades do usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<String> functionalities = new HashSet<>();
        entity.getUserFunctionalities().forEach(uf -> 
            functionalities.add(uf.getFunctionality().getChaveFuncionalidade())
        );
        return functionalities;
    }
    
    @Override
    public boolean hasPermission(Long userId, String functionalityKey) {
        log.debug("Verificando permissão do usuário {} para {}", userId, functionalityKey);
        return userRepositoryJpa.findById(userId)
                .map(user -> user.getUserFunctionalities().stream()
                        .anyMatch(uf -> uf.getFunctionality().getChaveFuncionalidade().equals(functionalityKey)))
                .orElse(false);
    }
    
    @Override
    public Set<String> getUserProfiles(Long userId) {
        log.debug("Recuperando perfis do usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<String> profiles = new HashSet<>();
        entity.getUserProfiles().forEach(up -> 
            profiles.add(up.getProfile().getCodPerfil())
        );
        return profiles;
    }
    
    // ============================================================
    // UserStatusPort: Implementações de Status
    // ============================================================
    
    @Override
    public void enableUser(Long userId) {
        log.info("Ativando usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setIndAtivo("S");
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public void disableUser(Long userId) {
        log.warn("Desativando usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setIndAtivo("N");
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public void lockUser(Long userId) {
        log.warn("Bloqueando usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setIndBloqueado("S");
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public void unlockUser(Long userId) {
        log.info("Desbloqueando usuário: {}", userId);
        UserEntityJpa entity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setIndBloqueado("N");
        userRepositoryJpa.save(entity);
    }
    
    @Override
    public boolean isUserActive(Long userId) {
        return userRepositoryJpa.findById(userId)
                .map(entity -> "S".equals(entity.getIndAtivo()))
                .orElse(false);
    }
    
    @Override
    public boolean isUserLocked(Long userId) {
        return userRepositoryJpa.findById(userId)
                .map(entity -> "S".equals(entity.getIndBloqueado()))
                .orElse(false);
    }
}
```

---

### 2.2 EmailSendingAdapter

**Arquivo**: `src/main/java/br/sptrans/scd/auth/adapter/port/out/email/EmailSendingAdapter.java`

```java
package br.sptrans.scd.auth.adapter.port.out.email;

import br.sptrans.scd.auth.application.port.out.EmailSendingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adaptador de E-mail — Substitui GatewayEmail.
 * 
 * ✅ Antes: GatewayEmailSmtpAdapter implements GatewayEmail
 * ✅ Depois: EmailSendingAdapter implements EmailSendingPort
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendingAdapter implements EmailSendingPort {
    
    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    
    @Override
    public void sendPasswordResetEmail(String destinatario, String nomeUsuario, String token) {
        log.info("Enviando e-mail de reset de senha para: {}", destinatario);
        try {
            String resetLink = "https://scd.sptrans.com.br/reset-password?token=" + token;
            String htmlContent = templateService.renderPasswordResetTemplate(nomeUsuario, resetLink);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject("Redefinir Sua Senha - SCD Auth");
            message.setText(htmlContent);
            message.setFrom("noreply@sptrans.com.br");
            
            mailSender.send(message);
            log.debug("E-mail enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de reset", e);
            throw new RuntimeException("Falha ao enviar e-mail", e);
        }
    }
    
    @Override
    public void sendUserCreatedEmail(String destinatario, String nomeUsuario) {
        log.info("Enviando e-mail de boas-vindas para: {}", destinatario);
        // Implementação similar...
    }
    
    @Override
    public void sendUnusualAccessEmail(String destinatario, String nomeUsuario, String details) {
        log.warn("Enviando aviso de acesso incomum para: {}", destinatario);
        // Implementação similar...
    }
}
```

---

## 💉 PASSO 3: Atualizar Use Cases (Injeções)

### 3.1 LoginUseCase

**Antes:**

```java
@Service
public class LoginUseCase {
    
    @Autowired
    private UserRepository userRepository;        // ❌ Antigo
    
    @Autowired
    private TokenGeneratorPort tokenGenerator;
}
```

**Depois:**

```java
@Service
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final UserPersistencePort userPort;  // ✅ Novo
    private final TokenGeneratorPort tokenGenerator;
    
    public LoginOutput execute(LoginInput input) {
        // Buscar usuário
        var user = userPort.findByLoginForAuthentication(input.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException());
        
        // Validar senha...
        
        // Registrar tentativa
        userPort.recordLoginAttempt(input.getLogin(), true);
        
        // Gerar token...
    }
}
```

---

### 3.2 ResetPasswordUseCase

**Antes:**

```java
@Service
public class ResetPasswordUseCase {
    
    @Autowired
    private PasswordTokenRepository tokenRepository;  // ❌ Antigo
    
    @Autowired
    private GatewayEmail emailGateway;               // ❌ Antigo
    
    @Autowired
    private UserRepository userRepository;            // ❌ Antigo
}
```

**Depois:**

```java
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {
    
    private final PasswordTokenPersistencePort tokenPort;  // ✅ Novo
    private final EmailSendingPort emailPort;              // ✅ Novo
    private final UserPersistencePort userPort;            // ✅ Novo
    
    public void execute(ResetPasswordInput input) {
        // Buscar usuário
        var user = userPort.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException());
        
        // Gerar token...
        
        // Salvar token
        tokenPort.save(resetToken);
        
        // Enviar e-mail
        emailPort.sendPasswordResetEmail(
            user.getNomEmail(),
            user.getNomUsuario(),
            resetToken.getToken()
        );
    }
}
```

---

## 🧪 PASSO 4: Atualizar Testes

### 4.1 LoginUseCaseTest

**Antes:**

```java
public class LoginUseCaseTest {
    
    @Mock
    private UserRepository userRepository;  // ❌ Antigo
    
    @InjectMocks
    private LoginUseCase useCase;
    
    @Test
    void testLoginSuccess() {
        when(userRepository.findByLoginForAuthentication("user"))
                .thenReturn(Optional.of(mockUser));
        
        // ...
    }
}
```

**Depois:**

```java
public class LoginUseCaseTest {
    
    @Mock
    private UserPersistencePort userPort;  // ✅ Novo
    
    @InjectMocks
    private LoginUseCase useCase;
    
    @Test
    void testLoginSuccess() {
        when(userPort.findByLoginForAuthentication("user"))
                .thenReturn(Optional.of(mockUser));
        
        // ...
    }
}
```

---

## 📊 PASSO 5: Cleanup - Deletar Antigas

Após testes passarem 100%:

```bash
# Delete arquivos antigos
rm src/main/java/br/sptrans/scd/auth/application/port/out/UserRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/UserReader.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/UserWriter.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/AuthenticationRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/AuthorizationRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/UserStatusRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/GatewayEmail.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/ProfileRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/GroupRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/GroupUserRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/GroupProfileRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/FunctionalityRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/ProfileFunctionalityRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/PasswordTokenRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/UserSessionRepository.java
rm src/main/java/br/sptrans/scd/auth/application/port/out/AuditLogRepository.java
```

---

## ✅ VERIFICAÇÃO FINAL

```bash
# Compilar
$ mvn clean compile

# Testar
$ mvn test

# Análise estática
$ mvn sonar:sonar

# Verificar que NENHUMA referência antiga persiste
$ grep -r "UserRepository " src/main/   # ❌ Nenhum resultado
$ grep -r "GatewayEmail" src/main/      # ❌ Nenhum resultado

# Verificar que TODOS os ports existem
$ grep -r "Port" src/main/ | grep "interface" | wc -l  # ✅ 16+

# Commit
$ git add .
$ git commit -m "refactor: standardize port naming to {Entity}Port pattern

- Rename 16 ports to consistent *Port suffix
- Segregate Query, Command, Authentication, Authorization operations
- Rename GatewayEmail to EmailSendingPort
- Update all adapters and use cases
- All tests passing"
```

---

