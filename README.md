BetterPersistentManager
==============

An extension of tomcat's [org.apache.catalina.session.PersistentManagerBase](https://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/session/PersistentManagerBase.html).

We needed our sessions to swap out without writing to the database (because we have them backup to the database way before they swap out). When a user has an active session on tomcat A, then switches to tomcat B (because tomcat A went down), then switches back to tomcat A (because it came back up), we don't want tomcat B to persist any of the information about the session that it has anymore.  The last time it should have persisted was after the last change was made before the user switched back to tomcat A.  Our change makes this possible.

Installation
==============
You will need to place BetterPersistentManager's jar file in tomcat's lib directory.

Configuration
==============
Configuration is the same as Apache Tomcat's PersistentManager.
See: https://tomcat.apache.org/tomcat-8.0-doc/config/manager.html

You will however need to reference BetterPersistentManager instead of PersistentManager.  Here is an example of what your application's context.xml would contain:

```xml
<Context>
  ...
  <Manager className="org.globalgiving.session.BetterPersistentManager"
    maxIdleBackup="1"
    saveOnRestart="true">
    <Store className="org.apache.catalina.session.JDBCStore"
      driverName="com.mysql.jdbc.Driver" />
  </Manager>
  ...
</Context>
```
