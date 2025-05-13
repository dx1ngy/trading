package com.dx1ngy.trading.quotation;

import cn.dev33.satoken.interceptor.SaInterceptor;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(info = @Info(title = "My API", version = "v1.0"))
@SecurityScheme(name = "token", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@Configuration
public class TradingQuotationConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public OperationCustomizer consumerOperationCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponse apiResponse = operation.getResponses().get("200");
            Content content = apiResponse.getContent();
            if (content != null) {
                content.forEach((mediaTypeKey, mediaType) -> {
                    Schema<?> schema = new Schema<>();
                    schema.addProperty("code", new IntegerSchema());
                    schema.addProperty("message", new StringSchema());
                    schema.addProperty("data", mediaType.getSchema());
                    mediaType.schema(schema);
                });
            } else {
                Content newContent = new Content();
                Schema<?> schema = new Schema<>();
                schema.addProperty("code", new IntegerSchema());
                schema.addProperty("message", new StringSchema());
                newContent.put("*/*", new MediaType().schema(schema));
                apiResponse.setContent(newContent);
            }
            return operation;
        };
    }

}
