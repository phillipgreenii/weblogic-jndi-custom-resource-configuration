package com.idmworks.weblogic.jndiconfiguration;

import java.util.Hashtable;
import javax.naming.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Specification for {@link JndiUtils}.
 *
 * @author pdgreen
 */
public class JndiUtilsSpec {

  @Test
  public void createInitialContextShouldCreateNonnullInitialContext() {
    final Hashtable<String, String> ht = new Hashtable<String, String>();
    InitialContext result = JndiUtils.createInitialContext(ht);
    assertThat(result, is(notNullValue()));
  }
}
