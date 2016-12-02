package org.bitbucket.ucf_crypto.ga_experiment.crack;

import com.github.beenotung.javalib.GA;
import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Affine;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Shift;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by beenotung on 11/30/16.
 */
public class GACrack implements Crack.ICrack {
  public static GACrack $MODULE = new GACrack();

  static {
    Crack.$MODULE.impls.add($MODULE);
  }

  public HashMap<Class, Crack.ICrack> impls = new HashMap<>();

  private GACrack() {
    final int n_pop = 100;
    impls.put(Shift.class, new Crack.ICrack() {
      @Override
      public void crack(Crypto.ICrypto crypto, Crypto.IConfig iConfig, ArrayList<Utils.Pair<ByteArray, ByteArray>> plaintext_cipher_pairs) {
        Shift.Config config = (Shift.Config) iConfig;
        final int l_gene = 1;
        final float p_crossover = 0.25f;
        final float p_mutate = 0.8f;
        final float a_mutate = 1f;
        GA.Param param = new GA.Param() {
          /**
           * hamming distance
           * */
          @Override
          public GA.IEval I_EVAL() {
            return new GA.IEval() {
              @Override
              public float eval(byte[] gene) {
                float acc = 0;
                Shift shift = new Shift();
                Shift.Config config_local = new Shift.Config();
                config_local.base = config.base;
                config_local.offset = gene[0];
                shift.prepare(config_local);
                ByteArray result = new ByteArray(0);
                for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
                  final ByteArray plaintext = plaintext_cipher_pair._1;
                  shift.decryp(plaintext_cipher_pair._2, result);
                  int n;
                  if (plaintext.len != result.len) {
                    n = Math.min(plaintext.len, result.len);
                    acc += Math.abs(plaintext.len - result.len) * config.base;
                  } else {
                    n = plaintext.len;
                  }
                  for (int i = 0; i < n; i++) {
                    acc += Math.abs(plaintext.data[i + plaintext.offset] - result.data[i + result.offset]);
                  }
                }
                return acc;
              }
            };
          }
        };
        GA ga = new GA(new GA.GARuntime(
          n_pop
          , l_gene
          , p_crossover
          , p_mutate
          , a_mutate
          , true), param);
        ga.init();
        GA.GAUtils.simpleRestartUntilTargetFitness(ga, 0f, 0.2f, 1f);
        ga.useRuntime(gaRuntime -> {
          config.offset = gaRuntime.getGeneByRank(0)[0];
          while (config.offset < config.base) {
            config.offset += config.base;
          }
          config.offset %= config.base;
        });
      }
    });
    impls.put(Affine.class, (crypto, config, plaintext_cipher_pairs) -> {
        GA.GARuntime gaRuntime = new GA.GARuntime(n_pop, 2, .25f, .8f, .5f, true);
        GA.Param param = new GA.Param() {
          @Override
          public GA.IEval I_EVAL() {
            return gene -> {
              float acc = 0;
              for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
                final ByteArray plaintext = plaintext_cipher_pair._1;
                Affine affine = new Affine();
                Affine.Config c = new Affine.Config();
                c.base = config.base;
                c.a = gene[0];
                c.b = gene[1];
                affine.prepare(c);
                ByteArray result = new ByteArray(plaintext.len);
                affine.decryp(plaintext_cipher_pair._2, result);
                for (int i = 0; i < plaintext.len; i++) {
                  acc += Math.abs(plaintext.data[i + plaintext.offset] - result.data[i + result.offset]);
                }
              }
              return acc;
            };
          }

          @Override
          public GA.IRandomGene I_RANDOM_GENE() {
            return new GA.IRandomGene() {
              @Override
              public void randomGene(byte[] gene, int offset, int length) {
                Affine.Config c = Affine.$MODULE.sampleConfig(config.base);
                gene[0] = c.a;
                gene[1] = c.b;
              }
            };
          }
        };
        GA ga = new GA(gaRuntime, param);
        ga.init();
        GA.GAUtils.simpleRestartUntilTargetFitness(ga, 0, .8f, .8f);
        ga.useRuntime(gaRuntime1 -> {
          Affine.Config c = (Affine.Config) config;
          byte[] bestGene = gaRuntime.getGeneByRank(0);
          c.a = bestGene[0];
          c.b = bestGene[1];
        });
      }
    );
  }

  @Override
  public void crack(Crypto.ICrypto crypto, Crypto.IConfig config, ArrayList<Utils.Pair<ByteArray, ByteArray>> plaintext_cipher_pairs) {
    Crack.ICrack cracker = impls.get(crypto.getClass());
    if (cracker != null) {
      cracker.crack(crypto, config, plaintext_cipher_pairs);
    } else {
      throw new NotImplementedException();
    }
  }
}
