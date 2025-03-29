package io.jistud.coindesk.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.service.CoinDeskService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoinDeskController.class)
public class CoinDeskControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CoinDeskService coinDeskService;

  @Test
  public void getCoinDeskData_ShouldReturnCoinDeskResponse() throws Exception {
    // Prepare test data
    CoinDeskResponse mockResponse = createMockCoinDeskResponse();

    // Mock service behavior
    when(coinDeskService.getCurrentBitcoinPrice()).thenReturn(mockResponse);

    // Perform the test
    mockMvc
        .perform(get("/api/v1/coindesk").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.time.updated").value("Feb 21, 2023 14:22:00 UTC"))
        .andExpect(jsonPath("$.disclaimer").exists())
        .andExpect(jsonPath("$.chartName").value("Bitcoin"))
        .andExpect(jsonPath("$.bpi.USD.code").value("USD"))
        .andExpect(jsonPath("$.bpi.USD.rate_float").isNumber());
  }

  private CoinDeskResponse createMockCoinDeskResponse() {
    CoinDeskResponse response = new CoinDeskResponse();

    // Set time data
    CoinDeskResponse.TimeInfo timeInfo = new CoinDeskResponse.TimeInfo();
    timeInfo.setUpdated("Feb 21, 2023 14:22:00 UTC");
    timeInfo.setUpdatedISO("2023-02-21T14:22:00+00:00");
    timeInfo.setUpdateduk("Feb 21, 2023 at 14:22 GMT");
    response.setTime(timeInfo);

    // Set basic info
    response.setDisclaimer("This data was produced from the CoinDesk Bitcoin Price Index");
    response.setChartName("Bitcoin");

    // Set BPI data
    Map<String, CoinDeskResponse.CurrencyInfo> bpi = new HashMap<>();

    CoinDeskResponse.CurrencyInfo usd = new CoinDeskResponse.CurrencyInfo();
    usd.setCode("USD");
    usd.setSymbol("&#36;");
    usd.setRate("24,870.9308");
    usd.setDescription("United States Dollar");
    usd.setRateFloat(24870.9308);

    CoinDeskResponse.CurrencyInfo gbp = new CoinDeskResponse.CurrencyInfo();
    gbp.setCode("GBP");
    gbp.setSymbol("&pound;");
    gbp.setRate("20,648.0489");
    gbp.setDescription("British Pound Sterling");
    gbp.setRateFloat(20648.0489);

    CoinDeskResponse.CurrencyInfo eur = new CoinDeskResponse.CurrencyInfo();
    eur.setCode("EUR");
    eur.setSymbol("&euro;");
    eur.setRate("24,105.6403");
    eur.setDescription("Euro");
    eur.setRateFloat(24105.6403);

    bpi.put("USD", usd);
    bpi.put("GBP", gbp);
    bpi.put("EUR", eur);

    response.setBpi(bpi);

    return response;
    }
    

}
