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

    ByteArray plaintext = crypto.preprocess(message);

    ByteArray cipher = new ByteArray();
    ByteArray result = new ByteArray();

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
      $MODULE.validTest(crypto, crypto.sampleConfig(), msg);
      crypto.prepare(crypto.sampleConfig());
      $MODULE.validTest(crypto.);
    }

    println("end test");
  }
}
