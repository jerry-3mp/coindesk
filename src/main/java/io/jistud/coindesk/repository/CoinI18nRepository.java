package io.jistud.coindesk.repository;

import io.jistud.coindesk.entity.CoinI18n;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinI18nRepository extends JpaRepository<CoinI18n, Long> {
    List<CoinI18n> findByCoinId(Long coinId);
    List<CoinI18n> findByLangCode(String langCode);
    Optional<CoinI18n> findByCoinIdAndLangCode(Long coinId, String langCode);
}
