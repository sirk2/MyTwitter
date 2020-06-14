package kp.tweets.mytwitter.service;

import kp.tweets.mytwitter.model.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class UserDaoService {

    private ConcurrentSkipListMap<User, Set<User>> users;

    public UserDaoService() {
        init();
    }

    public void init() {
        users = new ConcurrentSkipListMap<>(Comparator.comparing(User::getName));
    }

    public User registerUserIfAbsent(String userName) {
        User newUser = new User(userName);
        users.putIfAbsent(newUser, new ConcurrentSkipListSet<>(Comparator.comparing(User::getName)));
        return users.ceilingKey(newUser);
    }

    public boolean userNotRegistered(String userName) {
        return !users.containsKey(new User(userName));
    }

    public Set<User> getUsersFollowedBy(String userName) {
        return users.get(new User(userName));
    }

    public String addFollowedUser(String followerName, String followedName) {
        Set<User> followedUsers = getUsersFollowedBy(followerName);
        boolean isNewUser = followedUsers.add(users.ceilingKey(new User(followedName)));
        return (isNewUser ? "Started to follow '" + followedName + "'" : "'" + followedName + "' is already followed by you");
    }
}
