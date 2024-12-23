package com.memo.members.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memo.entity.Member;
import com.memo.members.dto.JoinRequest;
import com.memo.members.dto.LoginRequest;
import com.memo.members.exception.DuplicateMemberException;
import com.memo.members.exception.LoginFailedException;
import com.memo.members.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private JoinRequest joinRequest;
    private LoginRequest loginRequest;
    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원가입 요청 데이터 설정
        joinRequest = new JoinRequest();
        joinRequest.setId("testUser");
        joinRequest.setPassword("password123");
        joinRequest.setEmail("test@example.com");
        joinRequest.setNickname("테스터");

        // 테스트용 로그인 요청 데이터 설정
        loginRequest = new LoginRequest();
        loginRequest.setId("testUser");
        loginRequest.setPassword("password123");

        // 테스트용 회원 엔티티 설정
        testMember = new Member();
        testMember.setUserId(joinRequest.getId());
        testMember.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        testMember.setEmail(joinRequest.getEmail());
        testMember.setNickname(joinRequest.getNickname());
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void joinSuccess() throws Exception {
        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists()); // JWT 토큰이 반환되는지 확인
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복 회원")
    void joinFailDuplicate() throws Exception {
        // given
        memberRepository.save(testMember);

        // when & then
        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value(DuplicateMemberException.MESSAGE));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
        // given
        memberRepository.save(testMember);

        // when & then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists()); // JWT 토큰이 반환되는지 확인
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 인증 정보")
    void loginFail() throws Exception {
        // given
        memberRepository.save(testMember);
        loginRequest.setPassword("wrongPassword");

        // when & then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value(LoginFailedException.MESSAGE));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
    void loginFailUserNotFound() throws Exception {
        // given
        LoginRequest nonExistentUser = new LoginRequest();
        nonExistentUser.setId("nonExistent");
        nonExistentUser.setPassword("password123");

        // when & then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUser)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value(LoginFailedException.MESSAGE));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 유효성 검사")
    void joinFailValidation() throws Exception {
        // given
        JoinRequest invalidRequest = new JoinRequest();
        invalidRequest.setId(""); // 빈 ID
        invalidRequest.setPassword(""); // 빈 비밀번호

        // when & then
        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation").exists());
    }
}