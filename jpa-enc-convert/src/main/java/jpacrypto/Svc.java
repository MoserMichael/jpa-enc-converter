package jpacrypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("jpacrypto")
@EnableJpaRepositories("jpacrypto")
@EntityScan(basePackages="jpacrypto.db.entity")

public class Svc {

	public static void main(String[] args) {
		SpringApplication.run(Svc.class, args);
	}

}


