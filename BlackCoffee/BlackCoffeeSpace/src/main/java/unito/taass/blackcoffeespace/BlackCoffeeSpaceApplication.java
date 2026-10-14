package unito.taass.blackcoffeespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"unito.taass.blackcoffeespace",
		"unito.taass.blackcoffeeinfrastructure"
})
public class BlackCoffeeSpaceApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlackCoffeeSpaceApplication.class, args);
	}

}
