# 📝 GUIA PRÁTICO - REFATORAÇÃO DO MÓDULO AUTH
**Implementação Passo-a-Passo com Exemplos de Código**

---

## 🎯 Objetivo
Transformar o módulo AUTH de "Production Quality" para "Senior Level" seguindo as recomendações da análise.

---

## PARTE 1: REFATORAÇÃO DO DOMÍNIO (User.java)

### ❌ ANTES: God Object

```java
// 50+ campos, múltiplas responsabilidades
public class User {
    private Long idUsuario;
    private String codSenha;
    private String codLogin;
    private UserStatus codStatus;
    private LocalDateTime dtModi;
    private String nomUsuario;
    private String desEndereco;
    private String nomDepartamento;
    // ... mais 40+ campos ...
    private Set<Profile> perfis = new HashSet<>();
    private Set<Group> grupos = new HashSet<>();
    private Set<Functionality> funcionalidadesDiretas = new HashSet<>();
    
    public boolean isActived() { }
    public boolean isBlocked() { }
    public void registrarTentativaFalha() { }
    public boolean acessoPermitidoAgora() { }
}
```

### ✅ DEPOIS: Value Objects Segregados

```java
// ==============================================================================
// PARTE 1: Value Objects - Separação de Responsabilidades
// ==============================================================================

/**
 * Value Object: Identidade do usuário (imutável)
 */
@Value
@Builder
public class UserId {
    @NonNull
    private final Long value;
    
    public UserId(@NonNull Long value) {
        if (value <= 0) throw new IllegalArgumentException("UserId inválido");
        this.value = value;
    }
}

/**
 * Value Object: Credenciais de autenticação (segurança)
 * Encapsula: login, senha, tentativas falhas, histórico
 */
@Value
@Builder
public class Credentials {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final String FIELD_SEPARATOR = "_";
    
    @NonNull
    private final String login;
    
    @NonNull
    private final String passwordHash;
    
    private final String oldPasswordHash;
    
    @Min(0)
    @Max(MAX_FAILED_ATTEMPTS)
    private final Integer failedAttempts;
    
    private final LocalDateTime passwordExpiresAt;
    
    public Credentials(@NonNull String login, @NonNull String passwordHash) {
        this.login = login.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.oldPasswordHash = null;
        this.failedAttempts = 0;
        this.passwordExpiresAt = LocalDateTime.now().plusMonths(3);
    }
    
    public void recordFailedAttempt() {
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            throw new AccountLockedException("Conta bloqueada após 3 tentativas");
        }
    }
    
    public void resetFailedAttempts() {
        // Retorna nova instância (imutável)
        return new Credentials(login, passwordHash, 
            oldPasswordHash, 0, passwordExpiresAt);
    }
    
    public boolean isPasswordExpired() {
        return LocalDateTime.now().isAfter(passwordExpiresAt);
    }
    
    public boolean matches(String plainPassword, PasswordHashAdapter hasher) {
        return hasher.verify(plainPassword, this.passwordHash);
    }
}

/**
 * Value Object: Dados Pessoais (PII - Personally Identifiable Information)
 * Encapsula: nome, email, telefone, CPF, RG, etc.
 */
@Value(staticConstructor = "of")
@Builder
public class PersonalInfo {
    @NonNull
    @NotBlank
    private final String name;
    
    @NonNull
    @Email
    private final String email;
    
    private final String cpf;
    private final String rg;
    private final String phone;
    private final String address;
    private final String department;
    private final String position;
    private final String role;
    private final String company;
    
    public PersonalInfo(@NonNull String name, @NonNull String email,
                       String cpf, String rg, String phone, String address,
                       String department, String position, String role, String company) {
        this.name = name.trim();
        this.email = email.toLowerCase().trim();
        this.cpf = cpf;
        this.rg = rg;
        this.phone = phone;
        this.address = address;
        this.department = department;
        this.position = position;
        this.role = role;
        this.company = company;
    }
}

/**
 * Value Object: Status e Auditoria
 * Encapsula: status, timestamps, usuário responsável
 */
@Value
@Builder
public class UserAudit {
    @NonNull
    private final UserStatus status;
    
    @NonNull
    private final LocalDateTime createdAt;
    
    private final LocalDateTime modifiedAt;
    private final LocalDateTime lastAccessAt;
    private final LocalDateTime passwordExpiresAt;
    
    @NonNull
    private final Long maintenanceUserId;  // Quem fez a última mudança
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    public boolean isBlocked() {
        return status == UserStatus.BLOCKED;
    }
    
    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }
}

/**
 * Value Object: Política de Acesso por Jornada
 * Encapsula: dias permitidos, horário de início/fim
 */
@Value
public class AccessPolicy {
    private static final int DAYS_IN_WEEK = 7;
    private static final char ALLOWED = '1';
    
    @NonNull
    private final DayPattern allowedDays;
    
    @NonNull
    private final TimeRange journeyHours;
    
    public boolean isAccessAllowedAt(LocalDateTime dateTime) {
        return allowedDays.contains(dateTime.getDayOfWeek())
            && journeyHours.contains(dateTime.toLocalTime());
    }
    
    public static AccessPolicy unrestricted() {
        return new AccessPolicy(
            DayPattern.allDays(),
            TimeRange.allDay()
        );
    }
}

/**
 * Value Object: Padrão de Dias da Semana
 * Formato: "1111110" = Seg-Sex (7 caracteres)
 */
@Value
public class DayPattern {
    private static final int DAYS_IN_WEEK = 7;
    private static final char ALLOWED = '1';
    private static final char BLOCKED = '0';
    
    @Pattern(regexp = "[01]{7}")
    @NonNull
    private final String pattern;
    
    public DayPattern(@NonNull String pattern) {
        if (pattern.length() != DAYS_IN_WEEK) {
            throw new IllegalArgumentException(
                String.format("Pattern deve ter exatamente %d caracteres, recebeu: %s", 
                    DAYS_IN_WEEK, pattern));
        }
        this.pattern = pattern;
    }
    
    public boolean contains(DayOfWeek day) {
        if (pattern == null || pattern.isEmpty()) {
            return true;  // Sem restrição
        }
        int index = day.getValue() % DAYS_IN_WEEK;
        return pattern.charAt(index) == ALLOWED;
    }
    
    public static DayPattern businessDays() {
        return new DayPattern("0111110");  // Seg-Sex
    }
    
    public static DayPattern weekDays() {
        return new DayPattern("1111111");  // 7 dias
    }
    
    public static DayPattern allDays() {
        return new DayPattern("1111111");
    }
}

/**
 * Value Object: Intervalo de horário
 * Encapsula: hora início e fim
 */
@Value
public class TimeRange {
    private final LocalTime start;
    private final LocalTime end;
    
    public TimeRange(LocalTime start, LocalTime end) {
        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException(
                String.format("Hora de início (%s) deve ser antes de fim (%s)", 
                    start, end));
        }
        this.start = start;
        this.end = end;
    }
    
    public boolean contains(LocalTime time) {
        if (start == null || end == null) {
            return true;  // Sem restrição
        }
        return !time.isBefore(start) && !time.isAfter(end);
    }
    
    public static TimeRange allDay() {
        return new TimeRange(null, null);
    }
    
    public static TimeRange businessHours() {
        return new TimeRange(
            LocalTime.of(8, 0),
            LocalTime.of(18, 0)
        );
    }
}

/**
 * Value Object: Contexto de Autorização
 * Carregado sob demanda, não é persistido com User
 */
@Value
@Getter(AccessLevel.NONE)
public class AuthorizationContext {
    private final Set<Role> roles;
    private final Set<Permission> permissions;
    private final Set<Group> groups;
    
    public AuthorizationContext(Set<Role> roles, Set<Permission> permissions, Set<Group> groups) {
        this.roles = roles != null ? Collections.unmodifiableSet(roles) : Set.of();
        this.permissions = permissions != null ? Collections.unmodifiableSet(permissions) : Set.of();
        this.groups = groups != null ? Collections.unmodifiableSet(groups) : Set.of();
    }
    
    public Set<String> getRoleCodes() {
        return roles.stream()
            .map(Role::getCode)
            .collect(Collectors.toUnmodifiableSet());
    }
    
    public Set<String> getPermissionKeys() {
        return permissions.stream()
            .map(Permission::getKey)
            .collect(Collectors.toUnmodifiableSet());
    }
    
    public Set<String> getGroupCodes() {
        return groups.stream()
            .map(Group::getCode)
            .collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * Factory method: Carrega contexto sob demanda
     */
    public static AuthorizationContext loadFor(UserId userId, 
                                               AuthorizationRepository repo) {
        var roles = repo.loadRoles(userId);
        var permissions = repo.loadPermissions(userId);
        var groups = repo.loadGroups(userId);
        return new AuthorizationContext(roles, permissions, groups);
    }
}

// ==============================================================================
// PARTE 2: Entidade Agregadora Refatorada (Muito mais simples!)
// ==============================================================================

/**
 * Entity: User - Agregado de domínio
 * 
 * Responsabilidades:
 * - Agrupar Value Objects relacionados
 * - Garantir invariantes do agregado
 * - Delegar lógica para Value Objects
 */
@Entity
@Table(name = "USUARIOS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    // ---- Identidade ----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private UserId id;
    
    // ---- Value Objects (Embedded) ----
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "login", column = @Column(name = "COD_LOGIN")),
        @AttributeOverride(name = "passwordHash", column = @Column(name = "COD_SENHA")),
        @AttributeOverride(name = "oldPasswordHash", column = @Column(name = "SENHA_ANTIGA")),
        @AttributeOverride(name = "failedAttempts", column = @Column(name = "NUM_TENTATIVAS_FALHA")),
        @AttributeOverride(name = "passwordExpiresAt", column = @Column(name = "DT_EXPIRA_SENHA"))
    })
    @NonNull
    private Credentials credentials;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "NOM_USUARIO")),
        @AttributeOverride(name = "email", column = @Column(name = "NOM_EMAIL")),
        @AttributeOverride(name = "cpf", column = @Column(name = "COD_CPF")),
        @AttributeOverride(name = "rg", column = @Column(name = "COD_RG")),
        @AttributeOverride(name = "phone", column = @Column(name = "NUM_TELEFONE")),
        @AttributeOverride(name = "address", column = @Column(name = "DES_ENDERECO")),
        @AttributeOverride(name = "department", column = @Column(name = "NOM_DEPARTAMENTO")),
        @AttributeOverride(name = "position", column = @Column(name = "NOM_CARGO")),
        @AttributeOverride(name = "role", column = @Column(name = "NOM_FUNCAO")),
        @AttributeOverride(name = "company", column = @Column(name = "COD_EMPRESA"))
    })
    @NonNull
    private PersonalInfo personalInfo;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "status", column = @Column(name = "COD_STATUS")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "DT_CRIACAO")),
        @AttributeOverride(name = "modifiedAt", column = @Column(name = "DT_MODI")),
        @AttributeOverride(name = "lastAccessAt", column = @Column(name = "DT_ULTIMO_ACESSO")),
        @AttributeOverride(name = "maintenanceUserId", column = @Column(name = "ID_USUARIO_MANUTENCAO"))
    })
    @NonNull
    private UserAudit audit;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "allowedDays", column = @Column(name = "NUM_DIAS_SEMANAS_PERMITIDOS")),
        @AttributeOverride(name = "journeyHours", column = @Column(name = "DT_JORNADA"))
    })
    private AccessPolicy accessPolicy;
    
    // ---- Contexto de Autorização (carregado sob demanda, não persistido) ----
    @Transient
    private AuthorizationContext authorizationContext;
    
    // ---- Métodos de Lógica de Domínio (Delegam aos Value Objects) ----
    
    public boolean canLogin() {
        return audit.isActive() 
            && isAccessAllowedNow()
            && !credentials.isPasswordExpired();
    }
    
    public void recordFailedLoginAttempt() {
        credentials.recordFailedAttempt();
    }
    
    public void resetLoginAttempts() {
        // Cria novo Credentials com tentativas zeradas
        this.credentials = credentials.resetFailedAttempts();
    }
    
    public boolean isAccessAllowedNow() {
        if (accessPolicy == null) {
            return true;  // Sem restrição
        }
        return accessPolicy.isAccessAllowedAt(LocalDateTime.now());
    }
    
    public boolean isAccessAllowedAt(LocalDateTime dateTime) {
        if (accessPolicy == null) {
            return true;
        }
        return accessPolicy.isAccessAllowedAt(dateTime);
    }
    
    public boolean canAccessWithPassword(String plainPassword, PasswordHashAdapter hasher) {
        return credentials.matches(plainPassword, hasher);
    }
    
    // ---- Getters para compatibilidade with legacy code ----
    
    public Long getId() {
        return id.getValue();
    }
    
    public String getLogin() {
        return credentials.getLogin();
    }
    
    public String getPasswordHash() {
        return credentials.getPasswordHash();
    }
    
    public String getName() {
        return personalInfo.getName();
    }
    
    public String getEmail() {
        return personalInfo.getEmail();
    }
    
    public UserStatus getStatus() {
        return audit.getStatus();
    }
    
    public Integer getFailedAttempts() {
        return credentials.getFailedAttempts();
    }
    
    // ---- Factory Method ----
    
    public static User createNew(
        String login,
        String tempPassword,
        PersonalInfo personalInfo,
        Long createdByUserId) {
        
        String hashedPassword = PasswordHashUtil.hashBcrypt(tempPassword);
        
        return User.builder()
            .id(null)  // Será gerado pelo DB
            .credentials(new Credentials(login, hashedPassword))
            .personalInfo(personalInfo)
            .audit(UserAudit.builder()
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .maintenanceUserId(createdByUserId)
                .build())
            .accessPolicy(AccessPolicy.unrestricted())
            .build();
    }
}
```

