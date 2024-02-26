package am.ik.retrofacto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RetrofactoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetrofactoApplication.class, args);
	}

}
