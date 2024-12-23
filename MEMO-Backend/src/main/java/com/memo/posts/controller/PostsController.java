package com.memo.posts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memo.gpt.dto.ChatGPTRequest;
import com.memo.gpt.dto.ChatGPTResponse;
import com.memo.gpt.service.ChatGPTService;
import com.memo.model.dto.Visits;
import com.memo.photos.model.dto.Photos;
import com.memo.posts.dto.TimelineResponse;
import com.memo.posts.model.dto.PostWithVisits;
import com.memo.posts.model.dto.Posts;
import com.memo.posts.model.service.PostsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostsController {
    private final ChatGPTService chatGPTService;
    private final PostsService postsService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> insert(
            @RequestPart("timelineData") String timelineDataJson,
            @RequestParam Map<String, MultipartFile> files
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PostWithVisits timelineData = mapper.readValue(timelineDataJson, PostWithVisits.class);
            // 0.5++ GPT한테서 키워드 때오기
            String prompt = timelineData.getPost().getDescription() +
                    " 이 여행지 설명에 대한 중요 키워드로 띄워쓰기 없는 단어 3개만 뽑아서 답으로 '키워드1, 키워드2, 키워드3' 이런식으로 답해줘";
            ChatGPTRequest gptRequest = new ChatGPTRequest("gpt-4o-mini", prompt);
            ChatGPTResponse gptResponse = chatGPTService.chat(gptRequest);
            String gptResult = gptResponse.getChoices().get(0).getMessage().getContent();
            System.out.println("GPT키워드 : " + gptResult);
            String keywords = null;
            if (gptResult != null && gptResult.matches("^[^,]+,[^,]+,[^,]+$")) {
                keywords = gptResult;
            }
            else keywords = "저장된, 키워드가, 없습니다";
            // Posts에 키워드 설정
            timelineData.getPost().setKeywords(keywords);  // Posts 엔티티에 keywords 필드 필요

            // 1. Posts 저장 및 ID 받기
            int cnt = postsService.insertPosts(timelineData.getPost());
            int postId = timelineData.getPost().getId(); // 저장 후 생성된 ID

            // 2. Visits 처리 및 Photos 저장을 위한 Map
            Map<Integer, Integer> visitIndexToIdMap = new HashMap<>();

            // 3. Visits 저장 및 매핑 정보 저장
            List<PostWithVisits.Visit> visits = timelineData.getVisits();
            for (int i = 0; i < visits.size(); i++) {
                PostWithVisits.Visit visit = visits.get(i);
                Visits visitEntity = new Visits();
                visitEntity.setPostId(postId);
                visitEntity.setTitle(visit.getTitle());
                visitEntity.setDescription(visit.getDescription());
                visitEntity.setVisitDate(visit.getVisitDate());
                visitEntity.setPlaceId(visit.getPlace().getId());

                // visit 저장하고 생성된 ID 받기
                postsService.insertVisits(visitEntity);
                int visitId = visitEntity.getId();
                visitIndexToIdMap.put(i, visitId);
            }

            // 4. 파일 저장 로직
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 5. 각 파일 처리
            files.forEach((key, file) -> {
                String[] parts = key.split("_");
                if (parts.length == 4) {
                    int visitIndex = Integer.parseInt(parts[1]);
                    int placeId = Integer.parseInt(parts[2]);
                    int photoIndex = Integer.parseInt(parts[3]);

                    try {
                        String originalFilename = file.getOriginalFilename();
                        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
                        Path filePath = Paths.get(uploadDir + fileName);

                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        Photos photo = new Photos();
                        photo.setVisitId(visitIndexToIdMap.get(visitIndex));
                        photo.setFileName(fileName);
                        photo.setOriginalName(originalFilename);
                        photo.setFilePath(uploadDir + fileName);

                        postsService.insertPhoto(photo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (cnt != 0) {
                Map<String, Integer> response = new HashMap<>();
                response.put("id", postId);  // 생성된 postId를 응답에 포함
                return ResponseEntity.status(201).body(response);
            }
            return ResponseEntity.status(404).body("Posts Insert Failed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TimelineResponse>> getRecentPosts() {
        System.out.println("Recent timelines endpoint called");
        try {
            List<Posts> recentPosts = postsService.recentPosts(3);
            List<TimelineResponse> response = new ArrayList<>();

            for (Posts post : recentPosts) {
                TimelineResponse dto = new TimelineResponse();
                dto.setPost(post);

                Optional<Photos> firstPhoto = postsService.findFirstPhotoByPostId(post.getId());
                if (firstPhoto.isPresent()) {
                    Photos photo = firstPhoto.get();
                    String photoUrl = "/posts/uploads/" + photo.getFileName();  // URL 경로 수정
                    System.out.println("Generated photo URL: " + photoUrl);
                    dto.setThumbnailUrl(photoUrl);
                }
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/top")
    public ResponseEntity<List<TimelineResponse>> getTopPosts() {
        System.out.println("Top timelines endpoint called");
        try {
            List<Posts> topPosts = postsService.topPosts(3);
            List<TimelineResponse> response = new ArrayList<>();

            for (Posts post : topPosts) {
                System.out.println("탑 포스트 : " + post);
                TimelineResponse dto = new TimelineResponse();
                dto.setPost(post);

                Optional<Photos> firstPhoto = postsService.findFirstPhotoByPostId(post.getId());
                if (firstPhoto.isPresent()) {
                    Photos photo = firstPhoto.get();
                    String photoUrl = "/posts/uploads/" + photo.getFileName();  // URL 경로 수정
                    System.out.println("Generated photo URL: " + photoUrl);
                    dto.setThumbnailUrl(photoUrl);
                }
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/list/{pageNo}")
    public ResponseEntity<List<TimelineResponse>> getPostsLists(@PathVariable ("pageNo") int pageNo) {
        try {
            List<Posts> currentLists = postsService.loadAllPosts(pageNo);
            List<TimelineResponse> response = new ArrayList<>();

            for (Posts post : currentLists) {
                TimelineResponse dto = new TimelineResponse();
                dto.setPost(post);

                Optional<Photos> firstPhoto = postsService.findFirstPhotoByPostId(post.getId());
                if (firstPhoto.isPresent()) {
                    Photos photo = firstPhoto.get();
                    String photoUrl = "/posts/uploads/" + photo.getFileName();  // URL 경로 수정
                    System.out.println("Generated photo URL: " + photoUrl);
                    dto.setThumbnailUrl(photoUrl);
                }
                response.add(dto);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping("/uploads/{filename:.+}")  // .+ 패턴으로 파일 확장자 포함
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            Path filePath = Paths.get(uploadDir + filename);

            System.out.println("Attempting to serve file: " + filename);
            System.out.println("Full path: " + filePath.toString());
            System.out.println("File exists: " + Files.exists(filePath));

            if (Files.exists(filePath)) {
                Resource resource = new UrlResource(filePath.toUri());
                String contentType = Files.probeContentType(filePath);

                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                System.out.println("File not found at path: " + filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error serving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping
    public ResponseEntity<?> selectListByKeyword(@RequestParam(required = false) String keyword){
        System.out.println("리스트 키워드 : " + keyword);
        List<Posts> list = null;
        if (keyword == null) list = postsService.selectAll();
        else list = postsService.selectByKeyword(keyword);
        if (list != null) return ResponseEntity.ok(list);
        return ResponseEntity.status(404).body("Post List Query Failed");
    }

    @GetMapping("/{postsId}")
    public ResponseEntity<?> selectById(@PathVariable ("postsId") int postsId) {
        System.out.println("상세보기 아이디 : " + postsId);
        Posts post = postsService.selectById(postsId);
        System.out.println("Post: " + post);

        if (post != null) {
            List<Visits> visits = postsService.selectVisitsByPostsId(postsId);
            System.out.println("Visits with photos: " + visits);

            PostWithVisits result = PostWithVisits.createFromVisits(post, visits);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(404).body("Post Select Failed");
    }


    @DeleteMapping("/{postsId}")
    public ResponseEntity<?> delete(@PathVariable ("postsId") int postsId) {
        System.out.println("삭제할 아이디 : " + postsId);
        int cnt = postsService.deleteVisits(postsId);
        int cnt2 = postsService.deletePosts(postsId);
        if (cnt != 0) return ResponseEntity.ok().build();
        return ResponseEntity.status(404).body("Posts Delete Failed");
    }

    @PatchMapping("/{postsId}")
    public ResponseEntity<?> update(@PathVariable ("postsId") int postsId, @RequestBody PostsVisitsWrapper wrapper) {
        Posts posts = wrapper.getPosts();
        posts.setId(postsId);
        List<Visits> visits = wrapper.getVisits();
        List<Integer> idLists = postsService.selectVisitId(postsId);
        System.out.println("수정할 아이디 : " + postsId);
        System.out.println("수정할 데이터 : " + posts);
        int cnt = postsService.updatePosts(posts);
        for (int i = 0; i <visits.size(); i++) {
            visits.get(i).setPostId(postsId);
            visits.get(i).setId(idLists.get(i));
            System.out.println(visits.get(i));
            int cnt2 = postsService.updateVisits(visits.get(i));
        }
        if (cnt != 0) return ResponseEntity.ok().build();
        return ResponseEntity.status(404).body("Posts Update Failed");
    }

    @Getter
    public static class PostsVisitsWrapper {
        private Posts posts;
        private List<Visits> visits;
    }
}
