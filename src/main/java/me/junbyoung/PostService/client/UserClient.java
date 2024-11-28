package me.junbyoung.PostService.client;

import me.junbyoung.PostService.client.fallback.UserClientFallback;
import me.junbyoung.PostService.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping(value = "/api/users/{userId}",headers = "User-Agent=FeignClient")
    User getUserInfoByUserId(@PathVariable Long userId);
}
