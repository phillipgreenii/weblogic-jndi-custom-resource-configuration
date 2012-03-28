package com.idmworks.weblogic.jndiconfiguration;

import java.util.List;
import javax.naming.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Specification for {@link StringInitializer}.
 *
 * @author pdgreen
 */
public class StringInitializerSpec {

  @Test
  public void initializeShouldInitialzePropertiesDescriptor() throws NamingException {
    final InitialContext initialContext = mock(InitialContext.class);
    final Name jndiName = new CompositeName("properties/nothing");//CompositeName is used for testing
    final String value = "nose";

    final ValueDescriptor<String> stringDescriptor = ValueDescriptor.of(jndiName, value);

    final StringInitializer instance = new StringInitializer(initialContext);
    instance.initialize(stringDescriptor);

    verify(initialContext).bind(jndiName, value);
  }

  @Test
  public void parseShouldParseValidInputs() {
    final String[] args = {"properties/test=test", "properties/mock=value"};
    final NameParser nameParser = new NameParser() {

      public Name parse(String string) throws NamingException {
        return new CompositeName(string);//CompositeName is used for testing
      }
    };
    final List<ValueDescriptor<String>> result = StringInitializer.parse(nameParser, args);

    assertThat(result.size(), is(2));

    assertThat(result.get(0).getJndiName().toString(), is("properties/test"));
    assertThat(result.get(0).getValue(), is("test"));
    assertThat(result.get(1).getJndiName().toString(), is("properties/mock"));
    assertThat(result.get(1).getValue().toString(), is("value"));
  }
}
