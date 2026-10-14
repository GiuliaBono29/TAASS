package unito.taass.blackcoffeeclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"unito.taass.blackcoffeeclub",
		"unito.taass.blackcoffeeinfrastructure"
})
public class BlackCoffeeClubApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlackCoffeeClubApplication.class, args);
	}

}
