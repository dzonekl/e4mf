package org.eclipse.e4mf.edit.ui.e4.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.helpers.E4ToolsHelper;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

@SuppressWarnings("restriction")
@Singleton
public class EditingDomainContribution extends ContextFunction {

	// Either lookup the entries one by one or locate the edit menu and clone it
	// entirely to inject it in the popup.

	/** The ID of the Undo menu */
	public static final String UNDO_MENU_ID = "org.eclipse.e4mf.edit.ui.handledmenuitem.undo";

	/** A collection of Global Handler ID's **/
	public static final String[] GLOBAL_HANDLERS = new String[] { UNDO_MENU_ID, };

	public static final String EDIT_MENU_ID = "org.eclipse.emf.examples.library.e4editor.menu.edit";

	@Inject
	private E4ToolsHelper helpMeWith;

	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(EditingDomainContribution.class,
				context);
	}

	public void addGlobalHandlers(MTrimmedWindow trimmedWindow, MMenu menu) {
		System.out.println("Adding Editing Domain global handlers");
//		menu.getChildren()
//				.addAll(helpMeWith.locateGlobalHandlers(GLOBAL_HANDLERS,
//						trimmedWindow));

		// Clone the edit menu and add tot popup. 
		MMenuElement editMenu = helpMeWith
				.findMenu(trimmedWindow, EDIT_MENU_ID);
		if (editMenu != null) {
			MMenuElement cloneEditMenu = helpMeWith.cloneMenu(editMenu);
			menu.getChildren().add(cloneEditMenu);
		}
	}
	
	 /**
	   * This determines whether or not the delete action should clean up all references to the deleted objects.
	   * It is false by default, to provide the same beahviour, by default, as in EMF 2.1 and before.
	   * You should probably override this method to return true, in order to get the new, more useful beahviour.
	   * @since 2.2
	   */
	  protected boolean removeAllReferencesOnDelete()
	  {
	    return false;
	  }
}
