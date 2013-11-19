/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2005-2011 Ausenco Engineering Canada Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.sandwell.JavaSimulation;

import java.util.ArrayList;
import java.util.Comparator;

	/**
	 *
	 *
	 */
public class Util {
	private static String cargoUnits = "";
	private static String cargoUnitsPerHour = "";

	public static final Comparator<Entity> nameSort = new Comparator<Entity>() {
		@Override
		public int compare(Entity a, Entity b) {
			return a.getName().compareTo(b.getName());
		}
	};

	public static String getCargoUnits() {
		return cargoUnits;
	}

	public static String getCargoUnitsPerHour() {
		return cargoUnitsPerHour;
	}

	public static void setCargoUnits(String units) {
		cargoUnits = units;
		cargoUnitsPerHour = cargoUnits + "/h";
	}

	/**
	 *
	 *
	 * {  aaa, bbb,  ccc, ddd }
	 * {    ", bbb,  ccc, ddd }
	 * {  aaa, bbb,    ", ddd }
	 * { "aaa, bbb,  ccc, ddd }
	 * {  aaa, bbb, "ccc, ddd }
	 * { a"aa, bbb,  ccc, ddd } <-- disregard this possibility
	 * {  aaa, bbb, cc"c, ddd } <-- disregard this possibility
	 *
	 *
	 * Returns true if record is not empty; false, otherwise.
	 *
	 */
	public static boolean discardComment( Vector record ) {

		boolean containsCmd;
		int     recSize;
		int     indexFirst;    // Index of first cell containing a string that starts with "

		if ( record == null ) {
			return false;
		}

		recSize = record.size();
		if ( recSize == 0 ) {
			return false;
		}

		// Find in record the index of the first cell that contains string of the form "aaa.
		// Disregard strings of the forms aaa"bbb and aaa"
		indexFirst = recSize;
		for ( int i = 0; i < recSize; i++ ) {
			if ( ((String)record.get(i)).startsWith( "\"" )) {
				indexFirst = i;
				break;
			}
		}

		// Strip away comment string from record. Return true if record contains a command; false, otherwise
		// Note that indexFirst cannot be greater than recSize
		if ( indexFirst == recSize ) { // no comments found
			containsCmd = true;
		}
		else if ( indexFirst > 0 ) { // command and comment found
			containsCmd = true;

			// Discard elements from index to (recSize - 1)
			for ( int x = indexFirst; x < recSize; x++ ) {
				record.removeElementAt( indexFirst );
			}
		}
		else { // if (indexFirst == 0), it means that record contains only comment
			containsCmd = false;
			record.clear();
		}

		return containsCmd;

	} // discardComment

	/**
	 * if ( fileFullName = "C:\Projects\A01.cfg" ), returns "A01.cfg"
	 * if ( fileFullName = "A01.cfg" ), returns "A01.cfg"
	 */
	public static String fileShortName( String fileFullName ) {
		int indexLast = fileFullName.lastIndexOf( '\\' );
		indexLast = Math.max( indexLast, fileFullName.lastIndexOf( '/') );
		String fileName = fileFullName.substring( indexLast + 1 );

		return fileName;
	}

	public static String getAbsoluteFilePath( String filePath ) {
		String absoluteFilePath;
		try {
			java.io.File absFile = new java.io.File( filePath );
			if( absFile.isAbsolute() ) {
				absoluteFilePath = absFile.getCanonicalPath();
				if(absFile.isFile()) {
					absoluteFilePath = "file:/" + absoluteFilePath;
				}
				else {
					absoluteFilePath += System.getProperty( "file.separator" );
				}
			}
			else {
				// For absolute files inside the resource folder of the jar file
				if (Simulation.class.getResource(filePath) != null) {
					absoluteFilePath = Simulation.class.getResource(filePath).toString();
				}
				else {

					// Does the relative filepath exist inside the jar file?
					String relativeURL = FileEntity.getRelativeURL( filePath );
					if (Simulation.class.getResource(relativeURL) != null) {
						absoluteFilePath = Simulation.class.getResource(relativeURL).getFile();
					}
					else {
						absoluteFilePath = FileEntity.getRootDirectory() + System.getProperty( "file.separator" ) + filePath;
						absoluteFilePath = FileEntity.getRelativeURL(absoluteFilePath);
						if(FileEntity.fileExists(absoluteFilePath)) {
							absoluteFilePath = "file:/" + absoluteFilePath;
						}
					}
				}
			}
		}
		catch( Exception e ) {
			throw new ErrorException( e );
		}

		return absoluteFilePath;
	}

	public static String getFileExtention(String name){
		if(name.indexOf(".") < 0)
			return "";

		String ext = name.substring( name.lastIndexOf(".")).trim();
		return ext.replace(".", "").toUpperCase();
	}

	/**
	 * Expects a StringVector of one of two forms:
	 * 1.  { entry entry entry } { entry entry entry }
	 * 2.  entry entry entry
	 * If format 1, returns a vector of stringvectors without braces.
	 * if format 2, returns a vector of a stringvector, size1.
	 * @param data
	 * @return
	 */
	public static ArrayList<StringVector> splitStringVectorByBraces(StringVector data) {
		ArrayList<StringVector> newData = new ArrayList<StringVector>();
		for (int i=0; i < data.size(); i++) {

			//skip over opening brace if present
			if (data.get(i).equals("{") )
				continue;

			StringVector cmd = new StringVector();

			//iterate until closing brace, or end of entry
			for (int j = i; j < data.size(); j++, i++){
				if (data.get(j).equals("}"))
					break;

				cmd.add(data.get(j));
			}

			//add to vector
			newData.add(cmd);
		}

		return newData;
	}

    public static String HTMLString(String font, String size, String backgroundColor, String color, String accent, String text) {
    	StringBuilder ret = new StringBuilder("<html><pre style=\"font-family:");

		if (font == null)
			ret.append("verdana");
		else
			ret.append(font);

		if (size != null) {
			ret.append(";font-size:");
			ret.append(size);
		}

		if (backgroundColor != null) {
			ret.append(";background-color:");
			ret.append(backgroundColor);
		}

		if (color != null) {
			ret.append(";color:");
			ret.append(color);
		}

		ret.append("\">");

		if (accent != null) {
			ret.append(accent);
		}
		ret.append(text);

		return ret.toString();
    }
}
