package htwb.ai.minh.songsservice.repository;

import htwb.ai.minh.songsservice.entity.SongList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongListRepository extends JpaRepository<SongList, Integer> {
    @Query(value = "SELECT s FROM SongList s WHERE s.owner = ?1")
    List<SongList> findAllByOwnerId(String userId);

    @Query(value = "SELECT * FROM songlist WHERE ownerid = ?1 AND private = FALSE", nativeQuery = true)
    List<SongList> findAllPublicForUser(String userId);
}
