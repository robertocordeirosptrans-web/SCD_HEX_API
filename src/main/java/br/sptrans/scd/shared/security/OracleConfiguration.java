package br.sptrans.scd.shared.security;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class OracleConfiguration {

    protected static Logger logger = LoggerFactory.getLogger(OracleConfiguration.class);

    @Bean
    @SuppressWarnings("UseSpecificCatch")
    public DataSource dataSource(Environment environment) throws Exception {
        try {

            if (environment.getActiveProfiles().length != 0 && environment.getActiveProfiles()[0].equals("local")) {
                OracleDataSource dataSource = new OracleDataSource();
                    dataSource.setUser(environment.getProperty("spring.datasource.username"));
                    dataSource.setPassword(environment.getProperty("spring.datasource.password"));
                dataSource.setURL(environment.getProperty("spring.datasource.url"));
                dataSource.setImplicitCachingEnabled(true);

                // Configurar FCF via propriedades
                Properties props = new Properties();
                props.setProperty("oracle.jdbc.fastConnectionFailover", "true");
                dataSource.setConnectionProperties(props);

                return dataSource;
            }
            JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
            bean.setJndiName("java:comp/env/" + environment.getProperty("spring.datasource.jndi-name"));
            bean.setProxyInterface(DataSource.class);
            bean.setLookupOnStartup(false);
            bean.afterPropertiesSet();
            return (DataSource) bean.getObject();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("Erro ao obter datasource da aplicação SCDApplication");
        }

    }

}
