package org.eclipse.emf.examples.extlibrary.presentation;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4mf.edit.ui.e4.action.EditingDomainContribution;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

@SuppressWarnings("restriction")
public class EditorContribution  {

	@Inject
	private EditorSupport editorSupport;

	private MMenu copyOfcreateChildMenuManager;

	private MMenu copyOfcreateSiblingMenuManager;

	@PostConstruct
	public void init() {
	}

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {

		// CB TODO, Doesn't clean.
		// items.remove(copyOfcreateChildMenuManager);
		// items.remove(copyOfcreateChildMenuManager);
		// for (MMenuElement item : items) {
		// if(item instanceof MMenu){
		// ((MMenu) item).getChildren().clear();
		// }
		// }
		// items.clear();
		
	}

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {

		// // We need to re-create the menu, and dispose it later.
		//
		MMenu createChildMenuManager = editorSupport
				.getCreateChildMenuManager();
		System.out.println("EMF Editor selection changed child menu size: "
				+ createChildMenuManager.getChildren().size()
				+ "from editor support:" + editorSupport.hashCode());

		copyOfcreateChildMenuManager = (MMenu) EcoreUtil
				.copy((EObject) createChildMenuManager);

		items.add(copyOfcreateChildMenuManager);

		MMenu createSiblingMenuManager = editorSupport
				.getCreateSiblingMenuManager();
		System.out.println("EMF Editor selection changed sibling menu size: "
				+ createSiblingMenuManager.getChildren().size()
				+ "from editor support:" + editorSupport.hashCode());

		copyOfcreateSiblingMenuManager = (MMenu) EcoreUtil
				.copy((EObject) createSiblingMenuManager);

		items.add(copyOfcreateSiblingMenuManager);
	}
}
