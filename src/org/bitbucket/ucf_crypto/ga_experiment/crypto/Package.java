package org.bitbucket.ucf_crypto.ga_experiment.crypto;

/**
 * Created by beenotung on 11/30/16.
 */
public class Package {
  public static void loadAll() throws ClassNotFoundException {
    Class.forName(Shift.class.getName());
    Class.forName(Affine.class.getName());
    Class.forName(Substition.class.getName());
  }
}
