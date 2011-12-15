package org.eclipse.nebula.effects.stw.example;

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class STWDemo {

    static final String[] demos = {
          "Demo1"
        , "Demo2"
        , "Demo3"
    };
    
    AbstractSTWDemoFrame[] demoFrames = {
              new TransitionTest()
            , new TransitionTest1()
            , new TransitionTest2()
    };
    
    static final String[] transitions = {
            "Fade"
          , "Slide"
          , "Cubic Rotation"
    };
    
    Shell       sShell;
    Composite   frameHolder;
    StackLayout frameHolderStackLayout;
    Composite   currentSpecificOptionsComposite;
    Composite   fadeOptionsComposite;
    Composite   cubicRotationOptionsComposite;
    
    AbstractSTWDemoFrame    currentDemo;
    Transition              currentTransition;
    int                     currentDirection;
    int                     currentT;
    int                     currentFPS;
    int                     currentFOS;
    int                     currentFOP;
    int                     currentFIS;
    int                     currentFIP;
    int                     currentQuality;
    
    
    public static void main(String[] args) {
        /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
         * for the correct SWT library path in order to run with the SWT dlls. 
         * The dlls are located in the SWT plugin jar.  
         * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
         *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
         */
        Display display = Display.getDefault();
        STWDemo thisClass = new STWDemo();
        thisClass.createSShell();
        thisClass.sShell.open();

        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /**
     * This method initializes sShell
     */
    private void createSShell() {
        sShell = new Shell();
        sShell.setText("STW Demo");
        sShell.setSize(new Point(1000, 600));
        sShell.setLayout(new FillLayout());
        
        createMainComposite(sShell, SWT.NONE);
    }
    
    private Composite createMainComposite(Composite parent, int style) {

        Composite mainComposite = new Composite(parent, style);
        
        mainComposite.setLayout(new FormLayout());
        
        FormData fd;
        
        frameHolder = new Composite(mainComposite, SWT.BORDER);
        fd = new FormData();
        fd.left     = new FormAttachment(0, 0);
        fd.right    = new FormAttachment(75, 0);
        fd.top      = new FormAttachment(0, 0);
        fd.bottom   = new FormAttachment(100, 0);
        frameHolder.setLayoutData(fd);
        frameHolderStackLayout = new StackLayout();
        frameHolder.setLayout(frameHolderStackLayout);
        
        sShell.addListener (SWT.Resize,  new Listener() {
            public void handleEvent (Event e) {
                frameHolder.layout();
            }
        });
        
        //init frames
        for(AbstractSTWDemoFrame frame: demoFrames) {
            frame.init(frameHolder);
            frame.getContainerComposiste().setVisible(false);
        }
        
        Composite optionsComposite = new Composite(mainComposite, SWT.NONE);
        fd = new FormData();
        fd.left     = new FormAttachment(frameHolder, 5);
        fd.right    = new FormAttachment(100, -5);
        fd.top      = new FormAttachment(0, 5);
        fd.bottom   = new FormAttachment(100, -5);
        optionsComposite.setLayoutData(fd);
        optionsComposite.setLayout(new RowLayout(SWT.VERTICAL));
        
        new Label(optionsComposite, SWT.TRANSPARENT).setText("Demo:");
        final Combo comboDemo = new Combo(optionsComposite, SWT.DROP_DOWN|SWT.READ_ONLY);
        comboDemo.setItems(demos);
        comboDemo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectDemo(comboDemo.getSelectionIndex());
            }
        });
        comboDemo.select(0);
        currentDemo = demoFrames[0];
        
        new Label(optionsComposite, SWT.TRANSPARENT).setText("Transition Effect:");
        final Combo comboTransition = new Combo(optionsComposite, SWT.DROP_DOWN|SWT.READ_ONLY);
        comboTransition.setItems(transitions);
        comboTransition.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectTransition(comboTransition.getSelectionIndex());
            }
        });
        comboTransition.select(0);
        currentTransition = currentDemo.getTransitionEffect(0);
        currentDemo.getTransitionManager().setTransition(currentTransition);
        
        new Label(optionsComposite, SWT.TRANSPARENT).setText("Direction:");
        final Combo comboDirection = new Combo(optionsComposite, SWT.DROP_DOWN|SWT.READ_ONLY);
        comboDirection.setItems(AbstractSTWDemoFrame.DIRECTIONS_NAMES);
        comboDirection.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectDirection(comboDirection.getSelectionIndex());
            }
        });
        comboDirection.select(0);
        currentDirection = 0;
        currentDemo.selectDirection(0);
        
        new Label(optionsComposite, SWT.TRANSPARENT).setText("Total Transition Time (ms):");
        final Spinner spnrT = new Spinner(optionsComposite, SWT.NONE);
        spnrT.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setT(spnrT.getSelection());
            }
        });
        spnrT.setMinimum(0);spnrT.setMaximum(Integer.MAX_VALUE);
        spnrT.setIncrement(1);spnrT.setPageIncrement(50);
        spnrT.setSelection(1000);setT(1000);
        
        new Label(optionsComposite, SWT.TRANSPARENT).setText("Frames Per Second (fps):");
        final Spinner spnrFPS = new Spinner(optionsComposite, SWT.NONE);
        spnrFPS.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFPS(spnrFPS.getSelection());
            }
        });
        spnrFPS.setMinimum(1);spnrFPS.setMaximum(Integer.MAX_VALUE);
        spnrFPS.setIncrement(1);spnrFPS.setPageIncrement(10);
        spnrFPS.setSelection(60);setFPS(60);
        
        //specificOptionsComposite
        Composite specificOptionsComposite = new Composite(optionsComposite, SWT.NONE);
        StackLayout specificOptionsStackLayout = new StackLayout();
        specificOptionsComposite.setLayout(specificOptionsStackLayout);
        
        fadeOptionsComposite = new Composite(specificOptionsComposite, SWT.NONE);
        fadeOptionsComposite.setLayout(new RowLayout(SWT.VERTICAL));
        
        new Label(fadeOptionsComposite, SWT.TRANSPARENT).setText("Fade Out Start (%):");
        final Spinner spnrFOS = new Spinner(fadeOptionsComposite, SWT.NONE);
        spnrFOS.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFOS(spnrFOS.getSelection());
            }
        });
        spnrFOS.setMinimum(0);spnrFOS.setMaximum(100);
        spnrFOS.setIncrement(1);spnrFOS.setPageIncrement(10);
        spnrFOS.setSelection(0);setFOS(0);
        
        
        new Label(fadeOptionsComposite, SWT.TRANSPARENT).setText("Fade Out Stop (%):");
        final Spinner spnrFOP = new Spinner(fadeOptionsComposite, SWT.NONE);
        spnrFOP.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFOP(spnrFOP.getSelection());
            }
        });
        spnrFOP.setMinimum(0);spnrFOP.setMaximum(100);
        spnrFOP.setIncrement(1);spnrFOP.setPageIncrement(10);
        spnrFOP.setSelection(100);setFOP(100);
        
        new Label(fadeOptionsComposite, SWT.TRANSPARENT).setText("Fade In Start (%):");
        final Spinner spnrFIS = new Spinner(fadeOptionsComposite, SWT.NONE);
        spnrFIS.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFIS(spnrFIS.getSelection());
            }
        });
        spnrFIS.setMinimum(0);spnrFIS.setMaximum(100);
        spnrFIS.setIncrement(1);spnrFIS.setPageIncrement(10);
        spnrFIS.setSelection(0);setFIS(0);
        
        new Label(fadeOptionsComposite, SWT.TRANSPARENT).setText("Fade In Stop (%):");
        final Spinner spnrFIP = new Spinner(fadeOptionsComposite, SWT.NONE);
        spnrFIP.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFIP(spnrFIP.getSelection());
            }
        });
        spnrFIP.setMinimum(0);spnrFIP.setMaximum(100);
        spnrFIP.setIncrement(1);spnrFIP.setPageIncrement(10);
        spnrFIP.setSelection(100);setFIP(100);
        
        cubicRotationOptionsComposite = new Composite(specificOptionsComposite, SWT.NONE);
        cubicRotationOptionsComposite.setLayout(new RowLayout(SWT.VERTICAL));
        
        new Label(cubicRotationOptionsComposite, SWT.TRANSPARENT).setText("Quality (%):");
        final Spinner spnrQuality = new Spinner(cubicRotationOptionsComposite, SWT.NONE);
        spnrQuality.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setQuality(spnrQuality.getSelection());
            }
        });
        spnrQuality.setMinimum(0);spnrQuality.setMaximum(100);
        spnrQuality.setIncrement(1);spnrQuality.setPageIncrement(10);
        spnrQuality.setSelection(100);setQuality(100);
        
        currentSpecificOptionsComposite = fadeOptionsComposite;
        specificOptionsStackLayout.topControl = currentSpecificOptionsComposite;
        selectDemo(0);
        
        return mainComposite;
    }
    
    private void selectDemo(int index) {
        if(null != currentDemo)
            currentDemo.getContainerComposiste().setVisible(false);
        
        currentDemo = demoFrames[index];
        currentDemo.getContainerComposiste().setVisible(true);
        frameHolderStackLayout.topControl = currentDemo.getContainerComposiste();
        currentDemo.getTransitionManager().setTransition(currentTransition);
        currentDemo.selectDirection(currentDirection);
        currentTransition.setTotalTransitionTime(currentT);
        currentTransition.setFPS(currentFPS);
    }
    
    private void selectTransition(int index) {
        currentTransition = currentDemo.getTransitionEffect(index);
        if(null != currentSpecificOptionsComposite)
            currentSpecificOptionsComposite.setVisible(false);
        if(currentTransition instanceof FadeTransition) {
            currentSpecificOptionsComposite = fadeOptionsComposite;
            currentSpecificOptionsComposite.setVisible(true);
        } else if(currentTransition instanceof CubicRotationTransition) {
            currentSpecificOptionsComposite = cubicRotationOptionsComposite;
            currentSpecificOptionsComposite.setVisible(true);
        } else {
            currentSpecificOptionsComposite = null;
        }
        currentDemo.getTransitionManager().setTransition(currentTransition);
        currentTransition.setTotalTransitionTime(currentT);
        currentTransition.setFPS(currentFPS);
    }
    
    private void selectDirection(int index) {
        currentDirection = index;
        currentDemo.selectDirection(currentDirection);
    }
    
    private void setT(int T) {
        currentT = T;
        currentTransition.setTotalTransitionTime(currentT);
    }
    
    private void setFPS(int fps) {
        currentFPS = fps;
        currentTransition.setFPS(currentFPS);
    }
    
    private void setFOS(int fos) {
        currentFOS = fos;
        if(currentTransition instanceof FadeTransition) {
            ((FadeTransition)currentTransition).setFadeOutStart(currentFOS);
        }
    }
    private void setFOP(int fop) {
        currentFOP = fop;
        if(currentTransition instanceof FadeTransition) {
            ((FadeTransition)currentTransition).setFadeOutStop(currentFOP);
        }
    }
    private void setFIS(int fis) {
        currentFIS = fis;
        if(currentTransition instanceof FadeTransition) {
            ((FadeTransition)currentTransition).setFadeInStart(currentFIS);
        }
    }
    private void setFIP(int fip) {
        currentFIP = fip;
        if(currentTransition instanceof FadeTransition) {
            ((FadeTransition)currentTransition).setFadeInStop(currentFIP);
        }
    }
    
    private void setQuality(int quality) {
        currentQuality = quality;
        if(currentTransition instanceof CubicRotationTransition) {
            ((CubicRotationTransition)currentTransition).setQuality(currentQuality);
        }
    }

}
