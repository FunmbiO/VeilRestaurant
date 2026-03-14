package nbcc.resto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "nbcc.resto",
        "nbcc.auth",
        "nbcc.common"
})
@EntityScan(basePackages = {
        "nbcc.resto.persistence",
        "nbcc.auth.entity"
})
@EnableJpaRepositories(basePackages = {
        "nbcc.resto.persistence",
        "nbcc.auth.repository"
})
public class RestaurantApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
}
