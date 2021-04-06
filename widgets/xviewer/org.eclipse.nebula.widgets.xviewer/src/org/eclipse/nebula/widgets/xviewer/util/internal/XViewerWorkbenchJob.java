package org.eclipse.nebula.widgets.xviewer.util.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.nebula.widgets.xviewer.util.XViewerDisplay;
import org.eclipse.swt.widgets.Display;

/**
 * WorkbenchJob is a type of job that implements a done listener and does the shutdown checks before scheduling. This is
 * used if a job is not meant to run when the Workbench is shutdown.
 *
 * @since 3.0
 */
public abstract class XViewerWorkbenchJob extends XViewerUIJob {

   /**
    * Create a new instance of the receiver with the supplied display and name. Normally this constructor would not be
    * used as it is best to let the job find the display from the workbench
    *
    * @param jobDisplay Display. The display to run the job with.
    * @param name String
    */
   public XViewerWorkbenchJob(Display jobDisplay, String name) {
      super(jobDisplay, name);
      addDefaultJobChangeListener();
   }

   /**
    * Add a new instance of the reciever with the supplied name.
    *
    * @param name String
    */
   public XViewerWorkbenchJob(String name) {
      super(name);
      addDefaultJobChangeListener();
   }

   /**
    * Add a job change listeners that handles a done event if the result was IStatus.OK.
    */
   private void addDefaultJobChangeListener() {
      addJobChangeListener(new JobChangeAdapter() {
         /**
          * (non-Javadoc)
          * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
          */
         @Override
         public void done(IJobChangeEvent event) {

            //Abort if it is not running
            if (!XViewerDisplay.isStandaloneXViewer() && !XViewerDisplay.isWorkbenchRunning()) {
               return;
            }

            if (event.getResult().getCode() == IStatus.OK) {
               performDone(event);
            }
         }
      });
   }

   /**
    * Perform done with the supplied event. This will only occur if the returned status was OK. This is called only if
    * the job is finished with an IStatus.OK result and the workbench is still running.
    *
    * @param event IJobChangeEvent
    */
   public void performDone(IJobChangeEvent event) {
      //Do nothing by default.
   }


	/**
	 * @see org.eclipse.core.runtime.jobs.Job#shouldSchedule()
	 */
	@Override
   public boolean shouldSchedule() {
      boolean result =
         super.shouldSchedule() && (XViewerDisplay.isStandaloneXViewer() || XViewerDisplay.isWorkbenchRunning());
      return result;
   }

   /**
    * (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
    */
   @Override
   public boolean shouldRun() {
      boolean result = super.shouldRun() && (XViewerDisplay.isStandaloneXViewer() || XViewerDisplay.isWorkbenchRunning());
      return result;
   }

}
