// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScrobbleItem.java

package za.co.massdosage.scrobble;

public class ScrobbleItem {

  public ScrobbleItem() {
  }

  public ScrobbleItem(String artistName, String trackName) {
    this.artistName = artistName;
    this.trackName = trackName;
  }

  public String getArtistName() {
    return artistName;
  }

  public void setArtistName(String artistName) {
    this.artistName = artistName;
  }

  public String getTrackName() {
    return trackName;
  }

  public void setTrackName(String trackName) {
    this.trackName = trackName;
  }

  private String artistName;
  private String trackName;
}
