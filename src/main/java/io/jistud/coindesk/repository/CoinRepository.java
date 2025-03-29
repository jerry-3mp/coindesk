package io.jistud.coindesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.jistud.coindesk.entity.Coin;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
  Optional<Coin> findByName(String name);

  List<Coin> findByNameContainingIgnoreCase(String partialName);

  boolean existsByName(String name);
}
