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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.search.command.SearchCommand;

/**
 * TODO maybe we can create a default configuration that creates and registers the following
 * configuration.
 * 
 * <pre>
 * MarkupDisplayConverter converter = new MarkupDisplayConverter();
 * SearchMarkupCommandHandler handler = new SearchMarkupCommandHandler();
 * handler.registerMarkupDisplayConverter(converter);
 * natTable.registerCommandHandler(handler);
 * 
 * configRegistry.registerConfigAttribute(
 * 		CellConfigAttributes.DISPLAY_CONVERTER,
 * 		converter);
 * </pre>
 */
public class SearchMarkupCommandHandler implements ILayerCommandHandler<SearchCommand> {

	protected Collection<MarkupDisplayConverter> markupConverter = new ArrayList<>();

	protected String currentSearchValue;

	@Override
	public boolean doCommand(ILayer targetLayer, SearchCommand command) {
		for (MarkupDisplayConverter mdc : this.markupConverter) {
			mdc.unregisterMarkup(currentSearchValue);
			currentSearchValue = command.getSearchText();
			mdc.registerMarkup(
					currentSearchValue,
					"<span style=\"color:rgb(0, 0, 0);background-color:rgb(255, 255, 0)\">",
					"</span>");
		}

		// don't consume the command as we need to trigger the search too
		return false;
	}

	public void registerMarkupDisplayConverter(MarkupDisplayConverter mdc) {
		this.markupConverter.add(mdc);
	}

	public void unregisterMarkupDisplayConverter(MarkupDisplayConverter mdc) {
		this.markupConverter.remove(mdc);
	}

	@Override
	public Class<SearchCommand> getCommandClass() {
		return SearchCommand.class;
	}
}
