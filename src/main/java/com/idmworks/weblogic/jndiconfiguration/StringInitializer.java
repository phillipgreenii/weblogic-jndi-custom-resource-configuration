package com.idmworks.weblogic.jndiconfiguration;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;

/**
 * Add {@link String} to JNDI in weblogic during startup.
 */
public class StringInitializer {

  private static final Logger LOGGER = Logger.getLogger(StringInitializer.class.getName());
  private final InitialContext initialContext;

  public StringInitializer(final InitialContext initialContext) {
    this.initialContext = initialContext;
  }

  /**
   * Adds the String into JNDI as specified in valueDescriptor.
   *
   * @param stringDescriptor properties and JNDI name to use
   */
  public void initialize(ValueDescriptor<String> stringDescriptor) {
    LOGGER.log(Level.FINE, "initializing {0}", stringDescriptor);
    try {
      JndiUtils.ensureSubcontexts(initialContext, stringDescriptor.getJndiName());
      initialContext.bind(stringDescriptor.getJndiName(), stringDescriptor.getValue());
      LOGGER.log(Level.INFO, "Initialized {0}", stringDescriptor);
    } catch (NamingException e) {
      throw new IllegalArgumentException("Unable to bind " + stringDescriptor, e);
    }
  }

  /**
   * Parse arguments (jndiName=value) and loads String into JNDI.
   *
   * @param args
   */
  public static void main(final String[] args) {
    final Hashtable<String, String> ht = new Hashtable<String, String>();
    ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
    final InitialContext initialContext = JndiUtils.createInitialContext(ht);
    final NameParser nameParser = JndiUtils.createNameParser(initialContext);

    final StringInitializer propertiesInitializer = new StringInitializer(initialContext);

    for (final ValueDescriptor<String> stringDescriptor : parse(nameParser, args)) {
      propertiesInitializer.initialize(stringDescriptor);
    }
  }

  /**
   * Parse input arguments
   *
   * @param nameParser parser to parse JNDI Name
   * @param args args to parse
   * @return PropertyDescriptors based on parsed args
   * @throws IllegalArgumentException thrown when args can't be parsed
   */
  static List<ValueDescriptor<String>> parse(final NameParser nameParser, final String[] args) throws IllegalArgumentException {
    final List<ValueDescriptor<String>> stringDescriptors = new ArrayList<ValueDescriptor<String>>(args.length);
    for (String arg : args) {
      final String[] parts = arg.split("=", 2);
      final Name jndiName = JndiUtils.parseJndiName(nameParser, parts[0]);
      final String value = parts[1];
      stringDescriptors.add(ValueDescriptor.of(jndiName, value));
    }
    return stringDescriptors;
  }
}
