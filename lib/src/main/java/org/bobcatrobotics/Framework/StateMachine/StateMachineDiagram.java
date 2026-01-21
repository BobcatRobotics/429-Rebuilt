package org.bobcatrobotics.Framework.StateMachine;

import java.util.Collection;

public final class StateMachineDiagram {

    private StateMachineDiagram() {}

    public static <S extends Enum<S> & State>
    String generateDot(
            String name,
            S initialState,
            Collection<Transition<S>> transitions
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph ").append(name).append(" {\n");
        sb.append("  rankdir=LR;\n");
        sb.append("  node [shape=circle];\n\n");

        // Initial state marker
        sb.append("  __start [shape=point];\n");
        sb.append("  __start -> ").append(initialState.name()).append(";\n\n");

        for (Transition<S> t : transitions) {
            sb.append("  ")
              .append(t.getFrom().name())
              .append(" -> ")
              .append(t.getTo().name())
              .append(" [label=\"p=")
              .append(t.getPriority())
              .append("\"];\n");
        }

        sb.append("}\n");
        return sb.toString();
    }
}
