package edu.tcu.cs.backend.user.converter;

import edu.tcu.cs.backend.user.User;
import edu.tcu.cs.backend.user.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, User> {

    @Override
    public User convert(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setRole(userDto.role());
        user.setPosition(userDto.position());
        return user;
    }
}
