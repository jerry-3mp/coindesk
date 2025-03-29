package io.jistud.coindesk.service;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.dto.CoinDeskTransformedResponse;

/** Service for retrieving enriched coin details including external API data */
public interface CoinDeskService {

  /**
   * Retrieves current Bitcoin price information from the CoinDesk API
   *
   * @return Raw CoinDesk API response containing current Bitcoin price information
   * @throws RuntimeException if there is an error communicating with the API or parsing the
   *     response
   */
  CoinDeskResponse getCurrentBitcoinPrice();

  /**
   * Gets a transformed response that combines CoinDesk API data with localized coin information
   *
   * @return Transformed CoinDesk response
   */
  CoinDeskTransformedResponse getTransformedCoinDeskData();
}
