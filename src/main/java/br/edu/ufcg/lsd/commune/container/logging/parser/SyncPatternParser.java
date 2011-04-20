package br.edu.ufcg.lsd.commune.container.logging.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class SyncPatternParser extends PatternParser{

	static final int FULL_LOCATION_CONVERTER = 1000;
	static final int METHOD_LOCATION_CONVERTER = 1001;
	static final int CLASS_LOCATION_CONVERTER = 1002;
	static final int LINE_LOCATION_CONVERTER = 1003;
	static final int FILE_LOCATION_CONVERTER = 1004;

	static final int RELATIVE_TIME_CONVERTER = 2000;
	static final int THREAD_CONVERTER = 2001;
	static final int LEVEL_CONVERTER = 2002;
	static final int NDC_CONVERTER = 2003;
	static final int MESSAGE_CONVERTER = 2004;

	public SyncPatternParser(String pattern) {
		super(pattern);
	}

	protected
	void finalizeConverter(char c) {
		PatternConverter pc = null;
		switch(c) {
		case 'c':
			pc = new CategoryPatternConverter(formattingInfo,
					extractPrecisionOption());
			//LogLog.debug("CATEGORY converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'C':
			pc = new ClassNamePatternConverter(formattingInfo,
					extractPrecisionOption());
			//LogLog.debug("CLASS_NAME converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'd':
			String dateFormatStr = AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT;
			DateFormat df;
			String dOpt = extractOption();
			if(dOpt != null)
				dateFormatStr = dOpt;

			if(dateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT))
				df = new  ISO8601DateFormat();
			else if(dateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT))
				df = new AbsoluteTimeDateFormat();
			else if(dateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT))
				df = new DateTimeDateFormat();
			else {
				try {
					df = new SimpleDateFormat(dateFormatStr);
				}
				catch (IllegalArgumentException e) {
					LogLog.error("Could not instantiate SimpleDateFormat with " +
							dateFormatStr, e);
					df = (DateFormat) OptionConverter.instantiateByClassName(
							"org.apache.log4j.helpers.ISO8601DateFormat",
							DateFormat.class, null);
				}
			}
			pc = new DatePatternConverter(formattingInfo, df);
			//LogLog.debug("DATE converter {"+dateFormatStr+"}.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'D':
			String syncDateFormatStr = AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT;
			DateFormat sdf;
			String sdOpt = extractOption();
			if(sdOpt != null)
				syncDateFormatStr = sdOpt;

			if(syncDateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT))
				sdf = new  ISO8601DateFormat();
			else if(syncDateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT))
				sdf = new AbsoluteTimeDateFormat();
			else if(syncDateFormatStr.equalsIgnoreCase(
					AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT))
				sdf = new DateTimeDateFormat();
			else {
				try {
					sdf = new SimpleDateFormat(syncDateFormatStr);
				}
				catch (IllegalArgumentException e) {
					LogLog.error("Could not instantiate SimpleDateFormat with " +
							syncDateFormatStr, e);
					sdf = (DateFormat) OptionConverter.instantiateByClassName(
							"org.apache.log4j.helpers.ISO8601DateFormat",
							DateFormat.class, null);
				}
			}
			pc = new SyncDatePatternConverter(formattingInfo, sdf);
			//LogLog.debug("DATE converter {"+dateFormatStr+"}.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'F':
			pc = new LocationPatternConverter(formattingInfo,
					FILE_LOCATION_CONVERTER);
			//LogLog.debug("File name converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'l':
			pc = new LocationPatternConverter(formattingInfo,
					FULL_LOCATION_CONVERTER);
			//LogLog.debug("Location converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'L':
			pc = new LocationPatternConverter(formattingInfo,
					LINE_LOCATION_CONVERTER);
			//LogLog.debug("LINE NUMBER converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'm':
			pc = new BasicPatternConverter(formattingInfo, MESSAGE_CONVERTER);
			//LogLog.debug("MESSAGE converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'M':
			pc = new LocationPatternConverter(formattingInfo,
					METHOD_LOCATION_CONVERTER);
			//LogLog.debug("METHOD converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'p':
			pc = new BasicPatternConverter(formattingInfo, LEVEL_CONVERTER);
			//LogLog.debug("LEVEL converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 'r':
			pc = new BasicPatternConverter(formattingInfo,
					RELATIVE_TIME_CONVERTER);
			//LogLog.debug("RELATIVE time converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
		case 't':
			pc = new BasicPatternConverter(formattingInfo, THREAD_CONVERTER);
			//LogLog.debug("THREAD converter.");
			//formattingInfo.dump();
			currentLiteral.setLength(0);
			break;
			/*case 'u':
	      if(i < patternLength) {
		char cNext = pattern.charAt(i);
		if(cNext >= '0' && cNext <= '9') {
		  pc = new UserFieldPatternConverter(formattingInfo, cNext - '0');
		  LogLog.debug("USER converter ["+cNext+"].");
		  formattingInfo.dump();
		  currentLiteral.setLength(0);
		  i++;
		}
		else
		  LogLog.error("Unexpected char" +cNext+" at position "+i);
	      }
	      break;*/
		case 'x':
			pc = new BasicPatternConverter(formattingInfo, NDC_CONVERTER);
			//LogLog.debug("NDC converter.");
			currentLiteral.setLength(0);
			break;
		case 'X':
			String xOpt = extractOption();
			pc = new MDCPatternConverter(formattingInfo, xOpt);
			currentLiteral.setLength(0);
			break;
		default:
			LogLog.error("Unexpected char [" +c+"] at position "+i
					+" in conversion patterrn.");
			pc = new LiteralPatternConverter(currentLiteral.toString());
			currentLiteral.setLength(0);
		}

		addConverter(pc);
	}

	// ---------------------------------------------------------------------
	//                      PatternConverters
	// ---------------------------------------------------------------------

	private static class BasicPatternConverter extends PatternConverter {
		int type;

		BasicPatternConverter(FormattingInfo formattingInfo, int type) {
			super(formattingInfo);
			this.type = type;
		}

		public
		String convert(LoggingEvent event) {
			switch(type) {
			case RELATIVE_TIME_CONVERTER:
				return (Long.toString(event.timeStamp - LoggingEvent.getStartTime()));
			case THREAD_CONVERTER:
				return event.getThreadName();
			case LEVEL_CONVERTER:
				return event.getLevel().toString();
			case NDC_CONVERTER:
				return event.getNDC();
			case MESSAGE_CONVERTER: {
				return event.getRenderedMessage();
			}
			default: return null;
			}
		}
	}

	private static class LiteralPatternConverter extends PatternConverter {
		private String literal;

		LiteralPatternConverter(String value) {
			literal = value;
		}

		public
		final
		void format(StringBuffer sbuf, LoggingEvent event) {
			sbuf.append(literal);
		}

		public
		String convert(LoggingEvent event) {
			return literal;
		}
	}

	private static class DatePatternConverter extends PatternConverter {
		private DateFormat df;
		private Date date;

		DatePatternConverter(FormattingInfo formattingInfo, DateFormat df) {
			super(formattingInfo);
			date = new Date();
			this.df = df;
		}

		public
		String convert(LoggingEvent event) {
			date.setTime(event.timeStamp);
			String converted = null;
			try {
				converted = df.format(date);
			}
			catch (Exception ex) {
				LogLog.error("Error occured while converting date.", ex);
			}
			return converted;
		}
	}

	private static class SyncDatePatternConverter extends PatternConverter {

		private static final String IS_LOCAL = " (local-timestamp)";
		private DateFormat df;
		private Date date;

		SyncDatePatternConverter(FormattingInfo formattingInfo, DateFormat df) {
			super(formattingInfo);
			date = new Date();
			this.df = df;
		}

		public
		String convert(LoggingEvent event) {
			date.setTime(event.timeStamp);
			String converted = null;
			try {
				converted = df.format(date);
				if(event.isLocalTimeStamp()){
					converted += IS_LOCAL;
				}
			}
			catch (Exception ex) {
				LogLog.error("Error occured while converting date.", ex);
			}
			return converted;
		}
	}

	private static class MDCPatternConverter extends PatternConverter {
		private String key;

		MDCPatternConverter(FormattingInfo formattingInfo, String key) {
			super(formattingInfo);
			this.key = key;
		}

		public
		String convert(LoggingEvent event) {
			Object val = event.getMDC(key);
			if(val == null) {
				return null;
			} else {
				return val.toString();
			}
		}
	}


	private class LocationPatternConverter extends PatternConverter {
		int type;

		LocationPatternConverter(FormattingInfo formattingInfo, int type) {
			super(formattingInfo);
			this.type = type;
		}

		public
		String convert(LoggingEvent event) {
			LocationInfo locationInfo = event.getLocationInformation();
			switch(type) {
			case FULL_LOCATION_CONVERTER:
				return locationInfo.fullInfo;
			case METHOD_LOCATION_CONVERTER:
				return locationInfo.getMethodName();
			case LINE_LOCATION_CONVERTER:
				return locationInfo.getLineNumber();
			case FILE_LOCATION_CONVERTER:
				return locationInfo.getFileName();
			default: return null;
			}
		}
	}

	private static abstract class NamedPatternConverter extends PatternConverter {
		int precision;

		NamedPatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo);
			this.precision =  precision;
		}

		abstract
		String getFullyQualifiedName(LoggingEvent event);

		public
		String convert(LoggingEvent event) {
			String n = getFullyQualifiedName(event);
			if(precision <= 0)
				return n;
			else {
				int len = n.length();

				// We substract 1 from 'len' when assigning to 'end' to avoid out of
				// bounds exception in return r.substring(end+1, len). This can happen if
				// precision is 1 and the category name ends with a dot.
				int end = len -1 ;
				for(int i = precision; i > 0; i--) {
					end = n.lastIndexOf('.', end-1);
					if(end == -1)
						return n;
				}
				return n.substring(end+1, len);
			}
		}
	}

	private class ClassNamePatternConverter extends NamedPatternConverter {

		ClassNamePatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo, precision);
		}

		String getFullyQualifiedName(LoggingEvent event) {
			return event.getLocationInformation().getClassName();
		}
	}

	private class CategoryPatternConverter extends NamedPatternConverter {

		CategoryPatternConverter(FormattingInfo formattingInfo, int precision) {
			super(formattingInfo, precision);
		}

		String getFullyQualifiedName(LoggingEvent event) {
			return event.getLoggerName();
		}
	}

}
