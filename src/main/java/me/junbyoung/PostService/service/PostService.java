package me.junbyoung.PostService.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import me.junbyoung.PostService.model.Post;
import me.junbyoung.PostService.payload.PostRequest;
import me.junbyoung.PostService.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    public Post createPost(Long userId, PostRequest postRequest) {
        return postRepository.save(new Post(userId, postRequest));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional
    public Post updatePost(Long userId, Long postId, PostRequest postRequest) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));

        if (!post.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this post.");
        }

        post.updatePost(postRequest.getTitle(), postRequest.getContent());
        return post;
    }

    public void deletePost(Long userId, Long postId) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));

        if (!post.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this post.");
        }
        sendMessageToCommentService(post);
    }

    @Transactional
    @KafkaListener(topics = "user-events", groupId = "post-service-group")
    public void deletePosts(Long userId, Acknowledgment acknowledgment) {
        List<Post> posts = postRepository.findByUserId(userId);
        for (Post post : posts) {
            sendMessageToCommentService(post);
        }
        // 모든 작업이 성공적으로 완료된 후 오프셋 커밋
        acknowledgment.acknowledge();
    }

    void sendMessageToCommentService(Post post) {
        kafkaTemplate.send("post-events", post.getId())
                .thenAccept(result -> {
                    //카프카서버에 메시지가 저장되었을때만 삭제
                    postRepository.delete(post);
                })
                .exceptionally(ex -> {
                    LOGGER.warn("Failed to send message: {}", ex.getMessage());
                    return null;
                });
    }
}
