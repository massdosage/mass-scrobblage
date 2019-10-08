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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.test.file.DataFolder;
import fm.last.commons.test.file.RootDataFolder;

public class MassScrobblageTest {
  
  @Rule
  public DataFolder dataFolder = new RootDataFolder();

  @Ignore("This test only works as an integration test and requires a valid properties file on the classpath")
  @Test
  public void integration() throws Exception {
    MassScrobblage.main(new String[] {dataFolder.getFolder().getAbsolutePath()});
  }
  
}
