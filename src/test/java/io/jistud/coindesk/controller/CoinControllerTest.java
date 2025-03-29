package io.jistud.coindesk.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.entity.CoinI18n;
import io.jistud.coindesk.service.CoinService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoinController.class)
public class CoinControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CoinService coinService;

  @Test
  @DisplayName("Should create a new coin successfully")
  void shouldCreateNewCoinSuccessfully() throws Exception {
    // Arrange
    String coinName = "Bitcoin";
    String requestBody = "{\"name\":\"" + coinName + "\"}";

    Coin createdCoin = createCoin(1L, coinName);
    LocalDateTime now = LocalDateTime.now();
    createdCoin.setCreatedAt(now);
    createdCoin.setUpdatedAt(now);

    when(coinService.createCoin(eq(coinName), any())).thenReturn(createdCoin);

    // Act & Assert
    mockMvc
        .perform(post("/api/v1/coins").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is(coinName)))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("Should create a new coin with i18n names")
  void shouldCreateNewCoinWithI18nNames() throws Exception {
    // Arrange
    String coinName = "Bitcoin";
    String requestBody =
        "{\"name\":\"Bitcoin\",\"i18nNames\":{\"en\":\"Bitcoin\",\"es\":\"Bitcóin\",\"ja\":\"ビットコイン\"}}";

    Coin createdCoin = createCoin(1L, coinName);
    LocalDateTime now = LocalDateTime.now();
    createdCoin.setCreatedAt(now);
    createdCoin.setUpdatedAt(now);

    // Add i18n names to the coin
    CoinI18n i18nEn = new CoinI18n(createdCoin, "en", "Bitcoin");
    CoinI18n i18nEs = new CoinI18n(createdCoin, "es", "Bitcóin");
    CoinI18n i18nJa = new CoinI18n(createdCoin, "ja", "ビットコイン");

    createdCoin.addI18nName(i18nEn);
    createdCoin.addI18nName(i18nEs);
    createdCoin.addI18nName(i18nJa);

    when(coinService.createCoin(eq(coinName), anyMap())).thenReturn(createdCoin);

    // Act & Assert
    mockMvc
        .perform(post("/api/v1/coins").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is(coinName)))
        .andExpect(jsonPath("$.i18nNames", notNullValue()))
        .andExpect(jsonPath("$.i18nNames.en", is("Bitcoin")))
        .andExpect(jsonPath("$.i18nNames.es", is("Bitcóin")))
        .andExpect(jsonPath("$.i18nNames.ja", is("ビットコイン")));
  }

  @Test
  @DisplayName("Should return 400 when creating coin with empty name")
  void shouldReturn400WhenCreatingCoinWithEmptyName() throws Exception {
    // Arrange
    String requestBody = "{\"name\":\"\"}";

    // Act & Assert
    mockMvc
        .perform(post("/api/v1/coins").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 409 when creating coin with duplicate name")
  void shouldReturn409WhenCreatingCoinWithDuplicateName() throws Exception {
    // Arrange
    String coinName = "Bitcoin";
    String requestBody = "{\"name\":\"" + coinName + "\"}";

    doThrow(new IllegalStateException("Coin with name 'Bitcoin' already exists"))
        .when(coinService)
        .createCoin(eq(coinName), any());

    // Act & Assert
    mockMvc
        .perform(post("/api/v1/coins").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", containsString("already exists")));
  }

  @Test
  @DisplayName("Should return all coins when no parameters provided")
  void shouldReturnAllCoinsWhenNoParametersProvided() throws Exception {
    // Arrange
    List<Coin> coins =
        Arrays.asList(
            createCoin(1L, "Bitcoin"), createCoin(2L, "Ethereum"), createCoin(3L, "Litecoin"));

    when(coinService.findAll()).thenReturn(coins);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/coins").contentType(MediaType.APPLICATION_JSON))
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
    mockMvc
        .perform(
            get("/api/v1/coins")
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
    mockMvc
        .perform(
            get("/api/v1/coins").param("name", coinName).contentType(MediaType.APPLICATION_JSON))
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
    mockMvc
        .perform(get("/api/v1/coins").contentType(MediaType.APPLICATION_JSON))
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
    mockMvc
        .perform(
            get("/api/v1/coins")
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
    mockMvc
        .perform(
            get("/api/v1/coins")
                .param("name", nonExistentName)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("Should return 400 Bad Request when both id and name parameters are provided")
  void shouldReturnBadRequestWhenBothIdAndNameParametersProvided() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/v1/coins")
                .param("id", "1")
                .param("name", "Bitcoin")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should get a single coin by ID")
  void shouldGetSingleCoinById() throws Exception {
    // Arrange
    Long coinId = 1L;
    String coinName = "Bitcoin";

    Coin coin = createCoin(coinId, coinName);
    LocalDateTime now = LocalDateTime.now();
    coin.setCreatedAt(now);
    coin.setUpdatedAt(now);

    when(coinService.findById(coinId)).thenReturn(Optional.of(coin));

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/coins/{id}", coinId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Bitcoin")))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.i18nNames").doesNotExist());
  }

  @Test
  @DisplayName("Should get a coin with i18n names by ID")
  void shouldGetCoinWithI18nNamesById() throws Exception {
    // Arrange
    Long coinId = 1L;
    String coinName = "Bitcoin";

    Coin coin = createCoin(coinId, coinName);
    LocalDateTime now = LocalDateTime.now();
    coin.setCreatedAt(now);
    coin.setUpdatedAt(now);

    // Add i18n names to the coin
    CoinI18n i18nEn = new CoinI18n(coin, "en", "Bitcoin");
    CoinI18n i18nEs = new CoinI18n(coin, "es", "Bitcóin");
    CoinI18n i18nJa = new CoinI18n(coin, "ja", "ビットコイン");

    coin.addI18nName(i18nEn);
    coin.addI18nName(i18nEs);
    coin.addI18nName(i18nJa);

    when(coinService.findById(coinId)).thenReturn(Optional.of(coin));

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/coins/{id}", coinId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Bitcoin")))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.i18nNames", notNullValue()))
        .andExpect(jsonPath("$.i18nNames.en", is("Bitcoin")))
        .andExpect(jsonPath("$.i18nNames.es", is("Bitcóin")))
        .andExpect(jsonPath("$.i18nNames.ja", is("ビットコイン")));
  }

  @Test
  @DisplayName("Should return 404 when coin ID not found in getById")
  void shouldReturn404WhenCoinIdNotFoundInGetById() throws Exception {
    // Arrange
    Long nonExistentId = 999L;
    when(coinService.findById(nonExistentId)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/coins/{id}", nonExistentId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update a coin successfully")
  void shouldUpdateCoinSuccessfully() throws Exception {
    // Arrange
    Long coinId = 1L;
    String originalName = "Bitcoin";
    String updatedName = "BTC";
    String requestBody = "{\"name\":\"" + updatedName + "\"}";

    Coin existingCoin = createCoin(coinId, originalName);
    Coin updatedCoin = createCoin(coinId, updatedName);
    LocalDateTime now = LocalDateTime.now();
    updatedCoin.setCreatedAt(now);
    updatedCoin.setUpdatedAt(now);

    when(coinService.findById(coinId)).thenReturn(Optional.of(existingCoin));
    when(coinService.updateCoin(eq(coinId), eq(updatedName), any())).thenReturn(updatedCoin);

    // Act & Assert
    mockMvc
        .perform(
            put("/api/v1/coins/{id}", coinId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(coinId.intValue())))
        .andExpect(jsonPath("$.name", is(updatedName)))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("Should update a coin with i18n names")
  void shouldUpdateCoinWithI18nNames() throws Exception {
    // Arrange
    Long coinId = 1L;
    String originalName = "Bitcoin";
    String updatedName = "BTC";
    String requestBody =
        "{\"name\":\""
            + updatedName
            + "\",\"i18nNames\":{\"en\":\"Bitcoin\",\"es\":\"Bitcóin\",\"ja\":\"ビットコイン\"}}";

    Coin existingCoin = createCoin(coinId, originalName);
    Coin updatedCoin = createCoin(coinId, updatedName);
    LocalDateTime now = LocalDateTime.now();
    updatedCoin.setCreatedAt(now);
    updatedCoin.setUpdatedAt(now);

    // Add i18n names to the updated coin
    CoinI18n i18nEn = new CoinI18n(updatedCoin, "en", "Bitcoin");
    CoinI18n i18nEs = new CoinI18n(updatedCoin, "es", "Bitcóin");
    CoinI18n i18nJa = new CoinI18n(updatedCoin, "ja", "ビットコイン");

    updatedCoin.addI18nName(i18nEn);
    updatedCoin.addI18nName(i18nEs);
    updatedCoin.addI18nName(i18nJa);

    when(coinService.findById(coinId)).thenReturn(Optional.of(existingCoin));
    when(coinService.updateCoin(eq(coinId), eq(updatedName), anyMap())).thenReturn(updatedCoin);

    // Act & Assert
    mockMvc
        .perform(
            put("/api/v1/coins/{id}", coinId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(coinId.intValue())))
        .andExpect(jsonPath("$.name", is(updatedName)))
        .andExpect(jsonPath("$.i18nNames", notNullValue()))
        .andExpect(jsonPath("$.i18nNames.en", is("Bitcoin")))
        .andExpect(jsonPath("$.i18nNames.es", is("Bitcóin")))
        .andExpect(jsonPath("$.i18nNames.ja", is("ビットコイン")));
  }

  @Test
  @DisplayName("Should return 400 when updating coin with empty name")
  void shouldReturn400WhenUpdatingCoinWithEmptyName() throws Exception {
    // Arrange
    Long coinId = 1L;
    String requestBody = "{\"name\":\"\"}";

    // Act & Assert
    mockMvc
        .perform(
            put("/api/v1/coins/{id}", coinId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent coin")
  void shouldReturn404WhenUpdatingNonExistentCoin() throws Exception {
    // Arrange
    Long nonExistentId = 999L;
    String requestBody = "{\"name\":\"BTC\"}";

    when(coinService.updateCoin(eq(nonExistentId), anyString(), any()))
        .thenThrow(new IllegalArgumentException("Coin with ID " + nonExistentId + " not found"));

    // Act & Assert
    mockMvc
        .perform(
            put("/api/v1/coins/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 409 when updating coin with duplicate name")
  void shouldReturn409WhenUpdatingCoinWithDuplicateName() throws Exception {
    // Arrange
    Long coinId = 1L;
    String coinName = "Bitcoin";
    String duplicateName = "Ethereum";
    String requestBody = "{\"name\":\"" + duplicateName + "\"}";

    Coin coin = createCoin(1L, coinName);
    LocalDateTime now = LocalDateTime.now();
    coin.setCreatedAt(now);
    coin.setUpdatedAt(now);

    when(coinService.findById(coinId)).thenReturn(Optional.of(coin));
    when(coinService.updateCoin(eq(coinId), eq(duplicateName), any()))
        .thenThrow(
            new IllegalStateException("Coin with name '" + duplicateName + "' already exists"));

    // Act & Assert
    mockMvc
        .perform(
            put("/api/v1/coins/{id}", coinId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", containsString("already exists")));
  }

  @Test
  @DisplayName("Should delete coin successfully")
  void shouldDeleteCoinSuccessfully() throws Exception {
    // Arrange
    Long coinId = 1L;
    Coin coin = createCoin(coinId, "Bitcoin");

    when(coinService.findById(coinId)).thenReturn(Optional.of(coin));
    doNothing().when(coinService).deleteCoin(coinId);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/coins/{id}", coinId)).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent coin")
  void shouldReturn404WhenDeletingNonExistentCoin() throws Exception {
    // Arrange
    Long nonExistentId = 999L;

    when(coinService.findById(nonExistentId)).thenReturn(Optional.empty());
    doThrow(new IllegalArgumentException("Coin with ID " + nonExistentId + " not found"))
        .when(coinService)
        .deleteCoin(nonExistentId);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/coins/{id}", nonExistentId)).andExpect(status().isNotFound());
  }

  // Helper method to create coin objects for testing
  private Coin createCoin(Long id, String name) {
    Coin coin = new Coin(name);
    coin.setId(id);
    return coin;
  }
}
