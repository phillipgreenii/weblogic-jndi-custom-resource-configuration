package com.idmworks.weblogic.jndiconfiguration;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;

/**
 * Add {@link Properties} to JNDI in weblogic during startup.
 */
public class PropertiesInitializer {

  private static final Logger LOGGER = Logger.getLogger(PropertiesInitializer.class.getName());
  private final InitialContext initialContext;

  public PropertiesInitializer(final InitialContext initialContext) {
    this.initialContext = initialContext;
  }

  /**
   * Adds the properties to JNDI as specified in propertiesDescriptor.
   *
   * @param propertiesDescriptor properties and JNDI name to use
   */
  public void initialize(ValueDescriptor<Properties> propertiesDescriptor) {
    LOGGER.log(Level.FINE, "initializing {0}", propertiesDescriptor);
    try {
      JndiUtils.ensureSubcontexts(initialContext, propertiesDescriptor.getJndiName());
      initialContext.bind(propertiesDescriptor.getJndiName(), propertiesDescriptor.getProperties());
      LOGGER.log(Level.INFO, "Initialized {0}", propertiesDescriptor);
    } catch (NamingException e) {
      throw new IllegalArgumentException("Unable to bind " + propertiesDescriptor, e);
    }
  }

  /**
   * Parse arguments (jndiName=propertiesFile) and loads propertiesFile into JNDI.
   *
   * @param args
   */
  public static void main(final String[] args) {
    final Hashtable<String, String> ht = new Hashtable<String, String>();
    ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
    final InitialContext initialContext = JndiUtils.createInitialContext(ht);
    final NameParser nameParser = JndiUtils.createNameParser(initialContext);

    final PropertiesInitializer propertiesInitializer = new PropertiesInitializer(initialContext);

    for (final ValueDescriptor<Properties> propertiesDescriptor : parse(nameParser, args)) {
      propertiesInitializer.initialize(propertiesDescriptor);
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
  static List<ValueDescriptor<Properties>> parse(final NameParser nameParser, final String[] args) throws IllegalArgumentException {
    final List<ValueDescriptor<Properties>> propertiesDescriptors = new ArrayList<ValueDescriptor<Properties>>(args.length);
    for (String arg : args) {
      final String[] parts = arg.split("=", 2);
      final Name jndiName = JndiUtils.parseJndiName(nameParser, parts[0]);
      final Properties properties = PropertiesUtils.loadProperties(parts[1]);
      propertiesDescriptors.add(ValueDescriptor.of(jndiName, properties));
    }
    return propertiesDescriptors;
  }
}
