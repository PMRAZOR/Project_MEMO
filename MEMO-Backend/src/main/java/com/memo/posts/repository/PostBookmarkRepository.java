package com.memo.posts.repository;

import com.memo.entity.PostBookmark;
import com.memo.posts.dto.PostBookmarkRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostBookmarkRepository {
    void save(PostBookmark bookmark);
    void delete(PostBookmark request);

    List<Integer> findPostIdsByUserId(int userId);
    List<PostBookmarkRequest> getBookmarkedLists(int userId);
}
