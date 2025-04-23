package edu.tcu.cs.backend.crew;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(int id){
        super("Crew member with id " + id + " not found");
    }
}
