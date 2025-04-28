package edu.tcu.cs.backend.user;

import java.util.List;

public interface UserService {
    User registerUser(User user);

    User login(String email, String password);

    void invite(Integer inviterId, String inviteeEmail);

    List<User> findAll();

    User findById(int crewMemberId);

    User save(User user);

    User update(User newUser, int crewMemberId);

    void delete(int crewMemberId);
}
