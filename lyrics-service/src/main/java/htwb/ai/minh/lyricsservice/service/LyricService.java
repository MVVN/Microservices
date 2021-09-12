package htwb.ai.minh.lyricsservice.service;

import htwb.ai.minh.lyricsservice.entity.Lyric;
import htwb.ai.minh.lyricsservice.repository.LyricsRepository;
import htwb.ai.minh.lyricsservice.util.JWTdecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LyricService {
    private LyricsRepository lyricsRepository;

    @Autowired
    public LyricService(LyricsRepository lyricsRepository) {
        this.lyricsRepository = lyricsRepository;
    }

    public List<Lyric> getAllLyrics() {
        return lyricsRepository.findAll();
    }

    public Optional<Lyric> getLyricFromSong(String songTitle) {
        return lyricsRepository.findLyricBySongTitle(songTitle);
    }

    public Lyric postLyric(Lyric lyric) {
        return lyricsRepository.save(lyric);
    }

    public void updateLyric(Lyric lyric) {
        lyricsRepository.save(lyric);
    }

    public void deleteLyric(String lyricId) {
        lyricsRepository.deleteById(lyricId);
    }
}
