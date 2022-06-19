package ru.yandex.practicum.exceptions;

public class InvalidNameException extends Exception{
    public InvalidNameException(String massage){
        super(massage);
    }
}
