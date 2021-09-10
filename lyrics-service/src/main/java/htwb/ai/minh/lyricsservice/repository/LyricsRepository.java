package htwb.ai.minh.lyricsservice.repository;

import htwb.ai.minh.lyricsservice.entity.Lyric;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LyricsRepository extends MongoRepository<Lyric, String> {

    Optional<Lyric> findLyricBySongTitle(String title);
}
