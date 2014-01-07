package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.jface.action.IAction;

/**
 * description object to define which control, which swtStyle, which inputField and which inputType
 * 
 * @author Juergen Reichl
 */
public class CellEditDescriptor {

   private String inputField;
   private Class<?> inputType;
   private Class<?> control;
   private Integer swtStyle;
   private boolean fitInCell = true;
   private IAction action = null;

   /**
    * @param control - the control to create
    * @param swtStyle - style of the control
    * @param inputField - the input field (identifier)
    * @param inputType - type of the input
    */
   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType) {
      this(control, swtStyle, inputField, inputType, null);
   }

   /**
    * @param control - the control to create
    * @param swtStyle - style of the control
    * @param inputField - the input field (identifier)
    * @param inputType - type of the input
    * @param action - action for automatic isEnabled check
    */
   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType, IAction action) {
      this(control, swtStyle, inputField, inputType, action, true);
   }

   /**
    * @param control - the control to create
    * @param swtStyle - style of the control
    * @param inputField - the input field (identifier)
    * @param inputType - type of the input
    * @param fitInCell - fit control in cell
    */
   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType, boolean fitInCell) {
      this(control, swtStyle, inputField, inputType, null, fitInCell);
   }

   /**
    * @param control - the control to create
    * @param swtStyle - style of the control
    * @param inputField - the input field (identifier)
    * @param inputType - type of the input
    * @param action - action for automatic isEnabled check
    * @param fitInCell - fit control in cell
    */
   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType, IAction action, boolean fitInCell) {
      setControl(control);
      setSwtStyle(swtStyle);
      setInputField(inputField);
      setInputType(inputType);
      setAction(action);
      setFitInCell(fitInCell);
   }

   public String getInputField() {
      return inputField;
   }

   public void setInputField(String inputField) {
      this.inputField = inputField;
   }

   public Class<?> getInputType() {
      return inputType;
   }

   public void setInputType(Class<?> inputType) {
      this.inputType = inputType;
   }

   public Class<?> getControl() {
      return control;
   }

   public void setControl(Class<?> control) {
      this.control = control;
   }

   public void setSwtStyle(Integer swtStyle) {
      this.swtStyle = swtStyle;
   }

   public Integer getSwtStyle() {
      return swtStyle;
   }

   public void setAction(IAction action) {
      this.action = action;
   }

   public IAction getAction() {
      return action;
   }

   public boolean isFitInCell() {
      return fitInCell;
   }

   public void setFitInCell(boolean fitInCell) {
      this.fitInCell = fitInCell;
   }

}
