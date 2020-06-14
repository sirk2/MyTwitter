package kp.tweets.mytwitter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    private static final Contact DEFAULT_CONTACT = new Contact("Krzysztof Paczos", "https://www.linkedin.com/in/krzysztofpaczos/", "krzysztof.paczos@gmail.com");
    private static final ApiInfo MT_API_INFO = new ApiInfo(
            "MyTwitter API Documentation",
            "This is MyTwitter API Documentation",
            "0.0.1-SNAPSHOT",
            "",
            DEFAULT_CONTACT,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            new ArrayList()
    );
    private static final Set<String> COMSUMES = new HashSet<>(Arrays.asList("text/plain"));
    private static final Set<String> PRODUCES = new HashSet<>(Arrays.asList("application/json"));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(MT_API_INFO)
                .consumes(COMSUMES)
                .produces(PRODUCES)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
