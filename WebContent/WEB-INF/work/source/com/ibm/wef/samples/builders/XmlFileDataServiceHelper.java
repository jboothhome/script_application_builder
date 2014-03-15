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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.bowstreet.builders.webapp.methods.BuilderHelper;
import com.bowstreet.services.base.TaggedData;
import com.bowstreet.util.IXml;
import com.bowstreet.util.StringUtil;
import com.bowstreet.webapp.WebAppAccess;
import com.ibm.wef.samples.builders.XmlFileDataServiceBuilder.Constants;

/**
 * Helper LJO for a builder.  An instance of this class will be created
 * every time the generated WebApp is brought into a user's
 * session, so session-specific data can be stored here.
 */
public class XmlFileDataServiceHelper extends BuilderHelper
{
	public static HashMap<String, IXml> updatedRecords = new HashMap<String, IXml>(); 

    /**
     * Reads the XML data file and populates the target variable.
     *  
     * @param webAppAccess The current WebAppAccess.
     */
    public void fetchRecords(WebAppAccess webAppAccess)
    {
    	String name = (String)getProperty(webAppAccess, Constants.Name);
    	String fileName = (String)getProperty(webAppAccess, Constants.FileName);
 
    	// Read the XML from the file
    	IXml data = XmlFileUtil.readXmlFile(fileName);


    	// Populate the Data Service output variable 
    	webAppAccess.getVariables().setXml(name + "Records", data); //$NON-NLS-1$
    }

    /**
     * Finds a specific record, and populates the target variable.
     * 
     * @param webAppAccess The current WebAppAccess.
     */
    public void fetchRecord(WebAppAccess webAppAccess)
    {
    	// Get the values specified in the Builder inputs
    	String name = (String)getProperty(webAppAccess, Constants.Name);
    	String keyField = (String)getProperty(webAppAccess, Constants.KeyField);

    	// Get the Data Service input value to find the record
       	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
    	String retrieveOneName = "get" + operationNameSuffix;
       	String keyValue = webAppAccess.getDataServiceParameterText(name, retrieveOneName, "inputs", "Parameters/" + keyField);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    	// Get the records data to look through
    	IXml records = getRecords(webAppAccess);

    	// Find the target record
    	IXml record = XmlFileUtil.fetchRecord(records, keyField, keyValue);

    	// Populate the Data Service output variable 
    	webAppAccess.getVariables().setXml(name + "Record", record); //$NON-NLS-1$
    	
    }

    
    /**
     * Updates a specific record, and writes out the changes.
     * 
     * @param webAppAccess The current WebAppAccess.
    */
    public void updateRecord(WebAppAccess webAppAccess)
    {
    	// Get the values specified in the Builder inputs
    	String name = (String)getProperty(webAppAccess, Constants.Name);
    	String keyField = (String)getProperty(webAppAccess, Constants.KeyField);

    	// Get the Data Service input for the record to update
       	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
    	String updateName = "update" + operationNameSuffix;
    	IXml newRecord = webAppAccess.getDataServiceParameterXml(name, updateName, "inputs", null);  //$NON-NLS-1$ //$NON-NLS-2$ 

    	// Get the records data to look through
    	IXml records = getRecords(webAppAccess);

    	// Get the key value from the new record, so we can lookup the original.
    	String keyValue = newRecord.getText(keyField);

    	// Find the target record
    	IXml record = XmlFileUtil.fetchRecord(records, keyField, keyValue);

    	if(record != null)
    	{
    		// Replace with the new record
    		record.removeChildren();
    		
    		// Copy the updated record contents, using the local element name (i.e. no namespace)
			for( IXml child = newRecord.getFirstChildElement(); child != null; child = child.getNextSiblingElement())
			{
				record.addChildWithText(child.getLocalName(), child.getText());
			}
    		
    		String fileName = (String)getProperty(webAppAccess, Constants.FileName);

    		// Save the change to the XML file
    		XmlFileUtil.writeXmlFile(fileName, records);
    		updatedRecords.put(name, records);
     	}
    }

