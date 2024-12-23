package com.memo.posts.model.service;

import com.memo.model.dto.Visits;
import com.memo.photos.model.dto.Photos;
import com.memo.posts.model.dto.Posts;

import java.util.List;
import java.util.Optional;

public interface PostsService {
    public int insertPosts(Posts posts);
    public int insertVisits(Visits visits);
    public List<Posts> selectAll();
    public List<Posts> selectByKeyword(String keyword);
    public Posts selectById(int id);
    public List<Visits> selectVisitsByPostsId(int id);
    public int deleteVisits(int id);
    public int deletePosts(int id);
    public int updatePosts(Posts posts);
    public int updateVisits(Visits visits);
    public List<Integer> selectVisitId(int id);
    public int insertPhoto(Photos photo);
    public List<Posts> recentPosts(int limit);
    public Optional<Photos> findFirstPhotoByPostId(int postId);
    public List<Photos> findPhotosByVisitId(int id);
    public List<Posts> topPosts(int limit);
    public List<Posts> loadAllPosts(int start);
}
