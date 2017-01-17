package org.bookie.util;

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.bookie.model.BookingPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class BookingPatternConverter implements AttributeConverter<BookingPattern, String> {
	private static final Logger log = LoggerFactory.getLogger(BookingPatternConverter.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String convertToDatabaseColumn(final BookingPattern attribute) {
		String result = null;
		try {
			result = this.objectMapper.writeValueAsString(attribute);
		} catch (final JsonProcessingException e) {
			log.error("Cannot convert BookingPattern to String: {}", e.getMessage(), e);
		}
		return result;
	}

	@Override
	public BookingPattern convertToEntityAttribute(final String dbData) {
		BookingPattern result = null;
		try {
			result = this.objectMapper.readValue(dbData, BookingPattern.class);
		} catch (final IOException e) {
			log.error("Cannot convert String to BookingPattern: {}", e.getMessage(), e);
		}
		return result;
	}

}
