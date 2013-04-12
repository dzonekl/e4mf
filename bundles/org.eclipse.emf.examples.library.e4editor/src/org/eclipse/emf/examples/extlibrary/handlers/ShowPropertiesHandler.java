package org.eclipse.emf.examples.extlibrary.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.examples.extlibrary.presentation.EditorIdentities;

@SuppressWarnings("restriction")
public class ShowPropertiesHandler {

	@Execute
	public void execute(EPartService partService) {
		MPart propertiesPart = partService
				.findPart(EditorIdentities.PROPERTIES_PART_ID);

		if (!partService.isPartVisible(propertiesPart)) {
			partService.activate(propertiesPart, true);
		}
	}
}