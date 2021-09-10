package htwb.ai.minh.lyricsservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Lyric")
public class Lyric {
    @Id
    private String id;
    private Integer songId;
    private String songTitle;
    private String songText;

    public Lyric(Integer songId, String songTitle, String songText) {
        this.songTitle = songTitle;
        this.songText = songText;
        this.songId = songId;
    }
}
