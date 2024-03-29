package com.kerneldc.ipm.repository.instrumentdetail;

import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.ipm.domain.InvestmentPortfolioTableEnum;
import com.kerneldc.ipm.domain.instrumentdetail.InstrumentStock;
import com.kerneldc.ipm.repository.BaseInstrumentDetailRepository;

public interface InstrumentStockRepository extends BaseInstrumentDetailRepository<InstrumentStock, Long> {
	
	@Override
	default IEntityEnum canHandle() {
		return InvestmentPortfolioTableEnum.INSTRUMENT_STOCK;
	}
	
}
