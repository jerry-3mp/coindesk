package io.jistud.coindesk.controller;

import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.service.CoinService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoinController.class)
public class CoinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoinService coinService;

    @Test
    @DisplayName("Should return all coins when no parameters provided")
    void shouldReturnAllCoinsWhenNoParametersProvided() throws Exception {
        // Arrange
        List<Coin> coins = Arrays.asList(
            createCoin(1L, "Bitcoin"),
            createCoin(2L, "Ethereum"),
            createCoin(3L, "Litecoin")
        );
        
        when(coinService.findAll()).thenReturn(coins);

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Bitcoin")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Ethereum")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Litecoin")))
                .andExpect(jsonPath("$[0].i18nNames").doesNotExist());
    }

    @Test
    @DisplayName("Should filter coins by ID")
    void shouldFilterCoinsById() throws Exception {
        // Arrange
        Long coinId = 1L;
        Coin coin = createCoin(coinId, "Bitcoin");
        
        when(coinService.findById(coinId)).thenReturn(Optional.of(coin));

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .param("id", coinId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Bitcoin")));
    }

    @Test
    @DisplayName("Should filter coins by name")
    void shouldFilterCoinsByName() throws Exception {
        // Arrange
        String coinName = "Bitcoin";
        Coin coin = createCoin(1L, coinName);
        
        when(coinService.findByName(coinName)).thenReturn(Optional.of(coin));

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .param("name", coinName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Bitcoin")));
    }

    @Test
    @DisplayName("Should return empty array when no coins found")
    void shouldReturnEmptyArrayWhenNoCoinsFound() throws Exception {
        // Arrange
        when(coinService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return empty array when coin ID not found")
    void shouldReturnEmptyArrayWhenCoinIdNotFound() throws Exception {
        // Arrange
        Long nonExistentId = 999L;
        when(coinService.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .param("id", nonExistentId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return empty array when coin name not found")
    void shouldReturnEmptyArrayWhenCoinNameNotFound() throws Exception {
        // Arrange
        String nonExistentName = "NonExistentCoin";
        when(coinService.findByName(nonExistentName)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .param("name", nonExistentName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when both id and name parameters are provided")
    void shouldReturnBadRequestWhenBothIdAndNameParametersProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/coins")
                .param("id", "1")
                .param("name", "Bitcoin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Helper method to create coin objects for testing
    private Coin createCoin(Long id, String name) {
        Coin coin = new Coin(name);
        coin.setId(id);
        return coin;
    }
}