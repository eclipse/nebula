/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.examples;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.treemapper.TreeMapper;
import org.eclipse.nebula.widgets.treemapper.TreeMapperUIConfigProvider;
import org.eclipse.nebula.widgets.treemapper.examples.DOMSemanticTreeMapperSupport.DOMMappingBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author istria
 *
 */
public class TestWizardPage extends WizardPage {

	private static List<DOMMappingBean> mappings = new ArrayList<DOMSemanticTreeMapperSupport.DOMMappingBean>();
	private static Document xml;
	static {
		InputStream stream = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			stream = TestWizardPage.class.getResourceAsStream("globalweather.wsdl");
			xml = db.parse(stream);
			stream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * @param pageName
	 */
	protected TestWizardPage() {
		super("Test");
	}

	@Override
	public void createControl(Composite parent) {
		Color defaultColor = new Color(parent.getShell().getDisplay(), new RGB(247, 206, 206));
		Color selectedColor = new Color(parent.getShell().getDisplay(), new RGB(147, 86, 111));
		TreeMapperUIConfigProvider uiConfig = new TreeMapperUIConfigProvider(defaultColor, 1, selectedColor, 3);
		
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TreeMapper<DOMMappingBean, Node, Node> mapper = new TreeMapper<DOMMappingBean, Node, Node>(parent, new DOMSemanticTreeMapperSupport(), uiConfig);
		mapper.setContentProviders(new DOMTreeContentProvider(), new DOMTreeContentProvider());
		mapper.setLabelProviders(new DOMLabelProvider(), new DOMLabelProvider());
		mapper.setInput(xml, xml, mappings);
		getShell().setSize(600, 600);
		setControl(mapper.getControl());
	}
	

}
