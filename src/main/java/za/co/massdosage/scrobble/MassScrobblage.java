/**
 * Copyright (C) 2015-2019 Mass Dosage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package za.co.massdosage.scrobble;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MassScrobblage {

  private static final String PROPERTY_USER_NAME = "user.name";
  private static final String PROPERTY_PASSWORD = "password";
  private static final String PROPERTY_API_KEY = "api.key";
  private static final String PROPERTY_SECRET = "secret";

  private static final String DEFAULT_PROPERTIES_FILE_NAME = "mass-scrobblage.properties";

  private static Logger log = Logger.getLogger(MassScrobblage.class);

  private static void outputVersionInfo() {
    InputStream propertyStream = MassScrobblage.class
        .getResourceAsStream("/META-INF/maven/za.co.massdosage/mass-scrobblage/pom.properties");
    try {
      if (propertyStream != null) {
        String properties = IOUtils.toString(propertyStream, StandardCharsets.UTF_8.displayName());
        log.debug(properties);
      }
    } catch (IOException e) {
      log.warn("Error reading version information", e);
    } finally {
      IOUtils.closeQuietly(propertyStream);
    }
  }

  public static void main(String args[]) throws Exception {
    new JulToSlf4jBridge();

    outputVersionInfo();

    if (args.length < 1) {
      System.out.println("Usage: MassScrobblage path");
      System.exit(-1);
    }

    URL resource = MassScrobblage.class.getResource("/" + DEFAULT_PROPERTIES_FILE_NAME);
    if (resource == null) {
      throw new IOException("Default property file (" + DEFAULT_PROPERTIES_FILE_NAME + ") not found on classpath");
    }
    File configFile = new File(resource.toURI());
    log.info("Loading configuration from: " + configFile.getAbsolutePath());
    Reader configReader = new FileReader(configFile);
    Properties configuration = new Properties();
    configuration.load(configReader);

    String apiKey = extractProperty(configuration, PROPERTY_API_KEY);
    String secret = extractProperty(configuration, PROPERTY_SECRET);
    String userName = extractProperty(configuration, PROPERTY_USER_NAME);
    String passwordHash = extractProperty(configuration, PROPERTY_PASSWORD);
    File scrobblePath = extractScrobblePath(args);
    FileScrobbler scrobbler = new FileScrobbler(apiKey, secret, userName, passwordHash);
    scrobbler.scrobbleFolder(scrobblePath);
  }

  private static String extractProperty(Properties configuration, String propertyName) throws IOException {
    String value = StringUtils.trimToNull(configuration.getProperty(propertyName));
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
