package io.jistud.coindesk.service;

import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.entity.CoinI18n;
import io.jistud.coindesk.repository.CoinRepository;
import io.jistud.coindesk.repository.CoinI18nRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;
    private final CoinI18nRepository coinI18nRepository;

    @Autowired
    public CoinServiceImpl(CoinRepository coinRepository, CoinI18nRepository coinI18nRepository) {
        this.coinRepository = coinRepository;
        this.coinI18nRepository = coinI18nRepository;
    }

    @Override
    @Transactional
    public Coin createCoin(String name, Map<String, String> i18nNames) {
        // Business rule validation: check for uniqueness
        if (coinRepository.existsByName(name)) {
            throw new IllegalStateException("Coin with name '" + name + "' already exists");
        }

        // Create and save the coin
        Coin coin = new Coin(name);
        coin = coinRepository.save(coin);

        // Add i18n names if provided
        if (i18nNames != null && !i18nNames.isEmpty()) {
            for (Map.Entry<String, String> entry : i18nNames.entrySet()) {
                CoinI18n i18n = new CoinI18n(coin, entry.getKey(), entry.getValue());
                coinI18nRepository.save(i18n);
            }
        }

        return coin;
    }

    @Override
    public Optional<Coin> findById(Long id) {
        return coinRepository.findById(id);
    }

    @Override
    public Optional<Coin> findByName(String name) {
        return coinRepository.findByName(name);
    }

    @Override
    public List<Coin> findAll() {
        return coinRepository.findAll();
    }

    @Override
    @Transactional
    public Coin updateCoin(Long id, String name, Map<String, String> i18nNames) {
        // Find the coin by ID
        Coin coin = coinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coin with ID " + id + " not found"));
        
        // Update the coin name
        coin.setName(name);
        coin = coinRepository.save(coin);
        
        // Update i18n names if provided
        if (i18nNames != null && !i18nNames.isEmpty()) {
            // Get existing i18n entries for this coin
            List<CoinI18n> existingI18ns = coinI18nRepository.findByCoinId(id);
            
            // Create a map of existing entries by language code for quick lookup
            Map<String, CoinI18n> existingI18nMap = new HashMap<>();
            for (CoinI18n i18n : existingI18ns) {
                existingI18nMap.put(i18n.getLangCode(), i18n);
            }
            
            // Update or create i18n entries
            for (Map.Entry<String, String> entry : i18nNames.entrySet()) {
                String langCode = entry.getKey();
                String i18nName = entry.getValue();
                
                if (existingI18nMap.containsKey(langCode)) {
                    // Update existing entry
                    CoinI18n existingI18n = existingI18nMap.get(langCode);
                    existingI18n.setName(i18nName);
                    coinI18nRepository.save(existingI18n);
                } else {
                    // Create new entry
                    CoinI18n newI18n = new CoinI18n(coin, langCode, i18nName);
                    coinI18nRepository.save(newI18n);
                }
            }
            
            // Note: We don't delete entries that weren't in the i18nNames map,
            // implementing the merge approach as specified in the method contract
        }
        
        return coin;
    }
    
    @Override
    @Transactional
    public void deleteCoin(Long id) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}
