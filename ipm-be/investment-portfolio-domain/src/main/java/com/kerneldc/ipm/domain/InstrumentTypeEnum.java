package com.kerneldc.ipm.domain;

public enum InstrumentTypeEnum {
	CASH(null),
	STOCK(InvestmentPortfolioTableEnum.INSTRUMENT_STOCK),
	ETF(InvestmentPortfolioTableEnum.INSTRUMENT_ETF),
	MUTUAL_FUND(InvestmentPortfolioTableEnum.INSTRUMENT_MUTUAL_FUND),
	INTEREST_BEARING(InvestmentPortfolioTableEnum.INSTRUMENT_INTEREST_BEARING),
	BOND(InvestmentPortfolioTableEnum.INSTRUMENT_BOND);//, MONEY_MARKET, INV_SAVINGS_ACC, GIC, SAVINGS_ACC
	
	InvestmentPortfolioTableEnum investmentPortfolioTableEnum;
	InstrumentTypeEnum(InvestmentPortfolioTableEnum investmentPortfolioTableEnum) {
		this.investmentPortfolioTableEnum = investmentPortfolioTableEnum;
	}
	
	public InvestmentPortfolioTableEnum getInvestmentPortfolioTableEnum() {
		return investmentPortfolioTableEnum;
	}
}
