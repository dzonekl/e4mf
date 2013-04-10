 
package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4mf.common.ui.viewer.IViewerProvider;
import org.eclipse.jface.viewers.Viewer;

@SuppressWarnings("restriction")
public class RefreshHandler {
	
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part){
		return part.getObject() instanceof IViewerProvider;
	}
	
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {
		if (part.getObject() instanceof IViewerProvider) {
			Viewer viewer = ((IViewerProvider)part.getObject()).getViewer();
			if (viewer != null) {
				viewer.refresh();
			}
		}
	}
		
}