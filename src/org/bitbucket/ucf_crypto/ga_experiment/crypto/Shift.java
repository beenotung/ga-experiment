package org.bitbucket.ucf_crypto.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;

import static com.github.beenotung.javalib.Utils.objectToString;
import static com.github.beenotung.javalib.Utils.random;
import static com.github.beenotung.javalib.Utils.randomByte;

public class Shift implements Crypto.ICrypto {
  public static Shift $MODULE = new Shift();

  static {
    Crypto.$MODULE.impls.add($MODULE);
  }

  public static class Config extends Crypto.IConfig {
    public byte offset;

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
      return "offset=" + offset;
    }
  }

  private Config config;

  @Override
  public <A extends Crypto.IConfig> A sampleConfig() {
    Config c = new Config();
    c.offset = (byte) random.nextInt(c.base);
    return (A) c;
  }

  @Override
  public <A extends Crypto.IConfig> void prepare(A config) {
    this.config = (Config) config;
  }

  @Override
  public void preprocess(String plaintext, final ByteArray res) {
    if (res.data.length < plaintext.length())
      res.data = new byte[plaintext.length()];
    res.len = 0;
    for (byte b : plaintext.getBytes()) {
      if ('a' <= b && b <= 'z') {
        res.data[res.len++] = (byte) (b - 'a');
      } else if ('A' <= b && b <= 'Z') {
        res.data[res.len++] = (byte) (b - 'A');
      }
    }
  }

  @Override
  public String postprocess(ByteArray cipher) {
    char[] cs = new char[cipher.len];
    for (int i = 0; i < cipher.len; i++) {
      cs[i] = (char) (cipher.data[i + cipher.offset] + 'a');
    }
    return new String(cs);
  }

  @Override
  public void encryp(ByteArray plaintext, final ByteArray cipher) {
    if (cipher.data.length < plaintext.len) {
      cipher.data = new byte[plaintext.len];
    }
    cipher.len = plaintext.len;

    for (int i = 0; i < plaintext.len; i++) {
      cipher.data[i] =
        (byte) (
          (plaintext.data[i + plaintext.offset]
            + config.offset)
            % config.base
        );
    }
  }

  @Override
  public void decryp(ByteArray cipher, final ByteArray plaintext) {
    if (plaintext.data.length < cipher.len) {
      plaintext.data = new byte[cipher.len];
      plaintext.len = cipher.len;
    }
    plaintext.len = cipher.len;

    for (int i = 0; i < cipher.len; i++) {
      plaintext.data[i] =
        (byte) ((cipher.data[i + cipher.offset]
          - config.offset
          + config.base)
          % config.base
        );
    }
  }
}