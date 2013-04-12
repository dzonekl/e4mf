package org.eclipse.e4.tools.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * This class contains stuff, which I needed in e4 but is not available. It's
 * mainly used to visit and manipulate with the e4 Application model. For many
 * of these, there is an associated bug. Have Fun!
 * 
 * @author Christophe Bouhier
 */
@SuppressWarnings("restriction")
public class E4ToolsHelper extends ContextFunction {

	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(E4ToolsHelper.class, context);
	}

	/**
	 * Locates the global handlers, creates a copy and returns a collection.
	 * 
	 * @See EditorIdentities
	 * @param trimmedWindow
	 * @return
	 */
	public Collection<MMenuElement> locateGlobalHandlers(String[] ids,
			MTrimmedWindow trimmedWindow) {

		List<MMenuElement> globalHandlers = new ArrayList<MMenuElement>();

		for (String id : ids) {
			MMenuElement me = this.findMenu(trimmedWindow, id);
			if (me != null) {
				globalHandlers.add(this.cloneMenu(me));
			}
		}

		return globalHandlers;
	}

	/**
	 * Copy the menu entry to re-use elsewhere. FIXME, Dangerous as it creates
	 * duplicate UIElement ID's...
	 * 
	 * @param menuManager
	 * @return
	 */
	public MMenuElement cloneMenu(MMenuElement menuManager) {
		EObject copy = EcoreUtil.copy((EObject) menuManager);
		return (MMenuElement) copy;
	}

	/**
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=383403
	 * @param part
	 * @param id
	 * @return
	 */
	public MPopupMenu findPopupMenu(MPart part, String id) {

		MPopupMenu pum = null;
		for (MMenuElement me : part.getMenus()) {
			if (me instanceof MPopupMenu
					&& ((MPopupMenu) me).getElementId().equals(id)) {
				pum = (MPopupMenu) me;
			}
		}
		return pum;
	}

	/**
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=383403
	 * @param uiElement
	 * @param id
	 * @return
	 */
	public MMenuElement findMenu(MUIElement uiElement, String id) {

		if (match(uiElement, id)) {
			return (MMenuElement) uiElement;
		}

		if (uiElement instanceof MTrimmedWindow) {
			MMenuElement findMenu = findMenu(
					((MTrimmedWindow) uiElement).getMainMenu(), id);
			if (findMenu != null) {
				return findMenu;
			}
		} else if (uiElement instanceof MPart) {
			List<MMenu> menus = ((MPart) uiElement).getMenus();
			for (MMenu mm : menus) {
				MMenuElement findMenu = findMenu(mm, id);
				if (findMenu != null) {
					return findMenu;
				}
			}
		} else if (uiElement instanceof MMenu) {
			List<MMenuElement> children = ((MMenu) uiElement).getChildren();
			for (MMenuElement me : children) {
				MMenuElement findMenu = findMenu(me, id);
				if (findMenu != null) {
					return findMenu;
				}
			}
		}
		return null;
	}

	private boolean match(MUIElement uiElement, String id) {
		String idToMatch = uiElement.getElementId();
		return idToMatch != null ? idToMatch.equals(id) : false;
	}

}
