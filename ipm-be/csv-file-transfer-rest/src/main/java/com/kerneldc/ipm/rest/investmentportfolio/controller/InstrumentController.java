package com.kerneldc.ipm.rest.investmentportfolio.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kerneldc.common.exception.ApplicationException;
import com.kerneldc.ipm.batch.InstrumentDueNotificationService;
import com.kerneldc.ipm.domain.CurrencyEnum;
import com.kerneldc.ipm.domain.FinancialInstitutionEnum;
import com.kerneldc.ipm.domain.Instrument;
import com.kerneldc.ipm.domain.InstrumentTypeEnum;
import com.kerneldc.ipm.domain.InterestBearingTypeEnum;
import com.kerneldc.ipm.domain.TermEnum;
import com.kerneldc.ipm.domain.instrumentdetail.InstrumentInterestBearing;
import com.kerneldc.ipm.repository.instrumentdetail.InstrumentInterestBearingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/protected/instrumentController")
@Validated
@RequiredArgsConstructor
@Slf4j
public class InstrumentController {

	private static final String LOG_BEGIN = "Begin ...";
	private static final String LOG_END = "End ...";
	private final InstrumentInterestBearingRepository instrumentInterestBearingRepository;
	private final InstrumentDueNotificationService instrumentDueNotificationService;
	
	@Value("${instrument.due.days.to.notify}")
	private Long daysToNotify;

    @GetMapping("/getDefaultDaysToNotify")
	public ResponseEntity<Long> getDefaultDaysToNotify() throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(daysToNotify);
    }
    @GetMapping("/triggerInstrumetDueNotification")
	public ResponseEntity<BatchJobResponse> triggerInstrumetDueNotification(@RequestParam @Min(1) Long daysToNotify) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
    	var checkDueDateResponse = new BatchJobResponse();
    	try {
    		instrumentDueNotificationService.checkDueDate(daysToNotify);
	    	checkDueDateResponse.setMessage(StringUtils.EMPTY);
	    	checkDueDateResponse.setTimestamp(LocalDateTime.now());
		} catch (ApplicationException e) {
			Thread.currentThread().interrupt();
			LOGGER.error("Exception checking instrument due dates:\n", e);
	    	checkDueDateResponse.setMessage(e.getMessage());
	    	checkDueDateResponse.setTimestamp(LocalDateTime.now());
		}
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(checkDueDateResponse);
    	
    }
    @GetMapping("/getCurrencies")
	public ResponseEntity<List<CurrencyEnum>> getCurrencies() {
    	LOGGER.info(LOG_BEGIN);
    	var currencies = Arrays.asList(CurrencyEnum.values());
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(currencies);
    }
    @GetMapping("/getFinancialInstitutions")
	public ResponseEntity<List<FinancialInstitutionEnum>> getFinancialInstitutions() {
    	LOGGER.info(LOG_BEGIN);
    	var financialInstitutions = Arrays.asList(FinancialInstitutionEnum.values());
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(financialInstitutions);
    }
    
    @GetMapping("/getInstrumentTypes")
	public ResponseEntity<List<InstrumentTypeEnum>> getInstrumentTypes() {
    	LOGGER.info(LOG_BEGIN);
    	var instrumentTypes = Arrays.asList(InstrumentTypeEnum.values());
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(instrumentTypes);
    }
    @GetMapping("/getInterestBearingTypes")
	public ResponseEntity<List<InterestBearingTypeEnum>> getInterestBearingTypes() {
    	LOGGER.info(LOG_BEGIN);
    	var interestBearingTypes = Arrays.asList(InterestBearingTypeEnum.values());
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(interestBearingTypes);
    }
    @GetMapping("/getTerms")
	public ResponseEntity<List<TermEnum>> getTerms() {
    	LOGGER.info(LOG_BEGIN);
    	var terms = Arrays.asList(TermEnum.values());
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(terms);
    }

