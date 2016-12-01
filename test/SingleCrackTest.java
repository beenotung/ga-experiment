import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils7;
import org.bitbucket.ucf_crypto.ga_experiment.crack.BruteForceCrack;
import org.bitbucket.ucf_crypto.ga_experiment.crack.Crack;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.*;

import java.lang.management.BufferPoolMXBean;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Supplier;

import static com.github.beenotung.javalib.Utils.*;

/**
 * Created by beenotung on 11/30/16.
 */
public class SingleCrackTest {
  public static SingleCrackTest $MODULE = new SingleCrackTest();

  public static void main(String[] args) throws ClassNotFoundException {
    println("begin", $MODULE.getClass().getName());

    org.bitbucket.ucf_crypto.ga_experiment.crypto.Package.loadAll();

    for (String message : TestConfig.messages) {
      Crypto.$MODULE.impls.forEach(iCrypto -> {
        long acc = 0;
        long n = 0;
        for (int i = 0; i < TestConfig.N_Pair; i++) {
          print("\rcracking message of length", message.length(), i, "/", TestConfig.N_Pair, "\r");
          Pair<Boolean, Long> res = $MODULE.crack(BruteForceCrack.$MODULE, iCrypto, TestConfig.N_Pair, message);
          if (res._1) {
            acc += res._2;
            n++;
          }
        }
        if (n == 0) {
          println("msg len:", message.length(), '|', iCrypto.getClass().getSimpleName(), "failed on all case");
        } else {
          println("msg len:", message.length(), '|', iCrypto.getClass().getSimpleName(), "failed on", TestConfig.N_Pair - n, "case", "| avg sucess time:", nano_to_string(acc / n)g);
        }
      });
    }

    println("end", $MODULE.getClass().getName());
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
    return pair(keyConfig.equals(solutionConfig), end_time - start_time);
//    boolean equals = keyConfig.equals(solutionConfig);
//    if (equals) {
//      println("Passed Crack, key:", keyConfig);
//    } else {
//      println("Failed Crack, real key:", keyConfig, "| guess key:", solutionConfig);
//    }
//    println("used", nano_to_string(end_time - start_time));
//    return pair(equals, end_time - start_time);
  }
}
