package com.kerneldc.ipm.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;

import com.kerneldc.common.BaseTableRepository;
import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.ipm.domain.Holding;
import com.kerneldc.ipm.domain.InvestmentPortfolioTableEnum;

public interface HoldingRepository extends BaseTableRepository<Holding, Long> {

	// TODO figure out a way to globally define a formatter
	List<Holding> findByPortfolioIdAndInstrumentIdAndAsOfDate(Long portfolioId, Long instrumentId, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime asOfDate);
	
	@Query(value = """
			select * from holding h2 where (h2.as_of_date, h2.instrument_id, h2.portfolio_id) in (
					select max(as_of_date) as_of_date, instrument_id, portfolio_id from holding h group by instrument_id, portfolio_id
			)
			""", nativeQuery = true)
	List<Holding> findLatestAsOfDateHoldings();
	
	@Query(value = """
			with
			latest_price_timestamp as (
				select instrument_id,max(price_timestamp) latest_price_timestamp from price group by instrument_id
			),
			latest_price as (
				select pr.instrument_id, pr.price latest_price, lpt.latest_price_timestamp from price pr join latest_price_timestamp lpt on pr.instrument_id = lpt.instrument_id and pr.price_timestamp = lpt.latest_price_timestamp
			)
			select h.id, cast(h.as_of_date as varchar) asOfDate, h.instrument_id instrumentId, i.ticker, i.exchange, i.currency, i.name, h.quantity, lp.latest_price latestPrice, cast(lp.latest_price_timestamp as varchar) latestPriceTimestamp, h.version from holding h
			join instrument i on h.instrument_id = i.id
			left outer join latest_price lp on h.instrument_id = lp.instrument_id
			where h.portfolio_id = :portfolioId order by h.instrument_id, h.as_of_date
			"""
			, nativeQuery = true)
	List<HoldingDetail> findByPortfolioId(Long portfolioId);

	@Override
	default IEntityEnum canHandle() {
		return InvestmentPortfolioTableEnum.HOLDING;
	}
}
