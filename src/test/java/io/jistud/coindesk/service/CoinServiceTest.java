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

    @Test
    @DisplayName("Should update a coin name successfully")
    void shouldUpdateCoinNameSuccessfully() {
        // Arrange
        Long coinId = 1L;
        String oldName = "Bitcoin";
        String newName = "Bitcoin Updated";
        
        Coin existingCoin = new Coin(oldName);
        existingCoin.setId(coinId);
        
        Coin updatedCoin = new Coin(newName);
        updatedCoin.setId(coinId);
        
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(existingCoin));
        when(coinRepository.save(any(Coin.class))).thenReturn(updatedCoin);
        
        // Act
        Coin result = coinService.updateCoin(coinId, newName, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(coinRepository).findById(coinId);
        verify(coinRepository).save(any(Coin.class));
        verify(coinI18nRepository, never()).save(any(CoinI18n.class));
    }

    @Test
    @DisplayName("Should update coin with i18n names")
    void shouldUpdateCoinWithI18nNames() {
        // Arrange
        Long coinId = 1L;
        String coinName = "Bitcoin";
        Map<String, String> i18nNames = new HashMap<>();
        i18nNames.put("zh-TW", "比特幣");
        i18nNames.put("ja", "ビットコイン");
        
        Coin existingCoin = new Coin(coinName);
        existingCoin.setId(coinId);
        
        List<CoinI18n> existingI18ns = new ArrayList<>(); // Empty list, no existing i18ns
        
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(existingCoin));
        when(coinRepository.save(any(Coin.class))).thenReturn(existingCoin);
        when(coinI18nRepository.findByCoinId(coinId)).thenReturn(existingI18ns);
        
        // Act
        Coin result = coinService.updateCoin(coinId, coinName, i18nNames);
        
        // Assert
        assertNotNull(result);
        assertEquals(coinName, result.getName());
        verify(coinRepository).findById(coinId);
        verify(coinRepository).save(any(Coin.class));
        
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
            assertEquals(existingCoin, i18n.getCoin());
        }
        
        assertTrue(zhTwFound, "Traditional Chinese translation should be saved");
        assertTrue(jaFound, "Japanese translation should be saved");
    }

    @Test
    @DisplayName("Should update existing i18n names for coin and preserve non-updated entries")
    void shouldUpdateExistingI18nNames() {
        // Arrange
        Long coinId = 1L;
        String coinName = "Bitcoin";
        
        // Updated i18n names - only updating zh-TW and ja, not fr
        Map<String, String> updatedI18nNames = new HashMap<>();
        updatedI18nNames.put("zh-TW", "比特幣-更新");
        updatedI18nNames.put("ja", "ビットコイン-更新");
        
        Coin existingCoin = new Coin(coinName);
        existingCoin.setId(coinId);
        
        // Existing i18n entries - includes three languages
        CoinI18n existingZhTw = new CoinI18n(existingCoin, "zh-TW", "比特幣");
        CoinI18n existingJa = new CoinI18n(existingCoin, "ja", "ビットコイン");
        CoinI18n existingFr = new CoinI18n(existingCoin, "fr", "Bitcoin");
        List<CoinI18n> existingI18ns = Arrays.asList(existingZhTw, existingJa, existingFr);
        
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(existingCoin));
        when(coinRepository.save(any(Coin.class))).thenReturn(existingCoin);
        when(coinI18nRepository.findByCoinId(coinId)).thenReturn(existingI18ns);
        
        // Act
        Coin result = coinService.updateCoin(coinId, coinName, updatedI18nNames);
        
        // Assert
        assertNotNull(result);
        assertEquals(coinName, result.getName());
        verify(coinRepository).findById(coinId);
        verify(coinRepository).save(any(Coin.class));
        
        // Verify i18n entries were updated (only zh-TW and ja)
        verify(coinI18nRepository, times(2)).save(coinI18nCaptor.capture());
        
        // Check that the captured values contain updated language entries
        boolean zhTwUpdated = false;
        boolean jaUpdated = false;
        boolean frInCaptor = false;
        
        for (CoinI18n i18n : coinI18nCaptor.getAllValues()) {
            if ("zh-TW".equals(i18n.getLangCode()) && "比特幣-更新".equals(i18n.getName())) {
                zhTwUpdated = true;
            } else if ("ja".equals(i18n.getLangCode()) && "ビットコイン-更新".equals(i18n.getName())) {
                jaUpdated = true;
            } else if ("fr".equals(i18n.getLangCode())) {
                frInCaptor = true;
            }
        }
        
        assertTrue(zhTwUpdated, "Traditional Chinese translation should be updated");
        assertTrue(jaUpdated, "Japanese translation should be updated");
        assertFalse(frInCaptor, "French translation should not be in the captor as it was not in the update request");
        
        // Verify the French translation is preserved by not being deleted
        verify(coinI18nRepository, never()).delete(any(CoinI18n.class));
        verify(coinI18nRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent coin with i18n names")
    void shouldThrowExceptionWhenUpdatingNonExistentCoinWithI18nNames() {
        // Arrange
        Long coinId = 1L;
        String newName = "Bitcoin Updated";
        Map<String, String> i18nNames = new HashMap<>();
        i18nNames.put("zh-TW", "比特幣");
        
        when(coinRepository.findById(coinId)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coinService.updateCoin(coinId, newName, i18nNames);
        });
        
        assertEquals("Coin with ID " + coinId + " not found", exception.getMessage());
        verify(coinRepository).findById(coinId);
        verify(coinRepository, never()).save(any(Coin.class));
        verify(coinI18nRepository, never()).save(any(CoinI18n.class));
    }
}