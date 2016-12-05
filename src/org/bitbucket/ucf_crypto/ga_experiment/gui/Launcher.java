package org.bitbucket.ucf_crypto.ga_experiment.gui;

import com.github.beenotung.javalib.GA;
import com.github.beenotung.javalib.GraphicUtils.Screen;
import myutils.gui.CanvasJFrame;
import myutils.gui.Colors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.github.beenotung.javalib.Utils.*;

/**
 * Created by beenotung on 12/5/16.
 */
public class Launcher {
  public static int MIN_NPOP = 5;
  public static int MAX_N_POP = 100;
  public static int INIT_N_POP = 10;

  private final int[] source_pixels;//RGBArray
  private final int[] encryp_pixels;//RGBArray
  final int[] source_rgbArray;
  final int[] encryp_rgbArray;

  private final int n_grid_in_x;
  private final int n_grid_in_y;
  private final int grid_width;
  private final int grid_height;
  private final MainFrame mainFrame;
  private EventHandler eventHandler = new EventHandler();
  final GA ga;
  static final int MODE_COMPARE = 1;
  static final int MODE_GA = 2;
  int mode = MODE_COMPARE;
  int ga_round = 0;
  boolean auto_ga = false;

  public Launcher() throws IOException {
    /* -- init data source and UI -- */
    BufferedImage photo = ImageIO.read(new File("res/Mona_Lisa.jpg"));
    int max_width = Screen.WIDTH / 4;
    int max_height = Screen.HEIGHT / 4;
    double image_ratio = 1d * photo.getWidth() / photo.getHeight();
    if (image_ratio > Screen.RATIO) {
      /* wide image */
      grid_width = Math.min(max_width, photo.getWidth());
      /*
      * width / height = ratio
      * width / ratio = height
      * */
      grid_height = (int) (grid_width / image_ratio);
    } else {
      /* narrow image */
      grid_height = Math.min(max_height, photo.getHeight());
      /*
      * width / height = ratio
      * width = ratio * height
      * */
      grid_width = (int) (image_ratio * grid_height);
    }
    n_grid_in_x = Screen.WIDTH / grid_width;
    n_grid_in_y = Screen.HEIGHT / grid_height;
    MAX_N_POP = n_grid_in_x * n_grid_in_y;
    MAX_N_POP = 1000;
    source_pixels = new int[grid_width * grid_height];
    encryp_pixels = new int[grid_width * grid_height];
    source_rgbArray = new int[grid_width * grid_height * 3];
    encryp_rgbArray = new int[grid_width * grid_height * 3];
    for (int x = 0; x < grid_width; x++) {
      for (int y = 0; y < grid_height; y++) {
        source_pixels[x + y * grid_width] = photo.getRGB(
          x * photo.getWidth() / grid_width
          , y * photo.getHeight() / grid_height
        );
      }
    }
    pixels_to_rgbArray(source_pixels, source_rgbArray);
    mainFrame = new MainFrame();

    /* -- init GA -- */
    GA.GARuntime initRuntime = new GA.GARuntime(
      INIT_N_POP
      , 256
      , 0.25f
      , 0.8f
      , 0.2f
      , true
    );
    ThreadLocalStorage<int[]> rgbArrayThreadLocalStorage = new ThreadLocalStorage<int[]>(() -> new int[grid_height * grid_width * 3]);
    ThreadLocalStorage<int[]> tableThreadLocalStorage = new ThreadLocalStorage<int[]>(() -> new int[256]);
    GA.Param gaParam = new GA.Param() {
      @Override
      public GA.IEval I_EVAL() {
        return gene -> {
          float acc = 0;
          int[] rgbArray = rgbArrayThreadLocalStorage.current();
          int[] table = tableThreadLocalStorage.current();
          map_rgbArray(table, encryp_rgbArray, rgbArray);
          for (int i = 0; i < encryp_rgbArray.length; i++) {
            acc += Math.min(
              Math.abs((encryp_rgbArray[i] - rgbArray[i]) % 256)
              , Math.abs((rgbArray[i] - encryp_rgbArray[i]) % 256)
            );
          }
          return acc;
        };
      }
    };
    ga = new GA(initRuntime, gaParam);
    ga.init();
  }

  void gene_to_table(byte[] gene, int[] table) {
    for (int i = 0; i < 256; i++) {
      table[i] = i;
    }
    int t, a, b;
    for (int i = 1; i < 256; i++) {
      a = uint(gene[i - 1]);
      b = uint(gene[i]);
      t = table[a];
      table[a] = table[b];
      table[b] = t;
    }
  }

  public void start() {
    mainFrame.start();
  }

  int[] real_key = new int[256];
  int[] real_re_key = new int[256];

  /*
  * same function for encryp and decryp
  * */
  public void map_pixels(int[] table, int[] src_pixels, int[] dest_pixels) {
    int r, g, b;
    for (int i = 0; i < src_pixels.length; i++) {
      r = (src_pixels[i] >> 16) & 0xFF;
      g = (src_pixels[i] >> 8) & 0xFF;
      b = src_pixels[i] & 0xFF;
      r = table[r] & 0xFF;
      g = table[g] & 0xFF;
      b = table[b] & 0xFF;
      dest_pixels[i] = r << 16 | g << 8 | b;
    }
  }

  public void pixels_to_rgbArray(int[] ps, int[] rgbArray) {
    for (int i = 0; i < ps.length; i++) {
      rgbArray[i * 3] = (ps[i] >> 16) & 0xFF;
      rgbArray[i * 3 + 1] = (ps[i] >> 8) & 0xFF;
      rgbArray[i * 3 + 2] = (ps[i]) & 0xFF;
    }
  }

