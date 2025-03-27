package io.jistud.coindesk.repository;

import io.jistud.coindesk.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    Optional<Coin> findByName(String name);
    List<Coin> findByNameContainingIgnoreCase(String partialName);
}
