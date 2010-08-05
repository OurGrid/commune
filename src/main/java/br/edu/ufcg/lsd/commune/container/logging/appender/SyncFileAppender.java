package br.edu.ufcg.lsd.commune.container.logging.appender;

import java.text.SimpleDateFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class SyncFileAppender extends FileAppender{

	
	private String datePattern = "'.'yyyy-MM-dd";
 
	@Override
	public void append(LoggingEvent event) {
		
		TimeStampClient timeStampClient = new TimeStampClient();
		long newTimeStamp = timeStampClient.getTimeStamp();
		
		LoggingEvent newEvent = new LoggingEvent(
				event.getFqnOfCategoryClass(),
				event.getLogger(),
				newTimeStamp,
				timeStampClient.isLocal(),
				event.getLevel(),
				event.getMessage(),
				event.getThrowable());
		
		super.append(newEvent);
	}
	
	/**
    The <b>DatePattern</b> takes a string in the same format as
    expected by {@link SimpleDateFormat}. This options determines the
    rollover schedule.
	**/
	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	/** Returns the value of the <b>DatePattern</b> option. */
 	public String getDatePattern() {
 		return datePattern;
 	}
}
