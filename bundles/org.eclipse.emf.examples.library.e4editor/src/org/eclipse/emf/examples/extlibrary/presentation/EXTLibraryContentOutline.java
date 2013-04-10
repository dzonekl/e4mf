package org.eclipse.emf.examples.extlibrary.presentation;

import javax.inject.Inject;

import org.eclipse.e4.tools.outline.SWTOutlinePart;
import org.eclipse.e4mf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.e4mf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class EXTLibraryContentOutline extends SWTOutlinePart {

	@Inject
	private EditingDomain editingDomain;

	@Inject
	private AdapterFactory adapterFactory;

	protected TreeViewer contentOutlineViewer;

	@Override
	protected void createControl(Composite parent) {
		
		super.createControl(parent);
		contentOutlineViewer = getTreeViewer();
		contentOutlineViewer.addSelectionChangedListener(this);

		// Set up the tree viewer.
		//
		contentOutlineViewer
				.setContentProvider(new AdapterFactoryContentProvider(
						adapterFactory));
		contentOutlineViewer.setLabelProvider(new AdapterFactoryLabelProvider(
				adapterFactory));
		contentOutlineViewer.setInput(editingDomain.getResourceSet());

		// Make sure our popups work.
		//
		// createContextMenuFor(contentOutlineViewer);

		if (!editingDomain.getResourceSet().getResources().isEmpty()) {
			// Select the root object in the view.
			//
			contentOutlineViewer
					.setSelection(new StructuredSelection(editingDomain
							.getResourceSet().getResources().get(0)), true);
		}
	}
}
