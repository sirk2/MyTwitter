package kp.tweets.mytwitter.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(description = "MyTwitter tweet")
@JsonFilter("TweetsFilter")
public class Tweet {

    @ApiModelProperty(notes = "MyTwitter user")
    private User user;

    @ApiModelProperty(notes = "Tweet posting time")
    private LocalDateTime time;

    @ApiModelProperty(notes = "Tweet text. Cannot be empty and contain more than 140 characters")
    private String text;

    public Tweet(User user, LocalDateTime time, String text) {
        this.user = user;
        this.time = time;
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
