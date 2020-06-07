package indi.butterfly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@EnableJdbcRepositories
@SpringBootApplication
public class ButterflyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ButterflyApplication.class, args);
	}

}
