package org.bobcatrobotics.Util.Tunables;

/**
 * Factory for creating tunable values.
 */
public final class Tunables {

  private Tunables() {}

  public static TunableDouble doubleValue(String key, double defaultValue) {
    return new TunableDouble(key, defaultValue);
  }

  public static TunableInt intValue(String key, int defaultValue) {
    return new TunableInt(key, defaultValue);
  }

  public static TunableBoolean booleanValue(String key, boolean defaultValue) {
    return new TunableBoolean(key, defaultValue);
  }

  public static TunableString stringValue(String key, String defaultValue) {
    return new TunableString(key, defaultValue);
  }

  public static <E extends Enum<E>> TunableEnum<E> enumValue(
      String key, E defaultValue, Class<E> enumClass) {
    return new TunableEnum<>(key, defaultValue, enumClass);
  }
}
