package eu.europa.ec.simpl.sdtoolingbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.data1.common.client.schemasync.SchemaSyncAdapterClient;
import eu.europa.ec.simpl.data1.common.service.schemasyncclient.SchemaSyncClientService;
import eu.europa.ec.simpl.data1.common.service.schemasyncclient.SchemaSyncClientServiceImpl;
import eu.europa.ec.simpl.data1.common.service.schemasyncrepo.SchemaSyncRepoService;
import eu.europa.ec.simpl.data1.common.service.schemasyncrepo.SchemaSyncRepoServiceImpl;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Profile("!test")
@Configuration
@ComponentScan(
        basePackages = {
            "eu.europa.ec.simpl.sdtoolingbe",
            "eu.europa.ec.simpl.data1.common.tier2.client.catalogueadapter",
            "eu.europa.ec.simpl.data1.common.tier2.client.federatedcatalogue",
            "eu.europa.ec.simpl.data1.common.client",
            "eu.europa.ec.simpl.data1.common.config.feign",
            "eu.europa.ec.simpl.data1.common.config.openapi",
            "eu.europa.ec.simpl.data1.common.config.webmvc",
            "eu.europa.ec.simpl.data1.common.controller.advice.root",
            "eu.europa.ec.simpl.data1.common.controller.status",
            "eu.europa.ec.simpl.data1.common.filter",
            "eu.europa.ec.simpl.data1.common.logging",
            "eu.europa.ec.simpl.data1.common.service.jwt",
            "eu.europa.ec.simpl.data1.common.properties",
            "eu.europa.ec.simpl.data1.common.adapter.connector.rest"
        })
@EnableWebMvc
@EnableFeignClients(basePackages = {"eu.europa.ec.simpl.sdtoolingbe", "eu.europa.ec.simpl.data1.common.client"})
public class ApplicationConfig {

    @Bean
    public SchemaSyncRepoService schemaSyncRepoService(ObjectMapper objectMapper) {
        return new SchemaSyncRepoServiceImpl(objectMapper);
    }

    @Bean
    public SchemaSyncClientService schemaSyncClientService(
            SchemaSyncRepoService schemaSyncRepoService, SchemaSyncAdapterClient schemaSyncAdapterClient) {
        return new SchemaSyncClientServiceImpl(schemaSyncRepoService, schemaSyncAdapterClient);
    }
}
