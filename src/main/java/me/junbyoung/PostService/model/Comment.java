package me.junbyoung.PostService.model;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class Comment {
    private Long id;
    private Long postId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
