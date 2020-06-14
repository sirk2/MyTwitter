package kp.tweets.mytwitter.service;

import kp.tweets.mytwitter.model.Tweet;
import kp.tweets.mytwitter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TweetDaoService {

    private AtomicLong sorter;
    private ConcurrentSkipListMap<Long, Tweet> tweets;

    @Autowired
    private UserDaoService userService;

    public TweetDaoService() {
        init();
    }

    public void init() {
        sorter = new AtomicLong(Long.MAX_VALUE);
        tweets = new ConcurrentSkipListMap<>();
    }

    public void postTweet(String userName, String tweetText) {
        User tweetingUser = userService.registerUserIfAbsent(userName);
        tweets.putIfAbsent(sorter.getAndDecrement(), new Tweet(tweetingUser, LocalDateTime.now(), tweetText));
    }

    public List<Tweet> listUserTweets(String userName) {
        User follower = new User(userName);
        return tweets.values().stream().filter(v -> v.getUser().equals(follower)).collect(Collectors.toList());
    }

    public List<Tweet> listFollowedUsersTweets(String followerName) {
        Set<User> followedUsers = userService.getUsersFollowedBy(followerName);
        return tweets.values().stream().filter(v -> followedUsers.contains(v.getUser())).collect(Collectors.toList());
    }
}
