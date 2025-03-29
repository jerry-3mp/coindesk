package io.jistud.coindesk.dto;

/**
 * A simplified representation of a Coin entity containing only basic information. Used for list
 * views and summary displays.
 */
public class CoinSummaryDto {
  private Long id;
  private String name;

  public CoinSummaryDto() {}

  public CoinSummaryDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

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
}
