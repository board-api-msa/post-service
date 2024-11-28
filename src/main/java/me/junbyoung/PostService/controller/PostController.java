package me.junbyoung.PostService.controller;

import jakarta.validation.Valid;
import me.junbyoung.PostService.model.Comment;
import me.junbyoung.PostService.model.Post;
import me.junbyoung.PostService.model.User;
import me.junbyoung.PostService.payload.PostRequest;
import me.junbyoung.PostService.payload.PostResponse;
import me.junbyoung.PostService.service.CommentService;
import me.junbyoung.PostService.service.PostService;
import me.junbyoung.PostService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/posts") // 공통 경로 지정
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) throws ExecutionException, InterruptedException {
        Post post = postService.getPost(postId);
        //다른 서비스가 비활성화일때, 오류 전파를 막기위해 비동기실행
        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getUserInfoByUserId(post.getUserId()));
        User user = userFuture.get();
        CompletableFuture<List<Comment>> commentsFuture = CompletableFuture.supplyAsync(() -> commentService.getComments(postId));
        List<Comment> comments = commentsFuture.get();

        return ResponseEntity.ok(new PostResponse(post, user.getName(), comments));
    }


    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = new ArrayList<>();
        for (Post post : postService.getAllPosts()) {
            String userName = userService.getUserInfoByUserId(post.getUserId()).getName();
            posts.add(new PostResponse(post, userName));
        }
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
