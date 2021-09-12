package htwb.ai.minh.lyricsservice.controller;

import htwb.ai.minh.lyricsservice.entity.Lyric;
import htwb.ai.minh.lyricsservice.entity.Song;
import htwb.ai.minh.lyricsservice.service.LyricService;
import htwb.ai.minh.lyricsservice.util.JWTdecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/lyrics")
public class LyricController {
    private LyricService lyricService;
    private JWTdecoder jwtDecoder;
    private RestTemplate restTemplate;

    private final String songs_service_uri = "http://songs-service/songsMS/rest";
    private final char spaceChar = ' ';
    private final char underscoreChar = '_';

    @Autowired
    public LyricController(LyricService lyricService, JWTdecoder jwtDecoder, RestTemplate restTemplate) {
        this.lyricService = lyricService;
        this.jwtDecoder = jwtDecoder;
        this.restTemplate = restTemplate;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Lyric>> getAllLyrics(@RequestHeader("Authorization") String token) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Lyric> lyrics = lyricService.getAllLyrics();
        if (lyrics.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lyrics, HttpStatus.OK);
    }

    @GetMapping(path = "/{songTitle}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lyric> getLyricFromSong(@RequestHeader("Authorization") String token,
                                                  @PathVariable("songTitle") String songTitle) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String newTitle = songTitle.replace(underscoreChar, spaceChar);

        Optional<Lyric> lyric = lyricService.getLyricFromSong(newTitle);

        if (lyric.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lyric.get(), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postLyric(@RequestHeader("Authorization") String token,
                                            @RequestBody Lyric lyric) throws URISyntaxException {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (lyric.getSongTitle().isBlank() || lyric.getSongText().isBlank()) {
            return new ResponseEntity<>("SongTitle and SongText required.", HttpStatus.BAD_REQUEST);
        }

        Song song = null;
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.setBasicAuth(token);
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
//            song = restTemplate.getForObject(songs_service_uri + "/songs/byTitle/" + lyric.getSongTitle(), Song.class);

            ResponseEntity<Song> response = restTemplate.exchange(
                    songs_service_uri + "/songs/" + lyric.getSongId(),
                    HttpMethod.GET,
                    request,
                    Song.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
//                System.out.println(response.getBody());
                song = response.getBody();
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error on calling songs-service.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (song != null) {
            if (!song.getTitle().equals(lyric.getSongTitle())) {
                return new ResponseEntity<>("SongTitle doesn't match with Song in DB or Song not found.", HttpStatus.NOT_FOUND);
            }

            Optional<Lyric> existingLyric = lyricService.getLyricFromSong(lyric.getSongTitle());
            if (existingLyric.isPresent()) {
                return new ResponseEntity<>("Song already has lyrics.", HttpStatus.BAD_REQUEST);
            }

            Lyric newLyric;

            try {
                newLyric = lyricService.postLyric(lyric);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String newTitle = lyric.getSongTitle().replace(spaceChar, underscoreChar);

            URI uri = new URI(String.format("/songsMS/rest/lyrics/%s", newTitle));
            return ResponseEntity.created(uri).body(null);
        }
        return new ResponseEntity<>("Song not found.", HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "/{songTitle}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateLyric(@RequestHeader("Authorization") String token,
                                              @RequestBody Lyric lyric,
                                              @PathVariable("songTitle") String songTitle) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String songTitleWithSpace = songTitle.replace(underscoreChar, spaceChar);
        if (!songTitleWithSpace.equals(lyric.getSongTitle())) {
            return new ResponseEntity<>("SongTitle in Lyric and in Path are not equal.", HttpStatus.BAD_REQUEST);
        }
        if (lyric.getSongTitle().isBlank() || lyric.getSongText().isBlank()) {
            return new ResponseEntity<>("SongTitle and SongText required.", HttpStatus.BAD_REQUEST);
        }

        Optional<Lyric> oldLyric = lyricService.getLyricFromSong(lyric.getSongTitle());

        if (oldLyric.isEmpty()) {
            return new ResponseEntity<>("Song has no Lyrics yet.", HttpStatus.NOT_FOUND);
        }

        Song song = null;
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.setBasicAuth(token);
            headers.set("Authorization", token);
            HttpEntity request = new HttpEntity(headers);
//            song = restTemplate.getForObject(songs_service_uri + "/songs/byTitle/" + lyric.getSongTitle(), Song.class);

            ResponseEntity<Song> response = restTemplate.exchange(
                    songs_service_uri + "/songs/" + lyric.getSongId(),
                    HttpMethod.GET,
                    request,
                    Song.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
//                System.out.println(response.getBody());
                song = response.getBody();
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error on calling songs-service.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (song != null) {
            String lyricId = oldLyric.get().getId();
            lyric.setId(lyricId);
            lyricService.updateLyric(lyric);
            return new ResponseEntity<>("Lyric updated.", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Song doesn't exist anymore. Lyric can't be updated.", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(path = "/{songTitle}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteLyric(@RequestHeader("Authorization") String token,
                                              @PathVariable("songTitle") String songTitle) {
        if (!jwtDecoder.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String newTitle = songTitle.replace(underscoreChar, spaceChar);

        Optional<Lyric> lyric = lyricService.getLyricFromSong(newTitle);

        if (lyric.isEmpty()) {
            return new ResponseEntity<>("Lyric not found.", HttpStatus.NOT_FOUND);
        } else {
            lyricService.deleteLyric(lyric.get().getId());
            return new ResponseEntity<>("Song deleted successfully.", HttpStatus.NO_CONTENT);
        }
    }

}
