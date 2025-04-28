package edu.tcu.cs.backend.system.exception;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String objectName, int id){
        super("Could not find " + objectName + " with id " + id);
    }
}
