package kp.tweets.mytwitter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mytwitter")
public class MyTwitterConfig {

    private String userNamePattern;
    private int tweetMaxLength;

    public String getUserNamePattern() {
        return userNamePattern;
    }

    public void setUserNamePattern(String userNamePattern) {
        this.userNamePattern = userNamePattern;
    }

    public int getTweetMaxLength() {
        return tweetMaxLength;
    }

    public void setTweetMaxLength(int tweetMaxLength) {
        this.tweetMaxLength = tweetMaxLength;
    }
}
