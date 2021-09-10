package htwb.ai.minh.songsservice.controller;

import htwb.ai.minh.songsservice.entity.Song;
import htwb.ai.minh.songsservice.service.SongService;
import htwb.ai.minh.songsservice.util.JWTdecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/songs")
public class SongController {
    private SongService songService;
    private JWTdecoder jwtDecoder;

    @Autowired
    public SongController(SongService songService, JWTdecoder jwtDecoder) {
        this.songService = songService;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Song>> getAllSongs(@RequestHeader("Authorization") String token) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Song> songs = songService.getAllSongs();
        if (songs != null)
            return new ResponseEntity<>(songs, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Song> getSong(@RequestHeader("Authorization") String token,
                                        @PathVariable(value = "id") Integer id) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (id < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Song> song = songService.getSongById(id);
        if (song.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(song.get(), HttpStatus.OK);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postSong(@RequestHeader("Authorization") String token,
                                           @RequestBody Song song) throws URISyntaxException {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>("Unauthorized.", HttpStatus.UNAUTHORIZED);
        }
        if (song.getTitle().isBlank()) {
            return new ResponseEntity<>("Song needs a title.", HttpStatus.BAD_REQUEST);
        }

        Song newSong;
        try {
            newSong = songService.postSong(song);
        } catch (PersistenceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        URI uri = new URI(String.format("/songsMS/rest/songs/%d", newSong.getId()));
        return ResponseEntity.created(uri).body(null);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@RequestHeader("Authorization") String token,
                                             @RequestBody @Valid Song song,
                                             @PathVariable(value = "id") Integer id) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!id.equals(song.getId())) {
            return new ResponseEntity<>("ID mismatch.", HttpStatus.BAD_REQUEST);
        } else if (song.getTitle().isBlank()) {
            return new ResponseEntity<>("Song needs a title.", HttpStatus.BAD_REQUEST);
        } else if (songService.getSongById(id).isEmpty()) {
            return new ResponseEntity<>("No Song with requested ID.", HttpStatus.NOT_FOUND);
        }
        songService.updateSong(song);
        return new ResponseEntity<>("Song updated.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteSong(@RequestHeader("Authorization") String token,
                                             @PathVariable(value = "id") Integer id) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>("Unauthorized.", HttpStatus.UNAUTHORIZED);
        }
        if (id < 0) {
            return new ResponseEntity<>("ID must'nt be negative.", HttpStatus.UNAUTHORIZED);
        }
        Optional<Song> song = songService.getSongById(id);
        if (song.isEmpty()) {
            return new ResponseEntity<>("Song not found.", HttpStatus.NOT_FOUND);
        }
        songService.deleteSong(id);
        return new ResponseEntity<>("Song deleted.", HttpStatus.NO_CONTENT);
    }
}
