package io.jistud.coindesk.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "coins", uniqueConstraints = {
    @UniqueConstraint(name = "uk_coins_name", columnNames = {"name"})
})
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CoinI18n> i18nNames = new HashSet<>();

    // Constructors
    public Coin() {
    }

    public Coin(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<CoinI18n> getI18nNames() {
        return i18nNames;
    }

    public void setI18nNames(Set<CoinI18n> i18nNames) {
        this.i18nNames = i18nNames;
    }

    // Helper methods
    public void addI18nName(CoinI18n i18nName) {
        i18nNames.add(i18nName);
        i18nName.setCoin(this);
    }

    public void removeI18nName(CoinI18n i18nName) {
        i18nNames.remove(i18nName);
        i18nName.setCoin(null);
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
        return "Coin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", i18nNames=" + i18nNames.size() +
                '}';
    }
}
