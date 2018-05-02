package jp.aha.oretama.typoChecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TypoFixerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypoFixerAppApplication.class, args);
	}
}
