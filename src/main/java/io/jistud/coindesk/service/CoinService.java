package io.jistud.coindesk.service;

import io.jistud.coindesk.entity.Coin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CoinService {
    
    /**
     * Creates a new coin with optional internationalized names
     * 
     * @param name The primary name of the coin
     * @param i18nNames Map of language codes to localized names (can be null or empty)
     * @return The created Coin entity with its generated ID
     */
    Coin createCoin(String name, Map<String, String> i18nNames);
    
    /**
     * Finds a coin by its ID
     * 
     * @param id The coin ID
     * @return Optional containing the coin if found, empty otherwise
     */
    Optional<Coin> findById(Long id);
    
    /**
     * Finds a coin by its name
     *
     * @param name The coin name
     * @return Optional containing the coin if found, empty otherwise
     */
    Optional<Coin> findByName(String name);
    
    /**
     * Lists all coins
     * 
     * @return List of all coins
     */
    List<Coin> findAll();
}
