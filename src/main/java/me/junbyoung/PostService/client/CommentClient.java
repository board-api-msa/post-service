package me.junbyoung.PostService.client;

import me.junbyoung.PostService.client.fallback.CommentClientFallback;
import me.junbyoung.PostService.model.Comment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "comment-service", fallback = CommentClientFallback.class)
public interface CommentClient {
    @GetMapping("/api/posts/{postId}/comments")
    List<Comment> getComments(@PathVariable Long postId);
}

