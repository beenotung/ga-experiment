package org.bitbucket.ucf_crypto.ga_experiment.crack;

/**
 * Created by beenotung on 11/30/16.
 */
public class Package {
  static void loadAll() throws ClassNotFoundException {
    Class.forName(BruteForceCrack.class.getName());
    Class.forName(GACrack.class.getName());
  }
}
