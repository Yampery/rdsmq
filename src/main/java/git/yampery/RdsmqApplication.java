package git.yampery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RdsmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdsmqApplication.class, args);
	}
}
