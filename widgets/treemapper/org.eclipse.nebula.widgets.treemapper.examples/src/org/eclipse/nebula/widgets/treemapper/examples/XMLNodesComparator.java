package org.eclipse.nebula.widgets.treemapper.examples;

import java.util.Comparator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLNodesComparator implements Comparator<Node> {
	
	private ILabelProvider labelProvider;
	
	public XMLNodesComparator() {
		labelProvider = new DOMLabelProvider();
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Node arg0, Node arg1) {
		// At least one is an attribute
		if (arg0 instanceof Attr && !(arg1 instanceof Attr)) {
			return -1;
		} else if (arg1 instanceof Attr && !(arg0 instanceof Attr)) {
			return 1;
		} else if (arg0 instanceof Attr && arg1 instanceof Attr) {
			return labelProvider.getText(arg0).compareTo(labelProvider.getText(arg1));
		}
		
		// No attribute, at least one in an element
		if (arg0 instanceof Element && !(arg1 instanceof Element)) {
			return -1;
		} else if (arg1 instanceof Element && !(arg0 instanceof Element)) {
			return 1;
		} else if (arg0 instanceof Element && arg1 instanceof Element) {
			return labelProvider.getText(arg0).compareTo(labelProvider.getText(arg1));
		}
		
		return 0;
	}

}
