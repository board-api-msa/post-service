package me.junbyoung.PostService.payload;

import lombok.Getter;
import me.junbyoung.PostService.model.Comment;
import me.junbyoung.PostService.model.Post;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponse {
    private final Long id;
    private final String userName;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<Comment> comments;

    public PostResponse(Post post,String userName) {
        this(post,userName,null);
    }

    public PostResponse(Post post,String userName,List<Comment> comments) {
        this.id = post.getId();
        this.userName = userName;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.comments = comments;
    }
}
