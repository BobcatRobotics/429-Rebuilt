package org.bobcatrobotics.Framework.StateMachine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StateMachineTest {

    enum TestState implements State {
        A, B
    }

    @Test
    void transitionOccurs() {
        StateMachine<TestState> sm =
                new StateMachine<>(TestState.A, "Test/SM");

        sm.addTransition(new Transition<>(
                TestState.A,
                TestState.B,
                () -> true,
                1
        ));

        sm.update();

        assertEquals(TestState.B, sm.getState());
    }

    @Test
    void higherPriorityWins() {
        StateMachine<TestState> sm =
                new StateMachine<>(TestState.A, "Test/SM");

        sm.addTransition(new Transition<>(
                TestState.A,
                TestState.B,
                () -> true,
                1
        ));

        sm.addTransition(new Transition<>(
                TestState.A,
                TestState.A,
                () -> true,
                100
        ));

        sm.update();

        assertEquals(TestState.A, sm.getState());
    }
}
