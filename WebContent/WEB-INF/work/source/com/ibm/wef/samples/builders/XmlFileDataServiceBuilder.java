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


import java.util.Iterator;
import java.util.List;

import com.bowstreet.builders.webapp.api.ServiceTest;
import com.bowstreet.builders.webapp.foundation.WebAppBuilder;
import com.bowstreet.builders.webapp.methods.BuilderHelperUtil;
import com.bowstreet.builders.webapp.service.ServiceMetadata;
import com.bowstreet.generation.BuilderCall;
import com.bowstreet.generation.BuilderInputs;
import com.bowstreet.generation.GenContext;
import com.bowstreet.util.IXml;
import com.bowstreet.webapp.DataService;
import com.bowstreet.webapp.ServiceOperation;
import com.bowstreet.webapp.ServiceParameter;
import com.bowstreet.webapp.Variable;
import com.bowstreet.webapp.WebApp;
import com.bowstreet.xml.schema.SchemaCreator;
import com.bowstreet.xml.schema.SchemaCreatorComponent;

/**
 * Builder regen class for sample XML File Data Service builder
 * 
 * This sample builder adds the following to the model’s WebApp: 
 *   - Helper LJO for accessing backend data at runtime.
 *   - Schema for the structure of the XML data file.
 *   - Schema for the input to the getRow service operation.
 *   - Variables to hold service operations result data, and inputs.
 *   - Data Service to describe the operations exposed by this builder.
 *   
 * This is for development purposes only, and should not be used in a production environment.    
 */
public class XmlFileDataServiceBuilder implements WebAppBuilder
{

	/*##GENERATED_BEGIN*/
	private void makeHelper(BuilderInputs builderInputs, WebApp webApp, String rowElementName, String nameSuffix)
	{
		// Get the builder name and use it as the helper LJO name.
		String helperName = builderInputs.getString("Name", null); //$NON-NLS-1$
		if (helperName == null)
			helperName = "builderHelper"; //$NON-NLS-1$
		// BuilderHelperUtil is used to create the helper LJO with
		// support for properties and indirect references.
		// The helper class must extend BuilderHelper.
		// See Javadoc for BuilderHelperUtil and BuilderHelper for more information.
		BuilderHelperUtil util = new BuilderHelperUtil(webApp, helperName,
				"com.ibm.wef.samples.builders.XmlFileDataServiceHelper", builderInputs); //$NON-NLS-1$

		// Generated code that makes builder inputs and indirect references available in helper class
		util.setHelperProperty(Constants.Name);
		util.setHelperProperty(Constants.FileName);
		util.setHelperProperty(Constants.KeyField);
		util.setHelperProperty(Constants.RowElementName, rowElementName);
		util.setHelperProperty(Constants.OperationNameSuffix, nameSuffix);
	}
	/*##GENERATED_END*/

