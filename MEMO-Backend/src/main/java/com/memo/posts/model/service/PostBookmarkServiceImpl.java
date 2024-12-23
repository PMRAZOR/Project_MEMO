package com.memo.posts.model.service;


import com.memo.entity.PostBookmark;
import com.memo.posts.dto.PostBookmarkRequest;
import com.memo.posts.dto.TimelineResponse;
import com.memo.posts.model.dto.Posts;
import com.memo.posts.repository.PostBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostBookmarkServiceImpl implements PostBookmarkService {
    private final PostBookmarkRepository bookmarkRepository;
    private final PostsService postsService;

    @Override
    @Transactional
    public void addBookmark(PostBookmarkRequest request) {
        PostBookmark bookmark = new PostBookmark();
        bookmark.setUserId(request.getUserId());
        bookmark.setPostId(request.getPostId());

        bookmarkRepository.save(bookmark);
    }

    @Override
    @Transactional
    public void removeBookmark(PostBookmarkRequest request) {
        PostBookmark bookmark = new PostBookmark();
        bookmark.setUserId(request.getUserId());
        bookmark.setPostId(request.getPostId());

        bookmarkRepository.delete(bookmark);
    }


    @Override
    public List<TimelineResponse> getBookmarkedPosts(int userId) {
        // 1. 북마크된 postId 목록 조회
        List<Integer> postIds = bookmarkRepository.findPostIdsByUserId(userId);

        // 2. postId 목록으로 TimelineResponse 생성
        return postIds.stream()
                .map(this::createTimelineResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostBookmarkRequest> getBookmarkedLists(int userId) {
        List<PostBookmarkRequest> lists = bookmarkRepository.getBookmarkedLists(userId);
        System.out.println("디버그용 : " + lists);
        return lists;
    }

    private TimelineResponse createTimelineResponse(int postId) {
        TimelineResponse response = new TimelineResponse();

        // Posts 정보 설정
        Posts post = postsService.selectById(postId);
        response.setPost(post);

        // 썸네일 설정
        setThumbnailUrl(response, postId);

        return response;
    }

    private void setThumbnailUrl(TimelineResponse response, int postId) {
        postsService.findFirstPhotoByPostId(postId)
                .ifPresent(photo -> {
                    String photoUrl = "/posts/uploads/" + photo.getFileName();
                    response.setThumbnailUrl(photoUrl);
                });
    }
}
