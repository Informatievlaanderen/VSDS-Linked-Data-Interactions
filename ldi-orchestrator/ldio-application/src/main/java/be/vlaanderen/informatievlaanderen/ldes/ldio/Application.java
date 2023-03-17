package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.Arrays;

@SpringBootApplication
@EnableWebFlux
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
