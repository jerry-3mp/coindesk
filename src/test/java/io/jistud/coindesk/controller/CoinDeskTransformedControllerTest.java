package io.jistud.coindesk.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jistud.coindesk.dto.CoinDeskTransformedResponse;
import io.jistud.coindesk.service.CoinDeskService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoinDeskTransformedController.class)
public class CoinDeskTransformedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoinDeskService coinDeskService;

    @Test
    public void getTransformedCoinDeskData_NoLang_ShouldReturnTransformedResponse() throws Exception {
        // Prepare test data
        CoinDeskTransformedResponse mockResponse = createMockTransformedResponse(null);
        
        // Mock service behavior
        when(coinDeskService.getTransformedCoinDeskData(null)).thenReturn(mockResponse);

        // Perform the test
        mockMvc.perform(get("/api/v1/transformed-coindesk")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.localizedName").value("Bitcoin"))
                .andExpect(jsonPath("$.updateTime").exists());
    }
    
    @Test
    public void getTransformedCoinDeskData_WithLang_ShouldReturnLocalizedResponse() throws Exception {
        // Language code to test
        String langCode = "zh-TW";
        
        // Prepare test data with localized name
        CoinDeskTransformedResponse mockResponse = createMockTransformedResponse(langCode);
        
        // Mock service behavior
        when(coinDeskService.getTransformedCoinDeskData(langCode)).thenReturn(mockResponse);

        // Perform the test
        mockMvc.perform(get("/api/v1/transformed-coindesk")
                .param("lang", langCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.localizedName").value("比特幣"))
                .andExpect(jsonPath("$.updateTime").exists());
    }
    
    private CoinDeskTransformedResponse createMockTransformedResponse(String langCode) {
        CoinDeskTransformedResponse response = new CoinDeskTransformedResponse();
        response.setName("Bitcoin");
        
        // Set localized name based on language
        if ("zh-TW".equals(langCode)) {
            response.setLocalizedName("比特幣");
        } else {
            response.setLocalizedName("Bitcoin");
        }
        
        response.setUpdateTime(LocalDateTime.now());
        return response;
    }
}
