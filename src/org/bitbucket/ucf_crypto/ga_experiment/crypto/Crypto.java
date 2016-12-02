package org.bitbucket.ucf_crypto.ga_experiment.crypto;

import com.github.beenotung.javalib.Utils.ByteArray;

import java.util.ArrayList;

public class Crypto {
  public static final Crypto $MODULE = new Crypto();
  public ArrayList<ICrypto> impls = new ArrayList<>();

  /**
   * must be override to store the key internal
   * must override the equals method to check if the key match
   *
   * */
  public abstract static class IConfig {
    /**
     * base 26 for English letter (only lower case)
     * base 36 for English letter (only lower case) and digit
     * base 256 for raw data [0..255]
     * */
    public byte base;

    /**
     * @final
     * apply default settings
     * */
    public IConfig() {
      base = 26;
    }
  }

  /**
   * it should override toString to reflect the key
   * */
  public interface ICrypto {
    /**
     * it should return a random config that is different every time
     * */
    <A extends IConfig> A sampleConfig();

    <A extends IConfig> void prepare(A config);

    void preprocess(String plaintext, final ByteArray res);

    String postprocess(ByteArray cipher);

    void encryp(ByteArray plaintext, final ByteArray cipher);

    void decryp(ByteArray cipher, final ByteArray plaintext);
  }
}
