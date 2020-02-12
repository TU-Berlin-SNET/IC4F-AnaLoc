package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.processor.DataPointProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableEurekaClient
public class AnalysisApplication {
	private static final Logger LOG = LoggerFactory.getLogger(AnalysisApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AnalysisApplication.class, args);
		ApplicationHome home = new ApplicationHome(AnalysisApplication.class);
		LOG.info("Started DataProcessor");
		DataPointProcessor processor = context.getBean(DataPointProcessor.class);
		try {
			processor.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
