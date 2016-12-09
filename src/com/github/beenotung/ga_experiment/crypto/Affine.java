package com.github.beenotung.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;

import static com.github.beenotung.javalib.Utils.*;

public class Affine implements Crypto.ICrypto {
  public static final Affine $MODULE = new Affine();

  static {
    Crypto.$MODULE.impls.add($MODULE);
  }

  private Config config;
  private int[] table;
  private int[] re_table;

  public static class Config extends Crypto.IConfig {
    public int a;
    public int b;

    @Override
    public String toString() {
      return
        "base=" + base +
          ", a=" + a +
          ", b=" + b
        ;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Config config = (Config) o;

      if (a != config.a) return false;
      return b == config.b;
    }

    @Override
    public int hashCode() {
      int result = (int) a;
      result = 31 * result + (int) b;
      return result;
    }
  }

  @Override
  public String toString() {
    return "Affine{" +
      config +
      '}';
  }

  @Override
  public <A extends Crypto.IConfig> A sampleConfig(int base) {
    Config res = new Config();
    res.base = base;
    for (; ; ) {
      res.a = random.nextInt(res.base) % base;
      if (gcd(res.a, res.base) == 1) {
        break;
      }
    }
    res.b = random.nextInt(res.base) % base;
    return (A) res;
  }

  @Override
  public <A extends Crypto.IConfig> void prepare(A newConfig) {
    this.config = (Config) newConfig;
    table = new int[256];
    re_table = new int[256];
    for (int i = 0; i < 256; i++) {
      table[i] = ((i % config.base) * config.a + config.b) % config.base;
    }
    for (int i = 0; i < config.base; i++) {
      try {
        re_table[table[i]] = i % config.base;
      } catch (ArrayIndexOutOfBoundsException e) {
        e.printStackTrace();
      }
    }
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
    if (cipher.data.length < plaintext.len) {
      cipher.data = new byte[plaintext.len];
    }
    cipher.offset = 0;
    cipher.len = plaintext.len;
    for (int i = 0; i < plaintext.len; i++) {
      cipher.data[i] = (byte) table[plaintext.data[i + plaintext.offset]];
    }
  }

  @Override
  public void decryp(Utils.ByteArray cipher, Utils.ByteArray plaintext) {
    if (plaintext.data.length < cipher.len) {
      plaintext.data = new byte[cipher.len];
    }
    plaintext.offset = 0;
    plaintext.len = cipher.len;
    for (int i = 0; i < cipher.len; i++) {
      plaintext.data[i] = (byte) re_table[uint(cipher.data[i + cipher.offset])];
    }
  }
}
