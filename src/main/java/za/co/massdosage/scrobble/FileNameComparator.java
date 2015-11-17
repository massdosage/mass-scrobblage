/**
 * Copyright (C) 2015 Mass Dosage
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
import java.util.Comparator;

public class FileNameComparator implements Comparator {

  public FileNameComparator() {
  }

  public int compare(File o1, File o2) {
    return o1.getName().compareTo(o2.getName());
  }

  public int compare(Object x0, Object x1) {
    return compare((File) x0, (File) x1);
  }
}
