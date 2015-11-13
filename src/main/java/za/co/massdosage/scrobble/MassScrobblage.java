package za.co.massdosage.scrobble;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MassScrobblage {

  private static final String PROPERTY_USER_NAME = "user.name";
  private static final String PROPERTY_PASSWORD = "password";
  private static final String PROPERTY_API_KEY = "api.key";
  private static final String PROPERTY_SECRET = "secret";

  private static Logger log = Logger.getLogger(MassScrobblage.class);

  public static void main(String args[]) throws Exception {

    if (args.length < 1) {
      System.out.println("Usage: MassScrobblage path");
      System.exit(-1);
    }
    JulToSlf4jBridge bridge = new JulToSlf4jBridge();

    URL resource = MassScrobblage.class.getResource("/mass-scrobblage.properties");
    if (resource == null) {
      throw new IOException("Property file not found on classpath");
    }
    File configFile = new File(resource.toURI());
    log.info("Loading configuration from: " + configFile.getAbsolutePath());
    Reader configReader = new FileReader(configFile);
    Properties configuration = new Properties();
    configuration.load(configReader);
    log.info("Loaded config " + configuration);

    String apiKey = extractProperty(configuration, PROPERTY_API_KEY);
    String secret = extractProperty(configuration, PROPERTY_SECRET);
    String userName = extractProperty(configuration, PROPERTY_USER_NAME);
    String passwordHash = extractProperty(configuration, PROPERTY_PASSWORD);
    File scrobblePath = extractScrobblePath(args);
    FileScrobbler scrobbler = new FileScrobbler(apiKey, secret, userName, passwordHash);
    scrobbler.scrobbleFolder(scrobblePath);
  }

  private static String extractProperty(Properties configuration, String propertyName) throws IOException {
    String value = configuration.getProperty(propertyName);
    if (value == null) {
      throw new IOException("No configuration value found for " + propertyName);
    }
    return value;
  }

  private static File extractScrobblePath(String args[]) {
    String path = StringUtils.join(args, " ");
    return new File(path);
  }

}
