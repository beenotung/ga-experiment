package org.bitbucket.ucf_crypto.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;

import java.util.Arrays;

import static com.github.beenotung.javalib.Utils.*;

public class Substition implements Crypto.ICrypto {
  public static Substition $MODULE = new Substition();

  static {
    Crypto.$MODULE.impls.add($MODULE);
  }

  private Config config;

  @Override
  public String toString() {
    return "Substition{" +
      config +
      '}';
  }

  public static class Config extends Crypto.IConfig {
    public int[] table;
    public int[] re_table;

    @Override
    public String toString() {
      if (base == 256) {
        StringBuilder b = new StringBuilder();
        b.append("key=");
        b.append(arrayToString(table));
        return b.toString();
      } else {
        char[] key = new char[base];
        byte[] mapper = base == 26
          ? CryptoUtils.byte_to_char26
          : base == 36
          ? CryptoUtils.byte_to_char36
          : $$$();
        for (int i = 0; i < base; i++) {
          key[i] = (char) mapper[table[i]];
        }
        return "key=" + String.valueOf(key);
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Config)) return false;

      Config config = (Config) o;

      return Arrays.equals(table, config.table);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(table);
    }
  }

  public static Config gen_key_from_string(String s, int base) {
    Config c = new Config();
    c.base = base;
    c.table = new int[base];
    c.re_table = new int[base];
    boolean[] flag = new boolean[base];
    IntArray is = new IntArray(0);
    CryptoUtils.string_to_ints(s, base, is);
    int i = 0;
    for (int b : is.data) {
      if (flag[b])
        continue;
      c.table[i++] = b;
      flag[b] = true;
    }
    for (; i < base; i++) {
      for (int b = 0; b < base; b++) {
        if (!flag[b]) {
          c.table[i] = b;
          flag[b] = true;
          break;
        }
      }
    }
    for (i = 0; i < base; i++) {
      c.re_table[c.table[i]] = i;
    }
    return c;
  }

  @Override
  public <A extends Crypto.IConfig> A sampleConfig(int base) {
    Config c = new Config();
    c.base = base;
    c.table = new int[base];
    c.re_table = new int[base];
    for (int i = 0; i < base; i++) {
      c.table[i] = i;
    }
    for (int i = 0; i < base; i++) {
      int a = random.nextInt(base);
      int b = random.nextInt(base);
      int t = c.table[a];
      c.table[a] = c.table[b];
      c.table[b] = t;
    }
    for (int i = 0; i < base; i++) {
      c.re_table[c.table[i]] = i;
    }
    return (A) c;
  }

  @Override
  public <A extends Crypto.IConfig> void prepare(A config) {
    this.config = (Config) config;
  }

  @Override
  public void preprocess(String plaintext, Utils.ByteArray res) {
    CryptoUtils.string_to_bytes(plaintext, config.base, res);
  }

  @Override
  public String postprocess(Utils.ByteArray cipher) {
    return CryptoUtils.bytes_to_string(cipher, config.base);
  }

  @Override
  public void encryp(Utils.ByteArray plaintext, Utils.ByteArray cipher) {
    ensure_capacity(cipher, plaintext.len);
    cipher.offset = 0;
    cipher.len = plaintext.len;
    for (int i = 0; i < plaintext.len; i++) {
      cipher.data[i] = (byte) config.table[uint(plaintext.data[i + plaintext.offset]) % config.base];
    }
  }

  @Override
  public void decryp(Utils.ByteArray cipher, Utils.ByteArray plaintext) {
    ensure_capacity(plaintext, cipher.len);
    plaintext.offset = 0;
    plaintext.len = cipher.len;
    for (int i = 0; i < cipher.len; i++) {
      plaintext.data[i] = (byte) config.re_table[uint(cipher.data[i + cipher.offset]) % config.base];
    }
  }
}