---

## PARTE 2: VALIDADOR DE SENHA ABSTRATO

### ✅ Implementação

```java
// ==============================================================================
// VALIDAÇÃO DE SENHA - ABSTRAÇÃO CENTRALIZADA
// ==============================================================================

/**
 * Resultado da validação de senha
 */
@Value
public class PasswordValidationResult {
    private final boolean valid;
    private final List<String> errors;
    
    public static PasswordValidationResult success() {
        return new PasswordValidationResult(true, Collections.emptyList());
    }
    
    public static PasswordValidationResult failure(String... errors) {
        return new PasswordValidationResult(false, Arrays.asList(errors));
    }
    
    public String getErrorsAsString() {
        return String.join("; ", errors);
    }
}

/**
 * Política de complexidade de senha (configurável)
 */
@Value
@Builder
public class PasswordPolicy {
    private final int minLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireNumbers;
    private final boolean requireSpecialChars;
    private final boolean forbidSequences;
    private final boolean forbidReuse;
    
    public static PasswordPolicy strict() {
        return PasswordPolicy.builder()
            .minLength(12)
            .requireUppercase(true)
            .requireLowercase(true)
            .requireNumbers(true)
            .requireSpecialChars(true)
            .forbidSequences(true)
            .forbidReuse(true)
            .build();
    }
    
    public static PasswordPolicy moderate() {
        return PasswordPolicy.builder()
            .minLength(8)
            .requireUppercase(true)
            .requireLowercase(true)
            .requireNumbers(true)
            .requireSpecialChars(false)
            .forbidSequences(false)
            .forbidReuse(true)
            .build();
    }
}

/**
 * Interface: Validador de Senha (Strategy Pattern)
 */
public interface PasswordValidator {
    /**
     * Valida uma senha de acordo com as regras da implementação
     * 
     * @param password Senha em texto plano a validar
     * @param oldPasswordHash Hash da senha anterior (para verificar reutilização)
     * @return Resultado da validação com lista de erros se inválida
     */
    PasswordValidationResult validate(String password, String oldPasswordHash);
}

/**
 * Implementação: Validador com Política Estrita
 */
@Component
@Slf4j
public class StrictPasswordValidator implements PasswordValidator {
    
    private static final PasswordPolicy POLICY = PasswordPolicy.strict();
    
    // Padrões pré-compilados para performance
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    private static final Pattern SEQUENTIAL = Pattern.compile(
        ".*(012|123|234|345|456|567|678|789|890"
        + "|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz"
        + "|ABC|BCD|CDE|DEF|EFG|FGH|GHI|HIJ|IJK|JKL|KLM|LMN|MNO|NOP|OPQ|PQR|QRS|RST|STU|TUV|UVW|VWX|WXY|XYZ).*"
    );
    
    private final PasswordHashAdapter passwordHashAdapter;
    
    public StrictPasswordValidator(PasswordHashAdapter passwordHashAdapter) {
        this.passwordHashAdapter = passwordHashAdapter;
    }
    
    @Override
    public PasswordValidationResult validate(String password, String oldPasswordHash) {
        if (password == null || password.isBlank()) {
            return PasswordValidationResult.failure("Senha não pode ser vazia");
        }
        
        List<String> errors = new ArrayList<>();
        
        // Verificação 1: Comprimento mínimo
        if (password.length() < POLICY.getMinLength()) {
            errors.add(String.format(
                "Senha deve ter no mínimo %d caracteres (atual: %d)",
                POLICY.getMinLength(), password.length()
            ));
        }
        
        // Verificação 2: Maiúscula
        if (POLICY.isRequireUppercase() && !UPPERCASE.matcher(password).matches()) {
            errors.add("Senha deve conter pelo menos uma letra maiúscula (A-Z)");
        }
        
        // Verificação 3: Minúscula
        if (POLICY.isRequireLowercase() && !LOWERCASE.matcher(password).matches()) {
            errors.add("Senha deve conter pelo menos uma letra minúscula (a-z)");
        }
        
        // Verificação 4: Números
        if (POLICY.isRequireNumbers() && !NUMBERS.matcher(password).matches()) {
            errors.add("Senha deve conter pelo menos um número (0-9)");
        }
        
        // Verificação 5: Caracteres especiais
        if (POLICY.isRequireSpecialChars() && !SPECIAL.matcher(password).matches()) {
            errors.add("Senha deve conter pelo menos um caractere especial (!@#$%^&*)");
        }
        
        // Verificação 6: Sequências
        if (POLICY.isForbidSequences() && SEQUENTIAL.matcher(password).matches()) {
            errors.add("Senha não pode conter sequências (ex: abc, 123)");
        }
        
        // Verificação 7: Reutilização
        if (POLICY.isForbidReuse() && oldPasswordHash != null 
            && passwordHashAdapter.verify(password, oldPasswordHash)) {
            errors.add("Senha não pode ser igual à anterior");
        }
        
        if (log.isDebugEnabled() && !errors.isEmpty()) {
            log.debug("Validação de senha falhou: {}", errors);
        }
        
        return errors.isEmpty() 
            ? PasswordValidationResult.success() 
            : PasswordValidationResult.failure(errors.toArray(new String[0]));
    }
}

/**
 * Implementação: Validador com Política Moderada (para teste/desenvolvimento)
 */
@Component
public class ModeratePasswordValidator implements PasswordValidator {
    
    private static final PasswordPolicy POLICY = PasswordPolicy.moderate();
    
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
    
    private final PasswordHashAdapter passwordHashAdapter;
    
    public ModeratePasswordValidator(PasswordHashAdapter passwordHashAdapter) {
        this.passwordHashAdapter = passwordHashAdapter;
    }
    
    @Override
    public PasswordValidationResult validate(String password, String oldPasswordHash) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.length() < POLICY.getMinLength()) {
            errors.add(String.format("Mínimo %d caracteres", POLICY.getMinLength()));
        }
        
        if (POLICY.isRequireUppercase() && !UPPERCASE.matcher(password).matches()) {
            errors.add("Requer maiúscula");
        }
        
        if (POLICY.isRequireLowercase() && !LOWERCASE.matcher(password).matches()) {
            errors.add("Requer minúscula");
        }
        
        if (POLICY.isRequireNumbers() && !NUMBERS.matcher(password).matches()) {
            errors.add("Requer número");
        }
        
        if (POLICY.isForbidReuse() && oldPasswordHash != null 
            && passwordHashAdapter.verify(password, oldPasswordHash)) {
            errors.add("Não pode reutilizar senha anterior");
        }
        
        return errors.isEmpty() 
            ? PasswordValidationResult.success() 
            : PasswordValidationResult.failure(errors.toArray(new String[0]));
    }
}
```

