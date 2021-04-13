package br.gov.jfrj.siga.sr.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util {

	private static final Logger log = Logger.getLogger(Util.class);

	public static void copiar(Object dest, Object orig) {
		for (Method getter : orig.getClass().getDeclaredMethods()) {
			try {
				String getterName = getter.getName();
				if (!getterName.startsWith("get")) {
					continue;
				}
				if (Collection.class.isAssignableFrom(getter.getReturnType())) {
					continue;
				}
				String setterName = getterName.replace("get", "set");
				Object origValue = getter.invoke(orig);
				dest.getClass().getMethod(setterName, getter.getReturnType()).invoke(dest, origValue);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.warn(e);
			}
		}
	}

	public static Gson createGson(String... exclusions) {
		return new GsonBuilder()
			.addSerializationExclusionStrategy(FieldNameExclusionEstrategy.notIn(exclusions))
			.create();
	}

}
