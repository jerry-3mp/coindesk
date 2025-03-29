package io.jistud.coindesk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.dto.CoinDeskTransformedResponse;

@Service
public class CoinDeskServiceImpl implements CoinDeskService {

  private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

  private final RestTemplate restTemplate;

  @Autowired
  public CoinDeskServiceImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public CoinDeskResponse getCurrentBitcoinPrice() {
    throw new UnsupportedOperationException("Method not implemented yet");
  }

  @Override
  public CoinDeskTransformedResponse getTransformedCoinDeskData() {
    throw new UnsupportedOperationException("Method not implemented yet");
  }
}
