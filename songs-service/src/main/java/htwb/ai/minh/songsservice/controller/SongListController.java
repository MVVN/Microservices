package htwb.ai.minh.songsservice.controller;

import htwb.ai.minh.songsservice.entity.Song;
import htwb.ai.minh.songsservice.entity.SongList;
import htwb.ai.minh.songsservice.service.SongListService;
import htwb.ai.minh.songsservice.service.SongService;
import htwb.ai.minh.songsservice.util.JWTdecoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/songlists")
@NoArgsConstructor
public class SongListController {
    @Autowired
    private SongListService songListService;
    @Autowired
    private SongService songService;
    @Autowired
    private JWTdecoder jwtDecoder;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SongList>> getAllSonglists(@RequestHeader("Authorization") String token,
                                                          @RequestParam(value = "userId") String userId) {
        Claims claims;
        try {
            claims = jwtDecoder.checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (userId.equals(claims.getId())) {
            List<SongList> songlists = songListService.getAllSonglistsByOwnerID(userId);
            if (songlists.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(songlists, HttpStatus.OK);
        } else {
            List<SongList> songlists = songListService.getAllPublicSonglistsForUser(userId);
            if (songlists.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(songlists, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongList> getSonglistById(@RequestHeader("Authorization") String token,
                                                    @PathVariable(value = "id") Integer listId) {
        Claims claims;
        try {
            claims = jwtDecoder.checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (listId < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<SongList> songListOptional = songListService.getSongListById(listId);
        if (songListOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (songListOptional.get().getOwner().equals(claims.getId())) {
            return new ResponseEntity<>(songListOptional.get(), HttpStatus.OK);
        } else {
            if (songListOptional.get().isPrivate())
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return new ResponseEntity<>(songListOptional.get(), HttpStatus.OK);
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> postSongList(@RequestHeader("Authorization") String token,
                                               @RequestBody @Valid SongList songlist) throws URISyntaxException {
        Claims claims;
        try {
            claims = jwtDecoder.checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new ResponseEntity<>("Unauthorized.", HttpStatus.UNAUTHORIZED);
        }

        if (songlist.getSongList().isEmpty()) {
            return new ResponseEntity<>("Songlist is empty.", HttpStatus.BAD_REQUEST);
        } else if (songlist.getName().isBlank() || songlist.getName() == null) {
            return new ResponseEntity<>("Songlist has no name.", HttpStatus.BAD_REQUEST);
        }

        List<Song> songsForNewList = songlist.getSongList();
        for (Song song : songsForNewList) {
            Optional<Song> optionalSong = songService.getSongById(song.getId());
            if (optionalSong.isEmpty()) {
                return new ResponseEntity<>("Song: " + song.getTitle() + " with ID: " + song.getId() + " not found.", HttpStatus.BAD_REQUEST);
            }
            Song songToAdd = optionalSong.get();
            if (!songToAdd.getId().equals(song.getId())
                    || !songToAdd.getTitle().equals(song.getTitle())
                    || !songToAdd.getArtist().equals(song.getArtist())
                    || !songToAdd.getLabel().equals(song.getLabel())
                    || !songToAdd.getReleased().equals(song.getReleased())) {
                return new ResponseEntity<>("Song: " + song.getTitle() + " with ID: " + song.getId() + " not found.", HttpStatus.BAD_REQUEST);
            }
        }
        songlist.setOwner(claims.getId());
        int newSongListId = songListService.addSongList(songlist);
        URI uri = new URI(String.format("/songsMS/rest/songlists/%d", newSongListId));
        return ResponseEntity.created(uri).body(null);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSongList(@RequestHeader("Authorization") String token,
                                                 @RequestBody @Valid SongList songlist,
                                                 @PathVariable("id") Integer id) {
        Claims claims;
        try {
            claims = jwtDecoder.checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!songlist.getId().equals(id)) {
            return new ResponseEntity<>("ID mismatch.", HttpStatus.BAD_REQUEST);
        }
        Optional<SongList> songListToUpdate = songListService.getSongListById(id);
        if (!songListToUpdate.get().getOwner().equals(claims.getId())) {
            return new ResponseEntity<>("You are not the owner of the SongList.", HttpStatus.FORBIDDEN);
        }
        if (songListToUpdate.isEmpty()) {
            return new ResponseEntity<>("SongList not found.", HttpStatus.NOT_FOUND);
        }
        for (Song song : songlist.getSongList()) {
            Optional<Song> optionalSong = songService.getSongById(song.getId());
            if (optionalSong.isEmpty()) {
                return new ResponseEntity<>("Song: " + song.getTitle() + " with ID: " + song.getId() + " not found.", HttpStatus.BAD_REQUEST);
            }
            Song songToUpdate = optionalSong.get();
            if (!songToUpdate.getId().equals(song.getId())
                    || !songToUpdate.getTitle().equals(song.getTitle())
                    || !songToUpdate.getArtist().equals(song.getArtist())
                    || !songToUpdate.getLabel().equals(song.getLabel())
                    || !songToUpdate.getReleased().equals(song.getReleased())) {
                return new ResponseEntity<>("Song: " + song.getTitle() + " with ID: " + song.getId() + " not found.", HttpStatus.BAD_REQUEST);
            }
        }
        songlist.setOwner(claims.getId());
        songListService.updateSongList(songlist);
        return new ResponseEntity<>("SongList updated.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteSongList(@RequestHeader("Authorization") String token,
                                                 @PathVariable("id") Integer songListId) {
        Claims claims;
        try {
            claims = jwtDecoder.checkToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (songListId < 0) {
            return new ResponseEntity<>("ID mustn't be negative.", HttpStatus.BAD_REQUEST);
        }

        Optional<SongList> songListToDelete = songListService.getSongListById(songListId);
        if (songListToDelete.isEmpty()) {
            return new ResponseEntity<>("SongList not found.", HttpStatus.NOT_FOUND);
        }
        if (songListToDelete.get().getOwner().equals(claims.getId())) {
            songListService.deleteSongList(songListId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Access denied.", HttpStatus.FORBIDDEN);
        }
    }
}