  public void rbgArray_to_pixels(int[] rgbArray, int[] ps) {
    for (int i = 0; i < ps.length; i++) {
      ps[i] =
        rgbArray[i * 3] << 16
          | rgbArray[i * 3 + 1] << 8
          | rgbArray[i * 3 + 2]
      ;
    }
  }

  public void map_rgbArray(int[] table, int[] src_rgbArray, int[] dest_rgbArray) {
    for (int i = 0; i < src_rgbArray.length; i++) {
      dest_rgbArray[i] = table[src_rgbArray[i]];
    }
  }

  class MainFrame extends CanvasJFrame {
    MainFrame() {
      super(1, 1, 1, "Image Demo");
    }

    @Override
    protected void init() {
      cx = 0;
      cy = 0;
    }

    @Override
    protected void myTick() {
      if (auto_ga) {
        ga.next();
        ga_round++;
      }
    }

    int getCycleRGB(int[] pixels, int x, int y) {
      return pixels[x % grid_width + (y % grid_height) * grid_width];
    }

    int ga_rendered_round = -1;

    @Override
    protected void myRender() {
      if (mode == MODE_COMPARE) {
        screen.clear();
        /* draw source at left */
        for (int x = 0; x < WIDTH / 2; x++) {
          for (int y = 0; y < HEIGHT; y++) {
            screen.add(x, y, getCycleRGB(source_pixels, x * grid_width / (WIDTH / 2), y * grid_height / HEIGHT));
          }
        }
        for (int x = WIDTH / 2; x < WIDTH; x++) {
          for (int y = 0; y < HEIGHT; y++) {
            screen.add(x, y, getCycleRGB(encryp_pixels, x * grid_width / (WIDTH / 2), y * grid_height / HEIGHT));
          }
        }
      } else if (mode == MODE_GA) {
        if (ga_rendered_round == ga_round) {
          return;
        }
        screen.clear();
        ga.useRuntime(gaRuntime -> {
          ga_rendered_round = ga_round;
          int n = Math.min(n_grid_in_x * n_grid_in_y, gaRuntime.n_pop);
          int i_grid_x = -1;
          int i_grid_y = 0;
          int[] pixels = new int[grid_width * grid_height];
          int[] table = new int[256];
          for (int i_rank = 0; i_rank < n; i_rank++) {
            println("drawing rank", i_rank);
            i_grid_x++;
            if (i_grid_x == n_grid_in_x) {
              i_grid_x = 0;
              i_grid_y++;
            }
            byte[] gene = gaRuntime.getGeneByRank(i_rank);
            gene_to_table(gene, table);
            map_pixels(table, encryp_pixels, pixels);
            for (int x = 0; x < grid_width; x++) {
              for (int y = 0; y < grid_height; y++) {
                screen.add(
                  i_grid_x * grid_width + x
                  , i_grid_y * grid_height + y
                  , pixels[x + y * grid_width]
                );
              }
            }
            println("finished drawing rank", i_rank);
          }
        });
      } else {
        throw new Error("Undefined mode");
      }
    }

    @Override
    protected void keyHandling() {
//      super.keyHandling();
      if (keyHandler.esc.pressed) {
        stop();
      }
      if (keyHandler.typedLines.size() > 0) {
        eventHandler.handleCommand(keyHandler.typedLines.poll().trim());
      }
      if (keyHandler.e.pressed) {
        eventHandler.init_encryp();
      }
      if (keyHandler.p.pressed) {
        eventHandler.pauseOrPlay();
      }
      if (keyHandler.r.pressed) {
        eventHandler.reset();
      }
      if (keyHandler.m.pressed) {
        eventHandler.mutate();
      }
      if (keyHandler.closeBracket.pressed) {
        eventHandler.change_n_pop(1);
      }
      if (keyHandler.openBracket.pressed) {
        eventHandler.change_n_pop(-1);
      }
      if (keyHandler.s.pressed) {
        eventHandler.step();
      }
      if (keyHandler.c.pressed) {
        mode = MODE_COMPARE;
      }
      if (keyHandler.g.pressed) {
        mode = MODE_GA;
      }
    }

    @Override
    protected void mouseHandling() {
//      super.mouseHandling();
    }

    @Override
    protected void myDebugInfo() {
      super.myDebugInfo();
    }
  }

  class EventHandler {
    void handleCommand(String line) {
      println("handle command:", line);
      if (line.equals("start")) {
        /* TODO start ga */
      } else if (line.equals("pause")) {
        /* TODO pause */
      }
    }


    void init_encryp() {
      synchronized (ga) {
        println("init_encryp");
      /* gen random key */
        byte[] gene = randomBytes(256);
        gene_to_table(gene, real_key);
        for (int i = 0; i < 256; i++) {
          real_re_key[real_key[i]] = i;
        }
        map_rgbArray(real_key, source_rgbArray, encryp_rgbArray);
        rbgArray_to_pixels(encryp_rgbArray, encryp_pixels);
        ga_round = 0;
        mainFrame.ga_rendered_round = -1;
      }
    }


    void pauseOrPlay() {
      auto_ga = !auto_ga;
      println("auto_ga", auto_ga);
    }

    void reset() {
    }

    void mutate() {
    }

    void step() {
      ga.next();
      ga_round++;
    }

    void change_n_pop(int deltaChange) {
      final int[] new_val = {-1};
      ga.useRuntime(gaRuntime -> {
        new_val[0] = gaRuntime.n_pop + deltaChange;
        if (!(new_val[0] >= MIN_NPOP && new_val[0] <= MAX_N_POP)) {
          new_val[0] = -1;
        }
      });
      println("change_n_pop", new_val[0]);
      if (new_val[0] != -1) {
        ga.update_n_pop(new_val[0]);
      }
    }
  }
}
