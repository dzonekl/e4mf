package org.eclipse.emf.examples.extlibrary.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
public class ShowOutlineHandler {

	@Execute
	public void execute(EPartService partService) {
		MPart outlinePart = partService
				.findPart("org.eclipse.e4.tools.part.outline");

		if (!partService.isPartVisible(outlinePart)) {
			partService.activate(outlinePart, true);
		}
	}
}