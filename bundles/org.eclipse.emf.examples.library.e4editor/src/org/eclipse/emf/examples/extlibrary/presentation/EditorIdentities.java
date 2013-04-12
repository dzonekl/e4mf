package org.eclipse.emf.examples.extlibrary.presentation;

/**
 * Holds identities which are required for lookup and program created
 * Application model entities. It's not the complete list.
 * 
 * @author Christophe Bouhier
 */
public interface EditorIdentities {

	// LOOKUP ID's

	/**
	 * The parent ID of the part stack, used to re-create the stack if it's
	 * closed
	 */
	public static final String STACK_PARENT_ID = "org.eclipse.emf.examples.library.e4editor.partsashcontainer.main";
	/** The ID of the part stack which will be inserted */
	public static final String STACK_POSITION_ID = "org.eclipse.emf.examples.library.e4editor.partstack.editor.stack";

	/** The ID of the refresh menu item **/
	public static final String REFRESH_MENU_ID = "org.eclipse.emf.examples.library.e4editor.handledmenuitem.refresh";

	/** The ID of the show properties menu item **/
	public static final String SHOW_PROPERTIES_MENU_ID = "org.eclipse.emf.examples.library.e4editor.handledmenuitem.showProperties";

	/** The ID of the show outline menu item **/
	public static final String SHOW_OUTLINE_MENU_ID = "org.eclipse.emf.examples.library.e4editor.handledmenuitem.showOutline";

	/** The ID of the separator which acts as a marker to insert after */
	public static final String EDIT_SEPARATOR_ID = "org.eclipse.emf.examples.library.e4editor.menuseparator.edit";

	/** The ID of the separator for global handlers */
	public static final String LOAD_RESOURCE_SEPARATOR_ID = "org.eclipse.e4mf.edit.ui.menuseparator.loadResource";

	/** The ID of the load resource menu item **/
	public static final String LOAD_RESOURCE_ID = "org.eclipse.e4mf.edit.ui.handledmenuitem.loadResource";

	/** A collection of Global Handler ID's **/
	public static final String[] GLOBAL_HANDLERS = new String[] {
			LOAD_RESOURCE_ID, LOAD_RESOURCE_SEPARATOR_ID, REFRESH_MENU_ID,
			SHOW_PROPERTIES_MENU_ID, SHOW_OUTLINE_MENU_ID, EDIT_SEPARATOR_ID };

	/** Our main window */
	public static final String MAIN_WINDOW_ID = "org.eclipse.emf.examples.library.e4editor.trimmedwindow.main";

	// CREATION ID's

	/** The ID of the child create menu */
	public static final String CHILD_CREATE_ID = "org.eclipse.emf.examples.library.e4editor.menu.child.create";
	/** The ID of the sibling create menu */
	public static final String SIBLING_CREATE_ID = "org.eclipse.emf.examples.library.e4editor.menu.sibling.create";

	/** The ID of the editor part which is created */
	public static final String EDITOR_ID = "org.eclipse.emf.examples.library.e4editor.partstack.editor";
	/** The ID of the pop-up menu, which is programmatically created */
	public static final String EDITOR_POPUP_ID = "org.eclipse.emf.examples.library.e4editor.popup";

	// CONTRIBUTOR URI
	/** Use when asked for a Contributor */
	public static final String PLUGIN_ID = "platform:/plugin/org.eclipse.emf.example.library.e4.editor";

	// CONTRIBUTION URI's

	/** The URI for the contributed Editor */
	public static final String EDITOR_CONTRIBUTION_URI = "bundleclass://org.eclipse.emf.examples.library.e4editor/org.eclipse.emf.examples.extlibrary.presentation.EXTLibraryEditor";

	/** The URI for the contributed Action Delegate */
	public static final String CREATION_CONTRIBUTION_URI = "bundleclass://org.eclipse.emf.examples.library.e4editor/org.eclipse.emf.examples.extlibrary.presentation.ActionDelegate";

}
