package org.eclipse.e4mf.edit.ui.e4.action;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4mf.common.ui.viewer.IViewerProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

@SuppressWarnings("restriction")
public class SelectAllHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part ) {
			
		// The contribution object is our editor. 
		if(part.getObject() != null && part.getObject() instanceof IViewerProvider){
			Viewer v = ((IViewerProvider) part.getObject()).getViewer();
			if(v instanceof TreeViewer){
				((TreeViewer) v).getTree().selectAll();
//				v.setSelection(v.getSelection(), true);
			}
		}
	}
}