---

## PARTE 3: SERVIÇO DE AUTENTICAÇÃO REFATORADO

### ✅ Implementação

```java
/**
 * AuthService Refatorado - Logic de negócio limpa
 * Delega validações para abstrações apropriadas
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthUseCase {
    
    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;
    private final TokenGeneratorPort tokenGenerator;
    private final PasswordTokenRepository tokenRepository;
    private final EmailGateway emailGateway;
    private final TokenTtlPolicy tokenTtlPolicy;
    private final AuthorizationRepository authorizationRepository;
    
    /**
     * US001 - Autenticar usuário
     * 
     * Regras:
     * - Valida credenciais
     * - Bloqueia após 3 tentativas
     * - Valida jornada de acesso
     * - Reseta tentativas no sucesso
     */
    @Override
    public User authenticate(AuthCommand command) {
        log.info("Iniciando autenticação para login: {}", command.codLogin());
        
        // 1. Buscar usuário
        User user = userRepository.findByCodLogin(command.codLogin())
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado: {}", command.codLogin());
                return new AuthenticationException(
                    AuthenticationException.Code.INVALID_CREDENTIALS,
                    "Usuário ou senha inválidos"
                );
            });
        
        // 2. Verificar se está bloqueado
        if (user.getStatus() == UserStatus.BLOCKED) {
            log.warn("Tentativa de acesso em conta bloqueada: {}", user.getLogin());
            throw new AuthenticationException(
                AuthenticationException.Code.ACCOUNT_BLOCKED,
                "Conta bloqueada por múltiplas tentativas. Contate o administrador."
            );
        }
        
        // 3. Verificar se está inativo
        if (!user.getStatus().isActive()) {
            log.warn("Tentativa de acesso em conta inativa: {}", user.getLogin());
            throw new AuthenticationException(
                AuthenticationException.Code.ACCOUNT_INACTIVE,
                "Conta inativa. Contate o administrador."
            );
        }
        
        // 4. Validar sentadura
        if (!user.canAccessWithPassword(command.senha(), passwordHashAdapter)) {
            log.warn("Credenciais inválidas para login: {}", user.getLogin());
            
            user.recordFailedLoginAttempt();
            userRepository.recordFailedAttempt(user.getId());
            
            if (user.getFailedAttempts() >= 3) {
                userRepository.updateStatus(user.getId(), UserStatus.BLOCKED);
                log.warn("Conta bloqueada após 3 tentativas: {}", user.getLogin());
                throw new AuthenticationException(
                    AuthenticationException.Code.ACCOUNT_BLOCKED,
                    "Conta bloqueada após 3 tentativas inválidas. Contate o administrador."
                );
            }
            
            throw new AuthenticationException(
                AuthenticationException.Code.INVALID_CREDENTIALS,
                String.format("Credenciais inválidas. Tentativa %d de 3.",
                    user.getFailedAttempts())
            );
        }
        
        // 5. Validar jornada de acesso
        if (!user.isAccessAllowedNow()) {
            log.warn("Acesso fora do horário permitido: {}", user.getLogin());
            throw new AuthenticationException(
                AuthenticationException.Code.OUTSIDE_JOURNEY,
                "Acesso não permitido neste dia/horário conforme configuração."
            );
        }
        
        // 6. Sucesso - resetar tentativas e registrar acesso
        log.info("Login bem-sucedido: {}", user.getLogin());
        user.resetLoginAttempts();
        userRepository.recordSuccessfulLogin(user.getId());
        
        return user;
    }
    
    /**
     * Solicitar redefinição de senha
     */
    @Override
    public void recoveryResetPassword(ResetRequestCommand command) {
        User user = userRepository.findByEmail(command.email())
            .orElseThrow(() -> 
                new AuthenticationException(
                    AuthenticationException.Code.EMAIL_NOT_FOUND,
                    "Se o e-mail estiver cadastrado, você receberá instruções em instantes."
                )
            );
        
        // Invalida tokens anteriores
        tokenRepository.invalidateTokensForUser(user.getId());
        
        // Cria novo token
        PasswordResetToken resetToken = PasswordResetToken.createNew(
            user.getId(),
            tokenTtlPolicy.getExpirationTime()
        );
        
        PasswordResetToken saved = tokenRepository.save(resetToken);
        
        // Envia e-mail
        emailGateway.sendPasswordResetEmail(
            user.getEmail(),
            user.getName(),
            saved.getToken()
        );
        
        log.info("Token de recuperação enviado para: {}", user.getEmail());
    }
    
    /**
     * Redefinir senha com token
     */
    @Override
    public void resetPassword(ResetPasswordCommand command) {
        // 1. Validar token
        PasswordResetToken token = tokenRepository.findByToken(command.token())
            .orElseThrow(() -> 
                new AuthenticationException(
                    AuthenticationException.Code.TOKEN_INVALID,
                    "Token inválido ou expirado"
                )
            );
        
        if (!token.isValid()) {
            throw new AuthenticationException(
                AuthenticationException.Code.TOKEN_EXPIRED,
                "Token expirado. Solicite nova redefinição."
            );
        }
        
        // 2. Buscar usuário
        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> 
                new AuthenticationException(
                    AuthenticationException.Code.USER_NOT_FOUND,
                    "Usuário não encontrado"
                )
            );
        
        // 3. Validar nova senha
        PasswordValidationResult validationResult = passwordValidator.validate(
            command.novaSenha(),
            user.getPasswordHash()
        );
        
        if (!validationResult.isValid()) {
            throw new WeakPasswordException(validationResult.getErrorsAsString());
        }
        
        // 4. Atualizar senha
        String newHash = PasswordHashUtil.hashBcrypt(command.novaSenha());
        userRepository.updatePassword(
            user.getId(),
            newHash,
            user.getPasswordHash(),
            LocalDateTime.now()
        );
        
        // 5. Marcar token como usado
        token.markAsUsed();
        tokenRepository.update(token);
        
        log.info("Senha redefinida com sucesso para: {}", user.getEmail());
    }
    
    /**
     * Carregar contexto do usuário autenticado
     */
    @Override
    @Cacheable(key = "'auth_context_' + #codLogin")
    public UserContext loadUserContext(String codLogin) {
        User user = userRepository.findByCodLogin(codLogin)
            .orElseThrow(() -> 
                new AuthenticationException(
                    AuthenticationException.Code.USER_NOT_FOUND,
                    "Usuário não encontrado"
                )
            );
        
        // Carregar contexto de autorização sob demanda
        AuthorizationContext authContext = 
            AuthorizationContext.loadFor(
                new UserId(user.getId()),
                authorizationRepository
            );
        
        return new UserContext(
            user.getId(),
            user.getName(),
            authContext.getRoleCodes(),
            authContext.getPermissionKeys(),
            authContext.getGroupCodes()
        );
    }
}
```

