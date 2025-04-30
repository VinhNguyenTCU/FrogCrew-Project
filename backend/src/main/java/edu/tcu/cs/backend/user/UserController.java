package edu.tcu.cs.backend.user;

import edu.tcu.cs.backend.user.converter.UserDtoToUserConverter;
import edu.tcu.cs.backend.user.converter.UserToUserDtoConverter;
import edu.tcu.cs.backend.user.dto.UserDto;
import edu.tcu.cs.backend.system.Result;
import edu.tcu.cs.backend.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crewMember")
public class UserController {
    private final UserService userService;

    private final UserToUserDtoConverter userToUserDtoConverter;

    private final UserDtoToUserConverter userDtoToUserConverter;

    public UserController(UserService userService, UserToUserDtoConverter userToUserDtoConverter, UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @GetMapping
    public Result getAllCrewMembers() {
        List<User> foundUsers = this.userService.findAll();
        // Convert foundUsers to a list of crewMemberDtos

        List<UserDto> foundCrewMembersDto = foundUsers.stream()
                .map(this.userToUserDtoConverter::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Find Success", foundCrewMembersDto);
    }

    @GetMapping("/{crewMemberId}")
    public Result getCrewMemberById(@PathVariable int crewMemberId) {
        User user = this.userService.findById(crewMemberId);
        UserDto userDto = this.userToUserDtoConverter.convert(user);
        return new Result(true, StatusCode.SUCCESS, "Find Success", userDto);
    }

    /*
    * 1. Convert userDto to crewMember
    * - You receive the data from the frontend as a DTO.
    * - But you can't save a DTO to the database - only an Entity
    * - So you convert it into the Artifact entity to persist it using JPA.
    *
    * 2. After convert, we can save the converted DTO to our database
    * 3. Once the entity is saved, you might want to send back the saved data (including its generated ID).
    * - You convert back to a DTO so the frontend receives a clean, safe, API-shaped response.
    * */
    @PostMapping
    public Result addCrewMember(@RequestBody @Valid UserDto userDto) {
        // Convert userDto to user
        User user = userDtoToUserConverter.convert(userDto);
        User savedUser = this.userService.save(user);
        UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    @PutMapping("/{crewMemberId}")
    public Result updateCrewMember(@PathVariable int crewMemberId,@RequestBody @Valid UserDto userDto) {
        User user = this.userDtoToUserConverter.convert(userDto);
        User updatedUser = this.userService.update(user, crewMemberId);
        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{crewMemberId}")
    public Result deleteCrewMember(@PathVariable int crewMemberId) {
        this.userService.delete(crewMemberId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
