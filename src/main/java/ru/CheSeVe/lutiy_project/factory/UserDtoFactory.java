package ru.CheSeVe.lutiy_project.factory;

import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.dto.UserDto;
import ru.CheSeVe.lutiy_project.entity.User;

@Component
public class UserDtoFactory {
    public UserDto createUserDto(User user) {
        return UserDto.builder()
                .steamAccountId(user.getSteamAccountId())
                .username(user.getUsername())
                .password(user.getPassword())
                .rank(user.getRank())
                .created(user.getCreated()).build();
    }
}
