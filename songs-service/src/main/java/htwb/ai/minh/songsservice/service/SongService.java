package htwb.ai.minh.songsservice.service;

import htwb.ai.minh.songsservice.entity.Song;
import htwb.ai.minh.songsservice.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Optional<Song> getSongById(Integer id) {
        return songRepository.findById(id);
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song postSong(Song song) {
        return songRepository.save(song);
    }

    public void updateSong(Song song) {
        songRepository.save(song);
    }

    public void deleteSong(Integer id) {
        songRepository.deleteById(id);
    }
}
