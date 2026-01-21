package org.bobcatrobotics.Util.Tunables;

import org.littletonrobotics.junction.Logger;

/**
 * TunableEnum
 *
 * <p>Allows runtime tuning of enum values via NetworkTables using String names.
 *
 * @param <E> Enum type
 */
public final class TunableEnum<E extends Enum<E>> extends TunableBase<E> {

  private final Class<E> enumClass;

  /**
   * Creates a new TunableEnum.
   *
   * @param key NetworkTables key (e.g. "Shooter/Mode")
   * @param defaultValue Default enum value
   * @param enumClass Enum class reference
   */
  public TunableEnum(String key, E defaultValue, Class<E> enumClass) {
    super(key, defaultValue);
    this.enumClass = enumClass;
  }

  @Override
  protected E read(E fallback) {
    String name = entry.getString(fallback.name());

    try {
      return Enum.valueOf(enumClass, name);
    } catch (IllegalArgumentException ex) {
      // Invalid enum value typed in AdvantageScope
      return fallback;
    }
  }

  @Override
  protected void write(E value) {
    entry.setString(value.name());
  }

  @Override
  protected void logValue(E value) {
  Logger.recordOutput("Tunable/" + key + "/Value", value.name());
  Logger.recordOutput("Tunable/" + key + "/Ordinal", value.ordinal());
  Logger.recordOutput(
      "Tunable/" + key + "/Options",
      java.util.Arrays.stream(enumClass.getEnumConstants())
          .map(Enum::name)
          .toArray(String[]::new));
  }
}
