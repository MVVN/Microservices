package htwb.ai.minh.songsservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "songlist", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ownerid")
    private String owner;

    @Column(name = "name")
    private String name;

    @Column(name = "private")
    private boolean isPrivate;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "song_songlist",
            schema = "public",
            joinColumns = {@JoinColumn(name = "songlistid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "songid", referencedColumnName = "id")}
    )
    private List<Song> songList;
}
