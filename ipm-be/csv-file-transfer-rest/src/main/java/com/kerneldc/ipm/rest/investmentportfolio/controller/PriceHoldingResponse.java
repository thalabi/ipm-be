package com.kerneldc.ipm.rest.investmentportfolio.controller;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PriceHoldingResponse {

	private String message;
	private LocalDateTime timestamp;
}
