import com.github.beenotung.javalib.Utils.ByteArray;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Package;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Shift;

import java.util.ArrayList;

import static com.github.beenotung.javalib.Utils.println;

public class CryptoTest {
  public static CryptoTest $MODULE = new CryptoTest();

  boolean validTest(Crypto.ICrypto crypto, Crypto.IConfig config, String message) {
    crypto.prepare(config);

    println('\t', crypto.toString());

    int length = message.length();

    ByteArray plaintext = new ByteArray(length);
    crypto.preprocess(message, plaintext);

    ByteArray cipher = new ByteArray(length);
    ByteArray result = new ByteArray(length);

    crypto.encryp(plaintext, cipher);
    crypto.decryp(cipher, result);

    String cipher_text = crypto.postprocess(cipher);
    String result_text = crypto.postprocess(result);
    String plaintext_text = crypto.postprocess(plaintext);
    println("\t[", message, "] --> [", cipher_text, ']');
    println("\t[", cipher_text, "] <-- [", result_text, ']');

//    return plaintext.equals(result);
    return plaintext_text.equals(result_text);
  }

  public static void main(String[] args) throws ClassNotFoundException {
    println("begin", $MODULE.getClass().getName());
    String msg = "This is a sample message";

    Package.loadAll();
    for (Crypto.ICrypto crypto : Crypto.$MODULE.impls) {
      boolean res = $MODULE.validTest(crypto, crypto.sampleConfig(), msg);
      if (res) {
        println("Passed", crypto.getClass().getSimpleName());
      } else {
        println("Failed", crypto.getClass().getSimpleName());
      }
    }

    println("end", $MODULE.getClass().getName());
  }
}
