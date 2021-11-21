package com.arui.mall.core.seckill.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                // api文档分组名
                .groupName("seckillApi")
                .apiInfo(adminApiInfo())
                .select()
                // 正则匹配请求路径admin开头的
                .paths(Predicates.and(PathSelectors.regex("/seckill/.*")))
                .build();
    }

    /**
     * api文档元信息
     * @return
     */
    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("电商秒杀系统-API文档")
                .description("本文档描述了电商秒杀系统接口")
                .version("1.0")
                .contact(new Contact("arui", "https://arui.com", "arui@arui.com"))
                .build();
    }


    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                // api文档分组名
                .groupName("webProductApi")
                .apiInfo(adminApiInfo())
                .select()
                // 正则匹配请求路径admin开头的
                .paths(Predicates.and(PathSelectors.regex("/web/.*")))
                .build();
    }

    /**
     * api文档元信息
     * @return
     */
    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("电商mall-web系统-API文档")
                .description("本文档描述了电商mall-web系统接口")
                .version("1.0")
                .contact(new Contact("arui", "https://arui.com", "arui@arui.com"))
                .build();
    }
}