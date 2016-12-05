import com.github.beenotung.javalib.Utils;
import org.bitbucket.ucf_crypto.ga_experiment.crack.BruteForceCrack;
import org.bitbucket.ucf_crypto.ga_experiment.crack.Crack;
import org.bitbucket.ucf_crypto.ga_experiment.crack.GACrack;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;

import static com.github.beenotung.javalib.Utils.*;

/**
 * Created by beenotung on 11/30/16.
 */
public class SingleCrackTest {
  public static SingleCrackTest $MODULE = new SingleCrackTest();

  public static void main(String[] args) throws ClassNotFoundException {
    println("begin", $MODULE.getClass().getName());

    org.bitbucket.ucf_crypto.ga_experiment.crypto.Package.loadAll();

    $MODULE.crack(BruteForceCrack.$MODULE, TestConfig.messages, TestConfig.N_Pair);
    $MODULE.crack(GACrack.$MODULE, TestConfig.messages, TestConfig.N_Pair);

    println("end", $MODULE.getClass().getName());
  }

  void crack(Crack.ICrack cracker, String[] messages, int n_pair) {
    for (int base : TestConfig.Bases) {
      crack(cracker, messages, base, n_pair);
    }
  }

  boolean crack(Crack.ICrack cracker, Crypto.ICrypto crypto, String[] messages, int base) {
    Crypto.IConfig actualKey = crypto.sampleConfig(base);
    Crypto.IConfig guessKey = crypto.sampleConfig(base);
    ArrayList<Pair<ByteArray, ByteArray>> pairs = new ArrayList<>(messages.length);
    crypto.prepare(actualKey);
    for (int i = 0; i < messages.length; i++) {
      ByteArray plaintext = new ByteArray(0);
      ByteArray cipher = new ByteArray(0);
      crypto.preprocess(messages[i], plaintext);
      crypto.encryp(plaintext, cipher);
      pairs.add(pair(plaintext, cipher));
    }
    cracker.crack(crypto, guessKey, pairs);
    boolean res = actualKey.equals(guessKey);
    if (!res) {
      println(cracker.getClass().getSimpleName(), "failed", crypto.getClass().getSimpleName()
        , "| actual key:", actualKey
        , "| guess key:", guessKey
      );
      int breakpoint = 1;
    }
    return res;
  }

  void crack(Crack.ICrack cracker, String[] messages, int base, int n_pair) {
    try {
      boolean res = fork_and_wait_timeout(() -> {
        for (Crypto.ICrypto crypto : Crypto.$MODULE.impls) {
          println("\rCracking", n_pair, "pairs", crypto.getClass().getSimpleName(), "by", cracker.getClass().getSimpleName(), "| base:", base);
          out.flush();
          int n_correct = 0;
          long acc_time = 0;
          long best_time = Long.MAX_VALUE;
          long worst_time = Long.MIN_VALUE;
          for (int i = 0; i < n_pair; i++) {
            long start_time = System.nanoTime();
            if (crack(cracker, crypto, messages, base)) {
              long used_time = System.nanoTime() - start_time;
              n_correct++;
              acc_time += used_time;
              best_time = Math.min(used_time, best_time);
              worst_time = Math.max(used_time, worst_time);
            }
          }
          print(cracker.getClass().getSimpleName()
            , '|', crypto.getClass().getSimpleName()
            , "| base", base
          );
          if (n_correct != 0) {
            print(
              "\t| passed:", n_correct
              , "\t| failed:", n_pair - n_correct
              , "\t| avg time:", nano_to_string(acc_time / n_correct)
              , "\t| best time:", nano_to_string(best_time)
              , "\t| worst time:", nano_to_string(worst_time)
            );
          } else {
            print(
              "\t| all failed"
            );
          }
          println();
        }
      }, TestConfig.time_limit);
      if (!res) {
        println("| Failed to crack the key within time limit of:", nano_to_string(TestConfig.time_limit));
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
