package br.edu.ufcg.lsd.commune.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("restriction")
public class JsonMessageUtil {
	
	static final JsonParser PARSER = new JsonParser();
	static final String ENCODING = "ISO-8859-1";
	static final Map<String, Class<?>> PRIMITIVES = new HashMap<String, Class<?>>();
	
	static {
		PRIMITIVES.put(Byte.TYPE.getCanonicalName(), Byte.TYPE);
		PRIMITIVES.put(Short.TYPE.getCanonicalName(), Short.TYPE);
		PRIMITIVES.put(Integer.TYPE.getCanonicalName(), Integer.TYPE);
		PRIMITIVES.put(Long.TYPE.getCanonicalName(), Long.TYPE);
		PRIMITIVES.put(Character.TYPE.getCanonicalName(), Character.TYPE);
		PRIMITIVES.put(Float.TYPE.getCanonicalName(), Float.TYPE);
		PRIMITIVES.put(Double.TYPE.getCanonicalName(), Double.TYPE);
		PRIMITIVES.put(Boolean.TYPE.getCanonicalName(), Boolean.TYPE);
		PRIMITIVES.put(Void.TYPE.getCanonicalName(), Void.TYPE);
	}
	
	public static byte[] toBytes(Message message) throws IOException{
		JsonObject msgJson = new JsonObject();
		
		msgJson.add("parameters", encodeParameters(message));
		msgJson.add("senderID", new JsonPrimitive(message.getSource().toString()));
		msgJson.add("destID", new JsonPrimitive(message.getDestination().toString()));
		msgJson.add("funcName", new JsonPrimitive(message.getFunctionName()));
		msgJson.add("procType", new JsonPrimitive(message.getProcessorType()));
		
		Long session = message.getSession();
		if (session != null) {
			msgJson.add("session", new JsonPrimitive(session));
		}
		
		msgJson.add("seq", new JsonPrimitive(message.getSequence()));
		
		String senderPK = message.getSource().getPublicKey();
		if (senderPK != null) {
			msgJson.add("senderPK", new JsonPrimitive(senderPK));
		}
		
		X509CertPath senderCertificateChain = message.getSenderCertificatePath();
		if (senderCertificateChain != null) {
			msgJson.add("senderCert", new JsonPrimitive(serialize(senderCertificateChain)));
		}
		
		byte[] signature = message.getSignature();
		if (signature != null) {
			msgJson.add("signature", new JsonPrimitive(new String(signature, ENCODING)));
		}
		
		return msgJson.toString().getBytes(ENCODING);
	}

	public static Message parse(byte[] bytes) throws IOException, ClassNotFoundException {
		String msgStr = new String(bytes, ENCODING);
		JsonObject msgJson = PARSER.parse(msgStr).getAsJsonObject();
		
		CommuneAddress source = parseAddress(msgJson.get("senderID").getAsString());
		CommuneAddress destination = parseAddress(msgJson.get("destID").getAsString());
		
		String functionName = msgJson.get("funcName").getAsString();
		String procType = msgJson.get("procType").getAsString();
		
		Message msg = new Message(source, destination, functionName, null, procType);
		extractParameters(msgJson, msg);
		
		JsonElement sessionJson = msgJson.get("session");
		if (sessionJson != null) {
			msg.setSession(sessionJson.getAsLong());
		}
		
		msg.setSequence(msgJson.get("seq").getAsLong());
		
		JsonElement senderPKJson = msgJson.get("senderPK");
		if (senderPKJson != null) {
			msg.getSource().getContainerID().setPublicKey(senderPKJson.getAsString());
		}
		
		JsonElement senderCertJson = msgJson.get("senderCert");
		if (senderCertJson != null) {
			msg.setSenderCertificatePath((X509CertPath) deserialize(senderCertJson.getAsString()));
		}
		
		JsonElement signJson = msgJson.get("signature");
		if (signJson != null) {
			msg.setSignature(signJson.getAsString().getBytes(ENCODING));
		}
		
		return msg;
	}
	
	private static JsonArray encodeParameters(Message msg) throws IOException {
		
		JsonArray paramJson = new JsonArray();
		for (MessageParameter parameter : msg.getParameters()) {
			JsonArray jsonArray = new JsonArray();
			jsonArray.add(new JsonPrimitive(parameter.getType().getCanonicalName()));
			if (parameter.getType().equals(String.class)) {
				jsonArray.add(new JsonPrimitive((String)parameter.getValue()));
			} else if (parameter.getValue() instanceof Boolean) {
				jsonArray.add(new JsonPrimitive((Boolean)parameter.getValue()));
			} else if (parameter.getValue() instanceof Number) {
				jsonArray.add(new JsonPrimitive((Number)parameter.getValue()));
			} else {
				jsonArray.add(new JsonPrimitive(serialize(parameter.getValue())));
			}
			paramJson.add(jsonArray);
		}
		return paramJson;
	}
	
	private static void extractParameters(JsonObject msgJson, Message message) throws IOException, ClassNotFoundException {
		
		JsonArray paramJson = msgJson.getAsJsonArray("parameters");
		for (int i = 0; i < paramJson.size(); i++) {
			JsonArray paramPair = paramJson.get(i).getAsJsonArray();
			String className = paramPair.get(0).getAsString();
			
			Class<?> type = PRIMITIVES.get(className);
			if (type == null) {
				type = Class.forName(className);
			}
			
			Object value = null;
			JsonElement valueJson = paramPair.get(1);
			
			if (type.equals(String.class)) {
				value = valueJson.getAsString();
			} else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
				value = valueJson.getAsBoolean();
			} else if (Number.class.isAssignableFrom(type)
					|| type.equals(Integer.TYPE) || type.equals(Long.TYPE)
					|| type.equals(Double.TYPE) || type.equals(Short.TYPE)) {
				value = valueJson.getAsNumber();
			} else {
				value = deserialize(valueJson.getAsString());
			}
			
			message.addParameter(type, value);
		}
	}

	private static String serialize(Object obj) throws IOException {
		byte[] byteArray = serializeBytes(obj);
		if (byteArray == null) {
			return null;
		}
		
		return new String(Base64.encodeBase64(byteArray), ENCODING);
	}

	private static byte[] serializeBytes(Object obj) throws IOException {
		if (obj == null) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		byte[] byteArray = baos.toByteArray();
		oos.close();
		baos.close();
		
		return byteArray;
	}
	
	private static Object deserialize(String objStr) throws IOException, ClassNotFoundException {
		if (objStr == null) {
			return null;
		}
		
		byte[] bytes = Base64.decodeBase64(objStr.getBytes(ENCODING));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		Object obj = ois.readObject();
		ois.close();
		bis.close();
		
		return obj;
	}
	
	private static CommuneAddress parseAddress(String address) {
		try {
			return new DeploymentID(address);
		} catch (InvalidIdentificationException e) {
			try {
				return ServiceID.parse(address);
			} catch (InvalidIdentificationException e2) {
				return ContainerID.parse(address);
			}
		}
	}
}
