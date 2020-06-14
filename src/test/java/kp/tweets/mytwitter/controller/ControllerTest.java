package kp.tweets.mytwitter.controller;

import kp.tweets.mytwitter.exception.IncorrectTweetLengthException;
import kp.tweets.mytwitter.exception.IncorrectUserNameException;
import kp.tweets.mytwitter.exception.SelfFollowerException;
import kp.tweets.mytwitter.exception.UserNotFoundException;
import kp.tweets.mytwitter.model.Tweet;
import kp.tweets.mytwitter.service.TweetDaoService;
import kp.tweets.mytwitter.service.UserDaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ControllerTest {

    @Autowired
    TweetDaoService tweetDao;

    @Autowired
    UserDaoService userDao;

    @Autowired
    Controller controller;

    @BeforeEach
    void clean() {
        userDao.init();
        tweetDao.init();
    }

    /**
     * Post one tweet and check its existence and attributes
     */
    @Test
    void postTweet() {
        String user = "Kris";
        String tweetText = "Hello World. This is my first tweet";
        controller.postTweet(user, tweetText);
        MappingJacksonValue tweetsMapping = controller.listUserTweets(user);
        List<Tweet> tweets = (List<Tweet>)tweetsMapping.getValue();
        assertAll("check existence and attributes of the first tweet",
                () -> assertEquals(1, tweets.size()),
                () -> assertEquals(tweets.get(0).getText(), tweetText),
                () -> assertNotNull(tweets.get(0).getTime())
        );
    }

    /**
     * Post many tweets and get list of them. Check if the list is sorted in reverse chronological order.
     *
     * @throws InterruptedException
     */
    @Test
    void postManyTweets() throws InterruptedException {
        String user = "Kris";
        String[] tweetTexts = new String[]{"aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg"};
        int tweetsCount = 100;
        for (int i = 0; i < tweetsCount; i++) {
            String tweetText = tweetTexts[i % tweetTexts.length];
            controller.postTweet(user, tweetText);
            Thread.sleep(1);
        }
        MappingJacksonValue tweetsMapping = controller.listUserTweets(user);
        List<Tweet> tweets = (List<Tweet>)tweetsMapping.getValue();
        LocalDateTime subsequent = null;
        for (int i = 0; i < tweetsCount; i++) {
            if (0 == i) {
                subsequent = tweets.get(i).getTime();
            } else {
                LocalDateTime current = tweets.get(i).getTime();
                assertTrue(subsequent.compareTo(current) > 0);
                subsequent = current;
            }
        }
    }

    /**
     * Post many tweets as various users and then make all these users followed by another one.
     * Get list of the tweets and check if it is sorted in reverse chronological order.
     *
     * @throws InterruptedException
     */
    @Test
    void listTweetsOfFollowedUsers() throws InterruptedException {
        String follower = "Kris";
        String[] followedUsers = new String[]{"Lidia", "Aneta", "Artur", "Adam", "Jarek"};
        String[] tweetTexts = new String[]{"aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg"};
        int tweetsCount = 100;
        for (int i = 0; i < tweetsCount; i++) {
            String tweetText = tweetTexts[i % tweetTexts.length];
            String user = followedUsers[i % followedUsers.length];
            controller.postTweet(user, tweetText);
            Thread.sleep(1);
        }
        controller.postTweet(follower, "I send this tweet only to be registered");
        for (String user : followedUsers) {
            controller.addFollowedUser(follower, user);
        }
        List<Tweet> tweets = controller.listFollowedUsersTweets(follower);
        LocalDateTime subsequent = null;
        for (int i = 0; i < tweetsCount; i++) {
            if (0 == i) {
                subsequent = tweets.get(i).getTime();
            } else {
                LocalDateTime current = tweets.get(i).getTime();
                assertTrue(subsequent.compareTo(current) > 0);
                subsequent = current;
            }
        }
    }

    /**
     * Post tweets with incorrect text length
     * @param tweetText  tweet text
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 1" })
    void throwsIncorrectTweetLengthException(String tweetText) {
        assertThrows(IncorrectTweetLengthException.class, () -> controller.postTweet("Kris", tweetText));
    }

    /**
     * Post tweets with incorrect user names
     * @param userName  name of the user posting tweet
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "1Kris", "John Smith", "a234567890123456789012345678901", "Lidia#"})
    void throwsIncorrectUserNameException(String userName) {
        assertThrows(IncorrectUserNameException.class, () -> controller.postTweet(userName, "test tweet"));
    }

    /**
     * User tries to follow himself
     */
    @Test
    void throwsSelfFollowerException() {
        String userName = "Kris";
        controller.postTweet(userName, "test tweet");
        assertThrows(SelfFollowerException.class, () -> controller.addFollowedUser(userName, userName));
    }

    /**
     * Non-registered user tries to follow another non-registered user
     */
    @Test
    void throwsUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> controller.addFollowedUser("Kris", "Lidia"));
    }

    /**
     * Check if email address is acceptable user name
     * @param userName  name of the user posting tweet
     */
    @ParameterizedTest
    @ValueSource(strings = {"john.smith@gmail.com", "john-smith@gmail.com", "john_smith@gmail.com"})
    void checkIfEmailAddresCanBeUserName(String userName) {
        assertDoesNotThrow(() -> controller.postTweet(userName, "test tweet"));
    }
}