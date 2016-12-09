package com.github.beenotung.ga_experiment.crack;

import com.github.beenotung.javalib.Utils;
import com.github.beenotung.ga_experiment.crypto.Crypto;

import java.util.ArrayList;

/**
 * Created by beenotung on 11/30/16.
 */
public class Crack {
  public static final Crack $MODULE = new Crack();
  public ArrayList<ICrack> impls = new ArrayList<>();

  public interface ICrack {
    /**
     * crack a list of plaintext-cipher pairs with the same key
     * to guess the key
     *
     * in-place update the key in config as "return value"
     * the key in the input config is not valid (might be null)
     *
     * TODO implement a way to interrupt it when "timeout"
     * */
    void crack(
      final Crypto.ICrypto crypto
      , final Crypto.IConfig solutionConfig
      , final ArrayList<Utils.Pair<Utils.ByteArray, Utils.ByteArray>> plaintext_cipher_pairs
    );
  }
}