	/**
	 * This is the method that's called during generation of the WebApp.
	 */
	public void doBuilderCall(GenContext genContext, WebApp webApp, BuilderCall builderCall, BuilderInputs builderInputs)
	{
		// System.out.println("builderInputs: " + builderInputs);

		/*##GENERATED_BODY_BEGIN#InputAccessorCode#*/
		// Generated code to get all the builder inputs
		String name = builderInputs.getString(Constants.Name, null);
		String fileName = builderInputs.getString(Constants.FileName, null);
		String keyField = builderInputs.getString(Constants.KeyField, null);
		boolean cacheSchema = builderInputs.getBoolean(Constants.CacheSchema, false);
		boolean makeServicePublic = builderInputs.getBoolean(Constants.MakeServicePublic, false);
		boolean addTestingSupport = builderInputs.getBoolean(Constants.AddTestingSupport, false);
		String operationNameSuffix = builderInputs.getString(Constants.OperationNameSuffix, "Row");
		/*##GENERATED_BODY_END*/

		// Define the variable names that will be used
		String recordsData = name + "Records"; //$NON-NLS-1$
		String recordData = name + "Record"; //$NON-NLS-1$
		String recordInputData = name + "RecordInput"; //$NON-NLS-1$
		String updateRecordInputData = name + "UpdateRecordInput"; //$NON-NLS-1$
		String createRecordInputData = name + "CreateRecordInput"; //$NON-NLS-1$
		String deleteRecordInputData = name + "DeleteRecordInput"; //$NON-NLS-1$
		String searchRecordsInputData = name + "SearchRecordsInput"; //$NON-NLS-1$
		String searchRecordsResultData = name + "SearchRecordsResult"; //$NON-NLS-1$

		try
		{
			// Create a variable to hold the XML records data
			Variable recordsVariable = webApp.addVariable(recordsData, Variable.TYPE_XML);

			// fetch the data to use as a sample.
			IXml data = XmlFileUtil.readXmlFile(fileName);

			// Check XML file to see if it has the necessary three levels of data
			if (data == null || data.getFirstChildElement() == null || data.getFirstChildElement().getChildren().size() == 0) {
				builderCall.addMessage(BuilderCall.SEVERITY_ERROR, "XML file does not contain the necessary structure of top element, row element, and fields within row.");
				return;
			}
			String topElementName = data.getName();
			IXml rowElement = data.getFirstChildElement();
			String rowElementName = rowElement.getName();
			
			// Generate the schema for the structure of the XML file. 
			SchemaCreator schemaCreator = new SchemaCreator(recordsData, "http://" + webApp.getModelName() + "/" + recordsData);  //$NON-NLS-1$  //$NON-NLS-2$
			SchemaCreatorComponent rowSet = schemaCreator.addComplexType(topElementName);
	        SchemaCreatorComponent row = rowSet.addComplexType(rowElementName, "1", "unbounded");
			List<IXml> children = rowElement.getChildren();
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				IXml child = (IXml) iterator.next();
				String childName = child.getName();
				// Add each column, with the key field as the only required element
				row.addSimpleElement(childName, "string", childName.equals(keyField) ? "1" : "0", "1");
			}
			webApp.addSchema(recordsData, schemaCreator.getXML(), null);
			recordsVariable.setSchemaPath(recordsData + "/" + topElementName); //$NON-NLS-1$
			
			// Create an input schema for the getRow service operation
			SchemaCreator schemaCreatorInputs = new SchemaCreator(recordInputData, "http://" + webApp.getModelName() + "/" + recordInputData);  //$NON-NLS-1$  //$NON-NLS-2$
			SchemaCreatorComponent parameters = schemaCreatorInputs.addComplexType("Parameters"); //$NON-NLS-1$
			parameters.addStringElement(keyField);

			// Add the getRow input schema to the WebApp
			webApp.addSchema(recordInputData, schemaCreatorInputs.getXML(), null);

			// Create a Variable to hold the service input for the getRow operation
			Variable recordInputVariable = webApp.addVariable(recordInputData, Variable.TYPE_XML);
			recordInputVariable.setSchemaPath(recordInputData + "/Parameters"); //$NON-NLS-1$

			// Create a Variable to hold the results of the getRow service operation
			Variable recordVariable = webApp.addVariable(recordData, Variable.TYPE_XML);
			recordVariable.setSchemaPath(recordsData + "/" + rowElementName); //$NON-NLS-1$

			// Create a Variable to hold the inputs of the updateRow service operation
			Variable updateRecordVariable = webApp.addVariable(updateRecordInputData, Variable.TYPE_XML);
			updateRecordVariable.setSchemaPath(recordsData + "/" + rowElementName); //$NON-NLS-1$
			
			// Create a Variable to hold the inputs of the createRow service operation
			Variable createRecordVariable = webApp.addVariable(createRecordInputData, Variable.TYPE_XML);
			createRecordVariable.setSchemaPath(recordsData + "/" + rowElementName); //$NON-NLS-1$

			Variable deleteRecordInputVariable = webApp.addVariable(deleteRecordInputData, Variable.TYPE_XML);
			deleteRecordInputVariable.setSchemaPath(recordInputData + "/Parameters"); //$NON-NLS-1$

			// Create an input schema for search inputs
			SchemaCreator schemaCreatorSearchInputs = new SchemaCreator(recordInputData, "http://" + webApp.getModelName() + "/" + searchRecordsInputData);  //$NON-NLS-1$  //$NON-NLS-2$
			SchemaCreatorComponent searchParameters = schemaCreatorSearchInputs.addComplexType("Parameters"); //$NON-NLS-1$
			searchParameters.addSimpleElement("SEARCH_FIELD", "string", "0", "1");
			searchParameters.addSimpleElement("SEARCH_VALUE", "string", "0", "1");
			webApp.addSchema(searchRecordsInputData, schemaCreatorSearchInputs.getXML(), null);

			// Create a Variable for search results and input
			Variable searchInputVariable = webApp.addVariable(searchRecordsInputData, Variable.TYPE_XML);
			searchInputVariable.setSchemaPath(searchRecordsInputData+ "/Parameters"); //$NON-NLS-1$
			Variable searchResultVariable = webApp.addVariable(searchRecordsResultData, Variable.TYPE_XML);
			searchResultVariable.setSchemaPath(recordsData + "/" + topElementName); //$NON-NLS-1$

			// Add the Data Service to the WebApp, which will hold all of the operations 
			DataService dataService = webApp.addDataService(name);
        	ServiceMetadata service = new ServiceMetadata(webApp, name, builderCall, genContext);
			if (makeServicePublic) {
	        	service.setPublicService( true );
	            // makes this builder available to be looked up with picker from the 
	            // correspondent consumer
        		genContext.addExternalModelProperty( "Service2Enabled", "true" ); //$NON-NLS-1$
			}
			// Form the operation names using the specified suffix
			String createName = "create" + operationNameSuffix;
			String retrieveListName = "get" + operationNameSuffix + "s";
			String updateName = "update" + operationNameSuffix;
			String deleteName = "delete" + operationNameSuffix;
			String retrieveOneName = "get" + operationNameSuffix;
			String searchName = "search" + operationNameSuffix + "s";
			
            dataService.addLogicalOperation("Create", createName, keyField, null);
            dataService.addLogicalOperation("RetrieveList", retrieveListName, null, keyField);
            dataService.addLogicalOperation("Update", updateName, keyField, null);
            dataService.addLogicalOperation("Delete", deleteName, keyField, null);
            dataService.addLogicalOperation("RetrieveOne", retrieveOneName, keyField, null);
            dataService.addLogicalOperation("Search", searchName, null, keyField);
        	dataService = service.generateServiceImplementation();
			
	        	// Add operation to get all the records
			ServiceOperation operation = dataService.addOperation(retrieveListName, name + ".fetchRecords");  //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("results", recordsData, ServiceParameter.OUTPUT); //$NON-NLS-1$

			// Add operation to get a specified record
			operation = dataService.addOperation(retrieveOneName, name + ".fetchRecord"); //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("inputs", recordInputData, ServiceParameter.INPUT); //$NON-NLS-1$
			operation.addParameter("results", recordData, ServiceParameter.OUTPUT); //$NON-NLS-1$

			// Add operation to update a specified record
			operation = dataService.addOperation(updateName, name + ".updateRecord"); //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("inputs", updateRecordInputData, ServiceParameter.INPUT); //$NON-NLS-1$
			
			// Add operation to delete a specified record
			operation = dataService.addOperation(deleteName, name + ".deleteRecord"); //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("inputs", deleteRecordInputData, ServiceParameter.INPUT); //$NON-NLS-1$
			
			// Add operation to create a specified record
			operation = dataService.addOperation(createName, name + ".createRecord"); //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("inputs", createRecordInputData, ServiceParameter.INPUT); //$NON-NLS-1$
			
			// Add operation to search records
			operation = dataService.addOperation(searchName, name + ".searchRecords"); //$NON-NLS-1$  //$NON-NLS-2$
			operation.addParameter("inputs", searchRecordsInputData, ServiceParameter.INPUT); //$NON-NLS-1$
			operation.addParameter("results", searchRecordsResultData, ServiceParameter.OUTPUT); //$NON-NLS-1$
			
			// Add the helper LJO
			makeHelper(builderInputs, webApp, rowElementName, operationNameSuffix);
			
			// Add testing support if specified
			if (addTestingSupport) {
				ServiceTest st = new ServiceTest(builderCall, genContext);
				st.setCreateMain(true);
				st.setIncludeDocumentation(false);
				st.setServiceName(name);
				st.invokeBuilder();
			}
			
			// register a dependency on the imported file
			genContext.addDependency(GenContext.DEPTYPE_FILE, fileName);

		}catch(Throwable ex)
		{
			builderCall.addMessage(BuilderCall.SEVERITY_ERROR, ex.getClass().getName() + " - " + ex.getMessage()); //$NON-NLS-1$
			ex.printStackTrace();
		}

	}


	/**
	 * Interface that has constants for all the builder input names
	 */
	static public interface Constants
	{
		/*##GENERATED_BEGIN*/
		public static final String Name = "Name"; //$NON-NLS-1$
		public static final String FileName = "FileName"; //$NON-NLS-1$
		public static final String KeyField = "KeyField"; //$NON-NLS-1$
		public static final String CacheSchema = "CacheSchema"; //$NON-NLS-1$
		public static final String MakeServicePublic= "MakeServicePublic"; //$NON-NLS-1$
		public static final String AddTestingSupport= "AddTestingSupport"; //$NON-NLS-1$
		public static final String OperationNameSuffix= "OperationNameSuffix"; //$NON-NLS-1$
		/*##GENERATED_END*/

		public static final String RowElementName = "RowElementName"; //$NON-NLS-1$

	}

}
