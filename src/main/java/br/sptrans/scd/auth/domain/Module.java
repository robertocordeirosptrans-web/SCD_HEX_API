package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

public class Module {
    private ModuleKey id;
    private String nomModulo;
    private String nomExecutavel;
    private String codStatus;
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;

    public Module(ModuleKey id, String nomModulo, String nomExecutavel, String codStatus, LocalDateTime dtManutencao, Long idUsuarioManutencao) {
        this.id = id;
        this.nomModulo = nomModulo;
        this.nomExecutavel = nomExecutavel;
        this.codStatus = codStatus;
        this.dtManutencao = dtManutencao;
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public ModuleKey getId() { return id; }
    public String getNomModulo() { return nomModulo; }
    public String getNomExecutavel() { return nomExecutavel; }
    public String getCodStatus() { return codStatus; }
    public LocalDateTime getDtManutencao() { return dtManutencao; }
    public Long getIdUsuarioManutencao() { return idUsuarioManutencao; }

    public static class ModuleKey {
        private String codSistema;
        private String codModulo;

        public ModuleKey(String codSistema, String codModulo) {
            this.codSistema = codSistema;
            this.codModulo = codModulo;
        }

        public String getCodSistema() { return codSistema; }
        public String getCodModulo() { return codModulo; }
    }
}
