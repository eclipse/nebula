package org.eclipse.nebula.widgets.cdatetime.snippets;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CDTSnippet12 {
    
    private static Display display = new Display();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Shell shell = new Shell(display);
        
        shell.setText("Nebula CDateTime");
        shell.setLayout(new GridLayout());

        GridLayout layout = new GridLayout(1, false);
        shell.setLayout(layout);
        
        CDateTime cDateTime1 = new CDateTime(shell, CDT.DROP_DOWN | CDT.DATE_SHORT | CDT.BORDER);
        new CDateTime(shell, CDT.DROP_DOWN | CDT.DATE_SHORT | CDT.BORDER);
        
        cDateTime1.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent eParm){
                System.out.println("TraverseListener.keyTraversed() " + eParm);
            }
        });
        
        cDateTime1.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent eParm) {
                System.out.println("ModifyListener.modifyText() " + eParm);
            }
        });
        
        shell.pack();
        Point size = shell.getSize();
        Rectangle screen = display.getMonitors()[0].getBounds();
        shell.setBounds(
                (screen.width-size.x)/2,
                (screen.height-size.y)/2,
                size.x,
                size.y
        );
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}