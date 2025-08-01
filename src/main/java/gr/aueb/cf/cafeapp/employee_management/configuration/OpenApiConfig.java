package gr.aueb.cf.cafeapp.employee_management.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cafe Employee Management API",
                version = "1.0.0",
                description = "REST API για διαχείριση χρηστών, υπαλλλήλων και εργασιών σε καφετέρια",
                contact = @Contact(
                        name = "Dev: Nikolaos Michos",
                        email = "niqos89@gmail.com"
                )
        )
)
public class OpenApiConfig {
}
