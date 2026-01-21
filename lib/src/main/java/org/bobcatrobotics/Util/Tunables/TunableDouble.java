package org.bobcatrobotics.Util.Tunables;

import org.littletonrobotics.junction.Logger;

public final class TunableDouble extends TunableBase<Double> {

  public TunableDouble(String key, double defaultValue) {
    super(key, defaultValue);
  }

  @Override
  protected Double read(Double fallback) {
    return entry.getDouble(fallback);
  }

  @Override
  protected void write(Double value) {
    entry.setDouble(value);
  }

  @Override
  protected void logValue(Double value) {
    Logger.recordOutput("Tunable/" + key + "/Value", value);
  }
}

