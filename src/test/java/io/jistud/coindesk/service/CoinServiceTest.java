package io.jistud.coindesk.service;

import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.entity.CoinI18n;
import io.jistud.coindesk.repository.CoinI18nRepository;
import io.jistud.coindesk.repository.CoinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinServiceTest {

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private CoinI18nRepository coinI18nRepository;

    @Captor
    private ArgumentCaptor<Coin> coinCaptor;

    @Captor
    private ArgumentCaptor<CoinI18n> coinI18nCaptor;

    private CoinService coinService;

    @BeforeEach
    void setUp() {
        coinService = new CoinServiceImpl(coinRepository, coinI18nRepository);
    }

    @Test
    @DisplayName("Should create a coin with a name")
    void shouldCreateCoinWithName() {
        // Arrange
        String coinName = "Bitcoin";
        Coin savedCoin = new Coin(coinName);
        savedCoin.setId(1L);

        when(coinRepository.existsByName(coinName)).thenReturn(false);
        when(coinRepository.save(any(Coin.class))).thenReturn(savedCoin);

        // Act
        Coin result = coinService.createCoin(coinName, null);

        // Assert
        verify(coinRepository, times(1)).save(coinCaptor.capture());
        Coin capturedCoin = coinCaptor.getValue();

        assertEquals(coinName, capturedCoin.getName());
        assertEquals(1L, result.getId());
        assertEquals(coinName, result.getName());
        verify(coinI18nRepository, never()).save(any(CoinI18n.class));
    }

    @Test
    @DisplayName("Should create a coin with i18n names")
    void shouldCreateCoinWithI18nNames() {
        // Arrange
        String coinName = "Bitcoin";
        Map<String, String> i18nNames = new HashMap<>();
        i18nNames.put("zh-TW", "比特幣");
        i18nNames.put("ja", "ビットコイン");

        Coin savedCoin = new Coin(coinName);
        savedCoin.setId(1L);

        when(coinRepository.existsByName(coinName)).thenReturn(false);
        when(coinRepository.save(any(Coin.class))).thenReturn(savedCoin);

        // Act
        Coin result = coinService.createCoin(coinName, i18nNames);

        // Assert
        verify(coinRepository, times(1)).save(coinCaptor.capture());
        Coin capturedCoin = coinCaptor.getValue();

        assertEquals(coinName, capturedCoin.getName());
        assertEquals(1L, result.getId());

        // Verify i18n entries were saved
        verify(coinI18nRepository, times(2)).save(coinI18nCaptor.capture());

        // Check that the captured values contain both language entries
        boolean zhTwFound = false;
        boolean jaFound = false;

        for (CoinI18n i18n : coinI18nCaptor.getAllValues()) {
            if ("zh-TW".equals(i18n.getLangCode()) && "比特幣".equals(i18n.getName())) {
                zhTwFound = true;
            } else if ("ja".equals(i18n.getLangCode()) && "ビットコイン".equals(i18n.getName())) {
                jaFound = true;
            }
            assertEquals(savedCoin, i18n.getCoin());
        }

        assertTrue(zhTwFound, "Traditional Chinese translation should be saved");
        assertTrue(jaFound, "Japanese translation should be saved");
    }

    // We'll keep the unique name validation test as it's a business rule validation
    @Test
    @DisplayName("Should throw exception when coin name already exists")
    void shouldThrowExceptionWhenCoinNameAlreadyExists() {
        // Arrange
        String existingName = "Bitcoin";
        when(coinRepository.existsByName(existingName)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            coinService.createCoin(existingName, null);
        });

        verify(coinRepository).existsByName(existingName);
        verify(coinRepository, never()).save(any(Coin.class));
    }

    @Test
    @DisplayName("Should find coin by ID")
    void shouldFindCoinById() {
        // Arrange
        Long coinId = 1L;
        Coin coin = new Coin("Bitcoin");
        coin.setId(coinId);

        when(coinRepository.findById(coinId)).thenReturn(Optional.of(coin));

        // Act
        Optional<Coin> result = coinService.findById(coinId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(coinId, result.get().getId());
        assertEquals("Bitcoin", result.get().getName());
        verify(coinRepository).findById(coinId);
    }

    @Test
    @DisplayName("Should return empty optional when coin ID not found")
    void shouldReturnEmptyOptionalWhenCoinIdNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        when(coinRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Coin> result = coinService.findById(nonExistentId);

        // Assert
        assertFalse(result.isPresent());
        verify(coinRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should find coin by name")
    void shouldFindCoinByName() {
        // Arrange
        String coinName = "Bitcoin";
        Coin coin = new Coin(coinName);
        coin.setId(1L);

        when(coinRepository.findByName(coinName)).thenReturn(Optional.of(coin));

        // Act
        Optional<Coin> result = coinService.findByName(coinName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(coinName, result.get().getName());
        verify(coinRepository).findByName(coinName);
    }

    @Test
    @DisplayName("Should find all coins")
    void shouldFindAllCoins() {
        // Arrange
        List<Coin> coins = Arrays.asList(
                new Coin("Bitcoin"),
                new Coin("Ethereum"),
                new Coin("Litecoin")
        );

        when(coinRepository.findAll()).thenReturn(coins);

        // Act
        List<Coin> result = coinService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Bitcoin", result.get(0).getName());
        assertEquals("Ethereum", result.get(1).getName());
        assertEquals("Litecoin", result.get(2).getName());
        verify(coinRepository).findAll();
    }
}
