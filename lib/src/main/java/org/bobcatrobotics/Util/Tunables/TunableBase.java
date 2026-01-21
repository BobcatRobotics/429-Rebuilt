package org.bobcatrobotics.Util.Tunables;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.littletonrobotics.junction.Logger;

import java.util.Objects;

/**
 * Base implementation for all tunable types.
 *
 * @param <T> Value type
 */
public abstract class TunableBase<T> implements Tunable<T> {

  private static boolean tuningEnabled = false;

  protected final String key;
  protected final T defaultValue;
  protected T cachedValue;
  private boolean initialized = false;

  protected final NetworkTableEntry entry;

  protected TunableBase(String key, T defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;

    this.entry = NetworkTableInstance.getDefault()
        .getTable("Tunable")
        .getEntry(key);

    DefaultTunableRegistry.getInstance().register(this);

    Logger.recordOutput("Tunable/" + key + "/Default", defaultValue.toString());
  }

  @Override
  public final T get() {
    if (!initialized) {
      cachedValue = read(defaultValue);
      write(cachedValue);
      initialized = true;
    }

    if (tuningEnabled) {
      T newValue = read(cachedValue);
      if (!Objects.equals(newValue, cachedValue)) {
        cachedValue = newValue;
        Logger.recordOutput("Tunable/" + key + "/Changed", true);
      }
    }

    logValue(cachedValue);
    return cachedValue;
  }

  @Override
  public final boolean hasChanged() {
    T current = read(cachedValue);
    return !Objects.equals(current, cachedValue);
  }

  @Override
  public final String getKey() {
    return key;
  }

  /** Read value from NetworkTables */
  protected abstract T read(T fallback);

  /** Write value to NetworkTables */
  protected abstract void write(T value);

  /** Log value using AdvantageKit */
  protected abstract void logValue(T value);

  /** Enable or disable tuning globally */
  public static void enableTuning(boolean enabled) {
    tuningEnabled = enabled;
    Logger.recordOutput("Tunable/TuningEnabled", enabled);
  }

  @Override
  public final void resetToDefault() {
    cachedValue = defaultValue;
    write(defaultValue);
    Logger.recordOutput("Tunable/" + key + "/Reset", true);
  }
}
