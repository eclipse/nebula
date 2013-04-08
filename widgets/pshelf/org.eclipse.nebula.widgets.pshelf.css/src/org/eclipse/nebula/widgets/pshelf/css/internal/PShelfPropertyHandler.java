package org.eclipse.nebula.widgets.pshelf.css.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.nebula.widgets.pshelf.AbstractRenderer;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.css.CSSShelfRenderer;
import org.w3c.dom.css.CSSValue;

@SuppressWarnings("restriction")
public class PShelfPropertyHandler implements ICSSPropertyHandler {
	private Map<PShelf, Boolean> scheduled = new HashMap<>();
	
	@Override
	public boolean applyCSSProperty(Object element, String property,
			CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		final PShelf s = (PShelf)((PShelfElement)element).getNativeWidget();
		final AbstractRenderer r = s.getRenderer();
		
		if( r instanceof CSSShelfRenderer ) {
			if( ! scheduled.containsKey(s) ) {
				scheduled.put(s, Boolean.TRUE);
				// Queue the changes
				s.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						if( ! s.isDisposed() && ! r.isDisposed() ) {
							scheduled.remove(s);
							((CSSShelfRenderer) r).reinitialize();	
						}
					}
				});
			}
		}
		return true;
	}

	@Override
	public String retrieveCSSProperty(Object element, String property,
			String pseudo, CSSEngine engine) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}	
}