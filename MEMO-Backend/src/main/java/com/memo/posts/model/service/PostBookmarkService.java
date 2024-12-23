package com.memo.posts.model.service;

import com.memo.posts.dto.PostBookmarkRequest;
import com.memo.posts.dto.TimelineResponse;

import java.util.List;

public interface PostBookmarkService {
    void addBookmark(PostBookmarkRequest request);

    void removeBookmark(PostBookmarkRequest request);

    List<TimelineResponse> getBookmarkedPosts(int userId);

    List<PostBookmarkRequest> getBookmarkedLists(int userId);
}
