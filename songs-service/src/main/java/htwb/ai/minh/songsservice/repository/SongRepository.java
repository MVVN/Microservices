package htwb.ai.minh.songsservice.repository;

import htwb.ai.minh.songsservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

}
