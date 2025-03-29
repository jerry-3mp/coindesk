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
    
    /**
     * Updates an existing coin by its ID with optional internationalized names.
     * Existing i18n entries not included in the update request will be preserved (merge approach).
     * 
     * @param id The ID of the coin to update
     * @param name The updated name of the coin
     * @param i18nNames Map of language codes to localized names (can be null or empty)
     * @return The updated coin
     * @throws IllegalArgumentException if the coin does not exist
     */
    Coin updateCoin(Long id, String name, Map<String, String> i18nNames);
}
