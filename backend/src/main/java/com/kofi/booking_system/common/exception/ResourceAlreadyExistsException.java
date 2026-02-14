package com.kofi.booking_system.common.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
    public ResourceAlreadyExistsException(String message){
        super(message);
    }
}
