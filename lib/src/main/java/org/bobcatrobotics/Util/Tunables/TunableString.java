package org.bobcatrobotics.Util.Tunables;

import org.littletonrobotics.junction.Logger;

public final class TunableString extends TunableBase<String> {

  public TunableString(String key, String defaultValue) {
    super(key, defaultValue);
  }

  @Override
  protected String read(String fallback) {
    return entry.getString(fallback);
  }

  @Override
  protected void write(String value) {
    entry.setString(value);
  }

  @Override
  protected void logValue(String value) {
    Logger.recordOutput("Tunable/" + key + "/Value", value);
  }
}
