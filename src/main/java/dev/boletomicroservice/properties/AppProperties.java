package dev.boletomicroservice.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * AppProperties
 * 
 * This class is responsible for loading and providing access to the application
 * properties.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
public class AppProperties {

	private static final String PROPERTIES_FILE_NAME = "variable.properties";
	private static final String ERROR_MESSAGE_TEMPLATE = "There is no key: '%s' in the properties file";
	private static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final Logger LOGGER = LoggerFactory.getLogger(AppProperties.class);

	private final Properties properties;
	private static AppProperties instance = new AppProperties();

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return the singleton instance of this class
	 */
	public static AppProperties factory() {
		return instance;
	}

	/**
	 * Constructor for the AppProperties class.
	 * Private constructor, to force the usage of the factory method.
	 */
	private AppProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME));
		} catch (IOException ioex) {
			logError("IOException Occured while loading properties file::::".concat(ioex.getMessage()));
		}
	}
	
	/**
	 * Checks if given key exists in the properties file.
	 *
	 * @param keyName the name of the key to check
	 * @return True if exists, False if not.
	 */
	public boolean containsKey(String keyName) {
		return properties.containsKey(keyName);
	}

	/**
	 * Returns the value of the given key from the properties file.
	 *
	 * @param keyName the name of the key to get the value from
	 * @return the value of the given key from the properties file
	 */
	public String getValue(String keyName) {
		return properties.getProperty(keyName, String.format(ERROR_MESSAGE_TEMPLATE, keyName));
	}
	
	/**
	 * Returns the value of the given key from the properties file as a Integer.
	 *
	 * @param keyName the name of the key to get the value from
	 * @return the value of the given key from the properties file
	 */
	public Integer getValueAsInteger(String keyName) {
		return Integer.parseInt(properties.getProperty(keyName, null));
	}
	
	/**
	 * Returns the value of the given key from the properties file as a Boolean.
	 *
	 * @param keyName the name of the key to get the value from
	 * @return the value of the given key from the properties file
	 */
	public boolean getValueAsBoolean(String keyName) {
		return Boolean.parseBoolean(properties.getProperty(keyName, "false"));
	}

	/**
	 * Logs a debug message.
	 *
	 * @param message the message to log
	 */
	public static void logDebug(String message) {
		LOGGER.debug(log("\u001B[34m DEBUG: ", message));
	}

	/**
	 * Logs an info message.
	 *
	 * @param message the message to log
	 */
	public static void logInfo(String message) {
		LOGGER.info(log("\u001B[32m INFO: ", message));
	}

	/**
	 * Logs an error message.
	 *
	 * @param message the message to log
	 */
	public static void logError(String message) {
		LOGGER.error(log("\u001B[31m ERROR: ", message));
	}

	/**
	 * Logs an exception as an error message.
	 *
	 * @param e the exception to log
	 */
	public static void logError(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		LOGGER.error(log("\u001B[31m ERROR: ", exceptionAsString));
	}

	/**
	 * Logs a message with the given log level.
	 *
	 * @param logLevel the log level
	 * @param message  the message to log
	 * @return the logged message
	 */
	private static String log(String logLevel, String message) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOG_DATE_FORMAT);
		final String logDate = LocalDateTime.now().format(formatter);
		return String.join("", logDate, logLevel, message, "\u001B[0m");
	}

	public static AppProperties getInstance() {
		return instance;
	}

	public static void setInstance(AppProperties instance) {
		AppProperties.instance = instance;
	}

	public Properties getProperties() {
		return properties;
	}
	
}