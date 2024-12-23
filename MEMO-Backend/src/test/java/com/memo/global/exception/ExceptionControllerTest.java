package com.memo.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ExceptionController.class)
class ExceptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트용 DTO
    @Getter
    @Setter
    static class TestRequest {
        @NotBlank(message = "이름은 필수입니다")
        private String name;

        @NotBlank(message = "나이는 필수입니다")
        @Pattern(regexp = "^[0-9]{1,3}$", message = "나이는 1-3자리 숫자여야 합니다")
        private String age;
    }

    @TestConfiguration  // 이거 추가
    static class TestConfig {
        @Bean
        public TestController testController() {  // 테스트용 컨트롤러를 빈으로 등록
            return new TestController();
        }
    }

    // 테스트용 컨트롤러
    @RestController
    static class TestController {
        @PostMapping("/test")
        public void test(@Valid @RequestBody TestRequest request) {
        }
    }

    @Test
    @DisplayName("@Valid 실패시 ErrorResponse 응답 검증")
    void validationError() throws Exception {
        // given
        TestRequest request = new TestRequest();  // 값을 설정하지 않음

        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.name").value("이름은 필수입니다"))
                .andExpect(jsonPath("$.validation.age").value("나이는 필수입니다"))
                .andDo(print());
    }
}