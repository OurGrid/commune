package br.edu.ufcg.lsd.commune.container.logging.layout;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

import br.edu.ufcg.lsd.commune.container.logging.parser.SyncPatternParser;

public class SyncPatternLayout extends PatternLayout{

	// output buffer appended to when format() is invoked
	private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

	private String pattern;

	private PatternConverter head;

	public SyncPatternLayout(){
	}

	/**
    Constructs a SyncPatternLayout using the supplied conversion pattern.
	 */
	public SyncPatternLayout(String pattern) {
		this.pattern = pattern;
		this.head = createPatternParser((pattern == null) ? DEFAULT_CONVERSION_PATTERN : pattern).parse();
	}

	protected SyncPatternParser createPatternParser(String pattern) {
		return new SyncPatternParser(pattern);
	}

	/**
    Set the <b>ConversionPattern</b> option. This is the string which
    controls formatting and consists of a mix of literal content and
    conversion specifiers.
	 */
	public void setConversionPattern(String conversionPattern) {
		pattern = conversionPattern;
		head = createPatternParser(conversionPattern).parse();
	}

	/**
    Produces a formatted string as specified by the conversion pattern.
	 */
	public String format(LoggingEvent event) {
		// Reset working stringbuffer
		if(sbuf.capacity() > MAX_CAPACITY) {
			sbuf = new StringBuffer(BUF_SIZE);
		} else {
			sbuf.setLength(0);
		}

		PatternConverter c = head;

		while(c != null) {
			c.format(sbuf, event);
			c = c.next;
		}
		return sbuf.toString();
	}
}
