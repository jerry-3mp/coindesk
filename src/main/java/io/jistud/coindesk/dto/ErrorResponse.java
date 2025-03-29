package io.jistud.coindesk.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

/** Standard error response format for API errors. */
@Schema(description = "Error information returned when a request fails")
public class ErrorResponse {

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "Error message", example = "Invalid request parameters")
  private String message;

  @Schema(
      description = "Timestamp when the error occurred in UTC",
      example = "2023-02-21T14:22:00Z")
  private Instant timestamp;

  /** Default constructor. */
  public ErrorResponse() {
    this.timestamp = Instant.now();
  }

  /**
   * Constructor with status and message.
   *
   * @param status HTTP status code
   * @param message Error message
   */
  public ErrorResponse(int status, String message) {
    this();
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }
}
