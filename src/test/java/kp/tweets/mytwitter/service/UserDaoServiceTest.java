package kp.tweets.mytwitter.service;

import kp.tweets.mytwitter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDaoServiceTest {

    @Autowired
    UserDaoService userDao;

    @BeforeEach
    void clean() {
        userDao.init();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Kris", "Lidia", "Adam"})
    void registerUser(String userName) {
        assertEquals(userDao.registerUserIfAbsent(userName), new User(userName));
    }

    @Test
    void userNotRegistered() {
        assertTrue(userDao.userNotRegistered("Kris"));
    }

    @Test
    void addAndGetFollowedUsers() {
        String follower = "John";
        String userK = "Kris";
        String userL = "Lidia";
        String userA = "Adam";
        userDao.registerUserIfAbsent(follower);
        userDao.registerUserIfAbsent(userK);
        userDao.registerUserIfAbsent(userL);
        userDao.registerUserIfAbsent(userA);
        userDao.addFollowedUser(follower, userK);
        userDao.addFollowedUser(follower, userL);
        userDao.addFollowedUser(follower, userA);
        assertAll("",
                () -> assertEquals(userDao.getUsersFollowedBy(follower).size(), 3),
                () -> assertTrue(userDao.getUsersFollowedBy(follower).contains(new User(userK))),
                () -> assertTrue(userDao.getUsersFollowedBy(follower).contains(new User(userL))),
                () -> assertTrue(userDao.getUsersFollowedBy(follower).contains(new User(userA)))
        );
    }
}