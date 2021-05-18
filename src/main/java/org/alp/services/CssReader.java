package org.alp.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CssReader {

	private static CssReader cssReader = null;
	private static String file = null;

	String path = "src/main/resources/org/alp/graph.css";
	Scanner scanner;

	private CssReader() {
		File stylesFile = new File(path);
		try {
			scanner = new Scanner(stylesFile);
		} catch(FileNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	public static CssReader getInstance() {
		if(cssReader == null) {
			cssReader = new CssReader();
		}
		return cssReader;
	}

	public String getFile() {
		if(file != null) {
			return file;
		}

		StringBuilder stringBuilder = new StringBuilder();
		while(scanner.hasNextLine()) {
			stringBuilder.append(scanner.nextLine());
		}

		file = stringBuilder.toString();
		return file;
	}

}
