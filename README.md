# Introduction

Unlike other application servers (JBoss, Glassfish, Tomcat, ...) WebLogic (as of 10g) doesn't support any way of adding custom resources into JNDI ([Custom resource in JNDI on different application servers][stackoverflow-question]).  While searching for a solution I found [weblogic-jndi-startup].  While I felt the implementation wasn't inline with what I wanted, the theory was a pretty simple solution.  [weblogic-jndi-startup] is more generic in that it can load an object that has a constructor with a single String argument.  I have made multiple _Initializers_ for more specific use cases.

# Installation

Installation requires copying _weblogic-jndi-custom-resource-configuration-1.x.x.jar_ into the classpath of WebLogic:

`cp weblogic-jndi-custom-resource-configuration-1.x.x.jar  $WEBLOGIC_DOMAINS/$SERVER/lib`

Once the jar has been added to the classpath, you can add _Initializers_ as _Startup Classes_.

## WebLogic's Startup and Shutdown Classes
WebLogic provides a way to run classes during Startup and Shutdown.  The class is run by calling _main()_ with a configured argument string.

### Install _Initializer_
To add a _Startup Class_: 

  1. Connect to _WebLogic Administration Console_.  
  1. Navigate to $DOMAIN->Environment->Startup and Shutdown Classes
  1. Click "New"
  1. Select "Startup Class"
  1. Click "Next"
  1. Enter any value for "Name"
  1. Enter the class of the desired _Initializer_ (`com.idmworks.weblogic.jndiconfiguration.StringInitializer`)
  1. Click "Next"
  1. Select Desired Targets
  1. Click "Finish"

### Configure _Initializer_
In the previous section, the _Initializer_ has been added, but not configured.

  1. Click on the recently created Startup Class
  1. Update "Arguments" with the arguments to be passed to the _Initializer_ (read below for expected arguments format)
  1. Tick "Run Before Application Activations" (this makes sure the _Initializer_ is ran before any applications are started)
  1. Click "Save"
  1. Restart WebLogic



# Initializers

_Initializers_ are classes configured as _Startup Classes_.  They add objects to JNDI based on the arguments of their configuration.  Below is each type of _Initializer_ with configuration details.


## StringInitializer
class: `com.idmworks.weblogic.jndiconfiguration.StringInitializer`

### Description
Places a String Object at a particular JNDI Location.

### Arguments Format
`[jndiName=StringToAdd]*`

### Example
`jsf/ProjectStage=Development server/node=Test`

The above argument would add "Development" to JNDI at the location of `jsf/ProjectState` and "Test" at the location of `server/node`


## PropertiesInitializer
class: `com.idmworks.weblogic.jndiconfiguration.PropertiesInitializer`

### Description
Loads Properties from a location and places it at a particular JNDI Location.

### Arguments Format
`[jndiName=pathToProperties]*`

### Example
`properties/myAppConfiguration=/etc/myApp/config/myapp.properties`

The above argument would Load the properties found at `/etc/myApp/config/myapp.properties` and add it JNDI at location `properties/MyAppConfiguration`


## LdapDirContextInitializer
class: `com.idmworks.weblogic.jndiconfiguration.LdapDirContextInitializer`

### Description
Bound to the specified JNDI Location will be a DirContext factory that will generate DirContexts that can be used to connect to the LDAP.  The specified properties will contain the connection information.  

**Required** connection properties:

 * java.naming.provider.url


**Optional** connection properties:

 * java.naming.factory.initial
 * java.naming.security.credentials
 * java.naming.security.principal


### Arguments Format
`[jndiName=pathToConnectionProperties]*`

### Example
`ldap/myLdap=/etc/myApp/config/myldap-connection.properties`

The above argument would Load the properties found at `/etc/myApp/config/myldap-connection` and add it JNDI at location `ldap/myLdap__properties`.  At location `ldap/myLdap` will be the DirContext.


# References
  * [stackoverflow-question]
  * [weblogic-jndi-startup]

[stackoverflow-question]: http://stackoverflow.com/questions/3749799/custom-resource-in-jndi-on-different-application-servers "Custom resource in JNDI on different application servers on stackoverflow"
[weblogic-jndi-startup]: http://code.google.com/p/weblogic-jndi-startup/ "weblogic-jndi-startup on Google Code"