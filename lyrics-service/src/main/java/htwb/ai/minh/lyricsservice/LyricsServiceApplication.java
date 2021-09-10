package htwb.ai.minh.lyricsservice;

import htwb.ai.minh.lyricsservice.entity.Lyric;
import htwb.ai.minh.lyricsservice.repository.LyricsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class LyricsServiceApplication {

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(LyricsServiceApplication.class, args);
	}

	// just for testing
/*	@Bean
	CommandLineRunner runner(LyricsRepository repository) {
		return args -> {
			Lyric lyric = new Lyric(1, "TestSong", "Lal la lalala");
			Lyric lyric2 = new Lyric(2, "TestSong2", "Lal la lalala2222");
			repository.saveAll(List.of(lyric, lyric2));
		};
	}*/

}
