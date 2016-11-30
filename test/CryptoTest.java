import com.github.beenotung.javalib.Utils;
import com.github.beenotung.javalib.Utils.ByteArray;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Crypto;
import org.bitbucket.ucf_crypto.ga_experiment.crypto.Shift;

import java.util.ArrayList;

import static com.github.beenotung.javalib.Utils.println;

public class CryptoTest {
  public static CryptoTest $MODULE = new CryptoTest();

  boolean validTest(Crypto.ICrypto crypto, Crypto.Config config, String message) {
    crypto.prepare(config);

    int length = message.length();

    ByteArray plaintext = new ByteArray(length);
    crypto.preprocess(message, plaintext);

    ByteArray cipher = new ByteArray(length);
    ByteArray result = new ByteArray(length);

    crypto.encryp(plaintext, cipher);
    crypto.decryp(cipher, result);

    return plaintext.equals(result);
  }

  public static void main(String[] args) {
    println("begin test");
    String msg = "This is a sample message";

    ArrayList<Crypto.ICrypto> cryptos = new ArrayList<>();
    cryptos.add(new Shift());

    for (Crypto.ICrypto crypto : cryptos) {
      boolean res = $MODULE.validTest(crypto, crypto.sampleConfig(), msg);
      if (res) {
        println("Passed", crypto.getClass().getSimpleName());
      } else {
        println("Failed", crypto.getClass().getSimpleName());
      }
    }

    println("end test");
  }
}
