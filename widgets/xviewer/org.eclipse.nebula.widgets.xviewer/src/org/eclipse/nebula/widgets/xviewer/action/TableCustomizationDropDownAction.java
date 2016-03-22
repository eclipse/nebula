package org.eclipse.nebula.widgets.xviewer.action;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class TableCustomizationDropDownAction extends Action implements IMenuCreator {
   private Menu fMenu;
   private final XViewer xViewer;

   public TableCustomizationDropDownAction(XViewer xViewer) {
      this.xViewer = xViewer;
      setText(XViewerText.get("action.tableCustomization")); //$NON-NLS-1$
      setMenuCreator(this);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("customize.gif"); //$NON-NLS-1$
   }

   @Override
   public void run() {
      xViewer.getCustomizeMgr().handleTableCustomization();
   }

   @Override
   public String getToolTipText() {
      return XViewerText.get("toolTip.customizeTable"); //$NON-NLS-1$
   }

   @Override
   public Menu getMenu(Control parent) {
      if (fMenu != null) {
         fMenu.dispose();
      }

      fMenu = new Menu(parent);

      addActionToMenu(fMenu, new TableCustomizationAction(xViewer));
      addActionToMenu(fMenu,
         new TableCustomizationCustomizeDataAction(xViewer, xViewer.getCustomizeMgr().getTableDefaultCustData()));
      new MenuItem(fMenu, SWT.SEPARATOR);
      try {
         List<CustomizeData> savedCustDatas = xViewer.getCustomizeMgr().getSavedCustDatas();
         Collections.sort(savedCustDatas);
         for (CustomizeData custData : savedCustDatas) {
            addActionToMenu(fMenu, new TableCustomizationCustomizeDataAction(xViewer, custData));
         }
      } catch (Exception ex) {
         XViewerLog.log(TableCustomizationDropDownAction.class, Level.SEVERE, ex);
      }
      return fMenu;
   }

   @Override
   public void dispose() {
      if (fMenu != null) {
         fMenu.dispose();
         fMenu = null;
      }
   }

   @Override
   public Menu getMenu(Menu parent) {
      return null;
   }

   protected void addActionToMenu(Menu parent, Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
   }

   /**
    * Get's rid of the menu, because the menu hangs on to * the searches, etc.
    */
   void clear() {
      dispose();
   }

}
