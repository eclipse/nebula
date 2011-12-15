package org.eclipse.swt.nebula.widgets.compositetable;


/**
 * This adapter class provides default implementations for the
 * methods described by the <code>IDeleteHandler</code> interface.
 * <p>
 * Classes that wish to deal with delete events from the CompositeTable can 
 * extend this class and override only the methods which they are
 * interested in.
 * </p>
 *
 * @see IDeleteHandler
 */
public class DeleteAdapter implements IDeleteHandler {
    /** 
     * {@inheritDoc}
     */
    public boolean canDelete(int rowInCollection) {
        return false;
    }

    /** 
     * {@inheritDoc}
     */
    public void deleteRow(int rowInCollection) {
    }

    /** 
     * {@inheritDoc}
     */
    public void rowDeleted(int rowInCollection) {
    }
}
