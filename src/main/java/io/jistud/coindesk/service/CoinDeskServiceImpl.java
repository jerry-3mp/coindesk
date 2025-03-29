package io.jistud.coindesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.dto.CoinDeskTransformedResponse;

@Service
public class CoinDeskServiceImpl implements CoinDeskService {

  private static final Logger logger = LoggerFactory.getLogger(CoinDeskServiceImpl.class);

  private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

  private final RestTemplate restTemplate;

  @Autowired
  public CoinDeskServiceImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public CoinDeskResponse getCurrentBitcoinPrice() {
    logger.info("Fetching current Bitcoin price from CoinDesk API");

    try {
      CoinDeskResponse response =
          restTemplate.getForObject(COINDESK_API_URL, CoinDeskResponse.class);

      if (response == null) {
        logger.error("Received null response from CoinDesk API");
        throw new RuntimeException("Received null response from CoinDesk API");
      }

      logger.info("Successfully fetched Bitcoin price data");
      return response;
    } catch (RestClientException e) {
      logger.error("Error fetching data from CoinDesk API: {}", e.getMessage());
      throw e; // Re-throw to be handled by caller or global exception handler
    } catch (Exception e) {
      logger.error("Unexpected error processing CoinDesk API response: {}", e.getMessage());
      throw new RuntimeException("Error processing CoinDesk API response", e);
    }
  }

  @Override
  public CoinDeskTransformedResponse getTransformedCoinDeskData() {
    throw new UnsupportedOperationException("Method not implemented yet");
  }
}
