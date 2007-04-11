package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

public class BusyItemAnimation extends ImageAnimationPlayer
{
    private Item item;    
    
    public BusyItemAnimation(Item item)
    {
        super(item.getDisplay());
        this.item = item;
    }    

    @Override
    public void updateImage(Image i)
    {
        if (!i.isDisposed())
            item.setImage(i);
    }

}
