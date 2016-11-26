package com.github.mahnkong.byteman.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * Created by mahnkong on 26.11.2016.
 */
public class SSLKeyLoggerHelper extends Helper {
    protected SSLKeyLoggerHelper(Rule rule) {
        super(rule);
    }

    public void logSSLSessionData(byte[] clientNonce, byte[] masterSecret) throws IOException {
        String logline = String.format("CLIENT_RANDOM %s %s", printHexBinary(clientNonce), printHexBinary(masterSecret));
        String logfilePath = System.getenv("SSL_KEYLOG_PATH");
        if (logfilePath == null || logfilePath.isEmpty()) {
            System.out.println(logline);
        } else {
            Files.write(Paths.get(logfilePath), Arrays.asList(String.format("# %s", new Date().toString()), logline), UTF_8, APPEND, CREATE);
        }
    }
}
