package com.github.beenotung.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import com.github.beenotung.javalib.Utils.IntArray;

import static com.github.beenotung.javalib.Utils.*;

/**
 * Created by beenotung on 12/2/16.
 */
public class CryptoUtils {
  /**@deprecated */
  public static final byte[] char26_to_byte = new byte[256];
  /**@deprecated */
  public static final byte[] char36_to_byte = new byte[256];

  public static final byte[] byte_to_char26 = new byte[256];
  public static final byte[] byte_to_char36 = new byte[256];

  /*
  * free from negative value, unlike the charxx_to_byte tables, but same purpose
  * */
  public static final int[] char26_to_int = new int[256];
  public static final int[] char36_to_int = new int[256];

  /* for generic string generation */
  public static final char[] char26_cycle = new char[256];
  public static final char[] char36_cycle = new char[256];
  /* to eliminate edge case */
  public static final char[] char256 = new char[256];

  static {
    for (int i = 0; i < 256; i++) {
      char26_to_byte[i] = -1;
      char36_to_byte[i] = -1;
    }
    for (byte i = 0; i < 26; i++) {
      char26_to_byte[i + 'a'] = i;
      char26_to_byte[i + 'A'] = i;
    }
    for (byte i = 0; i < 26; i++) {
      char36_to_byte[i + 'a'] = i;
      char36_to_byte[i + 'A'] = i;
    }
    for (byte i = 0; i < 10; i++) {
      char36_to_byte[i + '0'] = (byte) (i + 26);
    }

    for (int i = 0; i < 256; i++) {
      byte_to_char26[i] = (byte) ((i % 26) + 'a');
      if (i % 36 < 26) {
        byte_to_char36[i] = (byte) ((i % 36) + 'a');
      } else {
        byte_to_char36[i] = (byte) ((i % 36) - 26 + '0');
      }
    }

    /* int version */
    for (int i = 0; i < 256; i++) {
      char26_to_int[i] = -1;
      char36_to_int[i] = -1;
    }
    for (int i = 0; i < 26; i++) {
      char26_to_int[i + 'a'] = i;
      char26_to_int[i + 'A'] = i;
      char36_to_int[i + 'a'] = i;
      char36_to_int[i + 'A'] = i;
    }
    for (int i = 0; i < 10; i++) {
      char36_to_int[i + '0'] = 26 + i;
    }

    /* for generic string generation */
    for (int i = 0; i < 26; i++) {
      char26_cycle[i] = (char) ('a' + i);
      char36_cycle[i] = (char) ('a' + i);
    }
    for (int i = 0; i < 10; i++) {
      char36_cycle[26 + i] = (char) ('0' + i);
    }
    for (int i = 26; i < 256; i++) {
      char26_cycle[i] = char26_cycle[i % 26];
      char36_cycle[i] = char36_cycle[i % 36];
    }
    /* eliminate edge case */
    for (int i = 0; i < 256; i++) {
      char256[i] = (char) i;
    }
  }

  /**@deprecated */
  public static void string_to_bytes(String s, int base, final ByteArray res) {
    res.offset = 0;
    if (base == 256) {
      res.data = s.getBytes();
      res.len = res.data.length;
      return;
    }
    final int n = s.length();
    if (res.data.length < n) {
      res.data = new byte[n];
    }
    res.len = 0;
    byte[] mapper = base == 26
      ? char26_to_byte
      : base == 36
      ? char36_to_byte
      : $$$();
    for (byte b : s.getBytes()) {
      int i = uint(b);
      if (mapper[i] != -1) {
        res.data[res.len++] = mapper[i];
      }
    }
  }

  public static void string_to_ints(String s, int base, final IntArray res) {
    byte[] bs = s.getBytes();
    res.offset = 0;
    if (base == 256) {
      if (res.data.length < bs.length) {
        res.data = new int[bs.length];
      }
      res.len = bs.length;
      for (int i = 0; i < bs.length; i++) {
        res.data[i] = uint(bs[i]);
      }
      return;
    }
    int[] mapper = base == 26
      ? char26_to_int
      : base == 36
      ? char36_to_int
      : $$$();
    ensure_capacity(res, bs.length);
    res.len = 0;
    for (int i = 0; i < bs.length; i++) {
      int b = mapper[uint(bs[i])];
      if (b != -1) {
        res.data[res.len++] = b;
      }
    }
  }

  public static String bytes_to_string(ByteArray bs, int base) {
    if (base == 0) {
      return new String(bs.data, bs.offset, bs.len);
    }
    StringBuilder b = new StringBuilder();
    byte[] mapper = base == 26
      ? byte_to_char26
      : base == 36
      ? byte_to_char36
      : $$$();
    for (int i = 0; i < bs.len; i++) {
      b.append((char) mapper[uint(bs.data[i + bs.offset])]);
    }
    return b.toString();
  }
}
