package org.bobcatrobotics.Framework.StateMachine;

public interface State {
    /** Called once when the state becomes active */
    default void onEnter() {}

    /** Called periodically while the state is active */
    default void onUpdate() {}

    /** Called once when the state is exited */
    default void onExit() {}
}

