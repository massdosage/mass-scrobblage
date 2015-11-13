// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NameValuePairComparator.java

package za.co.massdosage.scrobble;

import java.util.Comparator;
import org.apache.http.NameValuePair;

public class NameValuePairComparator implements Comparator {

  public NameValuePairComparator() {
  }

  public int compare(NameValuePair o1, NameValuePair o2) {
    return o1.getName().compareTo(o2.getName());
  }

  public int compare(Object x0, Object x1) {
    return compare((NameValuePair) x0, (NameValuePair) x1);
  }
}
