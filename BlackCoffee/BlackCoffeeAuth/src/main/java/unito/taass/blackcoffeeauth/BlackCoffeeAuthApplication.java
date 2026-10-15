package unito.taass.blackcoffeeauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"unito.taass.blackcoffeeauth",
		"unito.taass.blackcoffeeinfrastructure"
})
public class BlackCoffeeAuthApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlackCoffeeAuthApplication.class, args);
	}

}
