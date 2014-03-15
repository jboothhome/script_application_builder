/*
 * Licensed Materials - Property of IBM
 * 5724-O03
 * (C) Copyright 2003, 2013. IBM Corp. All rights reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 * The Program may contain sample source code or programs, which illustrate
 * programming techniques. You may only copy, modify, and distribute these
 * samples internally. These samples have not been tested under all conditions
 * and are provided to you by IBM without obligation of support of any kind.
 *
 * IBM PROVIDES THESE SAMPLES "AS IS" SUBJECT TO ANY STATUTORY WARRANTIES THAT
 * CANNOT BE EXCLUDED. IBM MAKES NO WARRANTIES OR CONDITIONS, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OR CONDITIONS OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT
 * REGARDING THESE SAMPLES OR TECHNICAL SUPPORT, IF ANY.
 */

package com.ibm.wef.samples.builders;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.bowstreet.util.IXml;
import com.bowstreet.util.SystemProperties;
import com.bowstreet.util.WrappedException;
import com.bowstreet.util.XmlUtil;

/**
 * Utility class for XML File Data Service sample builder. This class is acting as helper for accessing a backend.  
 *
 * This is for development prototyping purposes only, and should not be used in a production environment. 
 */
public class XmlFileUtil {

	/**
	 * Reads the specifid XML file
	 * @param fileName The name of the XML file to read.
	 * @return The contents of the XML file.
	 */
	public static IXml readXmlFile(String fileName) {

		IXml data = null;

		Reader reader = null;

		try {

			reader = new FileReader(new File(SystemProperties.getDocumentRoot(), fileName));

			data = XmlUtil.parseXml(reader);

		} catch (Throwable ex) {
			throw new WrappedException(ex);
		}
		finally{
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return data;
	}

	
	/**
	 * Writes the XML to the specified file
	 * @param fileName The name of the XML file to write.
	 * @param records The contents to write.
	 */
	public static void writeXmlFile(String fileName, IXml records) {

		Writer writer = null;

		try {

			writer = new FileWriter(new File(SystemProperties.getDocumentRoot(), fileName));

			 XmlUtil.writeXml(records, writer);

		} catch (Throwable ex) {
			throw new WrappedException(ex);
		}
		finally{
			if(writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	/**
	 * Find the record for the specified field. 
	 * 
	 * @param records The records XML data.
	 * @param keyField The name of the field to search on.
	 * @param keyValue The value of the record to fetch.
	 * @return The record for the specified key value.
	 */
	public static IXml fetchRecord(IXml records, String keyField, String keyValue) {
		IXml record = records.findElement("*/[" + keyField + "=" + keyValue + "]/..");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		return record;
	}
	
	
}
