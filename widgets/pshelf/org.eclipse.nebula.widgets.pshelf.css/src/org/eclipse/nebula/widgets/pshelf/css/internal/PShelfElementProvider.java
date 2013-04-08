package org.eclipse.nebula.widgets.pshelf.css.internal;

import org.eclipse.e4.ui.css.core.dom.IElementProvider;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.w3c.dom.Element;

@SuppressWarnings("restriction")
public class PShelfElementProvider implements IElementProvider {

	@Override
	public Element getElement(Object element, CSSEngine engine) {
		return new PShelfElement((PShelf) element, engine);
	}

}
