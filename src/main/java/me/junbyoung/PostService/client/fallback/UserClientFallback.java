package me.junbyoung.PostService.client.fallback;

import me.junbyoung.PostService.client.UserClient;
import me.junbyoung.PostService.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public User getUserInfoByUserId(Long userId) {
        return new User();
    }
}
