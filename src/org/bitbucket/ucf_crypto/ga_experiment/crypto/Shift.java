package org.bitbucket.ucf_crypto.ga_experiment.crypto;

public class Shift implements Crypto.ICrypto {
  public class Key extends Crypto.IKey {
  }

  @Override
  public <A extends Crypto.IKey> void prepare(A key) {

  }

  @Override
  public char[] encryp(char[] plaintext) {
    return new char[0];
  }

  @Override
  public char[] decryp(char[] cipher) {
    return new char[0];
  }
}
