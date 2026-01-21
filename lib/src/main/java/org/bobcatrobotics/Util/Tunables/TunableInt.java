package org.bobcatrobotics.Util.Tunables;

import org.littletonrobotics.junction.Logger;

public final class TunableInt extends TunableBase<Integer> {

  public TunableInt(String key, int defaultValue) {
    super(key, defaultValue);
  }

  @Override
  protected Integer read(Integer fallback) {
    return (int) entry.getDouble(fallback);
  }

  @Override
  protected void write(Integer value) {
    entry.setDouble(value);
  }

  @Override
  protected void logValue(Integer value) {
    Logger.recordOutput("Tunable/" + key + "/Value", value);
  }
}
