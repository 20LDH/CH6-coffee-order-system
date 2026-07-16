package com.example.ch6coffeeordersystem.point.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ch6coffeeordersystem.point.dto.PointChargeResponse;
import com.example.ch6coffeeordersystem.point.service.PointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointService pointService;

    @Test
    void charge_returns200WithChargedResult() throws Exception {
        // given
        when(pointService.charge(eq("user-1234"), eq(10000)))
                .thenReturn(PointChargeResponse.of("user-1234", 10000, 25000));

        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "user-1234", "amount": 10000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.data.userId").value("user-1234"))
                .andExpect(jsonPath("$.data.chargedAmount").value(10000))
                .andExpect(jsonPath("$.data.totalPoint").value(25000));
    }

    @Test
    void charge_returns400_whenAmountIsZeroOrNegative() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "user-1234", "amount": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청이 실패했습니다."))
                .andExpect(jsonPath("$.error.code").value("INVALID_AMOUNT"));
    }

    @Test
    void charge_returns400_whenUserIdIsBlank() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": "", "amount": 10000}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error.code").value("INVALID_USER_ID"));
    }
}
