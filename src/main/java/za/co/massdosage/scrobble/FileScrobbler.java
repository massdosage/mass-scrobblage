/**
 * Copyright (C) 2015-2017 Mass Dosage
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
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jdom.JDOMException;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;
import de.umass.lastfm.scrobble.ScrobbleResult;

public class FileScrobbler {

  private final Logger log = Logger.getLogger(getClass());
  private final String apiKey;
  private final String secret;
  private final String userName;
  private final String passwordHash;
  private int scrobbleTime;
  private static List<String> supportedFileTypes;
  private static final String DEFAULT_API_ROOT = "https://ws.audioscrobbler.com/2.0/";

  static {
    supportedFileTypes = new ArrayList<>();
    supportedFileTypes.add(".mp3");
    supportedFileTypes.add(".ogg");
    supportedFileTypes.add(".flac");
    supportedFileTypes.add(".m4a");
    supportedFileTypes.add(".wma");
  }

  public FileScrobbler(String apiKey, String secret, String userName, String passwordHash, String apiRoot) {
    this.apiKey = apiKey;
    this.secret = secret;
    this.userName = userName;
    this.passwordHash = passwordHash;

    Caller.getInstance().setApiRootUrl(apiRoot);
    log.debug("API root set to " + apiRoot);

    scrobbleTime = (int) ((new Date()).getTime() / 1000L);
    log.debug("Initialising scrobbler for user '" + userName + "'");
  }

  public FileScrobbler(String apiKey, String secret, String userName, String passwordHash) {
    this(apiKey, secret, userName, passwordHash, DEFAULT_API_ROOT);
  }

  public void scrobbleFolder(File folder) throws Exception {
    if (!folder.exists()) {
      log.info(folder.getAbsolutePath() + " doesn't exist, skipping");
      return;
    }
    log.info("About to scrobble contents of " + folder.getAbsolutePath());
    List<ScrobbleData> allScrobbles = new ArrayList<>();
    if (folder.isDirectory()) {
      allScrobbles.addAll(extractScrobbles(folder));
    } else {
      allScrobbles.add(extractScrobble(folder));
    }
    if (allScrobbles.isEmpty()) {
      log.info("Nothing found to scrobble in " + folder.getAbsolutePath());
      return;
    }
    Session session = authenticate();
    if (session == null) {
      log.warn("Unable to authenticate, please check whether your credentials are correct");
      return;
    }
    scrobble(allScrobbles, session);
  }

  private Session authenticate() throws IOException, JDOMException, URISyntaxException {
    return Authenticator.getMobileSession(userName, passwordHash, apiKey, secret);
  }

  private List<ScrobbleData> extractScrobbles(File scrobbleFolder)
    throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
    List<ScrobbleData> scrobbles = new ArrayList<>();
    File folders[] = scrobbleFolder.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
    if (folders != null) {
      for (File folder : folders) {
        scrobbles.addAll(extractScrobbles(folder));
      }
    }
    File files[] = scrobbleFolder.listFiles((FileFilter) new SuffixFileFilter(supportedFileTypes));
    if (files != null) {
      List<File> sortedFiles = Arrays.asList(files);
      Collections.sort(sortedFiles, new FileNameComparator());
      ScrobbleData scrobbleData;
      for (Iterator<File> iterator = sortedFiles.iterator(); iterator.hasNext(); scrobbles.add(scrobbleData)) {
        File file = iterator.next();
        scrobbleData = extractScrobble(file);
      }

    }
    return scrobbles;
  }

  // Visible for testing
  ScrobbleData extractScrobble(File file)
    throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
    log.info((new StringBuilder()).append("Extracting scrobble data from ").append(file).toString());
    AudioFile audioFile = AudioFileIO.read(file);
    Tag tag = audioFile.getTag();
    String artistName = tag.getFirst(FieldKey.ARTIST);
    String trackName = tag.getFirst(FieldKey.TITLE);
    String trackNumber = StringUtils.trimToNull(tag.getFirst(FieldKey.TRACK));
    String albumArtistName = StringUtils.trimToNull(tag.getFirst(FieldKey.ALBUM_ARTIST));
    String albumName = StringUtils.trimToNull(tag.getFirst(FieldKey.ALBUM));
    AudioHeader audioHeader = audioFile.getAudioHeader();
    int trackLength = audioHeader.getTrackLength();
    ScrobbleData scrobbleData = createScrobbleData(artistName, trackName, albumArtistName, albumName, trackNumber,
        trackLength);
    return scrobbleData;
  }

  // Visible for testing
  ScrobbleData createScrobbleData(
      String artistName,
      String trackName,
      String albumArtistName,
      String albumName,
      String trackNumber,
      int trackLength) {
    ScrobbleData scrobbleData = new ScrobbleData();
    scrobbleData.setArtist(artistName);
    scrobbleData.setTrack(trackName);
    if (albumArtistName != null) {
      scrobbleData.setAlbumArtist(albumArtistName);
    }
    if (albumName != null) {
      scrobbleData.setAlbum(albumName);
    }
    if (trackNumber != null) {
      int slashIndex = trackNumber.indexOf("/");
      if (slashIndex > 0) {
        trackNumber = trackNumber.substring(0, slashIndex);
      }
      scrobbleData.setTrackNumber(Integer.valueOf(trackNumber).intValue());
    }
    scrobbleTime = scrobbleTime - trackLength;
    scrobbleData.setDuration(trackLength);
    return scrobbleData;
  }

  private void scrobble(List<ScrobbleData> scrobbles, Session session) {
    for (ScrobbleData scrobble : scrobbles) {
      scrobble.setTimestamp(scrobbleTime);
      scrobbleTime = scrobbleTime + scrobble.getDuration();
      log.info("About to scrobble " + scrobble.toString());
      ScrobbleResult result = Track.scrobble(scrobble, session);
      log.info("Scrobble WS call result: " + result.toString());
    }
  }

}
