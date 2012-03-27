package com.idmworks.weblogic.jndiconfiguration;

import java.util.Properties;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Specification for {@link PropertiesUtils}.
 *
 * @author pdgreen
 */
public class PropertiesUtilsSpec {

  private String testPropertiesPath;

  @Before
  public void initializePropertiesPath() {
    testPropertiesPath = getClass().getClassLoader().getResource("test.properties").getPath();
  }

  @Test
  public void loadPropertiesShouldLoadPropertesWhenExists() {
    final String filename = testPropertiesPath;

    final Properties result = PropertiesUtils.loadProperties(filename);

    assertThat(result.size(), is(6));

    assertThat(result.getProperty("testfield1"), is("1"));
    assertThat(result.getProperty("testfield2"), is("1"));
    assertThat(result.getProperty("testfield3"), is("2"));
    assertThat(result.getProperty("testfield4"), is("3"));
    assertThat(result.getProperty("testfield5"), is("5"));
    assertThat(result.getProperty("testfield6"), is("8"));
  }
}
