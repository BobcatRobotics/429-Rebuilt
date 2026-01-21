package org.bobcatrobotics.Util.Tunables;

import org.littletonrobotics.junction.Logger;

/**
 * TunableBoolean
 *
 * <p>Runtime-tunable boolean backed by NetworkTables.
 * Useful for feature flags, enable switches, and behavior gating.
 */
public final class TunableBoolean extends TunableBase<Boolean> {

  /**
   * Creates a new TunableBoolean.
   *
   * @param key NetworkTables key (e.g. "Vision/Enabled")
   * @param defaultValue Default value
   */
  public TunableBoolean(String key, boolean defaultValue) {
    super(key, defaultValue);
  }

  @Override
  protected Boolean read(Boolean fallback) {
    return entry.getBoolean(fallback);
  }

  @Override
  protected void write(Boolean value) {
    entry.setBoolean(value);
  }

  @Override
  protected void logValue(Boolean value) {
    Logger.recordOutput("Tunable/" + key + "/Value", value);
  }
}
