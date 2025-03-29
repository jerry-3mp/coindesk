package io.jistud.coindesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.dto.CoinDeskTransformedResponse;
import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.entity.CoinI18n;
import io.jistud.coindesk.repository.CoinI18nRepository;
import io.jistud.coindesk.repository.CoinRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CoinDeskServiceImpl implements CoinDeskService {

  private static final Logger logger = LoggerFactory.getLogger(CoinDeskServiceImpl.class);

  private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

  private final RestTemplate restTemplate;
  private final CoinRepository coinRepository;
  private final CoinI18nRepository coinI18nRepository;

  @Autowired
  public CoinDeskServiceImpl(RestTemplate restTemplate, 
                            CoinRepository coinRepository,
                            CoinI18nRepository coinI18nRepository) {
    this.restTemplate = restTemplate;
    this.coinRepository = coinRepository;
    this.coinI18nRepository = coinI18nRepository;
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
  public CoinDeskTransformedResponse getTransformedCoinDeskData(String langCode) {
    logger.info("Fetching transformed CoinDesk data with language: {}", langCode);
    
    try {
      // 1. Get current Bitcoin price from CoinDesk API
      CoinDeskResponse coinDeskResponse = getCurrentBitcoinPrice();
      
      // 2. Create the transformed response
      CoinDeskTransformedResponse transformedResponse = new CoinDeskTransformedResponse();
      
      // 3. Set the default name from the CoinDesk response
      transformedResponse.setName(coinDeskResponse.getChartName());
      
      // 4. Set the update time by parsing the ISO timestamp from CoinDesk response
      String isoTime = coinDeskResponse.getTime().getUpdatedISO();
      LocalDateTime updateTime;
      try {
          // Parse as ZonedDateTime and then convert to LocalDateTime
          DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
          updateTime = LocalDateTime.ofInstant(
              Instant.from(formatter.parse(isoTime)),
              ZoneId.systemDefault());
      } catch (Exception e) {
          logger.warn("Could not parse timestamp: {}, error: {}, using current time instead", 
                   isoTime, e.getMessage());
          updateTime = LocalDateTime.now();
      }
      transformedResponse.setUpdateTime(updateTime);
      
      // 5. Look up the coin in our database by name (Bitcoin)
      Optional<Coin> coinOpt = coinRepository.findByName(coinDeskResponse.getChartName());
      
      if (coinOpt.isPresent()) {
        Coin coin = coinOpt.get();
        
        // 6. If langCode is provided, look for a localized name
        if (langCode != null && !langCode.isEmpty()) {
          Optional<CoinI18n> i18nOpt = coinI18nRepository.findByCoinIdAndLangCode(coin.getId(), langCode);
          
          // If localized name exists for the requested language, use it
          if (i18nOpt.isPresent()) {
            transformedResponse.setLocalizedName(i18nOpt.get().getName());
          } else {
            // Fall back to the default name if no translation exists
            transformedResponse.setLocalizedName(coin.getName());
            logger.info("No translation found for language {} and coin {}, using default name", 
                      langCode, coin.getName());
          }
        } else {
          // If no language specified, use default name
          transformedResponse.setLocalizedName(coin.getName());
        }
      } else {
        // If coin not found in database, use the default name from CoinDesk API
        transformedResponse.setLocalizedName(coinDeskResponse.getChartName());
        logger.warn("Coin '{}' not found in database, using default name", 
                  coinDeskResponse.getChartName());
      }
      
      return transformedResponse;
    } catch (Exception e) {
      logger.error("Error transforming CoinDesk data: {}", e.getMessage());
      throw new RuntimeException("Error transforming CoinDesk data", e);
    }
  }
}
