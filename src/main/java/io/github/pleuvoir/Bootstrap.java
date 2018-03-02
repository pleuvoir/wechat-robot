package io.github.pleuvoir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.pleuvoir.manager.RobotManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Bootstrap implements CommandLineRunner {

	@Autowired
	private RobotManager robotManager;

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(Bootstrap.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.run(args);
	}

	@Override
	public void run(String... args) {
		try {
			robotManager.start();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}