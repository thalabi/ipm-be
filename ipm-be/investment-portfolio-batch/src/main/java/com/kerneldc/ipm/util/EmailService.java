package com.kerneldc.ipm.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.kerneldc.common.exception.ApplicationException;
import com.kerneldc.ipm.domain.HoldingPriceInterdayV;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	@Value("${application.email.resetPasswordEmailFrom}")
	private String resetPasswordEmailFrom;
	@Value("${application.email.dailyMarketValueNotificationFrom}")
	private String dailyMarketValueNotificationFrom;
	@Value("${application.email.dailyMarketValueNotificationTo}")
	private String dailyMarketValueNotificationTo;

	private static final String RESET_PASSWORD_EMAIL_SUBJECT = "Reset password";
	private static final String RESET_PASSWORD_CONFIRMATION_EMAIL_SUBJECT = "Reset password confirmation";
	private static final String DAILY_MARKET_VALUE_NOTIFICATION_SUBJECT = "Daily Market Value";
	private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "resetPasswordEmail.ftlh";
	private static final String RESET_PASSWORD_CONFIRMATION_EMAIL_TEMPLATE = "resetPasswordConfirmationEmail.ftlh";
	private static final String DAILY_MARKET_VALUE_NOTIFICATION_TEMPLATE = "dailyMarketValueNotification.ftlh";
	private static final String DAILY_MARKET_VALUE_FAILURE_TEMPLATE = "dailyMarketValueFailure.ftlh";
	private JavaMailSender javaMailSender;
	private Configuration freeMarkerConfiguration;
	private int resetPasswordJwtExpiryInMinutes;
	
	public EmailService(JavaMailSender emailSender, Configuration freeMarkerConfiguration, @Value("${application.security.jwt.token.resetPasswordJwtExpiryInMinutes:60}" /* default of 1 hour */) int resetPasswordJwtExpiryInMinutes) {
		this.javaMailSender = emailSender;
		this.freeMarkerConfiguration = freeMarkerConfiguration;
		this.resetPasswordJwtExpiryInMinutes = resetPasswordJwtExpiryInMinutes;
	}

	public void sendPasswordResetEmail(String to, String resetPasswordUrl) throws MessagingException, IOException, TemplateException {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		mimeMessageHelper.setFrom(resetPasswordEmailFrom);
		mimeMessageHelper.setTo(to);
		mimeMessageHelper.setSubject(RESET_PASSWORD_EMAIL_SUBJECT);
		mimeMessageHelper.setText(processResetPasswordTemplate(resetPasswordJwtExpiryInMinutes/60, resetPasswordUrl), true);
		javaMailSender.send(mimeMessage);
		LOGGER.info("Sent password reset email to: {}", to);
	}
	
	public void sendPasswordResetConfirmationEmail(String to, String loginUrl) throws MessagingException, IOException, TemplateException {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		mimeMessageHelper.setFrom(resetPasswordEmailFrom);
		mimeMessageHelper.setTo(to);
		mimeMessageHelper.setSubject(RESET_PASSWORD_CONFIRMATION_EMAIL_SUBJECT);
		mimeMessageHelper.setText(processResetPasswordConfirmationTemplate(loginUrl), true);
		javaMailSender.send(mimeMessage);
		LOGGER.info("Sent password reset confirmation email to: {}", to);
	}

	public void sendSmsEmail(String from, String to, String smsMessage) throws MessagingException, IOException, TemplateException {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		mimeMessageHelper.setFrom(from);
		mimeMessageHelper.setTo(InternetAddress.parse(to));
		mimeMessageHelper.setText(smsMessage);
		javaMailSender.send(mimeMessage);
		LOGGER.info("Sent sms email to: {}", to);
	}
	
	public void sendDailyMarketValueNotification(LocalDateTime todaysSnapshot, BigDecimal todaysMarketValue, Float percentChange, List<HoldingPriceInterdayV> nMarketValues, ApplicationException priceHoldingsExceptions) throws ApplicationException {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(dailyMarketValueNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(dailyMarketValueNotificationTo));
			mimeMessageHelper.setSubject(DAILY_MARKET_VALUE_NOTIFICATION_SUBJECT);
			mimeMessageHelper.setText(processDailyMarketValueNotificationTemplate(todaysSnapshot, todaysMarketValue, percentChange, nMarketValues, priceHoldingsExceptions), true);
			javaMailSender.send(mimeMessage);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending Daily Market Value Notification email."; 
			LOGGER.error(message, e);
			throw new ApplicationException(message + " (" + e.getMessage() + ")");
			
		}
		LOGGER.info("Sent daily market value notification email to: {}", dailyMarketValueNotificationTo);
	}
	public void sendDailyMarketValueFailure(ApplicationException priceHoldingsExceptions) throws ApplicationException {
		var mimeMessage = javaMailSender.createMimeMessage();
		var mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
		try {
			mimeMessageHelper.setFrom(dailyMarketValueNotificationFrom);
			mimeMessageHelper.setTo(InternetAddress.parse(dailyMarketValueNotificationTo));
			mimeMessageHelper.setSubject(DAILY_MARKET_VALUE_NOTIFICATION_SUBJECT);
			mimeMessageHelper.setText(processDailyMarketValueFailureTemplate(priceHoldingsExceptions), true);
			javaMailSender.send(mimeMessage);
		} catch (MessagingException | IOException | TemplateException e) {
			var message = "Exception while sending Daily Market Value Failure email."; 
			LOGGER.error(message, e);
			throw new ApplicationException(message + " (" + e.getMessage() + ")");
			
		}
		LOGGER.info("Sent daily market value notification email to: {}", dailyMarketValueNotificationTo);
	}
	
	private String processResetPasswordTemplate(int linkExpiryInHours, String resetPasswordUrl) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("linkExpiryInHours", linkExpiryInHours);
		templateModelMap.put("resetPasswordUrl", resetPasswordUrl);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(RESET_PASSWORD_EMAIL_TEMPLATE), templateModelMap);
	}
	private String processResetPasswordConfirmationTemplate(String loginUrl) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("loginUrl", loginUrl);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(RESET_PASSWORD_CONFIRMATION_EMAIL_TEMPLATE), templateModelMap);
	}
	
	private String processDailyMarketValueNotificationTemplate(LocalDateTime todaysSnapshot, BigDecimal todaysMarketValue, Float percentChange, List<HoldingPriceInterdayV> nMarketValues, ApplicationException priceHoldingsExceptions) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("todaysSnapshot", TimeUtils.toDate(todaysSnapshot));
		templateModelMap.put("todaysMarketValue", todaysMarketValue);
		templateModelMap.put("percentChange", percentChange);
		templateModelMap.put("nMarketValues", nMarketValues);
		templateModelMap.put("priceHoldingsExceptions", priceHoldingsExceptions);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(DAILY_MARKET_VALUE_NOTIFICATION_TEMPLATE), templateModelMap);
	}
	private String processDailyMarketValueFailureTemplate(ApplicationException priceHoldingsExceptions) throws IOException, TemplateException {
		Map<String, Object> templateModelMap = new HashMap<>();
		templateModelMap.put("priceHoldingsExceptions", priceHoldingsExceptions);
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(DAILY_MARKET_VALUE_FAILURE_TEMPLATE), templateModelMap);
	}
}
