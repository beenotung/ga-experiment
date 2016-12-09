package com.github.beenotung.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils.ByteArray;

import static com.github.beenotung.javalib.Utils.*;

public class Shift implements Crypto.ICrypto {
  public static Shift $MODULE = new Shift();

  static {
    Crypto.$MODULE.impls.add($MODULE);
  }

  public static class Config extends Crypto.IConfig {
    public int offset;

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Config) {
        Config o = (Config) obj;
        return
          this.base == o.base
            && this.offset == o.offset
          ;
      }
      return super.equals(obj);
    }

    @Override
    public String toString() {
      return
        "base=" + base +
          ", offset=" + offset
        ;
    }
  }

  @Override
  public String toString() {
    return "Shift{" +
      config +
      '}';
  }

  public Config config;

  @Override
  public <A extends Crypto.IConfig> A sampleConfig(int base) {
    Config res = new Config();
    res.base = base;
    res.offset = random.nextInt(res.base);
    return (A) res;
  }

  @Override
  public <A extends Crypto.IConfig> void prepare(A config) {
    this.config = (Config) config;
  }

  @Override
  public void preprocess(String plaintext, final ByteArray res) {
    CryptoUtils.string_to_bytes(plaintext, config.base, res);
  }

  @Override
  public String postprocess(ByteArray cipher) {
    return CryptoUtils.bytes_to_string(cipher, config.base);
  }

  @Override
  public void encryp(ByteArray plaintext, final ByteArray cipher) {
    if (cipher.data.length < plaintext.len) {
      cipher.data = new byte[plaintext.len];
    }
    cipher.len = plaintext.len;
    cipher.offset = 0;
    for (int i = 0; i < plaintext.len; i++) {
      cipher.data[i + cipher.offset] = (byte) ((plaintext.data[i + plaintext.offset] + config.offset) % config.base);
    }
  }

  @Override
  public void decryp(ByteArray cipher, final ByteArray plaintext) {
    if (plaintext.data.length < cipher.len) {
      plaintext.data = new byte[cipher.len];
      plaintext.len = cipher.len;
    }
    plaintext.len = cipher.len;
    plaintext.offset = 0;
    for (int i = 0; i < cipher.len; i++) {
      plaintext.data[i + plaintext.offset] = (byte) ((cipher.data[i + cipher.offset] - config.offset + config.base) % config.base);
    }
  }
}
