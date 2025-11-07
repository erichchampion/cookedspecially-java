package com.cookedspecially.dto;

import java.sql.Timestamp;

public class HandoverRequestDTO {
  public Integer requestId;
  public String tillId;
  public String tillName;
  public Float expectedBalance;
  public String remarks;
  public String status;
  public Timestamp time;
}
