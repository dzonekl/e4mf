package org.eclipse.emf.examples.extlibrary.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4mf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class OpenURIHandler {

	@Inject
	private HandlerSupport support;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		
		// CB TODO, Based on Actions, rework the actions first, make the dialog available with DI. 
		LoadResourceAction.LoadResourceDialog loadResourceDialog = new LoadResourceAction.LoadResourceDialog(
				shell);

		if (Window.OK == loadResourceDialog.open()) {
			for (URI uri : loadResourceDialog.getURIs()) {
				support.openEditor(uri);
			}
		}
	}

}