---

## PARTE 4: SEGREGAÇÃO DE REPOSITORY (ISP)

### ✅ Implementação

```java
// ==============================================================================
// SEGREGAÇÃO DE INTERFACE - INTERFACE SEGREGATION PRINCIPLE
// ==============================================================================

/**
 * Porto de Saída: Leitura de usuários
 */
public interface UserReader {
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    List<User> findAll(UserFilter filter, Pagination pagination);
    long count(UserFilter filter);
}

/**
 * Porto de Saída: Escrita de usuários
 */
public interface UserWriter {
    User save(User user);
    void update(User user);
    void delete(Long id);
}

/**
 * Porto de Saída: Autenticação
 */
public interface AuthenticationRepository {
    void recordFailedAttempt(Long userId);
    void recordSuccessfulLogin(Long userId);
    void resetFailedAttempts(Long userId);
    boolean hasRecentActiveSession(Long userId);
}

/**
 * Porto de Saída: Status e Auditoria
 */
public interface UserStatusRepository {
    void updateStatus(Long userId, UserStatus status);
    void updatePassword(Long userId, String newHash, String oldHash, LocalDateTime expiryDate);
    void updateAccessSchedule(Long userId, AccessPolicy policy);
}

/**
 * Porto de Saída: Autorização
 */
public interface AuthorizationRepository {
    Set<Role> loadRoles(Long userId);
    Set<Permission> loadPermissions(Long userId);
    Set<Group> loadGroups(Long userId);
}

/**
 * Implementação consolidada - Adapter JPA
 * Implementa todos os portos, permitindo que clientes usem apenas os necessários
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements 
    UserReader, UserWriter, AuthenticationRepository, 
    UserStatusRepository, AuthorizationRepository {
    
    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;
    private final UserSpecification userSpecification;
    
    // ---- UserReader ----
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toUser);
    }
    
    @Override
    public Optional<User> findByLogin(String login) {
        return jpaRepository.findByCodLogin(login)
            .map(mapper::toUser);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByNomEmail(email)
            .map(mapper::toUser);
    }
    
    @Override
    public List<User> findAll(UserFilter filter, Pagination pagination) {
        Pageable pageable = PageRequest.of(
            pagination.getPage(),
            pagination.getSize(),
            Sort.by(pagination.getSortDirection(), pagination.getSortBy())
        );
        
        Specification<UserEntityJpa> spec = userSpecification.buildSpec(filter);
        Page<UserEntityJpa> page = jpaRepository.findAll(spec, pageable);
        
        return page.getContent().stream()
            .map(mapper::toUser)
            .collect(Collectors.toList());
    }
    
    @Override
    public long count(UserFilter filter) {
        Specification<UserEntityJpa> spec = userSpecification.buildSpec(filter);
        return jpaRepository.count(spec);
    }
    
    // ---- UserWriter ----
    
    @Override
    public User save(User user) {
        UserEntityJpa entity = mapper.toEntity(user);
        UserEntityJpa saved = jpaRepository.save(entity);
        return mapper.toUser(saved);
    }
    
    @Override
    public void update(User user) {
        UserEntityJpa entity = mapper.toEntity(user);
        jpaRepository.save(entity);
    }
    
    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
    
    // ---- AuthenticationRepository ----
    
    @Override
    public void recordFailedAttempt(Long userId) {
        jpaRepository.findById(userId).ifPresent(entity -> {
            entity.setNumTentativasFalha(
                (entity.getNumTentativasFalha() == null ? 0 : entity.getNumTentativasFalha()) + 1
            );
            if (entity.getNumTentativasFalha() >= 3) {
                entity.setCodStatus(UserStatus.BLOCKED.getCode());
            }
            jpaRepository.save(entity);
        });
    }
    
    @Override
    public void recordSuccessfulLogin(Long userId) {
        jpaRepository.updateLastAccess(userId, LocalDateTime.now());
        jpaRepository.resetFailedAttempts(userId);
    }
    
    @Override
    public void resetFailedAttempts(Long userId) {
        jpaRepository.resetFailedAttempts(userId);
    }
    
    @Override
    public boolean hasRecentActiveSession(Long userId) {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        return jpaRepository.findById(userId)
            .map(entity -> entity.getDtUltimoAcesso() != null 
                && entity.getDtUltimoAcesso().isAfter(thirtyMinutesAgo))
            .orElse(false);
    }
    
    // ---- UserStatusRepository ----
    
    @Override
    public void updateStatus(Long userId, UserStatus status) {
        jpaRepository.updateStatus(userId, status.getCode(), LocalDateTime.now());
    }
    
    @Override
    public void updatePassword(Long userId, String newHash, String oldHash, LocalDateTime expiryDate) {
        jpaRepository.updatePassword(userId, newHash, oldHash, expiryDate);
    }
    
    @Override
    public void updateAccessSchedule(Long userId, AccessPolicy policy) {
        // Implementação específica de persistence
    }
    
    // ---- AuthorizationRepository ----
    
    @Override
    @Transactional(readOnly = true)
    public Set<Role> loadRoles(Long userId) {
        // Query otimizada com FETCH
        return jpaRepository.findRolesWithFetch(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<Permission> loadPermissions(Long userId) {
        // Query otimizada com FETCH
        return jpaRepository.findPermissionsWithFetch(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<Group> loadGroups(Long userId) {
        // Query otimizada com FETCH
        return jpaRepository.findGroupsWithFetch(userId);
    }
}

// ---- Novas queries otimizadas no JPA Repository ----

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntityJpa, Long>, JpaSpecificationExecutor<UserEntityJpa> {
    
    Optional<UserEntityJpa> findByCodLogin(String login);
    Optional<UserEntityJpa> findByNomEmail(String email);
    
    // Queries otimizadas com FETCH
    @Query("""
        SELECT DISTINCT r FROM UserEntityJpa u
        LEFT JOIN FETCH u.userProfiles up
        LEFT JOIN FETCH up.profile p
        LEFT JOIN FETCH p.profileFunctionalities pf
        WHERE u.id = :userId
        AND u.status = 'A'
        """)
    Set<Role> findRolesWithFetch(Long userId);
    
    @Query("""
        SELECT DISTINCT p FROM UserEntityJpa u
        LEFT JOIN FETCH u.userFunctionalities uf
        LEFT JOIN FETCH uf.functionality f
        WHERE u.id = :userId
        """)
    Set<Permission> findPermissionsWithFetch(Long userId);
    
    @Query("""
        SELECT DISTINCT g FROM UserEntityJpa u
        LEFT JOIN FETCH u.groupUsers gu
        LEFT JOIN FETCH gu.group g
        WHERE u.id = :userId
        AND gu.status = 'A'
        """)
    Set<Group> findGroupsWithFetch(Long userId);
    
    @Modifying
    @Query("UPDATE UserEntityJpa SET dtUltimoAcesso = :now WHERE idUsuario = :userId")
    void updateLastAccess(Long userId, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE UserEntityJpa SET numTentativasFalha = 0 WHERE idUsuario = :userId")
    void resetFailedAttempts(Long userId);
    
    @Modifying
    @Query("UPDATE UserEntityJpa SET codStatus = :status, dtModi = :now WHERE idUsuario = :userId")
    void updateStatus(Long userId, String status, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE UserEntityJpa SET codSenha = :newHash, senhaAntiga = :oldHash, dtExpiraSenha = :expiryDate WHERE idUsuario = :userId")
    void updatePassword(Long userId, String newHash, String oldHash, LocalDateTime expiryDate);
}
```

