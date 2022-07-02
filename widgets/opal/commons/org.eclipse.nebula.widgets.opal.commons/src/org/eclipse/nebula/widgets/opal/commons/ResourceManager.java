package org.eclipse.nebula.widgets.opal.commons;

import java.util.ResourceBundle;

public class ResourceManager {
	private static final ResourceBundle RSC = ResourceBundle
			.getBundle(ResourceManager.class.getPackage().getName() + "/resources/opal");

	public static final String OK = "Ok";
	public static final String CANCEL = "Cancel";
	public static final String CLOSE = "Close";
	public static final String YES = "Yes";
	public static final String NO = "No";

	public static final String MEGABYTES = "megabytes";
	public static final String PERFORM_GC = "performGC";

	public static final String LOGIN = "login";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String REMEMBER_PASSWORD = "rememberPassword";
	public static final String LOGIN_FAILED = "loginFailed";

	public static final String INPUT = "Input";
	public static final String APPLICATION_ERROR = "ApplicationError";
	public static final String INFORMATION = "Information";
	public static final String WARNING = "Warning";
	public static final String CHOICE = "Choice";
	public static final String EXCEPTION = "Exception";
	public static final String SELECT = "Select";
	public static final String FEWER_DETAILS = "FewerDetails";
	public static final String MORE_DETAILS = "MoreDetails";

	public static final String TIP_OF_THE_DAY = "tipOfTheDay";
	public static final String DID_YOU_KNOW = "didYouKnow";
	public static final String SHOW_TIP_AT_STARTUP = "showTipAtStartup";
	public static final String PREVIOUS_TIP = "previousTip";
	public static final String NEXT_TIP = "nextTip";

	public static final String CHOOSE = "choose";
	public static final String PREFERENCES = "preferences";
	public static final String VALID_URL = "validURL";
	public static final String CHOOSE_DIRECTORY = "chooseDirectory";
	public static final String ITALIC = "italic";
	public static final String BOLD = "bold";

	public static final String CATEGORY_SHORT_DESCRIPTION = "category.shortDescription";
	public static final String DESCRIPTION_SHORT_DESCRIPTION = "description.shortDescription";
	public static final String SORT_SHORT_DESCRIPTION = "sort.shortDescription";
	public static final String PROPERTY = "property";
	public static final String VALUE = "value";
	public static final String EDIT_PROPERTY = "editProperty";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String ERASE_PROPERTY = "eraseProperty";

	public static final String PHYSICAL_MEMORY = "physicalMemory";
	public static final String HEAP_MEMORY = "heapMemory";
	public static final String THREADS = "threads";
	public static final String CPU_USAGE = "cpuUsage";
	public static final String PEAK = "peak";
	public static final String MB = "mb";

	public static final String CALCULATOR_DIVIDE_BY_ZERO = "calculator.dividebyzero";
	public static final String CALCULATOR_INVALID_VALUE = "calculator.invalid";

	public static final String MULTICHOICE_MESSAGE = "multichoice.message";
	public static final String MULTICHOICE_MESSAGE_PLURAL = "multichoice.message.plural";

	public static final String APPLY = "apply";

	public static final String SELECT_ALL = "selectAll";
	public static final String DESELECT_ALL = "deselectAll";

	/**
	 * Get a translated label
	 *
	 * @param key
	 *            key to get
	 * @return the translated value of the key
	 */
	public static String getLabel(final String key) {
		return RSC.getString(key);
	}

}
