package kp.tweets.mytwitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectUserNameException extends RuntimeException {
    public IncorrectUserNameException(String message) {
        super(message);
    }
}