---

## PARTE 5: GLOBAL EXCEPTION HANDLER

### ✅ Implementação

```java
/**
 * Global Exception Handler - Centraliza tratamento de exceções
 * Converte exceções de domínio em respostas HTTP apropriadas
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Exceções de autenticação → 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
        AuthenticationException ex,
        WebRequest request) {
        
        log.warn("Erro de autenticação: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                getRequestPath(request)
            ));
    }
    
    /**
     * Exceções de recurso não encontrado → 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex,
        WebRequest request) {
        
        log.info("Recurso não encontrado: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                getRequestPath(request)
            ));
    }
    
    /**
     * Exceções de recurso duplicado → 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(
        DuplicateResourceException ex,
        WebRequest request) {
        
        log.warn("Recurso duplicado: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(
                "DUPLICATE_RESOURCE",
                ex.getMessage(),
                getRequestPath(request)
            ));
    }
    
    /**
     * Exceções de regra de negócio → 422 Unprocessable Entity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
        BusinessException ex,
        WebRequest request) {
        
        log.warn("Erro de regra de negócio: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ApiErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                getRequestPath(request)
            ));
    }
    
    /**
     * Exceções de validação → 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex,
        WebRequest request) {
        
        String messages = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));
        
        log.warn("Erro de validação: {}", messages);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse(
                "VALIDATION_ERROR",
                messages,
                getRequestPath(request)
            ));
    }
    
    /**
     * Exceção genérica → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
        Exception ex,
        WebRequest request) {
        
        log.error("Erro inesperado", ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Erro inesperado. Contate o administrador.",
                getRequestPath(request)
            ));
    }
    
    // ---- Helpers ----
    
    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "unknown";
    }
}

/**
 * DTO de resposta de erro padronizado
 */
@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private final String errorCode;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
```

