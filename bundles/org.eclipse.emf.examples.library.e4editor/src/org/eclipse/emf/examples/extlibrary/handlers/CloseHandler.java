package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
public class CloseHandler {

	@CanExecute
	public boolean canExecute(
			@Named(IServiceConstants.ACTIVE_PART) MPart activePart) {
		return activePart.isCloseable();
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart activePart,
			EPartService ePartService) {
		ePartService.hidePart(activePart, true);
	}

}