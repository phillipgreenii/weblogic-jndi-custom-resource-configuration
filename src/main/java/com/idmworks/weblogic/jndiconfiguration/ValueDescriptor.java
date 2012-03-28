package com.idmworks.weblogic.jndiconfiguration;

import javax.naming.Name;

/**
 * Contains JNDI name and value to be added to JNDI
 *
 * @author pdgreen
 */
public class ValueDescriptor<T> {

  private final Name jndiName;
  private final T value;

  public ValueDescriptor(final Name jndiName, final T value) {
    this.jndiName = jndiName;
    this.value = value;
  }

  public static <T> ValueDescriptor<T> of(final Name jndiName, final T value) {
    return new ValueDescriptor<T>(jndiName, value);
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
   * Value to be added to JNDI
   *
   * @return value
   */
  public T getProperties() {
    return value;
  }

  @Override
  public String toString() {
    return new StringBuffer(ValueDescriptor.class.getName()).append("{").append(jndiName).append("=").append(value).append("}").toString();
  }
}
