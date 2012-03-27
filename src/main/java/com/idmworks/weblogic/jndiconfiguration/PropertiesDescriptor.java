package com.idmworks.weblogic.jndiconfiguration;

import java.util.Properties;
import javax.naming.Name;

/**
 * Contains JNDI name and properties to be added to JNDI
 *
 * @author pdgreen
 */
public class PropertiesDescriptor {

  private final Name jndiName;
  private final Properties properties;

  public PropertiesDescriptor(final Name jndiName, final Properties properties) {
    this.jndiName = jndiName;
    this.properties = properties;
  }

  /**
   * JNDI Name where the properties are to be located
   *
   * @return name of properties location in JNDI
   */
  public Name getJndiName() {
    return jndiName;
  }

  /**
   * Properties to be added to JNDI
   *
   * @return properties
   */
  public Properties getProperties() {
    return properties;
  }

  @Override
  public String toString() {
    return new StringBuffer(PropertiesDescriptor.class.getName()).append("{").append(jndiName).append("=").append(properties).append("}").toString();
  }
}
