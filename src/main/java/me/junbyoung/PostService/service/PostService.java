package me.junbyoung.PostService.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import me.junbyoung.PostService.model.Post;
import me.junbyoung.PostService.payload.PostRequest;
import me.junbyoung.PostService.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

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

        postRepository.delete(post);
    }
}
