package com.memo.posts.controller;

import com.memo.posts.dto.PostBookmarkRequest;
import com.memo.posts.dto.TimelineResponse;
import com.memo.posts.model.service.PostBookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/bookmarks")
public class PostBookmarkController {
    private final PostBookmarkService bookmarkService;
    @PostMapping
    public void addBookmark(@Valid @RequestBody PostBookmarkRequest request) {
        bookmarkService.addBookmark(request);
    }

    @DeleteMapping
    public void removeBookmark(@Valid @RequestBody PostBookmarkRequest request) {
        bookmarkService.removeBookmark(request);
    }

    @GetMapping("/{userId}")
    public List<TimelineResponse> getBookmarkedPosts(@PathVariable int userId) {
        return bookmarkService.getBookmarkedPosts(userId);
    }

    @GetMapping("/list/{userId}")
    public List<PostBookmarkRequest> getBookmarkedPostIds(@PathVariable int userId) {
        return bookmarkService.getBookmarkedLists(userId);
    }
}
