package com.kerneldc.ipm.domain.instrumentdetail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.kerneldc.common.domain.LogicalKeyHolder;
import com.kerneldc.ipm.domain.FinancialInstitutionEnum;
import com.kerneldc.ipm.domain.HolderEnum;
import com.kerneldc.ipm.domain.InterestBearingTypeEnum;
import com.kerneldc.ipm.domain.RegisteredAccountEnum;
import com.kerneldc.ipm.domain.TermEnum;
import com.kerneldc.ipm.domain.listener.FixedIncomeListener;
import com.opencsv.bean.CsvBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "inst_interest_bearing")
@SequenceGenerator(name = "default_seq_gen", sequenceName = "inst_interest_bearing_seq", allocationSize = 1)
@EntityListeners(FixedIncomeListener.class)
@Getter @Setter
public class InstrumentInterestBearing extends AbstractInstrumentDetailEntity implements IFixedPriceInstrumentDetail {
	
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private InterestBearingTypeEnum type;
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private FinancialInstitutionEnum financialInstitution;
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private BigDecimal price;
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private Float interestRate;
	@Enumerated(EnumType.STRING)
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private TermEnum term;
	@CsvBindByName
	private OffsetDateTime maturityDate;
	@CsvBindByName
	private OffsetDateTime nextPaymentDate;
	@CsvBindByName
	private Float promotionalInterestRate;
	@CsvBindByName
	private OffsetDateTime promotionEndDate;
	@CsvBindByName
	private Boolean emailNotification;
	@Enumerated(EnumType.STRING)
	@CsvBindByName
	private HolderEnum holder;
	@CsvBindByName(column = "account_number")
	private String accountNumber;
	@Enumerated(EnumType.STRING)
	@CsvBindByName(column = "registered_account")
	private RegisteredAccountEnum registeredAccount;

	public void setType(InterestBearingTypeEnum type) {
		this.type = type;
		setLogicalKeyHolder();
	}
	
	public void setFinancialInstitution(FinancialInstitutionEnum financialInstitution) {
		this.financialInstitution = financialInstitution;
		setLogicalKeyHolder();
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
		setLogicalKeyHolder();
	}
	
	public void setInterestRate(Float interestRate) {
		this.interestRate = interestRate;
		setLogicalKeyHolder();
	}
	
	public void setTerm(TermEnum term) {
		this.term = term;
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(instrument.getTicker(), type, financialInstitution, price, interestRate, term);
		setLogicalKeyHolder(logicalKeyHolder);
	}
}