//    @GetMapping("/getInstruments")
//	public ResponseEntity<List<InstrumentInterestBearing>> getInstruments(InstrumentTypeEnum instrumentType) throws ApplicationException {
//    	LOGGER.info(LOG_BEGIN);
//    	List<InstrumentInterestBearing> instrumentInterestBearingList;
//    	switch (instrumentType) {
//    	case INTEREST_BEARING:
//    		instrumentInterestBearingList = instrumentInterestBearingRepository.findAll();
//    		break;
//    		default:
//    			throw new ApplicationException(String.format("%s is not a valid instrument type or method not implemened", instrumentType));
//    	}
//    	LOGGER.info(LOG_END);
//		return ResponseEntity.ok(instrumentInterestBearingList);
//    }

    @PostMapping("/addInstrumentInterestBearing")
    public ResponseEntity<InstrumentInterestBearingResponse> addInstrumentInterestBearing(@Valid @RequestBody InstrumentInterestBearingRequest instrumentInterestBearingRequest) throws ApplicationException {
    	LOGGER.info(LOG_BEGIN);
    	LOGGER.info("instrumentInterestBearingRequest: {}", instrumentInterestBearingRequest);
    	validateInstrumentInterestBearingRequest(instrumentInterestBearingRequest);
    	var i = new Instrument();
    	i.setType(instrumentInterestBearingRequest.getInstrument().getType());
    	setTicker(i, instrumentInterestBearingRequest);
    	i.setCurrency( instrumentInterestBearingRequest.getInstrument().getCurrency());
    	i.setName(instrumentInterestBearingRequest.getInstrument().getName());
    	var iib = new InstrumentInterestBearing();
    	iib.setInstrument(i);
    	iib.setType(instrumentInterestBearingRequest.getType());
    	iib.setFinancialInstitution(instrumentInterestBearingRequest.getFinancialInstitution());
    	iib.setPrice(instrumentInterestBearingRequest.getPrice());
    	iib.setInterestRate(instrumentInterestBearingRequest.getInterestRate());
    	iib.setTerm(instrumentInterestBearingRequest.getTerm());
    	iib.setMaturityDate(instrumentInterestBearingRequest.getMaturityDate());
    	iib.setNextPaymentDate(instrumentInterestBearingRequest.getNextPaymentDate());
    	iib.setPromotionalInterestRate(instrumentInterestBearingRequest.getPromotionalInterestRate());
    	iib.setPromotionEndDate(instrumentInterestBearingRequest.getPromotionEndDate());
    	iib.setEmailNotification(instrumentInterestBearingRequest.getEmailNotification());
    	instrumentInterestBearingRepository.save(iib);
    	LOGGER.info("InstrumentInterestBearing: {}", iib);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(new InstrumentInterestBearingResponse(StringUtils.EMPTY, iib));
    }

	private void validateInstrumentInterestBearingRequest(
			InstrumentInterestBearingRequest instrumentInterestBearingRequest) throws ApplicationException {
		var exception = new ApplicationException();
		switch (instrumentInterestBearingRequest.getType()) {
		case MONEY_MARKET, CHEQUING, INVESTMENT_SAVINGS:
			if (instrumentInterestBearingRequest.getTerm() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a term",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getMaturityDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a maturity date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getNextPaymentDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a next payment date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getPromotionalInterestRate() != null) {
				exception.addMessage(
						String.format("%s interest bearing instrument can not have a promotional interestr eate",
								instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getPromotionEndDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a prmotion end date",
						instrumentInterestBearingRequest.getType()));
			}
			if (! /* not */ exception.getMessageList().isEmpty()) {
				throw exception;
			}
			break;
		case SAVINGS:
			if (instrumentInterestBearingRequest.getTerm() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a term",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getMaturityDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a maturity date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getNextPaymentDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a next payment date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getPromotionalInterestRate() != null
					&& instrumentInterestBearingRequest.getPromotionEndDate() == null
					|| instrumentInterestBearingRequest.getPromotionalInterestRate() == null
							&& instrumentInterestBearingRequest.getPromotionEndDate() != null) {
				exception.addMessage(String.format(
						"For a %s interest bearing instrument, if a promotion is in effect, both the promotional interest rate and promotion end date must to be provided",
						instrumentInterestBearingRequest.getType()));
			}
			break;
		case GIC, TERM_DEPOSIT:
			if (instrumentInterestBearingRequest.getTerm() == null) {
				exception.addMessage(String.format("%s interest bearing instrument must have a term",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getMaturityDate() == null) {
				exception.addMessage(String.format("%s interest bearing instrument must have a maturity date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getNextPaymentDate() == null) {
				exception.addMessage(String.format("%s interest bearing instrument must have a next payment date",
						instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getPromotionalInterestRate() != null) {
				exception.addMessage(
						String.format("%s interest bearing instrument can not have a promotional interestr eate",
								instrumentInterestBearingRequest.getType()));
			}
			if (instrumentInterestBearingRequest.getPromotionEndDate() != null) {
				exception.addMessage(String.format("%s interest bearing instrument can not have a prmotion end date",
						instrumentInterestBearingRequest.getType()));
			}
			break;
		}
	}
	@DeleteMapping("/deleteInstrumentInterestBearing/{id}")
    public ResponseEntity<InstrumentInterestBearingResponse> deleteInstrumentInterestBearing(@PathVariable Long id) {
    	LOGGER.info(LOG_BEGIN);
    	LOGGER.info("id: {}", id);
    	instrumentInterestBearingRepository.deleteById(id);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(new InstrumentInterestBearingResponse(StringUtils.EMPTY, null));
    }
    @PutMapping("/updateInstrumentInterestBearing/{id}")
    public ResponseEntity<InstrumentInterestBearingResponse> updateInstrumentInterestBearing(@PathVariable Long id, @Valid @RequestBody InstrumentInterestBearingRequest instrumentInterestBearingRequest) {
    	LOGGER.info(LOG_BEGIN);
    	LOGGER.info("id: {}", id);
    	var iib = instrumentInterestBearingRepository.findById(id).orElse(null);
    	if (iib == null) {
        	LOGGER.info(LOG_END);
    		return ResponseEntity.ok(new InstrumentInterestBearingResponse("Interest bearing instrument not found", null));
    	}
    	var i = iib.getInstrument();
    	setTicker(i, instrumentInterestBearingRequest);
    	i.setCurrency( instrumentInterestBearingRequest.getInstrument().getCurrency());
    	i.setName(instrumentInterestBearingRequest.getInstrument().getName());
    	iib.setInstrument(i);
    	iib.setType(instrumentInterestBearingRequest.getType());
    	iib.setFinancialInstitution(instrumentInterestBearingRequest.getFinancialInstitution());
    	iib.setPrice(instrumentInterestBearingRequest.getPrice());
    	iib.setInterestRate(instrumentInterestBearingRequest.getInterestRate());
    	iib.setTerm(instrumentInterestBearingRequest.getTerm());
    	iib.setMaturityDate(instrumentInterestBearingRequest.getMaturityDate());
    	iib.setPromotionalInterestRate(instrumentInterestBearingRequest.getPromotionalInterestRate());
    	iib.setNextPaymentDate(instrumentInterestBearingRequest.getNextPaymentDate());
    	iib.setPromotionEndDate(instrumentInterestBearingRequest.getPromotionEndDate());
    	iib.setEmailNotification(instrumentInterestBearingRequest.getEmailNotification());
    	instrumentInterestBearingRepository.save(iib);
    	LOGGER.info("InstrumentInterestBearing: {}", iib);
    	LOGGER.info(LOG_END);
    	return ResponseEntity.ok(new InstrumentInterestBearingResponse(StringUtils.EMPTY, iib));
    }

    private void setTicker(Instrument instrument, InstrumentInterestBearingRequest instrumentInterestBearingRequest) {
		if (Arrays.asList(InterestBearingTypeEnum.MONEY_MARKET, InterestBearingTypeEnum.INVESTMENT_SAVINGS)
				.contains(instrumentInterestBearingRequest.getType())) {
    		instrument.setTicker(instrumentInterestBearingRequest.getInstrument().getTicker());
    	} else {
    		LOGGER.info("md5 of [{}] is [{}]", instrumentInterestBearingRequest.getInstrument().getName(), md5(instrumentInterestBearingRequest.getInstrument().getName()));
    		instrument.setTicker(md5(instrumentInterestBearingRequest.getInstrument().getName()));
    	}
    	LOGGER.info("i.getTicker(): {}", instrument.getTicker());
    }
    private static String md5(String name) {
    	var hashBytes = DigestUtils.md5Digest(name.getBytes());
    	// Remove null bytes from hash because postgres does not allow nulls in varchar
    	var noNullHashBytesList = new ArrayList<Byte>();
    	for (byte hashByte: hashBytes) {
    		LOGGER.info("hashByte [{}]", hashByte);
    		if (hashByte == 0) continue;
    		noNullHashBytesList.add(hashByte);
    	}
    	for (byte noNullHashByte: noNullHashBytesList) {
    		LOGGER.info("noNullHashByte [{}]", noNullHashByte);
    	}
    	LOGGER.info("noNullHashBytes.size(): {}", noNullHashBytesList.size());
    	var noNullHashBytesArray = noNullHashBytesList.toArray(new Byte[noNullHashBytesList.size()]);
    	return new String(ArrayUtils.toPrimitive(noNullHashBytesArray), StandardCharsets.UTF_8);
    }
}
