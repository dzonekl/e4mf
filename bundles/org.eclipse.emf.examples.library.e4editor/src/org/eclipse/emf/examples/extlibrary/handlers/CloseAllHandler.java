package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
public class CloseAllHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart activePart,
			EPartService ePartService) {

		for (MPart p : ePartService.getParts()) {
			// We will also hide/delete Part Stacks with this
			System.out.println("Closing part " + p.getElementId());
			ePartService.hidePart(p, true);
		}
	}

}