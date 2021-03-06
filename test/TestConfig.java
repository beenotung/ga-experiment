import com.github.beenotung.javalib.Utils;

/**
 * Created by beenotung on 11/30/16.
 */
public class TestConfig {
  public static final int N_Pair = 10;
  /**
   * base 26 -> ['a'..'z']
   * base 36 -> ['a'..'z']++['0'..'9']
   * base 0 -> base 256 -> [0..255] (raw bytes)
   * */
  public static final int Bases[] = {26, 36, 256};
  public static final String[] messages = {
    "This is a short message."
    , "This is a longer message."
  };
  public static long time_limit = Utils.NANO_IN_SECOND * 10;
}
