package ru.CheSeVe.lutiy_project.service.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.dto.api.MatchDTO;
import ru.CheSeVe.lutiy_project.entity.Match;
import ru.CheSeVe.lutiy_project.entity.MatchPlayer;
import ru.CheSeVe.lutiy_project.entity.PlayerItem;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;

import java.time.Instant;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MatchMapper {

    public Match map(MatchDTO matchDTO) {
        if (matchDTO == null) {
            throw new BadRequestException("matchDTO is null");
        }
        Match match = new Match();

        match.setMatchId(matchDTO.id());
        match.setStartDateTime(Instant.ofEpochSecond(matchDTO.startDateTime()));

        matchDTO.players().forEach(player -> {

            MatchPlayer matchPlayer = new MatchPlayer(player.heroId(), player.isVictory(), match);

            Short[] itemIds = {
                    player.item0Id(),
                    player.item1Id(),
                    player.item2Id(),
                    player.item3Id(),
                    player.item4Id(),
                    player.item5Id(),
                    player.backpack0Id(),
                    player.backpack1Id(),
                    player.backpack2Id(),
                    player.neutral0Id() };

            for (Short itemId : itemIds) {
                if (itemId != null) {
                    matchPlayer.getItems().add(new PlayerItem(itemId, matchPlayer));
                }
            }
            match.getMatchPlayers().add(matchPlayer);
        });
        return match;
    }
}
