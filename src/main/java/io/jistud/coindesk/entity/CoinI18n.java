package io.jistud.coindesk.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "coin_i18n")
public class CoinI18n {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coin_id", nullable = false)
  private Coin coin;

  @Column(name = "lang_code", nullable = false, length = 10)
  private String langCode;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Constructors
  public CoinI18n() {}

  public CoinI18n(Coin coin, String langCode, String name) {
    this.coin = coin;
    this.langCode = langCode;
    this.name = name;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Coin getCoin() {
    return coin;
  }

  public void setCoin(Coin coin) {
    this.coin = coin;
  }

  public String getLangCode() {
    return langCode;
  }

  public void setLangCode(String langCode) {
    this.langCode = langCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  // JPA Callbacks
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return "CoinI18n{"
        + "id="
        + id
        + ", coin="
        + (coin != null ? coin.getId() : null)
        + ", langCode='"
        + langCode
        + '\''
        + ", name='"
        + name
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
