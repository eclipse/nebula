package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

public interface IEvaluator {

	Evaluation evaluate(String text);
	
	boolean isValid(String text);

}
