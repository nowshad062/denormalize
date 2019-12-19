package com.pvamu.den;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class BootMongoDBApp {

	public static void main(String[] args) {
		SpringApplication.run(BootMongoDBApp.class, args);
	}
	
	@Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pvamu.den.controller"))
                .paths(PathSelectors.any())
                .build();
    }
	
	private ApiInfo getApiInfo() {
        Contact contact = new Contact("Rajkumar", "", "rmurugesan@student.pvamu.edu");
        return new ApiInfoBuilder()
                .title("De-anonymization")
                .description("De-anonymization Api Definition")
                .version("0.0.1")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }
}