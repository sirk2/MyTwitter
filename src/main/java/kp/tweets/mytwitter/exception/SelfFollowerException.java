package kp.tweets.mytwitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SelfFollowerException extends RuntimeException {
    public SelfFollowerException(String message) {
        super(message);
    }
}
