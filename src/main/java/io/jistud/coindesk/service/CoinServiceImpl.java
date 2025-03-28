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
        // This method will be implemented after writing tests
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}
