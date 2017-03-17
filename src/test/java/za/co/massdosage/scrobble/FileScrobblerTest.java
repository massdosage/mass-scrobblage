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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;

import de.umass.lastfm.scrobble.ScrobbleData;
import fm.last.commons.test.file.DataFolder;
import fm.last.commons.test.file.RootDataFolder;

public class FileScrobblerTest {

  @Rule
  public DataFolder dataFolder = new RootDataFolder();

  @Test
  public void authenticationFailure() throws Exception {
    FileScrobbler scrobbler = new FileScrobbler("invalidKey", "invalidSecret", "scrobtestuser", "invalidHash");
    scrobbler.scrobbleFolder(dataFolder.getFolder());
  }

  @Test
  public void extractScrobbleDataFromFile() throws Exception {
    File mp3File = dataFolder.getFile("test2.mp3");
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
