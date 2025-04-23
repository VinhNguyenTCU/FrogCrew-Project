package edu.tcu.cs.backend.crew;

import edu.tcu.cs.backend.crew.converter.UserDtoToUser;
import edu.tcu.cs.backend.crew.converter.UserToUserDto;
import edu.tcu.cs.backend.crew.dto.UserDto;
import edu.tcu.cs.backend.system.Result;
import edu.tcu.cs.backend.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crewMember")
public class UserController {
    private final UserService userService;

    private final UserToUserDto userToUserDto;

    private final UserDtoToUser userDtoToUser;

    public UserController(UserService userService, UserToUserDto userToUserDto, UserDtoToUser userDtoToUser) {
        this.userService = userService;
        this.userToUserDto = userToUserDto;
        this.userDtoToUser = userDtoToUser;
    }

    @GetMapping
    public Result getAllCrewMembers() {
        List<User> foundUsers = this.userService.findAll();
        // Convert foundUsers to a list of crewMemberDtos

        List<UserDto> foundCrewMembersDto = foundUsers.stream()
                .map(this.userToUserDto::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Find Success", foundCrewMembersDto);
    }

    @GetMapping("/{crewMemberId}")
    public Result getCrewMemberById(@PathVariable int crewMemberId) {
        User user = this.userService.findById(crewMemberId);
        UserDto userDto = this.userToUserDto.convert(user);
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
        User user = userDtoToUser.convert(userDto);
        User savedUser = this.userService.save(user);
        UserDto savedUserDto = this.userToUserDto.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    @PutMapping("/{crewMemberId}")
    public Result updateCrewMember(@PathVariable int crewMemberId,@RequestBody @Valid UserDto userDto) {
        User user = this.userDtoToUser.convert(userDto);
        User updatedUser = this.userService.update(user, crewMemberId);
        UserDto updatedUserDto = this.userToUserDto.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{crewMemberId}")
    public Result deleteCrewMember(@PathVariable int crewMemberId) {
        this.userService.delete(crewMemberId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
