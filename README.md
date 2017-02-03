# Community Plugin Framework

**CPF** is a Plugin framework used as foundation for every CTool plugin

**CPF** is one of the _tools_ of the **CTools** family and it is shared library

#### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 1.8
* This [settings.xml](https://raw.githubusercontent.com/pentaho/maven-parent-poms/master/maven-support-files/settings.xml) in your <user-home>/.m2 directory

#### Building it

This is a maven project, and to build it use the following command
```
mvn clean install
```
The build result will be a Pentaho Plugin located in *core/target/cpf-core-**.jar *pentaho/target/cpf-pentaho-**.jar and . Then, this package can be resolved by other CTools build and used in their runtime.


For issue tracking and bug report please use http://jira.pentaho.com/browse/CDF. Its master branch is built upon commit merges in Jenkins Continuous Integration located in http://ci.pentaho.com/job/cpf-plugin/
