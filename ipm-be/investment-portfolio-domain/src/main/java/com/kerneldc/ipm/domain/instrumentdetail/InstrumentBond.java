package com.kerneldc.ipm.domain.instrumentdetail;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.kerneldc.common.domain.LogicalKeyHolder;
import com.kerneldc.ipm.domain.PaymentFrequencyEnum;
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

@Entity(name = "inst_bond")
@SequenceGenerator(name = "default_seq_gen", sequenceName = "inst_bond_seq", allocationSize = 1)
@EntityListeners(FixedIncomeListener.class)
@Getter @Setter
public class InstrumentBond extends AbstractInstrumentDetailEntity implements IFixedPriceInstrumentDetail {
	
	private static final long serialVersionUID = 1L;

	@CsvBindByName
	private String issuer;
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private String cusip;
	@CsvBindByName
	private BigDecimal price;
	@CsvBindByName
	private BigDecimal coupon;
	@CsvBindByName
	private OffsetDateTime issueDate;
	@CsvBindByName
	private OffsetDateTime maturityDate;
	@Enumerated(EnumType.STRING)
	@CsvBindByName
	private PaymentFrequencyEnum paymentFrequency;
	@CsvBindByName
	private OffsetDateTime nextPaymentDate;
	@CsvBindByName
	private Boolean emailNotification;

	public void setCusip(String cusip) {
		this.cusip = cusip;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(cusip);
		setLogicalKeyHolder(logicalKeyHolder);
	}
}
