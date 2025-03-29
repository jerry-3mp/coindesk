package io.jistud.coindesk.controller;

import io.jistud.coindesk.dto.CoinDeskTransformedResponse;
import io.jistud.coindesk.dto.ErrorResponse;
import io.jistud.coindesk.service.CoinDeskService;
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

@RestController
@RequestMapping("/api/v1/transformed-coindesk")
@Tag(name = "Transformed CoinDesk API", description = "Operations related to transformed CoinDesk data")
public class CoinDeskTransformedController {

    private final CoinDeskService coinDeskService;

    @Autowired
    public CoinDeskTransformedController(CoinDeskService coinDeskService) {
        this.coinDeskService = coinDeskService;
    }

    /**
     * Get transformed CoinDesk data with optional language filtering
     *
     * @param langCode Optional language code for localized names
     * @return Transformed response with CoinDesk data and localized names
     */
    @Operation(
        summary = "Get transformed CoinDesk data",
        description = "Retrieves transformed Bitcoin price data with localized coin names"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved transformed data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CoinDeskTransformedResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error occurred while processing data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    @GetMapping
    public ResponseEntity<CoinDeskTransformedResponse> getTransformedCoinDeskData(
            @Parameter(description = "Language code (e.g., 'zh-TW', 'en') for localized names")
            @RequestParam(name = "lang", required = false) String langCode) {
        
        CoinDeskTransformedResponse response = coinDeskService.getTransformedCoinDeskData(langCode);
        return ResponseEntity.ok(response);
    }
}
