package org.eclipse.emf.examples.extlibrary.presentation;

import org.eclipse.e4.core.di.annotations.Execute;

public class ActionDelegate {
	
	@Execute
    public void execute() {
		System.out.println(this);
    }
}
