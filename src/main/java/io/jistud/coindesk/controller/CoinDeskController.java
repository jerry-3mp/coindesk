package io.jistud.coindesk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jistud.coindesk.dto.CoinDeskResponse;
import io.jistud.coindesk.dto.ErrorResponse;
import io.jistud.coindesk.service.CoinDeskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/coindesk")
@Tag(name = "CoinDesk API", description = "Operations related to CoinDesk data")
public class CoinDeskController {

  private final CoinDeskService coinDeskService;

  @Autowired
  public CoinDeskController(CoinDeskService coinDeskService) {
    this.coinDeskService = coinDeskService;
  }

  /**
   * Get data from external CoinDesk API
   *
   * @return Current Bitcoin price information from CoinDesk API
   */
  @Operation(
      summary = "Get data from external CoinDesk API",
      description = "Retrieves current Bitcoin price information from the CoinDesk API")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved data",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CoinDeskResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while fetching data from external API",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @GetMapping
  public ResponseEntity<CoinDeskResponse> getCoinDeskData() {
    CoinDeskResponse response = coinDeskService.getCurrentBitcoinPrice();
    return ResponseEntity.ok(response);
  }
}
