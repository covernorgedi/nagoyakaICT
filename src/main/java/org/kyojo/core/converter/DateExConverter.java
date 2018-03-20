package org.kyojo.core.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.beanutils.converters.DateTimeConverter;

public class DateExConverter extends DateTimeConverter {

	public static final String[] STD_PATTERNS = {
			"yyyy-MM-dd'T'HH:mm:ssXXX",
			"yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm:ss'Z'",
			"yyyy-MM-dd",
			"HH:mm",
			"HH:mm:ss",
			"HH:mm:ssXXX"
		};

	public DateExConverter() {
		super();
		setPatterns(STD_PATTERNS);
	}

	public DateExConverter(final Object defaultValue) {
		super(defaultValue);
		setPatterns(STD_PATTERNS);
	}

	@Override
	protected Class<?> getDefaultType() {
		return Date.class;
	}

	@Override
	protected String convertToString(final Object value) throws Throwable {
		Date date = null;
		if(value instanceof Date) {
			date = (Date)value;
		} else if(value instanceof Calendar) {
			date = ((Calendar)value).getTime();
		} else if(value instanceof Long) {
			date = new Date(((Long)value).longValue());
		}

		String result = null;
		if(date != null) {
			DateFormat format = getFormat(getLocale(), getTimeZone());
			result = format.format(date);
		} else {
			result = value.toString();
		}

		return result;
	}

	@Override
	protected DateFormat getFormat(final Locale locale, final TimeZone timeZone) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		if(timeZone != null) {
			format.setTimeZone(timeZone);
		}
		return format;
	}

}
