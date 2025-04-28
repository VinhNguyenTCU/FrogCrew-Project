package edu.tcu.cs.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
// @NotEmpty applies to Strings, collections, arrays, maps, etc. Value is not null and not empty (size > 0).
// @NotBlank applies to only Strings, Value is not null, not empty, and not just whitespace

import java.util.List;

public record UserDto(
        Integer user_id,
        @NotBlank(message = "First name is required.")
        String firstName,
        @NotBlank(message = "Last name is required.")
        String lastName,
        @Email(message = "Email must be valid.") // ensure that a given String is a valid email address format.
        String email,
        @NotBlank(message = "Phone number is required.")
        String phoneNumber,
        @NotBlank(message = "Password is required.")
        String password,
        @NotBlank(message = "Role is required.")
        String role,
        @NotEmpty(message = "Position is required.")
        List<String> position
) {
}
