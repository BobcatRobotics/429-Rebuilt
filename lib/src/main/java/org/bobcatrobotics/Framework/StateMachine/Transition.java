package org.bobcatrobotics.Framework.StateMachine;
import java.util.function.BooleanSupplier;

public class Transition<S extends Enum<S> & State> {

    private final S from;
    private final S to;
    private final BooleanSupplier condition;
    private final int priority;

    public Transition(S from, S to, BooleanSupplier condition, int priority) {
        this.from = from;
        this.to = to;
        this.condition = condition;
        this.priority = priority;
    }

    public boolean isTriggered(S currentState) {
        return currentState == from && condition.getAsBoolean();
    }

    public S getTo() {
        return to;
    }

    public S getFrom() {
        return from;
    }

    public int getPriority() {
        return priority;
    }
}
