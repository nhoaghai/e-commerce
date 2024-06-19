package com.ecommerce.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI defineOpenApi() {

        Contact myContact = new Contact();
        myContact.setName("DataDynamics");
        myContact.setEmail("datadynamics@datdyn.com");


        Info information = new Info()
                .title("Control Panel System APIs")
                .version("1.0")
                .description("This API exposes endpoints to manage users,roles,active directories,data engine resources,databases etc.")
                .contact(myContact);
        return new OpenAPI().info(information);
    }


}
