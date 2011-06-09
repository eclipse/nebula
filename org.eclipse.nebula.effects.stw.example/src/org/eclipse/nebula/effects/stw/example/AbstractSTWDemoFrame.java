package org.eclipse.nebula.effects.stw.example;

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.nebula.effects.stw.transitions.SlideTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractSTWDemoFrame {
    
    public static final String[] DIRECTIONS_NAMES = {
          "Left"
        , "Right"
        , "Up"
        , "Down"
        , "Right/Left"
        , "Down/Up" 
        , "Random right and left"
        , "Random up and down"
        , "Random"
    };
    
    public static final int DIR_LEFT                    = 0;
    public static final int DIR_RIGHT                   = 1;
    public static final int DIR_UP                      = 2;
    public static final int DIR_DOWN                    = 3;
    public static final int DIR_RIGHT_LEFT              = 4;
    public static final int DIR_DOWN_UP                 = 5; 
    public static final int DIR_RANDOM_RIGHT_AND_LEFT   = 6;
    public static final int DIR_RANDOM_UP_AND_DOWN      = 7;
    public static final int DIR_RANDOM                  = 8;
    
    private int _selectedDirection = DIR_RANDOM;
    
    protected Composite         _containerComposite = null;
    protected TransitionManager _tm                 = null;
    
    public static final int TRANSITION_FADE           = 0;
    public static final int TRANSITION_SLIDE          = 1;
    public static final int TRANSITION_CUBIC_ROTATION = 2;
    
    private Transition[] _transitions;
    
    public void init(Composite parent) {
        _containerComposite = new Composite(parent, SWT.NONE);
        init();
        _transitions = new Transition[] {
                  new FadeTransition(_tm)
                , new SlideTransition(_tm)
                , new CubicRotationTransition(_tm)
        };
        _tm.setTransition(_transitions[0]);
        _tm.addTransitionListener(new TransitionListener() {
            public void transitionFinished(TransitionManager transition) {
                System.out.println("End Of Transition! current item: " 
                        + transition.getTransitionable().getSelection());
            }
        });
    }
    
    protected abstract void init();
    
    public Composite getContainerComposiste() {
        return _containerComposite;
    }

    public TransitionManager getTransitionManager() {
        return _tm;
    }
    
    public Transition getTransitionEffect(int transition) {
        return _transitions[transition];
    }
    
    public void selectDirection(int direction) {
        if(direction >= 0 && direction < DIRECTIONS_NAMES.length)
            _selectedDirection = direction;
    }
    
    protected double getSelectedDirection(int toIndex, int fromIndex) {
        switch(_selectedDirection) {
        case DIR_RIGHT:
            return Transition.DIR_RIGHT;
        case DIR_LEFT:
            return Transition.DIR_LEFT;
        case DIR_UP:
            return Transition.DIR_UP;
        case DIR_DOWN:
            return Transition.DIR_DOWN;
        case DIR_RIGHT_LEFT:
            return toIndex > fromIndex ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
        case DIR_DOWN_UP:
            return toIndex > fromIndex ? Transition.DIR_DOWN : Transition.DIR_UP;
        case DIR_RANDOM_RIGHT_AND_LEFT:
            return Math.random() > 0.5 ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
        case DIR_RANDOM_UP_AND_DOWN:
            return Math.random() > 0.5 ? Transition.DIR_DOWN : Transition.DIR_UP;
        default:
        case DIR_RANDOM:
            if(Math.random() > 0.5)
                return Math.random() > 0.5 ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
            else
                return Math.random() > 0.5 ? Transition.DIR_DOWN : Transition.DIR_UP;
        }
    }

}