    /**
     * Deletes a specific record, and writes out the changes.
     * 
     * @param webAppAccess The current WebAppAccess.
     */
    public void deleteRecord(WebAppAccess webAppAccess)
    {
    	// Get the values specified in the Builder inputs
    	String name = (String)getProperty(webAppAccess, Constants.Name);
    	String keyField = (String)getProperty(webAppAccess, Constants.KeyField);

    	// Get the Data Service input value to find the record
       	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
    	String deleteName = "delete" + operationNameSuffix;
    	String keyValue = webAppAccess.getDataServiceParameterText(name, deleteName, "inputs", "Parameters/" + keyField);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    	// Get the records data to look through
    	IXml records = getRecords(webAppAccess);

    	// Find the target record
    	IXml record = XmlFileUtil.fetchRecord(records, keyField, keyValue);

    	if(record != null)
    	{
    		// Remove the record
    		IXml parent = record.getParentElement();
    		parent.removeChildElement(record);

    		String fileName = (String)getProperty(webAppAccess, Constants.FileName);

    		// Save the change to the XML file
    		XmlFileUtil.writeXmlFile(fileName, records);
    		updatedRecords.put(name, records);
    	}
    }

    /*
     * Search for records where the specified searchFieldName value matches the specified searchValue
     */
    public IXml searchRecords(WebAppAccess webAppAccess) {
    	// Get the Data Service input value to find the record
       	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
    	String searchName = "search" + operationNameSuffix + "s";
    	String searchField = webAppAccess.getDataServiceParameterText(name, searchName, "inputs", "Parameters/SEARCH_FIELD");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	String searchValue = webAppAccess.getDataServiceParameterText(name, searchName, "inputs", "Parameters/SEARCH_VALUE");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    	IXml records = getRecords(webAppAccess).cloneElement();
    	// if either fieldname or value is null, return everything
    	if (!StringUtil.isEmpty(searchField) && !StringUtil.isEmpty(searchValue)) {
		    List<IXml> rows = records.getChildren();
		    for (Iterator<IXml> iterator = rows.iterator(); iterator.hasNext();) {
				IXml row = iterator.next();
				String value = row.getValueOf(searchField);
				if(value != null) {
					if (!value.contains(searchValue)){
			    		IXml parent = row.getParentElement();
			    		parent.removeChildElement(row);
					}
				}
			}
    	}
    	// Populate the Data Service output variable 
    	webAppAccess.getVariables().setXml(name + "SearchRecordsResult", records); //$NON-NLS-1$
	    return records;
    }
    
	/**
	 * Gets the XML for all the records.
     * @param webAppAccess The current WebAppAccess.
	 * @return The XML for all the records.
	 */
	private IXml getRecords(WebAppAccess webAppAccess) {
		
    	// Get the values specified in the Builder inputs
    	String name = (String)getProperty(webAppAccess, Constants.Name);
		
		// Get the records data to look through
    	IXml records = updatedRecords.get(name);
    	if(records == null)
    		records = webAppAccess.getVariables().getXml(name + "Records"); //$NON-NLS-1$

    	// If the records are not in the service output variable then we may be in a stateless provider, so refetch the data.
    	if(records == null)
    	{
    		fetchRecords(webAppAccess);
           	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
        	String retrieveListName = "get" + operationNameSuffix + "s";
    		records = webAppAccess.getDataServiceParameterXml(name, retrieveListName, "results", null);  //$NON-NLS-1$//$NON-NLS-2$
    	}
		return records;
	}
    
	   /**
     * create a new record, and writes out the changes.
     * 
     * @param webAppAccess The current WebAppAccess.
     */
    public void createRecord(WebAppAccess webAppAccess)
    {
    	// Get the values specified in the Builder inputs
    	String name = (String)getProperty(webAppAccess, Constants.Name);
    	String keyField = (String)getProperty(webAppAccess, Constants.KeyField);

    	// Get the Data Service input for the record to update
       	String operationNameSuffix = (String)getProperty(webAppAccess, Constants.OperationNameSuffix);
    	String updateName = "create" + operationNameSuffix;
    	IXml newRecord = webAppAccess.getDataServiceParameterXml(name, updateName, "inputs", null);  //$NON-NLS-1$ //$NON-NLS-2$ 

    	// Get the records data to look through
    	IXml records = getRecords(webAppAccess);

    	// Get the key value from the new record, so we can lookup the original.
    	String keyValue = newRecord.getText(keyField);

    	// Find the target record
    	IXml record = XmlFileUtil.fetchRecord(records, keyField, keyValue);

    	if(record == null)
    	{
    		// String rowName = records.getFirstChildElement().getName();
    		String rowName = (String)getProperty(webAppAccess, Constants.RowElementName);
    		TaggedData newRow = new TaggedData(rowName);
    		newRow.copyTaggedDataChildren((TaggedData) newRecord);
    		records.addChildElement(newRow);
    		String fileName = (String)getProperty(webAppAccess, Constants.FileName);

    		// Save the change to the XML file
    		XmlFileUtil.writeXmlFile(fileName, records);
    		updatedRecords.put(name, records);
    	}
    }
}
