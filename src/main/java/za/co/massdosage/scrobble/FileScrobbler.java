/**
 * Copyright (C) 2015-2016 Mass Dosage
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

import org.apache.commons.codec.digest.DigestUtils;
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

import de.umass.lastfm.scrobble.ScrobbleResult;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;

public class FileScrobbler {

  private Logger log = Logger.getLogger(getClass());
  private String apiKey;
  private String secret;
  private String userName;
  private String passwordHash;
  private int scrobbleTime;
  private static List supportedFileTypes;

  static {
    supportedFileTypes = new ArrayList();
    supportedFileTypes.add(".mp3");
    supportedFileTypes.add(".ogg");
    supportedFileTypes.add(".flac");
  }

  public FileScrobbler(String apiKey, String secret, String userName, String passwordHash) {
    this.apiKey = apiKey;
    this.secret = secret;
    this.userName = userName;
    this.passwordHash = passwordHash;

    scrobbleTime = (int) ((new Date()).getTime() / 1000L);
    log.debug("Initialising scrobbler for user '" + userName + "'");
  }

  public void scrobbleFolder(File folder) throws Exception {
    if (!folder.exists()) {
      log.info(folder.getAbsolutePath() + " doesn't exist, skipping");
      return;
    }
    log.info("About to scrobble contents of " + folder.getAbsolutePath());
    List<ScrobbleData> allScrobbles = new ArrayList<ScrobbleData>();
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

  private List<ScrobbleData> extractScrobbles(File scrobbleFolder) throws CannotReadException, IOException,
      TagException, ReadOnlyFileException, InvalidAudioFrameException {
    List<ScrobbleData> scrobbles = new ArrayList<ScrobbleData>();
    File folders[] = scrobbleFolder.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
    if (folders != null) {
      for (File folder : folders) {
        scrobbles.addAll(extractScrobbles(folder));
      }
    }
    File files[] = scrobbleFolder.listFiles((FileFilter) new SuffixFileFilter(supportedFileTypes));
    if (files != null) {
      List sortedFiles = Arrays.asList(files);
      Collections.sort(sortedFiles, new FileNameComparator());
      ScrobbleData scrobbleData;
      for (Iterator iterator = sortedFiles.iterator(); iterator.hasNext(); scrobbles.add(scrobbleData)) {
        File file = (File) iterator.next();
        scrobbleData = extractScrobble(file);
      }

    }
    return scrobbles;
  }

  private ScrobbleData extractScrobble(File file) throws CannotReadException, IOException, TagException,
      ReadOnlyFileException, InvalidAudioFrameException {
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
      scrobbleData.setTrackNumber(Integer.valueOf(trackNumber).intValue());
    }
    scrobbleTime = scrobbleTime - trackLength;
    scrobbleData.setDuration(trackLength);
    return scrobbleData;
  }

  private void scrobble(List scrobbles, Session session) {
    for (Iterator iterator = scrobbles.iterator(); iterator.hasNext();) {
      ScrobbleData scrobble = (ScrobbleData) iterator.next();
      scrobble.setTimestamp(scrobbleTime);
      scrobbleTime = scrobbleTime + scrobble.getDuration();
      log.info("About to scrobble " + scrobble.toString());
      ScrobbleResult result = Track.scrobble(scrobble, session);
      log.info("Scrobble WS call result: " + result.toString());
    }
  }

}
