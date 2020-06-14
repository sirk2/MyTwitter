package kp.tweets.mytwitter.service;

import kp.tweets.mytwitter.model.Tweet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TweetDaoServiceTest {

    @Autowired
    TweetDaoService tweetDao;

    @Autowired
    UserDaoService userDao;

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
        tweetDao.postTweet(user, tweetText);
        List<Tweet> tweets = tweetDao.listUserTweets(user);
        assertAll("check existence and attributes of the first tweet",
                () -> assertEquals(1, tweets.size()),
                () -> assertEquals(tweets.get(0).getText(), tweetText),
                () -> assertEquals(tweets.get(0).getUser().getName(), user),
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
            tweetDao.postTweet(user, tweetText);
            Thread.sleep(1);
        }
        List<Tweet> tweets = tweetDao.listUserTweets(user);
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
            tweetDao.postTweet(user, tweetText);
            Thread.sleep(1);
        }
        tweetDao.postTweet(follower, "I send this tweet only to be registered");
        for (String user : followedUsers) {
            userDao.addFollowedUser(follower, user);
        }
        List<Tweet> tweets = tweetDao.listFollowedUsersTweets(follower);
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
}