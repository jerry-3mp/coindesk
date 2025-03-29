package io.jistud.coindesk.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.jistud.coindesk.dto.CoinDeskResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
public class CoinDeskServiceTest {

  private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

  @Mock private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;
  private CoinDeskService coinDeskService;

  @BeforeEach
  void setUp() {
    RestTemplate restTemplateInstance = new RestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplateInstance);

    coinDeskService = new CoinDeskServiceImpl(restTemplateInstance);
  }

  @Test
  @DisplayName("Should fetch current Bitcoin price data successfully")
  void shouldFetchBitcoinPriceDataSuccessfully() {
    // Arrange
    String mockResponse =
        "{"
            + "\"time\": {"
            + "\"updated\": \"Feb 21, 2023 14:22:00 UTC\","
            + "\"updatedISO\": \"2023-02-21T14:22:00+00:00\","
            + "\"updateduk\": \"Feb 21, 2023 at 14:22 GMT\""
            + "},"
            + "\"disclaimer\": \"This data was produced from the CoinDesk Bitcoin Price Index (USD).\","
            + "\"chartName\": \"Bitcoin\","
            + "\"bpi\": {"
            + "\"USD\": {"
            + "\"code\": \"USD\","
            + "\"symbol\": \"&#36;\","
            + "\"rate\": \"24,870.9308\","
            + "\"description\": \"United States Dollar\","
            + "\"rate_float\": 24870.9308"
            + "},"
            + "\"GBP\": {"
            + "\"code\": \"GBP\","
            + "\"symbol\": \"&pound;\","
            + "\"rate\": \"20,648.0489\","
            + "\"description\": \"British Pound Sterling\","
            + "\"rate_float\": 20648.0489"
            + "},"
            + "\"EUR\": {"
            + "\"code\": \"EUR\","
            + "\"symbol\": \"&euro;\","
            + "\"rate\": \"24,105.6403\","
            + "\"description\": \"Euro\","
            + "\"rate_float\": 24105.6403"
            + "}"
            + "}"
            + "}";

    mockServer
        .expect(requestTo(COINDESK_API_URL))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

    // Act
    CoinDeskResponse response = coinDeskService.getCurrentBitcoinPrice();

    // Assert
    assertNotNull(response);
    assertEquals("Bitcoin", response.getChartName());
    assertNotNull(response.getBpi());
    assertEquals(3, response.getBpi().size());
    assertTrue(response.getBpi().containsKey("USD"));
    assertTrue(response.getBpi().containsKey("GBP"));
    assertTrue(response.getBpi().containsKey("EUR"));
    assertEquals(24870.9308, response.getBpi().get("USD").getRateFloat());
    assertEquals("United States Dollar", response.getBpi().get("USD").getDescription());

    // Verify mock server expectations were met
    mockServer.verify();
  }

  @Test
  @DisplayName("Should handle API error and throw exception")
  void shouldHandleApiErrorAndThrowException() {
    // Arrange
    mockServer
        .expect(requestTo(COINDESK_API_URL))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withServerError());

    // Act & Assert
    assertThrows(
        HttpServerErrorException.class,
        () -> {
          coinDeskService.getCurrentBitcoinPrice();
        });

    // Verify mock server expectations were met
    mockServer.verify();
  }

  @Test
  @DisplayName("Should handle malformed API response")
  void shouldHandleMalformedApiResponse() {
    // Arrange
    String malformedJson = "{\"time\": {\"updated\": \"Feb 21, 2023 14:22:00 UTC\"},";

    mockServer
        .expect(requestTo(COINDESK_API_URL))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(malformedJson, MediaType.APPLICATION_JSON));

    // Act & Assert
    assertThrows(
        Exception.class,
        () -> {
          coinDeskService.getCurrentBitcoinPrice();
        });

    // Verify mock server expectations were met
    mockServer.verify();
  }
}
