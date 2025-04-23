package edu.tcu.cs.backend.crew.converter;

import edu.tcu.cs.backend.crew.User;
import edu.tcu.cs.backend.crew.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDto {

    public UserDto convert(User user) {
        return new UserDto(
                user.getId(),
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
