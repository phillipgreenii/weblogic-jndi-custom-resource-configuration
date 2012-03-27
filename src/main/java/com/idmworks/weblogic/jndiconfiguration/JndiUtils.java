package com.idmworks.weblogic.jndiconfiguration;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;

/**
 * Common methods when dealing with JNDI ({@link Context}).
 *
 * @author pdgreen
 */
public class JndiUtils {

  private static final Logger LOGGER = Logger.getLogger(JndiUtils.class.getName());

  public static InitialContext createInitialContext(final Hashtable<String, String> ht) throws IllegalStateException {
    try {
      return new InitialContext(ht);
    } catch (NamingException ex) {
      throw new IllegalStateException("unable to create Initial context", ex);
    }
  }

  public static NameParser createNameParser(final Context context) throws IllegalStateException {
    try {
      return context.getNameParser("");
    } catch (NamingException ex) {
      throw new IllegalStateException("unable to create NameParser", ex);
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
  public static Name parseJndiName(final NameParser nameParser, final String jndiName) {
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
  public static void ensureSubcontexts(final Context baseContext, final Name name) throws NamingException {
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
  public static Context retrieveOrCreateSubcontext(final Context context, final Name subcontext) throws NamingException {
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
}
