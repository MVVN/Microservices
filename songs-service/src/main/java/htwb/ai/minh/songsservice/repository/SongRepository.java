package htwb.ai.minh.songsservice.repository;

import htwb.ai.minh.songsservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

    @Query(value = "SELECT s FROM Song s WHERE s.title = ?1")
    Optional<Song> findByTitle(String songTitle);
}
