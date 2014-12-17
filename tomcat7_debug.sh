#set PORT=%1
#if "" == "%PORT%" set PORT=7555
# may pass in -o as second argument for faster startup but then you need to pass in port as first arg
#set TIMEZONE=-Duser.timezone=GMT+00:00
#set MAVEN_OPTS=-Xmx1024m %TIMEZONE% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%PORT%,server=y,suspend=n
#mvn %2 tomcat7:run-war
#mvn clean install -U tomcat7:run-war
mvn tomcat7:run-war