package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Juergen Reichl
 */
public interface IXViewerLabelProvider {

	/**
	 * Returns the label text for the given column of the given element.
	 */
	public abstract String getColumnText(Object element, int columnIndex);
	
	/**
	 * Returns the label text for the given column of the given element.
	 */
	public abstract String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception;
	
	/**
	 * Returns XViewerColumn of the given index
	 */
	public abstract XViewerColumn getTreeColumnOffIndex(int columnIndex);
	
	/**
	 * Return value between 0..100 and cell will show bar graph shading that portion of the cell
	 */
	public abstract int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception;
	
	/**
	 * Returns the backing data object for operations like sorting
	 */
	public abstract Object getBackingData(Object element, XViewerColumn xViewerColumn, int columnIndex) throws Exception;
	
	/**
	 * When columns get re-ordered, need to clear out this cache so indexing can be re-computed
	 */
	public abstract void clearXViewerColumnIndexCache();
	
}
