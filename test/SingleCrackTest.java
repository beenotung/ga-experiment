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
    for (Crypto.ICrypto crypto : Crypto.$MODULE.impls) {
      print("\rCracking", n_pair, "pairs", crypto.getClass().getSimpleName(), "by", cracker.getClass().getSimpleName(), "| base:", base);
      out.flush();
      out.print('\r');
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
  }

  void crack_old(Crack.ICrack cracker, String[] messages, int base, int n_pair) {
    for (int i = 0; i < n_pair; i++) {
      int finalI = i;
      Crypto.$MODULE.impls.forEach(iCrypto -> {
        long acc = 0;
        long best = Long.MAX_VALUE;
        long worst = Long.MIN_VALUE;
        long n = 0;
        for (String message : messages) {
          print("\rcracking message of length", message.length(), finalI, "/", TestConfig.N_Pair, "\r");
          Pair<Boolean, Long> res = $MODULE.crack(cracker, iCrypto, TestConfig.N_Pair, message, base);
          if (res._1) {
            acc += res._2;
            n++;
            best = Math.min(best, res._2);
            worst = Math.max(worst, res._2);
          } else {
            int breakpoint = 1;
          }
        }
        print(cracker.getClass().getSimpleName(), '|', iCrypto.getClass().getSimpleName(), "| base:", base, "| ");
        if (n == 0) {
          println("failed on all case");
        } else {
          print("passed", n, "case | failed", TestConfig.N_Pair - n, "case");
          print("\t| avg time:", nano_to_string(acc / n));
          print("\t| best time:", nano_to_string(best));
          print("\t| worst time:", nano_to_string(worst));
          println();
        }
      });
    }
  }

  Pair<Boolean, Long> crack(final Crack.ICrack cracker, Crypto.ICrypto crypto, final int n_pair, final String message, int base) {
    print("Cracking", cracker.getClass().getSimpleName(), "with", crypto.getClass().getSimpleName(), "for", n_pair, "pairs on message of length", message.length());
    out.flush();
    out.print('\r');
    Crypto.IConfig keyConfig = crypto.sampleConfig(base);
    crypto.prepare(keyConfig);
    Crypto.IConfig solutionConfig = crypto.sampleConfig(base);
    ArrayList<Utils.Pair<Utils.ByteArray, Utils.ByteArray>> pairs = list(fill(n_pair, () -> {
      ByteArray plaintext = new ByteArray(0);
      ByteArray cipher = new ByteArray(0);
      crypto.preprocess(message, plaintext);
      crypto.encryp(plaintext, cipher);
      return pair(plaintext, cipher);
    }, Pair.class));
    long start_time = System.nanoTime();
    cracker.crack(crypto, solutionConfig, pairs);
    long end_time = System.nanoTime();
    print("\rdone");
    out.flush();
    print('\r');
    boolean equals = keyConfig.equals(solutionConfig);
    if (!equals) {
      println(cracker.getClass().getSimpleName(), "failed cracking", crypto.getClass().getSimpleName()
        , "| real key:", keyConfig
        , "| guess key:", solutionConfig
      );
    }
    return pair(equals, end_time - start_time);
  }
}
