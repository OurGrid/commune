package br.edu.ufcg.lsd.commune.container;

import java.util.Arrays;
import java.util.List;

public class ContainerUtils {

	public static boolean isEnabled(String property) {
		return property != null && property.equalsIgnoreCase( "yes" );
	}

	public static List<String> parseStringList(final String val) {
		String[] splitVal = val.split(";");
		return Arrays.asList(splitVal);
	}
	
	public static String getBooleanProperty(boolean property) {
		return property ? "yes" : "no";
	}
	
	public static String getStringListProperty(List<String> property) {
		StringBuffer listBuffer = new StringBuffer();
		for (int i = 0; i < property.size(); i++) {
			if (i > 0) {
				listBuffer.append(';');
			}
			listBuffer.append(property.get(i));
		}
		
		return listBuffer.toString();
	}
	
}
