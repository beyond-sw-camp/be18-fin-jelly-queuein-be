package com.beyond.qiin.domain.inventory.controller;

import com.beyond.qiin.domain.inventory.dto.category.request.CreateCategoryRequestDto;
import com.beyond.qiin.domain.inventory.dto.category.response.CreateCategoryResponseDto;
import com.beyond.qiin.domain.inventory.service.command.CategoryCommandService;
import com.beyond.qiin.domain.inventory.service.query.CategoryQueryService;
import com.beyond.qiin.security.config.SecurityConfig;
import com.beyond.qiin.security.jwt.JwtFilter;
import com.beyond.qiin.security.jwt.JwtTokenProvider;
import com.beyond.qiin.security.jwt.RedisTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CategoryController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ---- Mock dependencies ----
    @MockBean
    private CategoryCommandService categoryCommandService;

    @MockBean
    private CategoryQueryService categoryQueryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisTokenRepository redisTokenRepository;

    @Test
    @DisplayName("카테고리 생성 성공 - DTO Builder 사용")
    void createCategory_success() throws Exception {

        // given (요청 DTO)
        CreateCategoryRequestDto request = CreateCategoryRequestDto.builder()
                                                                   .name("회의실")
                                                                   .description("설명입니다.")
                                                                   .build();

        // 서비스에서 반환할 응답 DTO
        CreateCategoryResponseDto response = CreateCategoryResponseDto.builder()
                                                                      .categoryId(1L)
                                                                      .name("회의실")
                                                                      .description("설명입니다.")
                                                                      .build();

        when(categoryCommandService.createCategory(any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/assets/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.categoryId").value(1L))
               .andExpect(jsonPath("$.name").value("회의실"))
               .andExpect(jsonPath("$.description").value("설명입니다."));
    }
}
