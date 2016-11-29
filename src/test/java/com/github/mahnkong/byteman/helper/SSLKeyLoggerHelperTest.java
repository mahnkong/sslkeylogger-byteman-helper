package com.github.mahnkong.byteman.helper;

import com.github.mahnkong.testutils.byteman.BytemanAgentInstaller;
import com.github.mahnkong.testutils.byteman.BytemanRuleFile;
import com.github.mahnkong.testutils.byteman.BytemanRuleSubmitter;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mahnkong on 27.11.2016.
 */
public class SSLKeyLoggerHelperTest {
    static {
        disableSslVerification();
    }

    private SSLKeyLoggerHelper sslKeyLoggerHelper;
    private ByteArrayOutputStream outContent;
    private final File sslkey_testlog = new File(System.getProperty("java.io.tmpdir") + File.separator + "sslkeys_test.log");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @ClassRule
    public static BytemanAgentInstaller bytemanAgentInstaller = new BytemanAgentInstaller.Builder().installIntoBootstrapClasspath(true).transformAll(true).build();

    @Rule
    public BytemanRuleSubmitter bytemanRuleSubmitter = new BytemanRuleSubmitter.Builder().build();

    private PrintStream oldSysOut = System.out;

    @Before
    public void setup() throws ParseException, TypeException, CompileException {
        sslKeyLoggerHelper = new SSLKeyLoggerHelper(null);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        sslkey_testlog.delete();
    }

    @After
    public void cleanUpStreams() {
        System.setOut(oldSysOut);
    }

    @Test
    public void testLogSessionSSLDataToStdout() throws IOException {
        sslKeyLoggerHelper.logSSLSessionData("A".getBytes(), "B".getBytes());
        assertEquals("CLIENT_RANDOM 41 42", outContent.toString().trim());
    }

    @Test
    @BytemanRuleFile(filepath = "build/resources/test/mock_system_getenv.btm")
    public void testLogSessionSSLDataToLog() throws IOException {
        sslKeyLoggerHelper.logSSLSessionData("A".getBytes(), "B".getBytes());
        List<String> lines=Files.readAllLines(Paths.get(sslkey_testlog.getAbsolutePath()), Charset.forName("UTF-8"));
        assertEquals("CLIENT_RANDOM 41 42", lines.get(1));
    }

    @Test
    @BytemanRuleFile(filepath = "build/resources/test/mock_system_getenv_fail.btm")
    public void testLogSessionSSLDataToLogFailure() throws IOException {
        thrown.expect(IOException.class);
        sslKeyLoggerHelper.logSSLSessionData("A".getBytes(), "B".getBytes());
    }

    @Test
    @BytemanRuleFile(filepath = "build/resources/main/ssl_keylogger.btm")
    public void testLogSessionSSLDataUsingRule() throws IOException {

        String httpsURL = "https://google.com";
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);

        while (in.readLine() != null) {
        }
        in.close();
        assertTrue(outContent.toString().trim().startsWith("CLIENT_RANDOM "));
    }

    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
