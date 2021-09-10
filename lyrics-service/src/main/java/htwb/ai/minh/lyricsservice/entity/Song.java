package htwb.ai.minh.lyricsservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    private Integer id;
    private String title;
    private String artist;
    private String label;
    private Integer released;

}
