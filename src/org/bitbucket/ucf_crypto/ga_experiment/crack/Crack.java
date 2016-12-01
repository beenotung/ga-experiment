package org.bitbucket.ucf_crypto.ga_experiment.crack;

import com.github.beenotung.javalib.Utils;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;

import java.util.ArrayList;

/**
 * Created by beenotung on 11/30/16.
 */
public class Crack {
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
      , final Crypto.IConfig config
      , final ArrayList<Utils.Pair<Utils.ByteArray, Utils.ByteArray>> plaintext_cipher_pairs
    );
  }
}
