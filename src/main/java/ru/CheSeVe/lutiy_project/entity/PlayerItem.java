package ru.CheSeVe.lutiy_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_item_seq")
    @SequenceGenerator(name = "player_item_seq", sequenceName = "player_item_seq")
    Long id;

    Short itemId;

    @ManyToOne
    @JoinColumn(name = "match_player_id")
    MatchPlayer matchPlayer;

    public PlayerItem(Short itemId, MatchPlayer matchPlayer) {
        this.itemId = itemId;
        this.matchPlayer = matchPlayer;
    }
}
