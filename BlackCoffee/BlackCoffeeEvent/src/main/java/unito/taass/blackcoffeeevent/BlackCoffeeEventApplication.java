package unito.taass.blackcoffeeevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"unito.taass.blackcoffeeevent",         // Package del servizio
		"unito.taass.blackcoffeeinfrastructure" // Package della libreria comune
})
public class BlackCoffeeEventApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlackCoffeeEventApplication.class, args);
	}

}
