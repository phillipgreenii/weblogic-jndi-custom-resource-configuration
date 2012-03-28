package com.idmworks.weblogic.jndiconfiguration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;
import javax.naming.directory.DirContext;

/**
 * Add {@link DirContext} (for connecting to an LDAP) to JNDI in weblogic during startup.
 */
public class LdapDirContextInitializer {

  private static final Logger LOGGER = Logger.getLogger(PropertiesInitializer.class.getName());
  static final String PROPERTIES_NAME_SUFFIX = "__properties";
  private final InitialContext initialContext;

  public LdapDirContextInitializer(final InitialContext initialContext) {
    this.initialContext = initialContext;
  }

  /**
   * Creates and adds DirContext LDAP factory and the properties to JNDI as specified in propertiesDescriptor. <br>
   * <code>PropertiesDescriptor.getJndiName()</code> returns the name the DirContext LDAP factory location<br>
   * <code>PropertiesDescriptor.getProperties()</code> returns the connection properties for the DirContext LDAP
   * factory<br>
   *
   * @param propertiesDescriptor properties and JNDI name to use
   */
  public void initialize(ValueDescriptor<Properties> propertiesDescriptor) {
    LOGGER.log(Level.FINE, "initializing {0}", propertiesDescriptor);
    try {
      JndiUtils.ensureSubcontexts(initialContext, propertiesDescriptor.getJndiName());

      final Name ldapConnectionFactoryName = propertiesDescriptor.getJndiName();
      final Name propertiesName = buildPropertiesName(propertiesDescriptor.getJndiName());

      final Reference reference = new Reference(DirContext.class.getName(), LdapDirContextFactory.class.getName(), null);
      initialContext.bind(ldapConnectionFactoryName, reference);
      LOGGER.log(Level.INFO, "Bound LDAP Connection Factory to {0} as {1}", new Object[]{ldapConnectionFactoryName, reference});

      initialContext.bind(propertiesName, propertiesDescriptor.getValue());
      LOGGER.log(Level.INFO, "Bound LDAP Connection Factory Properties of {0} to {1}", new Object[]{ldapConnectionFactoryName, propertiesName});
    } catch (NamingException e) {
      throw new IllegalArgumentException("Unable to bind " + propertiesDescriptor, e);
    }
  }

  /**
   * Builds the name of the associated properties.
   *
   * @param ldapName name of the DirContext Factory for LDAP
   * @return name of the properties
   */
  public static Name buildPropertiesName(final Name ldapName) {
    try {
      final Name propertiesName = ldapName.getPrefix(ldapName.size() - 1);
      final String propertiesStringName = ldapName.get(ldapName.size() - 1) + PROPERTIES_NAME_SUFFIX;
      propertiesName.add(propertiesStringName);
      return propertiesName;
    } catch (InvalidNameException ex) {
      throw new IllegalArgumentException("unable to build properties name for " + ldapName, ex);
    }
  }

  /**
   * Parse arguments (ldapJndiName=ldapConnectionPropertiesFile) and loads DirContext factory and propertiesFile into
   * JNDI.
   *
   * @param args
   */
  public static void main(final String[] args) {
    final Hashtable<String, String> ht = new Hashtable<String, String>();
    ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
    final InitialContext initialContext = JndiUtils.createInitialContext(ht);
    final NameParser nameParser = JndiUtils.createNameParser(initialContext);

    final LdapDirContextInitializer ldapConnectionInitializer = new LdapDirContextInitializer(initialContext);

    for (final ValueDescriptor<Properties> propertiesDescriptor : parse(nameParser, args)) {
      ldapConnectionInitializer.initialize(propertiesDescriptor);
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
