package kp.tweets.mytwitter.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.ApiOperation;
import kp.tweets.mytwitter.configuration.MyTwitterConfig;
import kp.tweets.mytwitter.exception.IncorrectTweetLengthException;
import kp.tweets.mytwitter.exception.IncorrectUserNameException;
import kp.tweets.mytwitter.exception.SelfFollowerException;
import kp.tweets.mytwitter.exception.UserNotFoundException;
import kp.tweets.mytwitter.model.Tweet;
import kp.tweets.mytwitter.service.TweetDaoService;
import kp.tweets.mytwitter.service.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private TweetDaoService tweetDao;

    @Autowired
    private UserDaoService userDao;

    @Autowired
    private MyTwitterConfig myTwitterConfig;

    /**
     * Lists tweets posted by specified user
     *
     * @param userName tweets' author
     * @return list of this user's tweets
     */
    @GetMapping("/user-tweets/{userName}")
    @ApiOperation("Lists all tweets posted by user whose name is operation parameter. Intended for listing current user tweets. The user must already be registered in MyTwitter.")
    public MappingJacksonValue listUserTweets(@PathVariable String userName) {
        if (userDao.userNotRegistered(userName)) {
            throw new UserNotFoundException("User '" + userName + "' is not registered");
        }
        List<Tweet> tweetsList = tweetDao.listUserTweets(userName);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("time", "text");
        FilterProvider filters = new SimpleFilterProvider().addFilter("TweetsFilter", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(tweetsList);
        mapping.setFilters(filters);
        return mapping;
    }

    /**
     * Posts a tweet
     *
     * @param userName  name of the user posting tweet
     * @param tweetText tweet text
     * @return list of this user's tweets
     */
    @PostMapping("/post-tweet/{userName}")
    @ApiOperation("Posts current user's tweet. User's name is the parameter. Posting first tweet registers user in MyTwitter.")
    public MappingJacksonValue postTweet(@PathVariable String userName, @RequestBody String tweetText) throws IncorrectTweetLengthException {
        userName = userName.trim();
        if (!userName.matches(myTwitterConfig.getUserNamePattern())) {
            throw new IncorrectUserNameException("Incorrect user name: " + userName);
        }
        tweetText = tweetText.trim();
        int lentgth = tweetText.length();
        int maxLength = myTwitterConfig.getTweetMaxLength();
        if (0 == lentgth || lentgth > maxLength) {
            throw new IncorrectTweetLengthException("Tweet text length can be neither 0 nor greater than " + maxLength);
        }
        tweetDao.postTweet(userName, tweetText);
        return listUserTweets(userName);
    }

    /**
     * Adds new followed user
     *
     * @param followerName name of the user who adds new user to his/her list of followed users
     * @param followedName name of the user added as new followed user
     * @return message informing if user was added or is already contained in the list
     */
    @PostMapping("/follow/{followerName}/{followedName}")
    @ApiOperation("Makes one user follower of another. Parameters: 1) follower name, 2) followed name. Both users must already be registered in MyTwitter")
    public String addFollowedUser(@PathVariable String followerName, @PathVariable String followedName) {
        if (userDao.userNotRegistered(followerName)) {
            throw new UserNotFoundException("Follower '" + followerName + "' is not registered");
        }
        if (userDao.userNotRegistered(followedName)) {
            throw new UserNotFoundException("Followed user '" + followedName + "' is not registered");
        }
        if (followerName.equals(followedName)) {
            throw new SelfFollowerException("User '" + followedName + "' tries to follow herself/himself");
        }
        return userDao.addFollowedUser(followerName, followedName);
    }

    /**
     * Lists all tweets of users followed by specific user
     *
     * @param followerName follower name
     * @return List of all tweets of users followed by specific user
     */
    @GetMapping("/followed-tweets/{followerName}")
    @ApiOperation("Lists all tweets of all users followed by user whose name is in the parameter. Intended to list tweets posted by all followed by current user. The user must already be registered in MyTwitter.")
    public List<Tweet> listFollowedUsersTweets(@PathVariable String followerName) {
        if (userDao.userNotRegistered(followerName)) {
            throw new UserNotFoundException("Follower '" + followerName + "' is not registered");
        }
        return tweetDao.listFollowedUsersTweets(followerName);
    }
}
