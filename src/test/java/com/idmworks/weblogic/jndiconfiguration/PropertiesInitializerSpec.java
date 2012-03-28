package com.idmworks.weblogic.jndiconfiguration;

import java.util.List;
import java.util.Properties;
import javax.naming.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Specification for {@link PropertiesInitializer}.
 *
 * @author pdgreen
 */
public class PropertiesInitializerSpec {

  private String testPropertiesPath;
  private String mockPropertiesPath;

  @Before
  public void initializePropertiesPath() {
    testPropertiesPath = getClass().getClassLoader().getResource("test.properties").getPath();
    mockPropertiesPath = getClass().getClassLoader().getResource("mock.properties").getPath();
  }

  @Test
  public void initializeShouldInitialzePropertiesDescriptor() throws NamingException {
    final InitialContext initialContext = mock(InitialContext.class);
    final Name jndiName = new CompositeName("properties/nothing");//CompositeName is used for testing
    final Properties properties = new Properties();

    final ValueDescriptor<Properties> propertiesDescriptor = ValueDescriptor.of(jndiName, properties);

    final PropertiesInitializer instance = new PropertiesInitializer(initialContext);
    instance.initialize(propertiesDescriptor);

    verify(initialContext).bind(jndiName, properties);
  }

  @Test
  public void parseShouldParseValidInputs() {
    final String[] args = {"properties/test=" + testPropertiesPath, "properties/mock=" + mockPropertiesPath};
    final NameParser nameParser = new NameParser() {

      public Name parse(String string) throws NamingException {
        return new CompositeName(string);//CompositeName is used for testing
      }
    };
    final List<ValueDescriptor<Properties>> result = PropertiesInitializer.parse(nameParser, args);

    assertThat(result.size(), is(2));

    assertThat(result.get(0).getJndiName().toString(), is("properties/test"));
    assertThat(result.get(1).getJndiName().toString(), is("properties/mock"));
  }
}
