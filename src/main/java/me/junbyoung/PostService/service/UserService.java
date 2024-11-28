package me.junbyoung.PostService.service;

import me.junbyoung.PostService.client.UserClient;
import me.junbyoung.PostService.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserClient userClient;

    public User getUserInfoByUserId(Long userId) {
        return userClient.getUserInfoByUserId(userId);
    }
}
