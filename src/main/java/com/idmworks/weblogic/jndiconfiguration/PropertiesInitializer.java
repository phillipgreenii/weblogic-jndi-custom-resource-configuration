package com.idmworks.weblogic.jndiconfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
  public void initialize(PropertiesDescriptor propertiesDescriptor) {
    LOGGER.log(Level.FINE, "initializing {0}", propertiesDescriptor);
    try {
      ensureSubcontexts(initialContext, propertiesDescriptor.getJndiName());
      initialContext.bind(propertiesDescriptor.getJndiName(), propertiesDescriptor.getProperties());
      LOGGER.log(Level.INFO, "Initialized {0}", propertiesDescriptor);
    } catch (NamingException e) {
      throw new IllegalArgumentException("Unable to bind " + propertiesDescriptor, e);
    }
  }

  /**
   * Parse String into Name.
   *
   * @param nameParser name parser to use
   * @param jndiName name to parse
   * @return Name
   * @throws IllegalArgumentException when nameParser is unable to parse jndiName
   */
  static Name parseJndiName(final NameParser nameParser, final String jndiName) {
    try {
      return nameParser.parse(jndiName);
    } catch (NamingException ex) {
      throw new IllegalArgumentException("Inproper JNDI Name: " + jndiName, ex);
    }
  }

  /**
   * Makes sure all subcontexts of name exist in baseContext.
   *
   * @param baseContext base context that subcontexts should exist in
   * @param name name whose subcontexts should exist in baseContext
   * @throws NamingException thrown when unable to ensure subcontext
   */
  static void ensureSubcontexts(final Context baseContext, final Name name) throws NamingException {
    LOGGER.log(Level.FINE, "ensureSubcontexts({0},{1})", new Object[]{baseContext, name});
    for (int i = 1; i < name.size(); i++) {
      retrieveOrCreateSubcontext(baseContext, name.getPrefix(i));
    }
  }

  /**
   * Retrieve or load subcontext of context
   *
   * @param context context to retrieve subcontext
   * @param subcontext subcontext to retrieve or create
   * @return retrieved or newly created subcontext
   * @throws NamingException thrown when unable to create or load subcontext
   */
  static Context retrieveOrCreateSubcontext(final Context context, final Name subcontext) throws NamingException {
    try {
      final Context createdContext = context.createSubcontext(subcontext);
      LOGGER.log(Level.INFO, "Created: {0}", createdContext);
      return createdContext;
    } catch (NameAlreadyBoundException ex) {
      final Object obj = context.lookup(subcontext);
      if (obj instanceof Context) {
        LOGGER.log(Level.FINE, "Found: {0}", obj);
        return (Context) obj;
      } else {
        throw new IllegalArgumentException("JNDI object isn't Context", ex);
      }
    }
  }

  /**
   * Contains JNDI name and properties to be added to JNDI
   */
  public static class PropertiesDescriptor {

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

  /**
   * Parse arguments (jndiName=propertiesFile) and loads propertiesFile into JNDI.
   *
   * @param args
   */
  public static void main(final String[] args) {
    final Hashtable<String, String> ht = new Hashtable<String, String>();
    ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
    final InitialContext initialContext = createInitialContext(ht);
    final NameParser nameParser = createNameParser(initialContext);

    final PropertiesInitializer propertiesInitializer = new PropertiesInitializer(initialContext);

    for (final PropertiesDescriptor propertiesDescriptor : parse(nameParser, args)) {
      propertiesInitializer.initialize(propertiesDescriptor);
    }
  }

  static InitialContext createInitialContext(final Hashtable<String, String> ht) throws IllegalStateException {
    try {
      return new InitialContext(ht);
    } catch (NamingException ex) {
      throw new IllegalStateException("unable to create Initial context", ex);
    }
  }

  static NameParser createNameParser(final Context context) throws IllegalStateException {
    try {
      return context.getNameParser("");
    } catch (NamingException ex) {
      throw new IllegalStateException("unable to create NameParser", ex);
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
  static List<PropertiesDescriptor> parse(final NameParser nameParser, final String[] args) throws IllegalArgumentException {
    final List<PropertiesDescriptor> propertiesDescriptors = new ArrayList<PropertiesDescriptor>(args.length);
    for (String arg : args) {
      final String[] parts = arg.split("=", 2);
      final Name jndiName = parseJndiName(nameParser, parts[0]);
      final Properties properties = loadProperties(parts[1]);
      propertiesDescriptors.add(new PropertiesDescriptor(jndiName, properties));
    }
    return propertiesDescriptors;
  }

  /**
   * Loads properties specified at filename
   *
   * @param filename name of properties file
   * @return properties loaded from filename
   * @throws IllegalArgumentException thrown when filename is invalid
   */
  static Properties loadProperties(final String filename) throws IllegalArgumentException {
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
