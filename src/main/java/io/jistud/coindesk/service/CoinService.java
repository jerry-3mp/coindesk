package io.jistud.coindesk.service;

import io.jistud.coindesk.entity.Coin;

import java.util.Map;

public interface CoinService {
    
    /**
     * Creates a new coin with optional internationalized names
     * 
     * @param name The primary name of the coin
     * @param i18nNames Map of language codes to localized names (can be null or empty)
     * @return The created Coin entity with its generated ID
     */
    Coin createCoin(String name, Map<String, String> i18nNames);
}
