package za.co.massdosage.scrobble;

import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.test.file.DataFolder;
import fm.last.commons.test.file.RootDataFolder;

public class MassScrobblageTest {
  
  @Rule
  public DataFolder dataFolder = new RootDataFolder();

  @Test
  public void integration() throws Exception {
    MassScrobblage.main(new String[] {dataFolder.getFolder().getAbsolutePath()});
  }
  
}