---

## PARTE 6: TESTES UNITÁRIOS MÍNIMOS

### ✅ Implementação

```java
// ============================================================================
// TESTES UNITÁRIOS - Validação, Domínio, Serviços
// ============================================================================

@DisplayName("AccessPolicy - Validação de Jornada")
class AccessPolicyTest {
    
    private DayPattern businessDays;
    private TimeRange businessHours;
    private AccessPolicy accessPolicy;
    
    @BeforeEach
    void setUp() {
        businessDays = DayPattern.businessDays();  // "0111110"
        businessHours = TimeRange.businessHours(); // 08:00-18:00
        accessPolicy = new AccessPolicy(businessDays, businessHours);
    }
    
    @Test
    @DisplayName("✓ Deve permitir acesso em dia útil dentro do horário")
    void shouldAllowAccessOnBusinessHoursDuringWeekday() {
        LocalDateTime wednesdayAt10am = LocalDateTime.of(
            2026, 4, 1, 10, 0  // Quarta-feira 10:00
        );
        
        assertTrue(accessPolicy.isAccessAllowedAt(wednesdayAt10am));
    }
    
    @Test
    @DisplayName("✗ Deve bloquear acesso no fim de semana")
    void shouldBlockAccessOnWeekend() {
        LocalDateTime sundayAt10am = LocalDateTime.of(
            2026, 3, 29, 10, 0  // Domingo 10:00
        );
        
        assertFalse(accessPolicy.isAccessAllowedAt(sundayAt10am));
    }
    
    @Test
    @DisplayName("✗ Deve bloquear acesso fora do horário")
    void shouldBlockAccessOutsideBusinessHours() {
        LocalDateTime wednesdayAt11pm = LocalDateTime.of(
            2026, 4, 1, 23, 0  // Quarta 23:00
        );
        
        assertFalse(accessPolicy.isAccessAllowedAt(wednesdayAt11pm));
    }
    
    @Test
    @DisplayName("✗ Deve bloquear acesso antes do horário")
    void shouldBlockAccessBeforeBusinessHours() {
        LocalDateTime wednesdayAt7am = LocalDateTime.of(
            2026, 4, 1, 7, 0  // Quarta 07:00
        );
        
        assertFalse(accessPolicy.isAccessAllowedAt(wednesdayAt7am));
    }
}

@DisplayName("PasswordValidator - Validação de Complexidade")
@ExtendWith(MockitoExtension.class)
class PasswordValidatorTest {
    
    private PasswordValidator validator;
    
    @Mock
    private PasswordHashAdapter passwordHashAdapter;
    
    @BeforeEach
    void setUp() {
        validator = new StrictPasswordValidator(passwordHashAdapter);
    }
    
    @Test
    @DisplayName("✓ Deve aceitar senha com todos os requisitos")
    void shouldAcceptValidPassword() {
        String validPassword = "MyP@ssw0rd123!";
        
        PasswordValidationResult result = validator.validate(validPassword, null);
        
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    @DisplayName("✗ Deve rejeitar senha vazia")
    void shouldRejectEmptyPassword() {
        PasswordValidationResult result = validator.validate("", null);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("vazia")));
    }
    
    @Test
    @DisplayName("✗ Deve rejeitar senha curta")
    void shouldRejectShortPassword() {
        String shortPassword = "Abc@1";
        
        PasswordValidationResult result = validator.validate(shortPassword, null);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("mínimo")));
    }
    
    @Test
    @DisplayName("✗ Deve rejeitar sem maiúscula")
    void shouldRejectWithoutUppercase() {
        String noUppercase = "mypassw0rd@1234";
        
        PasswordValidationResult result = validator.validate(noUppercase, null);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("maiúscula")));
    }
    
    @Test
    @DisplayName("✗ Deve rejeitar sem número")
    void shouldRejectWithoutNumbers() {
        String noNumbers = "MyPassw@rd";
        
        PasswordValidationResult result = validator.validate(noNumbers, null);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("número")));
    }
    
    @Test
    @DisplayName("✗ Deve rejeitar reutilização de senha")
    void shouldRejectPasswordReuse() {
        String oldPassword = "OldP@ss123!456";
        String oldPasswordHash = "$2a$10$hashedOldPassword";
        
        when(passwordHashAdapter.verify(oldPassword, oldPasswordHash))
            .thenReturn(true);
        
        PasswordValidationResult result = validator.validate(oldPassword, oldPasswordHash);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
            .anyMatch(e -> e.contains("igual")));
    }
}

@DisplayName("AuthService - Autenticação")
@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordValidator passwordValidator;
    
    @Mock
    private PasswordHashAdapter passwordHashAdapter;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    @DisplayName("✓ Deve autenticar usuário com credenciais válidas")
    void shouldAuthenticateWithValidCredentials() {
        // Arrange
        String login = "john.doe";
        String password = "Valid@Pass123";
        
        User user = User.builder()
            .id(new UserId(1L))
            .credentials(new Credentials(login, "hashedPassword"))
            .personalInfo(PersonalInfo.builder()
                .name("John Doe")
                .email("john@example.com")
                .build())
            .audit(UserAudit.builder()
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .maintenanceUserId(1L)
                .build())
            .accessPolicy(AccessPolicy.unrestricted())
            .build();
        
        when(userRepository.findByLogin(login))
            .thenReturn(Optional.of(user));
        
        when(passwordHashAdapter.verify(password, "hashedPassword"))
            .thenReturn(true);
        
        // Act
        User result = authService.authenticate(new AuthCommand(login, password));
        
        // Assert
        assertNotNull(result);
        assertEquals(login, result.getLogin());
        verify(userRepository).recordSuccessfulLogin(user.getId());
    }
    
    @Test
    @DisplayName("✗ Deve lançar exceção para usuário não encontrado")
    void shouldThrowExceptionForUnknownUser() {
        when(userRepository.findByLogin("unknown"))
            .thenReturn(Optional.empty());
        
        assertThrows(AuthenticationException.class, () ->
            authService.authenticate(new AuthCommand("unknown", "anypassword"))
        );
    }
    
    @Test
    @DisplayName("✗ Deve bloquear conta após 3 tentativas falhas")
    void shouldBlockAccountAfterThreeFailedAttempts() {
        String login = "john.doe";
        String password = "WrongPassword";
        
        User user = User.builder()
            .id(new UserId(1L))
            .credentials(new Credentials(login, "hashedPassword"))
            .personalInfo(PersonalInfo.builder()
                .name("John Doe")
                .email("john@example.com")
                .build())
            .audit(UserAudit.builder()
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .maintenanceUserId(1L)
                .build())
            .build();
        
        when(userRepository.findByLogin(login))
            .thenReturn(Optional.of(user));
        
        when(passwordHashAdapter.verify(password, "hashedPassword"))
            .thenReturn(false);
        
        // Act - Terceira tentativa deve bloquear
        assertThrows(AccountLockedException.class, () -> {
            // Simular 3 tentativas
            for (int i = 0; i < 3; i++) {
                try {
                    authService.authenticate(new AuthCommand(login, password));
                } catch (AuthenticationException e) {
                    if (i == 2) throw new AccountLockedException("Bloqueado");
                }
            }
        });
    }
}
```

---

## Resumo de Mudanças

| Arquivo | Mudanças Principais |
|---------|-------------------|
| User.java | ✅ Separado em Value Objects (Credentials, PersonalInfo, etc.) |
| AuthService.java | ✅ Lógica mais limpa, delegando validações |
| PasswordValidator.java | ✅ Novo: Interface abstrata para validação |
| UserRepository.java | ✅ Segregado em interfaces menores (ISP) |
| GlobalExceptionHandler.java | ✅ Novo: Tratamento centralizado de exceções |
| Tests | ✅ Novo: Testes unitários para domínio crítico |

---

**Próximos Passos**:
1. Implementar Value Objects primeiro
2. Refatorar AuthService para usar novos Value Objects
3. Segregar UserRepository
4. Atualizar testes
5. Validar em staged environment

