package org.bitbucket.ucf_crypto.ga_experiment.crypto;

public class Crypto {
  public static class IKey {
  }

  public interface ICrypto {
    <A extends IKey> void prepare(A key);

    char[] encryp(char[] plaintext);

    char[] decryp(char[] cipher);
  }
}
