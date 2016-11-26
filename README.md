# About 
This project contains a Byteman helper class and rule providing functionality to log SSL connection keys in a format used by wireshark to decrypt SSL Traffic.
The logfile path can be specified with the environment variable *SSL_KEYLOG_PATH*. If not specified, the keys will be printed to STDOUT

# Usage
The rule is part of the jar file, so in order to use the rule it must first be extracted. 

## run with log path specified
```
$ unzip path/to/sslkeylogger-byteman-helper.jar ssl_keylogger.btm
$ SSL_KEYLOG_PATH=/path/to/ssl.log java -Dorg.jboss.byteman.transform.all -javaagent:$BYTEMAN_HOME}/lib/byteman.jar=script:ssl_keylogger.btm,boot:${BYTEMAN_HOME}/lib/byteman.jar,boot:path/to/sslkeylogger-byteman-helper.jar -jar path/to/ssl_client.jar
$ cat $SSL_KEYLOG_PATH
# Sat Nov 26 19:12:11 UTC 2016
CLIENT_RANDOM 5839DE8A73FDA2FAF426A23863B59AFBBD345AEAED77EEE0DE0778BE63E3FE1E CBB90E69A8DE2BDA63FC978548BF90D6136049817806F5E72C035379CF27D5B5C83B71AB70250AEB4CEDE1A297616CC9
```

## run without log path specified
```
$ unzip path/to/sslkeylogger-byteman-helper.jar ssl_keylogger.btm
$ java -Dorg.jboss.byteman.transform.all -javaagent:$BYTEMAN_HOME}/lib/byteman.jar=script:ssl_keylogger.btm,boot:${BYTEMAN_HOME}/lib/byteman.jar,boot:path/to/sslkeylogger-byteman-helper.jar -jar -jar path/to/ssl_client.jar
CLIENT_RANDOM 5839DE8A73FDA2FAF426A23863B59AFBBD345AEAED77EEE0DE0778BE63E3FE1E CBB90E69A8DE2BDA63FC978548BF90D6136049817806F5E72C035379CF27D5B5C83B71AB70250AEB4CEDE1A297616CC9
```

### another Usage example
See project: [sslkeylogger-byteman-helper-example](https://github.com/mahnkong/sslkeylogger-byteman-helper-example)

# Build and install into local maven repo
./gradlew install
