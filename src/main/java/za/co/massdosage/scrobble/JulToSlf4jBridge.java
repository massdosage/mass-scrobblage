package za.co.massdosage.scrobble;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Constructing an instance of this class will result in all Java Util Logging calls to get sent to SLF4J.
 */
public class JulToSlf4jBridge {

  // see http://blog.cn-consult.dk/2009/03/bridging-javautillogging-to-slf4j.html
  public JulToSlf4jBridge() {
    // first we remove the JUL loggers (otherwise we get them AND the slf4j messages)
    // Optionally remove existing handlers attached to j.u.l root logger
    SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)

    // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
    // the initialization phase of your application
    SLF4JBridgeHandler.install();
  }

}
