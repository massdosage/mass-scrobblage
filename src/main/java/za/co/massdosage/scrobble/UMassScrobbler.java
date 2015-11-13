// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UMassScrobbler.java

package za.co.massdosage.scrobble;

//TODO: do we even need this class anymore?
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.JDOMException;

// Referenced classes of package fm.last.scrobble:
//            Scrobbler2

public class UMassScrobbler {

  public UMassScrobbler() {
  }

  public static void main(String args[]) throws IOException, JDOMException, URISyntaxException {
    String apiKey = null;  //TODO: replace this with getting from property file
    String secret = null; //TODO: replace this with getting from property file
    String userName = null;  //TODO: replace this with getting from property file
    String password = null;  //TODO: replace this with getting from property file
    String authToken = DigestUtils.md5Hex((new StringBuilder())
        .append(userName)
        .append(DigestUtils.md5Hex(password))
        .toString());
    // String sessionKey = Scrobbler2.getMobileSession(userName, authToken);
    // Session session = Session.createSession(apiKey, secret, sessionKey);

    Session session = Authenticator.getMobileSession(userName, password, apiKey, secret);

    String artistName = "Kendrick Lamar";
    String trackName = "King Kunta";
    ScrobbleData scrobbleData = new ScrobbleData();
    scrobbleData.setTimestamp((int) ((new Date()).getTime() / 1000L));
    scrobbleData.setTrack(trackName);
    scrobbleData.setArtist(artistName);
    de.umass.lastfm.scrobble.ScrobbleResult result = Track.scrobble(scrobbleData, session);
    System.out.println((new StringBuilder()).append("Result: ").append(result).toString());
  }
}
