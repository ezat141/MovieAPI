package com.movie.movieapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class FileExistsException extends RuntimeException {
    public FileExistsException(String message) {
        super(message);
    }

}
