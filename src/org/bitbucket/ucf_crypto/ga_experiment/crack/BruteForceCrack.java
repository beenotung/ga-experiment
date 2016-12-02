package org.bitbucket.ucf_crypto.ga_experiment.crack;

import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Affine;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Shift;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.beenotung.javalib.Utils.gcd;
import static com.github.beenotung.javalib.Utils.println;

/**
 * Created by beenotung on 11/30/16.
 */
public class BruteForceCrack implements Crack.ICrack {
  public static BruteForceCrack $MODULE = new BruteForceCrack();

  static {
    Crack.$MODULE.impls.add($MODULE);
  }

  HashMap<Class, Crack.ICrack> impls = new HashMap<>();

  private BruteForceCrack() {
    impls.put(Shift.class, new Crack.ICrack() {
      @Override
      public void crack(final Crypto.ICrypto crypto, final Crypto.IConfig config, ArrayList<Utils.Pair<ByteArray, ByteArray>> plaintext_cipher_pairs) {
        final Shift shift = (Shift) crypto;
        final Shift.Config c = (Shift.Config) config;
        ByteArray result = new ByteArray(0);
        for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
          for (byte i = 0; i < c.base; i++) {
            c.offset = i;
            shift.prepare(c);
            shift.decryp(plaintext_cipher_pair._2, result);
//            println("plaintext:",plaintext_cipher_pair._1.toString(),"result:",result.toString());
            if (result.equals(plaintext_cipher_pair._1)) {
              return;
            }
          }
        }
      }
    });
    impls.put(Affine.class, (crypto, config, plaintext_cipher_pairs) -> {
      final Affine affine = (Affine) crypto;
      final Affine.Config c = (Affine.Config) config;
      ByteArray result = new ByteArray(0);
      for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
        for (c.a = 0; c.a < c.base; c.a++) {
          if (gcd(c.a, c.base) != 1) {
            continue;
          }
          for (c.b = 0; c.b < c.base; c.b++) {
            affine.prepare(c);
            affine.decryp(plaintext_cipher_pair._2, result);
            if (plaintext_cipher_pair._1.equals(result)) {
              return;
            }
          }
        }
      }
    });
  }

  @Override
  public void crack(final Crypto.ICrypto crypto, final Crypto.IConfig config, final ArrayList<Utils.Pair<ByteArray, ByteArray>> plaintext_cipher_pairs) {
    Crack.ICrack cracker = impls.get(crypto.getClass());
    if (cracker != null) {
      cracker.crack(crypto, config, plaintext_cipher_pairs);
    } else {
      throw new NotImplementedException();
    }
  }
}
