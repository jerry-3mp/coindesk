package io.jistud.coindesk.controller;

import io.jistud.coindesk.dto.CoinSummaryDto;
import io.jistud.coindesk.dto.ErrorResponse;
import io.jistud.coindesk.entity.Coin;
import io.jistud.coindesk.service.CoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/coins")
@Tag(name = "Coin Management", description = "APIs for managing cryptocurrency coin information")
public class CoinController {
    
    private final CoinService coinService;
    
    @Autowired
    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }
    
    /**
     * Get all coins or filter by ID or name
     * 
     * @param id Optional ID to filter by
     * @param name Optional name to filter by
     * @return List of coins matching criteria
     * @throws IllegalArgumentException if both id and name parameters are provided
     */
    @Operation(summary = "Get coins", description = "Retrieve all coins or filter by ID or name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CoinSummaryDto>> getCoins(
            @Parameter(description = "Coin ID to filter by") @RequestParam(required = false) Long id,
            @Parameter(description = "Coin name to filter by") @RequestParam(required = false) String name) {
        
        // Check if both id and name parameters are provided
        if (id != null && name != null) {
            throw new IllegalArgumentException("Cannot filter by both id and name simultaneously");
        }
        
        List<Coin> coins = new ArrayList<>();
        
        // Filter by ID if provided
        if (id != null) {
            Optional<Coin> coin = coinService.findById(id);
            coin.ifPresent(coins::add);
        } 
        // Filter by name if provided
        else if (name != null) {
            Optional<Coin> coin = coinService.findByName(name);
            coin.ifPresent(coins::add);
        } 
        // Return all coins if no filter is provided
        else {
            coins = coinService.findAll();
        }
        
        // Convert to summary DTOs
        List<CoinSummaryDto> response = coins.stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Converts a Coin entity to a CoinSummaryDto
     * 
     * @param coin The coin entity
     * @return CoinSummaryDto with basic coin information
     */
    private CoinSummaryDto convertToSummary(Coin coin) {
        return new CoinSummaryDto(coin.getId(), coin.getName());
    }
}
