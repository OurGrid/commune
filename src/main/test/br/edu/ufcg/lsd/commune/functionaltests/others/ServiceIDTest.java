package br.edu.ufcg.lsd.commune.functionaltests.others;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ServiceIDTest {

	private String validServiceID01 = "peer@xmpp.org/abcd/abcd";
	private String validServiceID02 = "peer@xmpp.org/abcd/abcd/ai";
	private String validServiceID03 = "peer@xmpp.org/a/a";
	private String validServiceID04 = "peer@xmpp.org/abcd/abcd/ai/aj/ad/a/b/c/d";
	private String notvalidServiceID01 = "peer@xmpp.org/abcd/";
	private String notvalidServiceID02 = "peer@xmpp.org/abcd";
	private String notvalidServiceID03 = "peer@xmpp.org/";
	private String notvalidServiceID04 = "peer@xmpp.org";
	private String notvalidServiceID05 = "peer";
	
	@Test
	public void MethodvalidateTest(){
		Assert.assertTrue(ServiceID.validate(validServiceID01));
		Assert.assertTrue(ServiceID.validate(validServiceID02));
		Assert.assertTrue(ServiceID.validate(validServiceID03));
	}

	@Test
	public void invalidValidadeTest() {
		Assert.assertFalse(ServiceID.validate(notvalidServiceID01));
		Assert.assertFalse(ServiceID.validate(notvalidServiceID02));
		Assert.assertFalse(ServiceID.validate(notvalidServiceID03));
		Assert.assertFalse(ServiceID.validate(notvalidServiceID04));
		Assert.assertFalse(ServiceID.validate(notvalidServiceID05));
	}
	
	@Test(expected = InvalidIdentificationException.class)
	public void invalidParseTest01() throws InvalidIdentificationException{
		ServiceID.parse(notvalidServiceID01);
	}
	
	@Test(expected = InvalidIdentificationException.class)
	public void invalidParseTest02() throws InvalidIdentificationException{
		ServiceID.parse(notvalidServiceID02);
	}
	
	@Test(expected = InvalidIdentificationException.class)
	public void invalidParseTest03() throws InvalidIdentificationException{
		ServiceID.parse(notvalidServiceID03);
	}
	
	@Test(expected = InvalidIdentificationException.class)
	public void invalidParseTest04() throws InvalidIdentificationException{
		ServiceID.parse(notvalidServiceID04);
	}
	
	@Test(expected = InvalidIdentificationException.class)
	public void invalidParseTest05() throws InvalidIdentificationException{
		ServiceID.parse(notvalidServiceID05);
	}
	
	@Test
	public void validParseTest(){
		Assert.assertEquals(validServiceID01, ServiceID.parse(validServiceID01).toString());
		Assert.assertEquals(validServiceID01, ServiceID.parse(validServiceID02).toString());
		Assert.assertEquals(validServiceID03, ServiceID.parse(validServiceID03).toString());
		Assert.assertEquals(validServiceID01, ServiceID.parse(validServiceID04).toString());
	}

}
