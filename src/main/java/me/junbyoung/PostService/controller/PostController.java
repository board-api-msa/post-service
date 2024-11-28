package me.junbyoung.PostService.controller;

import jakarta.validation.Valid;
import me.junbyoung.PostService.model.Post;
import me.junbyoung.PostService.payload.PostRequest;
import me.junbyoung.PostService.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/posts") // 공통 경로 지정
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) throws ExecutionException, InterruptedException {
        Post post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Post> addPost(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody PostRequest postRequest) {
        Post post = postService.createPost(userId, postRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/posts/{postId}")
                .buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long postId,
                                           @Valid @RequestBody PostRequest postRequest) throws AccessDeniedException {
        Post post = postService.updatePost(userId, postId, postRequest);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long postId) throws AccessDeniedException {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
