/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     Christophe Bouhier <christophe.bouhier@netxforge.com> Refactored to use Adapter pattern. 
 ******************************************************************************/
package org.eclipse.e4.tools.outline.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.tools.outline.IOutlinePartProvider;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class ContentOutlinePart {
	private Composite parent;

	private Control partControl;

	private IEclipseContext partContext;

	@Inject
	private Adapter adapter;

	@Inject
	public ContentOutlinePart(Composite parent) {
		this.parent = parent;
	}

	@Inject
	public void setActivePart(
			@Optional @Named(IServiceConstants.ACTIVE_PART) MPart activePart) {
		
		if (parent.isDisposed()) {
			return;
		}
		
		IEclipseContext oldPartContext = partContext;
		Control oldPartControl = partControl;

		if (activePart != null) {

			// Assume, the editor is a contribution.
			Object object = activePart.getObject();

			IOutlinePartProvider adapt = adapter.adapt(object,
					IOutlinePartProvider.class);
			if (adapt != null) {
				Class<?> outlineClass = adapt.getOutlineClass();
				partContext = activePart.getContext().createChild();
				Composite comp = new Composite(parent, SWT.NONE);
				comp.setLayout(new FillLayout());
				partControl = comp;
				partContext.set(Composite.class, comp);
				ContextInjectionFactory.make(outlineClass, partContext);
				// }
				// if (activePart.getObject() instanceof IOutlinePartProvider) {
				// Class<?> outlineClass = ((IOutlinePartProvider) activePart
				// .getObject()).getOutlineClass();
				// partContext = activePart.getContext().createChild();
				// Composite comp = new Composite(parent, SWT.NONE);
				// comp.setLayout(new FillLayout());
				// partControl = comp;
				// partContext.set(Composite.class, comp);
				// ContextInjectionFactory.make(outlineClass, partContext);
			} else {
				// We could actually the part...
				// if so do not thing. 
				if(activePart.getElementId().equals("org.eclipse.e4.tools.part.outline") ){
					return;
				}
				
			}
		} else {
			Label label = new Label(parent, SWT.NONE);
			label.setText("No outline available: ");
			partControl = label;
		}

		// Dispose afterwards which helps potential image resource pooling
		if (oldPartContext != null) {
			oldPartContext.dispose();
		}

		if (oldPartControl != null) {
			oldPartControl.dispose();
		}

		parent.layout(true, true);
	}

}
