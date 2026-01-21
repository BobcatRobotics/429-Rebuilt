package org.bobcatrobotics.Framework.StateMachine;

import edu.wpi.first.wpilibj2.command.Command;

public class StateMachineCommand<S extends Enum<S> & State> extends Command {

    private final StateMachine<S> stateMachine;

    public StateMachineCommand(StateMachine<S> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void execute() {
        stateMachine.update();
    }

    @Override
    public boolean isFinished() {
        return false; // runs continuously
    }
}
