package com.kerneldc.ipm.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.kerneldc.common.domain.AbstractPersistableEntity;
import com.kerneldc.common.domain.LogicalKeyHolder;
import com.opencsv.bean.CsvBindByName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "inst_rtf")
@SequenceGenerator(name = "default_seq_gen", sequenceName = "inst_rtf_seq", allocationSize = 1)
@Getter @Setter
public class InstrumentEtf extends AbstractPersistableEntity {
	
	private static final long serialVersionUID = 1L;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;
	
	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private String exchange;


	public void setExchange(String exchange) {
		this.exchange = exchange;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		var logicalKeyHolder = LogicalKeyHolder.build(instrument.getTicker(), exchange);
		setLogicalKeyHolder(logicalKeyHolder);
	}
}
