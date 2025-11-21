package ru.CheSeVe.lutiy_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_player_seq")
    @SequenceGenerator(name = "match_player_seq", sequenceName = "match_player_seq")
    Long id;

    Short heroId;

    Boolean isVictory;

    @ManyToOne
    @JoinColumn(name = "match_id")
    Match match;

    @OneToMany(mappedBy = "matchPlayer", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PlayerItem> items = new ArrayList<>();

    public MatchPlayer(Short heroId, Boolean isVictory, Match match) {
        this.heroId = heroId;
        this.isVictory = isVictory;
        this.match = match;
    }
}
