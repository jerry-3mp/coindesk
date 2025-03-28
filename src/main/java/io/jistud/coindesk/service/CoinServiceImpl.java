package io.jistud.coindesk.service;

import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.entity.CoinI18n;
import io.jistud.coindesk.repository.CoinRepository;
import io.jistud.coindesk.repository.CoinI18nRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
}
