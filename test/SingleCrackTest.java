import com.github.beenotung.javalib.Utils;
import org.bitbucket.ucf_crypto.ga_experiment.crack.BruteForceCrack;
import org.bitbucket.ucf_crypto.ga_experiment.crack.Crack;
import org.bitbucket.ucf_crypto.ga_experiment.crack.GACrack;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;

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

  void crack(Crack.ICrack cracker, String[] messages, int base, int n_pair) {
    for (String message : messages) {
      Crypto.$MODULE.impls.forEach(iCrypto -> {
        long acc = 0;
        long best = Long.MAX_VALUE;
        long worst = Long.MIN_VALUE;
        long n = 0;
        for (int i = 0; i < n_pair; i++) {
          print("\rcracking message of length", message.length(), i, "/", TestConfig.N_Pair, "\r");
          Pair<Boolean, Long> res = $MODULE.crack(cracker, iCrypto, TestConfig.N_Pair, message);
          if (res._1) {
            acc += res._2;
            n++;
            best = Math.min(best, res._2);
            worst = Math.max(worst, res._2);
          }
        }
        print(cracker.getClass().getSimpleName(), '|', iCrypto.getClass().getSimpleName(), "| msg len:", message.length(), '|', "base:", base, "| ");
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

  Pair<Boolean, Long> crack(final Crack.ICrack cracker, Crypto.ICrypto crypto, final int n_pair, final String message) {
    print("Cracking", cracker.getClass().getSimpleName(), "with", crypto.getClass().getSimpleName(), "for", n_pair, "pairs on message of length", message.length());
    Crypto.IConfig keyConfig = crypto.sampleConfig();
    crypto.prepare(keyConfig);
    Crypto.IConfig solutionConfig = crypto.sampleConfig();
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
    print("\rdone\r");
    boolean equals = keyConfig.equals(solutionConfig);
    if (!equals) {
      println(cracker.getClass().getSimpleName(), "failed cracking", crypto.getClass().getSimpleName(), "| real key:", keyConfig, "| guess key:", solutionConfig);
    }
    return pair(equals, end_time - start_time);
  }
}
