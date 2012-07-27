package org.eclipse.nebula.widgets.pagination.snippets.tree;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.pagination.snippets.model.Team;

public class TeamContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_OBJECT = new Object[0];
	private static TeamContentProvider instance;

	/**
	 * Returns an instance of ArrayContentProvider. Since instances of this
	 * class do not maintain any state, they can be shared between multiple
	 * clients.
	 * 
	 * @return an instance of ArrayContentProvider
	 * 
	 * @since 3.5
	 */
	public static TeamContentProvider getInstance() {
		synchronized (TeamContentProvider.class) {
			if (instance == null) {
				instance = new TeamContentProvider();
			}
			return instance;
		}
	}

	/**
	 * Returns the elements in the input, which must be either an array or a
	 * <code>Collection</code>.
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		if (inputElement instanceof Collection) {
			return ((Collection) inputElement).toArray();
		}
		return EMPTY_OBJECT;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Team) {
			return ((Team) parentElement).getPersons().toArray();
		}
		return EMPTY_OBJECT;
	}

	public Object getParent(Object element) {

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Team) {
			return ((Team) element).getPersons().size() > 0;
		}
		return false;
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
