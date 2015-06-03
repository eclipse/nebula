/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.nattable.extension.nebula.richtext;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.ContextualDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

public class MarkupDisplayConverter extends ContextualDisplayConverter {

	protected IDisplayConverter wrappedConverter;
	protected Map<String, MarkupValue> markups = new HashMap<>();

	public MarkupDisplayConverter() {
		this(new DefaultDisplayConverter());
	}

	public MarkupDisplayConverter(IDisplayConverter wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
	}

	@Override
	public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry, Object canonicalValue) {
		Object wrappedConverterResult = this.wrappedConverter.canonicalToDisplayValue(cell, configRegistry, canonicalValue);
		String result = null;
		if (wrappedConverterResult != null) {
			result = wrappedConverterResult.toString();

			// add markups
			for (MarkupValue markup : this.markups.values()) {
				result = result.replace(markup.originalValue, markup.markupValue);
			}
		}
		return result;
	}

	@Override
	public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry, Object displayValue) {
		// remove markups
		if (displayValue != null) {
			String result = displayValue.toString();
			for (MarkupValue markup : this.markups.values()) {
				result = result.replace(markup.markupValue, markup.originalValue);
			}
			return this.wrappedConverter.displayToCanonicalValue(cell, configRegistry, result);
		}
		return this.wrappedConverter.displayToCanonicalValue(cell, configRegistry, displayValue);
	}

	/**
	 * Registers a value and the markup that should be placed around the value while rendering.
	 * 
	 * @param value
	 *            The value that should be replacement with the markup for rendering.
	 * @param markupPrefix
	 *            The String that will be added as prefix to the value.
	 * @param markupSuffix
	 *            The String that will be added as suffix to the value.
	 */
	public void registerMarkup(String value, String markupPrefix, String markupSuffix) {
		MarkupValue markup = new MarkupValue();
		markup.originalValue = value;
		markup.markupValue = markupPrefix + value + markupSuffix;
		this.markups.put(value, markup);
	}

	/**
	 * Unregister the markup that was registered for the given value.
	 * 
	 * @param value
	 *            The value for which a markup was registered.
	 */
	public void unregisterMarkup(String value) {
		this.markups.remove(value);
	}

	/**
	 * Remove all registered markups.
	 */
	public void clearMarkups() {
		this.markups.clear();
	}

	/**
	 * Simple value class to store the original value and the markup replacement.
	 */
	protected class MarkupValue {
		String originalValue;
		String markupValue;
	}
}
