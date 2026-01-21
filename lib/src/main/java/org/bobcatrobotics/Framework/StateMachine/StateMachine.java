package org.bobcatrobotics.Framework.StateMachine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.Timer;

public class StateMachine<S extends Enum<S> & State> {

    private S currentState;
    private final List<Transition<S>> transitions = new ArrayList<>();
    private final Timer stateTimer = new Timer();
    private final String logPath;

    public StateMachine(S initialState, String logPath) {
        this.logPath = logPath;
        this.currentState = initialState;

        stateTimer.start();
        currentState.onEnter();
        log();
    }

    public void addTransition(Transition<S> transition) {
        transitions.add(transition);
        transitions.sort(
                Comparator.comparingInt(Transition<S>::getPriority).reversed()
        );
    }

    /** Call from robotPeriodic */
    public void update() {
        currentState.onUpdate();

        for (Transition<S> transition : transitions) {
            if (transition.isTriggered(currentState)) {
                setState(transition.getTo());
                break;
            }
        }

        log();
    }

    private void setState(S newState) {
        if (newState == currentState) {
            return;
        }

        currentState.onExit();
        currentState = newState;

        stateTimer.reset();
        currentState.onEnter();
    }

    public S getState() {
        return currentState;
    }

    public double getTimeInState() {
        return stateTimer.get();
    }

    private void log() {
        Logger.recordOutput(logPath + "/State", currentState.name());
        Logger.recordOutput(logPath + "/TimeInState", getTimeInState());
    }
}
