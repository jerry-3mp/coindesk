package io.jistud.coindesk.service;

import io.jistud.coindesk.dto.CoinDeskTransformedResponse;

/** Service for retrieving enriched coin details including external API data */
public interface CoinDeskService {

  /**
   * Gets a transformed response that combines CoinDesk API data with localized coin information
   *
   * @return Transformed CoinDesk response
   */
  CoinDeskTransformedResponse getTransformedCoinDeskData();
}
