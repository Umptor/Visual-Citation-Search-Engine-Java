package org.alp.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonMapperService {
	public static <T> T mapJson(String str, Class<T> classOfT) {
		T returnValue = null;
		try {
			returnValue = new Gson().fromJson(str, classOfT);
		} catch(JsonSyntaxException exception) {
			if(str.contains("Unhandled Exception")) {
				System.out.println("Unhandled Exception while parsing OC data");
			} else {
				System.out.println("Exception while parsing json");
				System.out.println("Json in question");
				System.out.println(str);
				System.out.println(exception.toString());
			}
		}
		return returnValue;
	}
}
