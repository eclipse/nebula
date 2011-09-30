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
   private IAction action = null;

   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType) {
      setControl(control);
      setSwtStyle(swtStyle);
      setInputField(inputField);
      setInputType(inputType);
   }

   public CellEditDescriptor(Class<?> control, Integer swtStyle, String inputField, Class<?> inputType, IAction action) {
      setControl(control);
      setSwtStyle(swtStyle);
      setInputField(inputField);
      setInputType(inputType);
      setAction(action);
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

}
