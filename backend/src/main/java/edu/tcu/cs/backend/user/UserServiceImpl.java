package edu.tcu.cs.backend.user;

import edu.tcu.cs.backend.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        return null;
    }

    @Override
    public User login(String email, String password) {
        return null;
    }

    @Override
    public void invite(Integer inviterId, String inviteeEmail) {

    }

    // Find crew members
    @Override
    public List<User> findAll(){
        return userRepository.findAll();
    }

    // Find crew member by ID
    @Override
    public User findById(int crewMemberId){
        return this.userRepository.findById(crewMemberId)
                .orElseThrow(() -> new ObjectNotFoundException("Crew member", crewMemberId));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // Update a crew member
    @Override
    public User update(User newUser, int crewMemberId) {
        return this.userRepository.findById(crewMemberId)
                .map(oldCrewMember -> {
                    oldCrewMember.setFirstName(newUser.getFirstName());
                    oldCrewMember.setLastName(newUser.getLastName());
                    oldCrewMember.setEmail(newUser.getEmail());
                    oldCrewMember.setPassword(newUser.getPassword());
                    oldCrewMember.setPhoneNumber(newUser.getPhoneNumber());
                    oldCrewMember.setRole(newUser.getRole());
                    oldCrewMember.setPosition(newUser.getPosition());
                    return this.userRepository.save(oldCrewMember);
                })
                .orElseThrow(() -> new ObjectNotFoundException("Crew member", crewMemberId));
    }

    @Override
    public void delete(int crewMemberId) {
        this.userRepository.findById(crewMemberId)
                .orElseThrow(() -> new ObjectNotFoundException("Crew member", crewMemberId));
        this.userRepository.deleteById(crewMemberId);
    }
}
