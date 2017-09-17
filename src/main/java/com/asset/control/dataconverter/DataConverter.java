package com.asset.control.dataconverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Mina.Georgy 
 * Data Translator Input data from a data vendor needs to be filtered and translated.
 *
 */
public class DataConverter {

	// define logger
	public static final Logger log = Logger.getLogger("DataConverter");
	// name of actual data file
	public static String INPUT_DATE = "";
	// name of config column data file
	public static String COLUMN_FILE_NAME = "";
	// name of config row data file
	public static String ROW_FILE_NAME = "";

	// name of output file
	public static String OUTPUT_FILE_NAME = "";

	public static boolean loadFromResources = false;

	public static final String TAB_SEPARATOR = "\\s+";

	private static List<List<String>> rows = new ArrayList<List<String>>();
	private static List<String> columns = new ArrayList<String>();
	private static List<List<String>> translatedRows = new ArrayList<List<String>>();
	private static List<String> translatedColumns = new ArrayList<String>();
	private static Map<String, String> columnMap = new HashMap<String, String>();
	private static Map<String, String> rowMap = new HashMap<String, String>();

	public static void dataConverter() {
		// parse input data
		parseInputData();
		// parse extracted columns
		parseColumnConfigFile();
		// translate columns to extracted columns and add its rows
		translateColumns();
		// parse extracted rows
		parseRowConfigFile();
		// map rows to extracted rows
		translateRows();
		// print final result
		printResult();
	}

	private static void parseInputData() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(loadFile(INPUT_DATE));
			if (!scanner.hasNextLine()) {
				throw new UnsupportedOperationException("File is Empty.");

			}
			String firstRow = scanner.nextLine();
			columns = Arrays.asList(firstRow.split(TAB_SEPARATOR));
			while (scanner.hasNextLine()) {
				String[] rowValues = scanner.nextLine().split(TAB_SEPARATOR);
				List<String> row = new ArrayList<String>(Arrays.asList(rowValues));
				rows.add(row);
			}
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Input data file doesn't exist.", e);
		} catch (UnsupportedOperationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			scanner.close();
		}
	}

	private static void parseColumnConfigFile() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(loadFile(COLUMN_FILE_NAME));
			while (scanner.hasNextLine()) {
				String[] columnValues = scanner.nextLine().split(TAB_SEPARATOR);
				if (columnValues.length < 2) {
					throw new UnsupportedOperationException("Invalid File Data Format.");
				}
				columnMap.put(columnValues[0], columnValues[1]);
			}
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Column Config file doesn't exist.", e);
		} catch (UnsupportedOperationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			scanner.close();
		}
	}

	private static void translateColumns() {

		for (int i = 0; i < columns.size(); i++) {
			if (columnMap.containsKey(columns.get(i))) {
				translatedColumns.add(columnMap.get(columns.get(i)));
			} else { // remove row values when column not found
				for (int j = 0; j < rows.size(); j++) {
					rows.get(j).remove(i);
				}
			}
		}

	}

	private static void parseRowConfigFile() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(loadFile(ROW_FILE_NAME));
			while (scanner.hasNextLine()) {
				String[] rowValues = scanner.nextLine().split(TAB_SEPARATOR);
				if (rowValues.length < 2) {
					throw new UnsupportedOperationException("Invalid Rows Config File Data Format.");
				}
				rowMap.put(rowValues[0], rowValues[1]);
			}
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Row Config file doesn't exist.", e);
		} catch (UnsupportedOperationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			scanner.close();
		}
	}

	private static void translateRows() {

		for (int i = 0; i < rows.size(); i++) {
			String key = rows.get(i).get(0);
			if (rowMap.containsKey(key)) {
				rows.get(i).remove(0);
				rows.get(i).add(0, rowMap.get(key));
				translatedRows.add(rows.get(i));
			}
		}

	}

	private static void printResult() {

		BufferedWriter bw = null;
		FileWriter fw = null;
		File file = null;

		try {
			if (loadFromResources) {
				file = new File(DataConverter.class.getClassLoader().getResource(OUTPUT_FILE_NAME).getFile());
			} else {
				file = new File(OUTPUT_FILE_NAME);
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (String column : translatedColumns) {
				bw.write(column + " ");
			}
			bw.newLine();
			for (List<String> row : translatedRows) {
				for (String rowValue : row) {
					bw.write(rowValue + " ");
				}
				bw.newLine();
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}

		}

	}

	/**
	 * @param fileName
	 * @return File
	 * 
	 */
	private static File loadFile(String fileName) {
		File file = null;
		try {
			// Begin Executing load file method
			log.fine("load file with name : " + fileName);
			// load input file
			if (loadFromResources) {
				file = new File(DataConverter.class.getClassLoader().getResource(fileName).getFile());
			} else {
				file = new File(fileName);
			}

		} catch (Exception e) {

			log.log(Level.SEVERE, "failed to load " + fileName, e);

		}
		// Ending of method load file
		log.fine("loaded file Successfully with name : " + fileName);
		// return loaded file
		return file;
	}

	public static void main(String[] args) {
		System.out.println("Please Enter a data vendor delivery data file path");
		Scanner scanner = new Scanner(System.in);
		INPUT_DATE = scanner.nextLine();
		System.out.println("Please Enter a column config file path");
		COLUMN_FILE_NAME = scanner.nextLine();
		System.out.println("Please Enter a row config file path");
		ROW_FILE_NAME = scanner.nextLine();
		System.out.println("Please Enter output file path");
		OUTPUT_FILE_NAME = scanner.nextLine();
		dataConverter();
		System.out.println("-------End Excecution of Data Converter Successfully ---------");
	}

}
