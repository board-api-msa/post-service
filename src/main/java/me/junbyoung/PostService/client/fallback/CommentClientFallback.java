package me.junbyoung.PostService.client.fallback;

import me.junbyoung.PostService.client.CommentClient;
import me.junbyoung.PostService.model.Comment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CommentClientFallback implements CommentClient {
    @Override
    public List<Comment> getComments(Long postId) {
        // Comment 서비스가 비활성화된 경우 빈 리스트 반환
        return Collections.emptyList();
    }
}
