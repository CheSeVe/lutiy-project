package ru.CheSeVe.lutiy_project.dto;

import lombok.*;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @NonNull
    Long steamAccountId;

    @NonNull
    String username;

    @NonNull
    String password;

    String rank;

    Instant created;
}
