package unito.taass.blackcoffeenotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
		"unito.taass.blackcoffeenotification",
		"unito.taass.blackcoffeeinfrastructure"
})
public class BlackCoffeeNotificationApplication {

	public static void main(String[] args) {

		SpringApplication.run(BlackCoffeeNotificationApplication.class, args);
	}

}
