package com.memo.posts.model.service;

import com.memo.model.dto.Visits;
import com.memo.photos.model.dto.Photos;
import com.memo.posts.model.dto.Posts;
import com.memo.posts.model.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class PostsServiceImpl implements PostsService {
    private final PostsMapper postsMapper;
    @Override
    public int insertPosts(Posts posts) {
        return postsMapper.insertPosts(posts);
    }

    @Override
    public int insertVisits(Visits visits) {
        return postsMapper.insertVisits(visits);
    }

    @Override
    public List<Posts> selectAll() {
        return postsMapper.selectAll();
    }

    @Override
    public List<Posts> selectByKeyword(String keyword) {
        return postsMapper.selectByKeyword(keyword);
    }

    @Override
    public Posts selectById(int id) {
        return postsMapper.selectById(id);
    }

    @Override
    public List<Visits> selectVisitsByPostsId(int id) {
        return postsMapper.selectVisitsByPostsId(id);
    }

    @Override
    public int deleteVisits(int id) {
        return postsMapper.deleteVisits(id);
    }

    @Override
    public int deletePosts(int id) {
        return postsMapper.deletePosts(id);
    }

    @Override
    public int updatePosts(Posts posts) {
        return postsMapper.updatePosts(posts);
    }

    @Override
    public int updateVisits(Visits visits) {
        return postsMapper.updateVisits(visits);
    }

    @Override
    public List<Integer> selectVisitId(int id) {
        return postsMapper.selectVisitId(id);
    }

    @Override
    public int insertPhoto(Photos photo) {
        return postsMapper.insertPhoto(photo);
    }

    @Override
    public List<Posts> recentPosts(int limit) {
        return postsMapper.recentPosts(limit);
    }

    @Override
    public Optional<Photos> findFirstPhotoByPostId(int postId) {
        return postsMapper.findFirstPhotoByPostId(postId);
    }

    @Override
    public List<Photos> findPhotosByVisitId(int id) {
        return postsMapper.findPhotosByVisitId(id);
    }

    @Override
    public List<Posts> topPosts(int limit) {
        return postsMapper.topPosts(limit);
    }

    @Override
    public List<Posts> loadAllPosts(int start) {
        return postsMapper.loadAllPosts(start);
    }
}
