package com.asset.control.dataconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import junit.framework.TestCase;

public class DataConvertorTest extends TestCase {

	String INPUT_DATE = "";
	String COLUMN_FILE_NAME = "";
	String ROW_FILE_NAME = "";
	String OUTPUT_FILE_NAME = "";

	@Override
	protected void setUp() throws Exception {

		INPUT_DATE = "data_template.txt";
		COLUMN_FILE_NAME = "column_config.txt";
		ROW_FILE_NAME = "row_config.txt";
		OUTPUT_FILE_NAME = "output.txt";

	}

	public void test() {

		DataConverter.INPUT_DATE = INPUT_DATE;
		DataConverter.COLUMN_FILE_NAME = COLUMN_FILE_NAME;
		DataConverter.ROW_FILE_NAME = ROW_FILE_NAME;
		DataConverter.loadFromResources = true;
		DataConverter.OUTPUT_FILE_NAME = OUTPUT_FILE_NAME;
		Scanner scanner=null;
		try {
			DataConverter.dataConverter();
			scanner = new Scanner(loadFile(OUTPUT_FILE_NAME));
			String firstLine=scanner.nextLine().replaceAll(" ", "");
			String secondLine=scanner.nextLine().replaceAll(" ", "");
			assertEquals("OURIDOURCOL1OURCOL3", firstLine);
			assertEquals("OURIDXXXVAL21VAL23", secondLine);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

	}

	private static File loadFile(String fileName) {
		File file = null;
		try {
			file = new File(DataConvertorTest.class.getClassLoader().getResource(fileName).getFile());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}
