package io.jistud.coindesk.dto;

import java.time.LocalDateTime;

/** Transformed response that combines external CoinDesk API data with the localized name */
public class CoinDeskTransformedResponse {
  private String name;
  private String localizedName;
  private LocalDateTime updateTime;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocalizedName() {
    return localizedName;
  }

  public void setLocalizedName(String localizedName) {
    this.localizedName = localizedName;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }
}
