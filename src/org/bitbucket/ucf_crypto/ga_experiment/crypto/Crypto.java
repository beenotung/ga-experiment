package org.bitbucket.ucf_crypto.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import com.github.beenotung.javalib.Utils.CharArray;

import java.util.ArrayList;

public class Crypto {
  public static Crypto $MODULE = new Crypto();

  /* including the key */
  public static class Config {
    /**
     * base 26 for English letter (only lower case)
     * base 36 for English letter (only lower case) and digit
     * base 256 for raw data [0..255]
     * */
    byte base;

    /**
     * @final
     * apply default settings
     * */
    public Config() {
      base = 26;
    }
  }

  public interface ICrypto {
    <A extends Config> A sampleConfig();

    <A extends Config> void prepare(A config);

    ByteArray preprocess(String plaintext);

    String postprocess(ByteArray cipher);

    void encryp(ByteArray plaintext, ByteArray cipher);

    void decryp(ByteArray cipher, ByteArray plaintext);
  }
}
