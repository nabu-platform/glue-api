#!/bin/bash
user=`whoami`
export M2_REPO=/home/$user/.m2/repository
export NABU_REPO=$M2_REPO/be/nabu

export CLASSPATH=$NABU_REPO/libs/converter/converter-api/1.0-SNAPSHOT/converter-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/converter/converter-base/1.0-SNAPSHOT/converter-base-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/evaluator/evaluator-api/1.0-SNAPSHOT/evaluator-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/glue/glue-api/1.0-SNAPSHOT/glue-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/glue/glue/1.0-SNAPSHOT/glue-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/resources/resources-api/1.0-SNAPSHOT/resources-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/resources/resources-file/1.0-SNAPSHOT/resources-file-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/utils/utils-io/1.0-SNAPSHOT/utils-io-1.0-SNAPSHOT.jar

# selenium dependencies
export CLASSPATH=$CLASSPATH:$NABU_REPO/glue/glue-selenese/1.0-SNAPSHOT/glue-selenese-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-java/2.43.1/selenium-java-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-chrome-driver/2.43.1/selenium-chrome-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-remote-driver/2.43.1/selenium-remote-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/cglib/cglib-nodep/2.1_3/cglib-nodep-2.1_3.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/json/json/20080701/json-20080701.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-api/2.43.1/selenium-api-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/com/google/guava/guava/15.0/guava-15.0.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/apache/httpcomponents/httpclient/4.3.4/httpclient-4.3.4.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/commons-codec/commons-codec/1.9/commons-codec-1.9.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/apache/commons/commons-exec/1.1/commons-exec-1.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/java/dev/jna/platform/3.4.0/platform-3.4.0.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-htmlunit-driver/2.43.1/selenium-htmlunit-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/sourceforge/htmlunit/htmlunit/2.15/htmlunit-2.15.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/xalan/xalan/2.7.1/xalan-2.7.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/xalan/serializer/2.7.1/serializer-2.7.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/xml-apis/xml-apis/1.4.01/xml-apis-1.4.01.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/apache/httpcomponents/httpmime/4.3.3/httpmime-4.3.3.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/sourceforge/htmlunit/htmlunit-core-js/2.15/htmlunit-core-js-2.15.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/xerces/xercesImpl/2.11.0/xercesImpl-2.11.0.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/sourceforge/nekohtml/nekohtml/1.9.21/nekohtml-1.9.21.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/net/sourceforge/cssparser/cssparser/0.9.14/cssparser-0.9.14.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/w3c/css/sac/1.3/sac-1.3.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/commons-io/commons-io/2.4/commons-io-2.4.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/eclipse/jetty/jetty-websocket/8.1.15.v20140411/jetty-websocket-8.1.15.v20140411.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/eclipse/jetty/jetty-util/8.1.15.v20140411/jetty-util-8.1.15.v20140411.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/eclipse/jetty/jetty-io/8.1.15.v20140411/jetty-io-8.1.15.v20140411.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/eclipse/jetty/jetty-http/8.1.15.v20140411/jetty-http-8.1.15.v20140411.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-firefox-driver/2.43.1/selenium-firefox-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-ie-driver/2.43.1/selenium-ie-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-safari-driver/2.43.1/selenium-safari-driver-2.43.1.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/webbitserver/webbit/0.4.15/webbit-0.4.15.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/io/netty/netty/3.5.5.Final/netty-3.5.5.Final.jar
export CLASSPATH=$CLASSPATH:$M2_REPO/org/seleniumhq/selenium/selenium-support/2.43.1/selenium-support-2.43.1.jar

java -cp $CLASSPATH be.nabu.glue.Main "$@"

