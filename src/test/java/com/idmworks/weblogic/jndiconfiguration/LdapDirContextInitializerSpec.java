package com.idmworks.weblogic.jndiconfiguration;

import java.util.List;
import java.util.Properties;
import javax.naming.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Specification for {@link LdapDirContextInitializer}.
 *
 * @author pdgreen
 */
public class LdapDirContextInitializerSpec {

  private String ldapTestPropertiesPath;

  @Before
  public void initializePropertiesPath() {
    ldapTestPropertiesPath = getClass().getClassLoader().getResource("ldap-test.properties").getPath();
  }

  @Test
  public void initializeShouldInitialzePropertiesDescriptor() throws NamingException {
    final InitialContext initialContext = mock(InitialContext.class);
    final Name jndiName = new CompositeName("properties/nothing");//CompositeName is used for testing
    final Name propertiesName = new CompositeName("properties/nothing" + LdapDirContextInitializer.PROPERTIES_NAME_SUFFIX);//CompositeName is used for testing
    final Properties properties = new Properties();

    final PropertiesDescriptor propertiesDescriptor = new PropertiesDescriptor(jndiName, properties);

    final LdapDirContextInitializer instance = new LdapDirContextInitializer(initialContext);
    instance.initialize(propertiesDescriptor);

    verify(initialContext).bind(eq(jndiName), anyObject());
    verify(initialContext).bind(propertiesName, properties);
  }

  @Test
  public void parseShouldParseValidInputs() {
    final String[] args = {"properties/test=" + ldapTestPropertiesPath};
    final NameParser nameParser = new NameParser() {

      public Name parse(String string) throws NamingException {
        return new CompositeName(string);//CompositeName is used for testing
      }
    };
    final List<PropertiesDescriptor> result = LdapDirContextInitializer.parse(nameParser, args);

    assertThat(result.size(), is(1));

    assertThat(result.get(0).getJndiName().toString(), is("properties/test"));
  }

  @Test
  public void buildPropertiesNameShouldBuildPropertiesNameFromLdapName() throws InvalidNameException {
    final Name jndiName = new CompositeName("properties/nothing");//CompositeName is used for testing
    final Name propertiesName = new CompositeName("properties/nothing" + LdapDirContextInitializer.PROPERTIES_NAME_SUFFIX);//CompositeName is used for testing

    final Name result = LdapDirContextInitializer.buildPropertiesName(jndiName);

    assertThat(result, is(propertiesName));
  }
}
