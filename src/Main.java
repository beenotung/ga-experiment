import com.github.beenotung.javalib.GraphicUtils;
import myutils.gui.CanvasJFrame;
import org.bitbucket.ucf_crypto.ga_experiment.gui.Launcher;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.github.beenotung.javalib.Utils.*;
import static com.github.beenotung.javalib.GraphicUtils.Screen;

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
