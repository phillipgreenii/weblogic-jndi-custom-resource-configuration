package com.idmworks.weblogic.jndiconfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Common methods when dealing with {@link Properties}.
 *
 * @author pdgreen
 */
public class PropertiesUtils {

  /**
   * Loads properties specified at filename
   *
   * @param filename name of properties file
   * @return properties loaded from filename
   * @throws IllegalArgumentException thrown when filename is invalid
   */
  public static Properties loadProperties(final String filename) throws IllegalArgumentException {
    final File file = new File(filename);
    final Properties properties = new Properties();
    try {
      properties.load(new FileReader(file));
    } catch (FileNotFoundException ex) {
      throw new IllegalArgumentException("File not found: " + file, ex);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Unable to load properties: " + file, ex);
    }
    return properties;
  }
}
