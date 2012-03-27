package com.idmworks.weblogic.jndiconfiguration;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.ObjectFactory;

/**
 * Creates {@link DirContext} for an LDAP connection.
 *
 * @author pdgreen
 */
public class LdapDirContextFactory implements ObjectFactory {

  public Object getObjectInstance(Object o, Name name, Context cntxt, Hashtable<?, ?> hshtbl) throws Exception {

    final Name propertiesName = LdapDirContextInitializer.buildPropertiesName(name);
    final Properties properties = (Properties) cntxt.lookup(propertiesName);

    final DirContext ldapDirContext = new InitialDirContext(buildInitialDirContextHashtableFrom(properties));

    return ldapDirContext;
  }

  static Hashtable buildInitialDirContextHashtableFrom(final Properties properties) {
    final Hashtable env = new Hashtable(properties);

    if (!env.containsKey(Context.INITIAL_CONTEXT_FACTORY)) {
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");//FIXME don't hardcode values
    }

    if (!env.containsKey(Context.PROVIDER_URL)) {
      throw new IllegalArgumentException(Context.PROVIDER_URL + " must be specified");
    }

    return env;
  }
}
