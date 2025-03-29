package io.jistud.coindesk.dto;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request DTO for creating a new coin */
public class CoinCreateRequest {

  @Schema(description = "Name of the cryptocurrency", example = "Bitcoin", required = true)
  @NotBlank(message = "Coin name is required")
  @Size(min = 1, max = 255, message = "Coin name must be between 1 and 255 characters")
  private String name;

  @Schema(
      description = "Map of language codes to localized coin names",
      example = "{\"en\":\"Bitcoin\",\"es\":\"Bitcóin\",\"ja\":\"ビットコイン\"}",
      required = false)
  private Map<String, String> i18nNames;

  // Getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getI18nNames() {
    return i18nNames;
  }

  public void setI18nNames(Map<String, String> i18nNames) {
    this.i18nNames = i18nNames;
  }
}
