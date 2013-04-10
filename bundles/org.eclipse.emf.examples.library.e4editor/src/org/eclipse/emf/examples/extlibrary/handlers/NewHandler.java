package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.examples.extlibrary.presentation.EXTLibraryModelWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class NewHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell, EXTLibraryModelWizard modelWizard) {
		WizardDialog wizardDialog = new WizardDialog(activeShell, modelWizard);
		wizardDialog.open();
	}

}