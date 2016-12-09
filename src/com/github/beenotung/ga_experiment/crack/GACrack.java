package com.github.beenotung.ga_experiment.crack;

import com.github.beenotung.ga_experiment.crypto.Affine;
import com.github.beenotung.ga_experiment.crypto.Shift;
import com.github.beenotung.ga_experiment.crypto.Substition;
import com.github.beenotung.javalib.GA;
import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import com.github.beenotung.javalib.Utils.ThreadLocalStorage;
import com.github.beenotung.ga_experiment.crypto.Crypto;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.beenotung.javalib.Utils.uint;

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
    impls.put(Shift.class, new Crack.ICrack() {
      @Override
      public void crack(Crypto.ICrypto crypto, Crypto.IConfig iConfig, ArrayList<Utils.Pair<ByteArray, ByteArray>> plaintext_cipher_pairs) {
        Shift.Config config = (Shift.Config) iConfig;
        final int l_gene = 1;
        final float p_crossover = 0.25f;
        final float p_mutate = 0.8f;
        final float a_mutate = 1f;
        float[] cache_value = new float[config.base];
        boolean[] cache_flag = new boolean[config.base];
        Utils.IMap<Integer, Float> cache = new Utils.IMap<Integer, Float>() {
          @Override
          public Float get(Integer a) {
            if (!cache_flag[a]) {
              float acc = 0;
              Shift shift = new Shift();
              Shift.Config config_local = new Shift.Config();
              config_local.base = config.base;
              config_local.offset = a;
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
              cache_value[a] = acc;
              cache_flag[a] = true;
            }
            return cache_value[a];
          }
        };
        GA.Param param = new GA.Param() {
          /**
           * hamming distance
           * */
          @Override
          public GA.IEval I_EVAL() {
            return new GA.IEval() {
              @Override
              public float eval(byte[] gene) {
                return cache.get(uint(gene[0]) % config.base);
              }
            };
          }
        };
        final int n_pop = 100;
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
          config.offset = uint(gaRuntime.getGeneByRank(0)[0]) % config.base;
        });
      }
    });
    impls.put(Affine.class, (crypto, config, plaintext_cipher_pairs) -> {
        final int n_pop = Math.max(100, config.base * config.base / 2);
        GA.GARuntime gaRuntime = new GA.GARuntime(n_pop, 2, .25f, .9f, 1f, true);
        float[][] fitness_cache = new float[config.base][config.base];
        boolean[][] fitness_cache_flag = new boolean[config.base][config.base];
        for (int i = 0; i < config.base; i++) {
          for (int j = 0; j < config.base; j++) {
            fitness_cache_flag[i][j] = false;
          }
        }
        Utils.IMap2<Integer, Integer, Float> fitness_cacher = new Utils.IMap2<Integer, Integer, Float>() {
          @Override
          public Float get(Integer a, Integer b) {
            if (!fitness_cache_flag[a][b]) {
              float acc = 0;
              Affine affine = new Affine();
              Affine.Config c = new Affine.Config();
              c.base = config.base;
              c.a = a;
              c.b = b;
              affine.prepare(c);
              ByteArray result = new ByteArray(0);
              for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
                final ByteArray plaintext = plaintext_cipher_pair._1;
                affine.decryp(plaintext_cipher_pair._2, result);

                acc += (plaintext.len - result.len) * c.base;

                for (int i = 0; i < plaintext.len; i++) {
                  acc += Math.abs(plaintext.data[i + plaintext.offset] - result.data[i + result.offset]);
                }
              }
              if (c.base == 256) {
                if (c.a == 41 && c.b == 179) {
                  int bp = 1;
                }
              }
              fitness_cache[a][b] = acc;
              fitness_cache_flag[a][b] = true;
            }
            return fitness_cache[a][b];
          }
        };
        GA.Param param = new GA.Param() {
          @Override
          public GA.IEval I_EVAL() {
            return gene ->
              fitness_cacher.get(uint(gene[0]) % config.base, uint(gene[1]) % config.base);
          }

          @Override
          public GA.IRandomGene I_RANDOM_GENE() {
            return new GA.IRandomGene() {
              @Override
              public void randomGene(byte[] gene, int offset, int length) {
                Affine.Config c = Affine.$MODULE.sampleConfig(config.base);
                gene[0] = (byte) c.a;
                gene[1] = (byte) c.b;
              }
            };
          }

          @Override
          public GA.ICrossover I_CROSSOVER() {
            return new GA.ICrossover() {
              @Override
              public void crossover(byte[] p1, byte[] p2, byte[] child) {
//                ThreadLocalRandom r = ThreadLocalRandom.current();
                child[0] = (byte) ((uint(p1[0]) + uint(p2[0])) / 2);
                child[1] = (byte) ((uint(p1[1]) + uint(p2[1])) / 2);
              }
            };
          }
        };
        GA ga = new GA(gaRuntime, param);
        ga.init();
        if (config.base == 256) {
          GA.GAUtils.partialRestartUntilTargetFitness(ga, 0, .9f, 1f);
        } else {
          GA.GAUtils.simpleRestartUntilTargetFitness(ga, 0, .9f, 1f);
        }
        ga.useRuntime(gaRuntime1 -> {
          Affine.Config c = (Affine.Config) config;
          byte[] bestGene = gaRuntime.getGeneByRank(0);
          c.a = uint(bestGene[0]) % c.base;
          c.b = uint(bestGene[1]) % c.base;
        });
      }
    );
    impls.put(Substition.class, (crypto, solutionConfig, plaintext_cipher_pairs) -> {
      Substition substition = (Substition) crypto;
      Substition.Config c = (Substition.Config) solutionConfig;
      int n_pop = 500;
      GA.GARuntime initRuntime = new GA.GARuntime(n_pop, c.base, 0.25f, 0.2f, 1f / c.base);
      ThreadLocalStorage<Substition> substitionThreadLocalStorage = new ThreadLocalStorage<Substition>(() -> new Substition());
      ThreadLocalStorage<Substition.Config> configThreadLocalCoders = new ThreadLocalStorage<Substition.Config>(() -> substitionThreadLocalStorage.current().sampleConfig(c.base));
      ThreadLocalStorage<ByteArray> resultThreadLocalStorage = new ThreadLocalStorage<ByteArray>(() -> new ByteArray(0));
      GA.Param gaParam = new GA.Param() {
        @Override
        public GA.IEval I_EVAL() {
          return gene -> {
            Substition.Config c = configThreadLocalCoders.current();
            for (int i = 0; i < c.base; i++) {
              c.table[i] = uint(gene[i]) % c.base;
            }
            for (int i = 0; i < c.base; i++) {
              c.re_table[c.table[i]] = i;
            }
            Substition s = substitionThreadLocalStorage.current();
            s.prepare(c);
            ByteArray r = resultThreadLocalStorage.current();
            float acc = 0;
            for (Utils.Pair<ByteArray, ByteArray> plaintext_cipher_pair : plaintext_cipher_pairs) {
              s.decryp(plaintext_cipher_pair._2, r);
              ByteArray plaintext = plaintext_cipher_pair._1;
              acc += (r.len - plaintext.len) * c.base;
              for (int i = 0; i < r.len; i++) {
                acc += Math.abs(
                  r.data[i + r.offset]
                    - plaintext.data[i + plaintext.offset]
                );
              }
            }
            return acc;
          };
        }
      };
      GA ga = new GA(initRuntime, gaParam);
      ga.init();
      GA.GAUtils.simpleRestartUntilTargetFitness(ga, 0f, 0.9f, 0.9f);
      ga.useRuntime(gaRuntime -> {
        byte[] gene = gaRuntime.getGeneByRank(0);
        for (int i = 0; i < c.base; i++) {
          c.table[i] = uint(gene[i]);
        }
        for (int i = 0; i < c.base; i++) {
          c.re_table[c.table[i]] = i;
        }
      });
    });
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
