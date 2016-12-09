import com.github.beenotung.javalib.GraphicUtils;
import com.github.beenotung.ga_experiment.gui.Launcher;

import static com.github.beenotung.javalib.Utils.*;

public class Main {
  public static void main(String[] args) throws Exception {
    println("begin");
    if (GraphicUtils.Screen.IS_HEADLESS) {
      throw new Exception("GraphicsEnvironment is not supported but it is reburied.");
    }
    Launcher launcher = new Launcher();
    launcher.start();
    println("end");
  }
}
