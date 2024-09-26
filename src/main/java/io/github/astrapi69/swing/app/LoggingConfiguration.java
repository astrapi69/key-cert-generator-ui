package io.github.astrapi69.swing.app;

import java.io.IOException;
import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The class {@link LoggingConfiguration} setups the logging for the application
 */
public final class LoggingConfiguration
{

	/**
	 * Private constructor to prevent instantiation
	 */
	private LoggingConfiguration()
	{
	}

	/**
	 * Sets up all for logging
	 */
	public static void setup()
	{
		// Remove existing handlers attached to the root logger
		LogManager.getLogManager().reset();
		loadLoggingFile();
		setupJavaUtilLoggingToSlf4jBridge();
	}


	/**
	 * Sets up the SLF4J bridge handler to capture JUL logs. It removes the default JUL loggers and
	 * installs the SLF4J bridge handler to capture JUL logs
	 */
	public static void setupJavaUtilLoggingToSlf4jBridge()
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	/**
	 * Load the logging properties file to the {@link LogManager}
	 */
	public static void loadLoggingFile()
	{
		try
		{
			LogManager.getLogManager().readConfiguration(
				StartApplication.class.getClassLoader().getResourceAsStream("logging.properties"));
		}
		catch (IOException e)
		{
			System.err.println("Could not load logging properties file");
		}
	}

}
