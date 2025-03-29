package io.jistud.coindesk.dto;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class CoinDeskResponseTest {

  @Autowired private JacksonTester<CoinDeskResponse> json;

  @Test
  @DisplayName("Should correctly deserialize CoinDesk API response")
  void shouldCorrectlyDeserializeCoinDeskResponse() throws IOException {
    // Arrange
    String jsonContent =
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

    // Act
    CoinDeskResponse response = json.parse(jsonContent).getObject();

    // Assert
    assertNotNull(response);
    assertEquals("Bitcoin", response.getChartName());

    // Test BPI field mapping
    assertNotNull(response.getBpi());
    assertEquals(3, response.getBpi().size());

    // Test rate_float field mapping
    assertEquals(24870.9308, response.getBpi().get("USD").getRateFloat());
    assertEquals(20648.0489, response.getBpi().get("GBP").getRateFloat());
    assertEquals(24105.6403, response.getBpi().get("EUR").getRateFloat());
  }
}
