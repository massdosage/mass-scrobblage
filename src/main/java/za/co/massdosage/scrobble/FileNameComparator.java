// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FileNameComparator.java

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
