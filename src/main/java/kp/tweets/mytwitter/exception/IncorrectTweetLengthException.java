package kp.tweets.mytwitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectTweetLengthException extends RuntimeException {
    public IncorrectTweetLengthException(String message) {
        super(message);
    }
}
