package htwb.ai.minh.songsservice.config;

import htwb.ai.minh.songsservice.entity.Song;
import htwb.ai.minh.songsservice.repository.SongRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SongConfig {
    @Bean
    CommandLineRunner commandLineRunner(SongRepository repository) {
        return args -> {
            Song s1 = new Song(1,"MacArthur Park", "Richard Harris", "Dunhill Records", 1968);
            Song s2 = new Song(2, "Afternoon Delight", "Starland Vocal Band", "Windsongs", 1976);
            Song s3 = new Song(3, "Muskrat Love", "Captain and Tennille", "A&M", 1976);
            Song s4 = new Song(4, "Sussudio", "Phil Collins", "Virgin", 1985);
            Song s5 = new Song(5, "We Built This City", "Starship", "Grunt/RCA", 1985);
            Song s6 = new Song(6, "Achy Breaky Heart", "Billy Ray Cyrus", "PolyGram Mercury", 1992);
            Song s7 = new Song(7, "What´s up", "4 Non Blondes", "Interscope", 1993);
            Song s8 = new Song(8,"Who Let the Dogs Out?", "Baha Men", "S-Curve", 2000);
            Song s9 = new Song(9,"My Humps", "Black Eyes Peas", "Universal Music", 2005);
            Song s10 = new Song(10,"Chinese Food", "Alison Gold", "PMW Live", 2013);

            repository.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10));
        };
    }
}
