package com.kerneldc.ipm.repository;

import java.time.OffsetDateTime;
import java.util.List;

import com.kerneldc.common.BaseTableRepository;
import com.kerneldc.common.enums.CurrencyEnum;
import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.ipm.domain.ExchangeRate;
import com.kerneldc.ipm.domain.InvestmentPortfolioTableEnum;

public interface ExchangeRateRepository extends BaseTableRepository<ExchangeRate, Long> {
	
	List<ExchangeRate> findByDateAndFromCurrencyAndToCurrency(OffsetDateTime date, CurrencyEnum fromCurrency, CurrencyEnum toCurrency);
	
	@Override
	default IEntityEnum canHandle() {
		return InvestmentPortfolioTableEnum.EXCHANGE_RATE;
	}
	
}