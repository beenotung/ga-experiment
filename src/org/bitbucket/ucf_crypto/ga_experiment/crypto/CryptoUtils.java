package org.bitbucket.ucf_crypto.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static com.github.beenotung.javalib.Utils.$$$;
import static com.github.beenotung.javalib.Utils.to_int;

/**
 * Created by beenotung on 12/2/16.
 */
public class CryptoUtils {
  public static final byte[] char26_to_byte = new byte[256];
  public static final byte[] char36_to_byte = new byte[256];

  public static final byte[] byte_to_char26 = new byte[256];
  public static final byte[] byte_to_char36 = new byte[256];

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
//      char36_to_byte[i + 'A'] = (byte) (i + 26);
      char36_to_byte[i + 'A'] = i;
    }
    for (byte i = 0; i < 10; i++) {
//      char36_to_byte[i + '0'] = (byte) (i + 26 + 26);
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
  }

  public static void string_to_bytes(String s, int base, final Utils.ByteArray res) {
    res.offset = 0;
    if (base == 0) {
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
    for (char c : s.toCharArray()) {
      if (mapper[c] != -1) {
        res.data[res.len++] = mapper[c];
      }
    }
  }

  public static String bytes_to_string(Utils.ByteArray bs, int base) {
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
      b.append((char) mapper[to_int(bs.data[i + bs.offset])]);
    }
    return b.toString();
  }
}
