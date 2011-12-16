package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public abstract class ImageAnimationPlayer
{
    private Image[] images;
    private int delay = 120;
    private Display display;
    private int currentImage = 0;
    
    private boolean stopped = false;
    
    public ImageAnimationPlayer(Display display)
    {
        this.display = display;
    }
    
    public void start()
    {
        stopped = false;
        if (images == null || images.length == 0) return;       
        
        updateImage(images[0]);
        Runnable run = new Runnable()
        {        
            public void run()
            {
                if (stopped) return;
                
                currentImage ++;
                if (currentImage == images.length)
                    currentImage = 0;
                updateImage(images[currentImage]);
                
                display.timerExec(delay, this);
            }        
        };
        
        display.timerExec(delay,run);
        
    }
    
    public void stop()
    {
        stopped = true;
    }
    
    
    public Image[] getImages()
    {
        return images;
    }

    public void setImages(Image[] images)
    {
        this.images = images;
    }

    public abstract void updateImage(Image i);

    public Display getDisplay()
    {
        return display;
    }

    public int getDelay()
    {
        return delay;
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }
    
}
