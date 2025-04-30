package edu.tcu.cs.backend.user.converter;

import edu.tcu.cs.backend.user.User;
import edu.tcu.cs.backend.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter {

    public UserDto convert(User user) {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getPosition()
                );

    }
}
