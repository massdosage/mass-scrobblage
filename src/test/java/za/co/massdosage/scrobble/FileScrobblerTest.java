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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import de.umass.lastfm.scrobble.ScrobbleData;
import fm.last.commons.test.file.ClassDataFolder;
import fm.last.commons.test.file.DataFolder;

public class FileScrobblerTest {

  @Rule
  public DataFolder dataFolder = new ClassDataFolder();

  @Test
  public void authenticationFailure() throws Exception {
    FileScrobbler scrobbler = new FileScrobbler("invalidKey", "invalidSecret", "scrobtestuser", "invalidHash");
    scrobbler.scrobbleFolder(dataFolder.getFile("nohidden"));
  }

  @Test
  public void extractScrobbleDataFromFolder() throws Exception {
    File audioFolder = dataFolder.getFile("nohidden");
    FileScrobbler scrobbler = new FileScrobbler("key", "secret", "scrobtestuser", "hash");
    List<ScrobbleData> scrobbleData = scrobbler.extractScrobbles(audioFolder);
    assertThat(scrobbleData.size(), is(2));
  }

  @Test
  public void extractScrobbleDataFromFolderWithHiddenFile() throws Exception {
    File audioFolder = dataFolder.getFile("hidden");
    FileScrobbler scrobbler = new FileScrobbler("key", "secret", "scrobtestuser", "hash");
    List<ScrobbleData> extracted = scrobbler.extractScrobbles(audioFolder);
    assertThat(extracted.size(), is(1));
    ScrobbleData scrobbleData = extracted.get(0);
    assertThat(scrobbleData.getArtist(), is("DJ Mass Dosage"));
    assertThat(scrobbleData.getTrack(), is("How DJ can you Dosed Mix"));
  }

  @Test
  public void extractScrobbleDataFromFile() throws Exception {
    File mp3File = dataFolder.getFile("nohidden/test2.mp3");
    FileScrobbler scrobbler = new FileScrobbler("key", "secret", "scrobtestuser", "hash");
    ScrobbleData scrobbleData = scrobbler.extractScrobble(mp3File);
    assertThat(scrobbleData.getArtist(), is("ArtistName"));
    assertThat(scrobbleData.getTrack(), is("TrackTitle"));
    assertThat(scrobbleData.getAlbumArtist(), is("AlbumArtistName"));
    assertThat(scrobbleData.getAlbum(), is("AlbumName"));
    assertThat(scrobbleData.getTrackNumber(), is(1));
    assertThat(scrobbleData.getDuration(), is(5));
  }

  @Test
  public void createScrobbleDataTypical() {
    FileScrobbler scrobbler = new FileScrobbler("key", "secret", "scrobtestuser", "hash");
    ScrobbleData scrobbleData = scrobbler.createScrobbleData("artistName", "trackName", "albumArtistName", "albumName",
        "13", 33);
    assertThat(scrobbleData.getArtist(), is("artistName"));
    assertThat(scrobbleData.getTrack(), is("trackName"));
    assertThat(scrobbleData.getAlbumArtist(), is("albumArtistName"));
    assertThat(scrobbleData.getAlbum(), is("albumName"));
    assertThat(scrobbleData.getTrackNumber(), is(13));
    assertThat(scrobbleData.getDuration(), is(33));
  }

  // See https://github.com/massdosage/mass-scrobblage/issues/1
  @Test
  public void createScrobbleDataTrackNumberWithSlash() {
    FileScrobbler scrobbler = new FileScrobbler("key", "secret", "scrobtestuser", "hash");
    ScrobbleData scrobbleData = scrobbler.createScrobbleData("artistName", "trackName", "albumArtistName", "albumName",
        "1/10", 33);
    assertThat(scrobbleData.getTrackNumber(), is(1));
  }

}
