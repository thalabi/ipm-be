package com.kerneldc.ipm.rest.investmentportfolio.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.kerneldc.ipm.domain.FinancialInstitutionEnum;
import com.kerneldc.ipm.domain.HolderEnum;
import com.kerneldc.ipm.domain.Instrument;
import com.kerneldc.ipm.domain.InterestBearingTypeEnum;
import com.kerneldc.ipm.domain.RegisteredAccountEnum;
import com.kerneldc.ipm.domain.TermEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InstrumentInterestBearingRequest {

	private Long id;
    @NotNull
	private Instrument instrument;
    @NotNull
    private InterestBearingTypeEnum type;
    @NotNull
	private FinancialInstitutionEnum financialInstitution;
	@Positive
	private BigDecimal price;
    @Positive
	private Float interestRate;
	private TermEnum term;
	private OffsetDateTime maturityDate;
	private OffsetDateTime nextPaymentDate;
	private Float promotionalInterestRate;
	private OffsetDateTime promotionEndDate;
	@NotNull
	private Boolean emailNotification;
	private String accountNumber;
	private HolderEnum holder;
	private RegisteredAccountEnum registeredAccount;
	private Long rowVersion;
}
