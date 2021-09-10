package htwb.ai.minh.songsservice.service;

import htwb.ai.minh.songsservice.entity.SongList;
import htwb.ai.minh.songsservice.repository.SongListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongListService {
    private SongListRepository songListRepository;

    @Autowired
    public SongListService(SongListRepository songListRepository) {
        this.songListRepository = songListRepository;
    }

    public List<SongList> getAllSonglistsByOwnerID(String userId) {
        return songListRepository.findAllByOwnerId(userId);
    }

    public List<SongList> getAllPublicSonglistsForUser(String userId) {
       return songListRepository.findAllPublicForUser(userId);
    }

    public Optional<SongList> getSongListById(Integer listId) {
        return songListRepository.findById(listId);
    }

    public int addSongList(SongList songlist) {
        SongList newSongList = songListRepository.save(songlist);
        return newSongList.getId();
    }

    public void updateSongList(SongList songlist) {
        songListRepository.save(songlist);
    }

    public void deleteSongList(Integer songListId) {
        songListRepository.deleteById(songListId);
    }
}
