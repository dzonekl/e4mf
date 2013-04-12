package org.eclipse.e4mf.edit.ui.e4.action;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4mf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

@SuppressWarnings("restriction")
public class SelectAllHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part ) {
			
		// The contribution object is our editor. 
		if(part.getObject() != null && part.getObject() instanceof IViewerProvider){
			Viewer v = ((IViewerProvider) part.getObject()).getViewer();
			
			Object input = v.getInput();
			
			
			List<Object> toSelect = new ArrayList<Object>();
			
			// Flatten the input to set the selection. 
			if(input instanceof ResourceSet){
				TreeIterator<Notifier> allContents = ((ResourceSet) input).getAllContents();
				while( allContents.hasNext() ){
					Notifier next = allContents.next();
					toSelect.add(next);
				}
			}
			IStructuredSelection all = new StructuredSelection(toSelect);
			v.setSelection(all, true);
		}
		
	}

}