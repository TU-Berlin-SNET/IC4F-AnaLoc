package de.tuberlin.snet.AnaLoc.CoreServices.BatchService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executors;

/**
 * Description:	The type Async config.
 * 		used to process asynchronous request
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	/**
	 * Web mvc configurer web mvc configurer.
	 *
	 * @return the web mvc configurer
	 */
	@Bean
	protected WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
				configurer.setTaskExecutor(getTaskExecutor());
			}

		};
	}

	/**
	 * Gets task executor.
	 *
	 * @return the task executor
	 */
	@Bean
	protected ConcurrentTaskExecutor getTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(5));
	}
}