package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.nebula.widgets.pgroup.PGroup;

public class BusyGroupAnimation extends ImageAnimationPlayer
{
    private PGroup group;    
    
    public BusyGroupAnimation(PGroup group)
    {
        super(group.getDisplay());
        this.group = group;
    }    

    @Override
    public void updateImage(Image i)
    {
        if (!i.isDisposed())
            group.setImage(i);
    }

}
