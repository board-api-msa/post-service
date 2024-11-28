package me.junbyoung.PostService.service;

import me.junbyoung.PostService.client.CommentClient;
import me.junbyoung.PostService.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentClient commentClient;

    public List<Comment> getComments(Long postId) {
        return commentClient.getComments(postId);
    }
}
