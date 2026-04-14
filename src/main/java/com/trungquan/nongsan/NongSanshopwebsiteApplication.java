package com.trungquan.nongsan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition()
public class NongSanshopwebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(NongSanshopwebsiteApplication.class, args);
    }


}
