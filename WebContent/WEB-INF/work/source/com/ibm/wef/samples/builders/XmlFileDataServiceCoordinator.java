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

import com.bowstreet.editor.uitools.coordinator.WebAppBaseCoordinator;
import com.bowstreet.generation.DynamicBuilderInputDefinition;
import com.bowstreet.util.IXml;
import com.ibm.wef.samples.builders.XmlFileDataServiceBuilder.Constants;

/**
 * Coordinator implementation
 */
public class XmlFileDataServiceCoordinator extends WebAppBaseCoordinator
{

	InputDefinitions defs = new InputDefinitions();

	/**
	 * The initialization method is called each time the builder call is opened.
	 * Here you can set defaults, create dynamic pick lists, show/hide/create inputs.
	 */
	public String initializeInputs(boolean isNewBuilderCall)
	{
		/*##GENERATED_BODY_BEGIN#CoordinatorDefInitCode#*/
		// Generated code to initialize all the input definitions
		defs.name = context.findInputDefinition(Constants.Name);
		defs.fileName = context.findInputDefinition(Constants.FileName);
		defs.keyField = context.findInputDefinition(Constants.KeyField);
		defs.cacheSchema = context.findInputDefinition(Constants.CacheSchema);
		/*##GENERATED_BODY_END*/


		// Populate the keys select list  input if not a new call
		if (!isNewBuilderCall)
		{
			populateKeys();
		}

		return null;
	}

	/**
	 * This is called whenever any input is changed.
	 * NOTE: This method must return "true" to have UI updated.
	 */
	public boolean processInputChange(DynamicBuilderInputDefinition changed)
	{
		// If the file name has changed then repopulate the names of the keys
		if (changed == defs.fileName)
		{
			populateKeys();
			// update UI
			return true;
		}

		// don't update UI by default
		return false;
	}

	/**
	 * Populates the the Key select list input with the names of the columns.  
	 * 
	 */
	private void populateKeys()
	{

		String fileName = defs.fileName.getString();

		if(fileName != null && fileName.length() > 0)
		{
			// Use the XML data to find the column names
			IXml data = XmlFileUtil.readXmlFile(fileName);

			if(data != null)
			{
				StringBuffer choiceValues = new StringBuffer();

				// Look at the first row
				IXml row = data.getFirstChildElement();
				for( IXml child = row.getFirstChildElement(); child != null; child = child.getNextSiblingElement())
				{
					choiceValues.append(child.getName());
					if(child.getNextSiblingElement() != null)
						choiceValues.append(","); //$NON-NLS-1$
				}

				// Add the columns names to the select list. Label and Data are the same in this case.
				defs.keyField.setArgument("listData", choiceValues.toString()); //$NON-NLS-1$
				defs.keyField.setArgument("listLabels", choiceValues.toString()); //$NON-NLS-1$
			}
		}
	}

	/**
	 *  This method is called whenever OK or Apply is pressed.
	 *  NOTE: This method can be implemented to remove extra inputs,
	 *  but this should only be done if the inputs aren't needed any
	 *  more, since the Builder Call may still remain open for further
	 *  editing after Apply is pressed.
	 */
	public void terminate()
	{
	}


	static class InputDefinitions
	{
		/*##GENERATED_BEGIN*/
		DynamicBuilderInputDefinition name;
		DynamicBuilderInputDefinition fileName;
		DynamicBuilderInputDefinition keyField;
		DynamicBuilderInputDefinition cacheSchema;
		/*##GENERATED_END*/


	}

}
