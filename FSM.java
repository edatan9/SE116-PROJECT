import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashSet;

 interface InterFSM {
    boolean addSymbol(String symbol);
    boolean addState(String state);
    boolean setInitialState(String state);

    boolean addFinalState(String state);
    boolean addTransition(String symbol, String fromState, String toState);

    String getCurrentState();
    List<String> execute(String input);
    void clear();

    Set<String> getSymbols();
    Set<String> getStates();
    Set<String> getFinalStates();
    Map<Pair<String, String>, String> getTransitions();
}



 class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}


 class FSM implements InterFSM {
    private Set<String> symbols;
    private Set<String> states;
    private Set<String> finalStates;
    private String initialState;
    private String currentState;
    private Map<Pair<String, String>, String> transitions;

    public FSM() {
        symbols = new HashSet<>();
        states = new LinkedHashSet<>();  // Sıralı tutması için
        finalStates = new HashSet<>();
        transitions = new HashMap<>();
        initialState = null;
        currentState = null;
    }

    @Override
    public boolean addSymbol(String symbol) {
        if (symbol == null || !symbol.matches("[a-zA-Z0-9]")) {
            System.out.println("Invalid symbol: " + symbol);
            return false;
        }
        symbol = symbol.toUpperCase();
        if (!symbols.add(symbol)) {
            System.out.println("Warning: symbol " + symbol + " already declared.");
            return false;
        }
        return true;
    }

    @Override
    public boolean addState(String state) {
        if (state == null || !state.matches("[a-zA-Z0-9]+")) {
            System.out.println("Invalid state: " + state);
            return false;
        }
        state = state.toUpperCase();
        boolean added = states.add(state);
        if (!added) {
            System.out.println("Warning: state " + state + " already declared.");
        } else if (initialState == null) {
            initialState = state;
            currentState = state;
        }
        return added;
    }

    @Override
    public boolean setInitialState(String state) {
        state = state.toUpperCase();
        if (!states.contains(state)) {
            System.out.println("Warning: " + state + " was not previously declared as a state.");
            states.add(state);  // Otomatik ekle
        }
        initialState = state;
        currentState = state;
        return true;
    }

    @Override
    public boolean addFinalState(String state) {
        state = state.toUpperCase();
        if (!states.contains(state)) {
            System.out.println("Warning: " + state + " was not previously declared as a state.");
            states.add(state);
        }
        if (!finalStates.add(state)) {
            System.out.println("Warning: " + state + " was already a final state.");
            return false;
        }
        return true;
    }
@Override
 public boolean addTransition(String symbol, String fromState, String toState) {
     symbol = symbol.toUpperCase();
     fromState = fromState.toUpperCase();
     toState = toState.toUpperCase();

     if (!symbols.contains(symbol)) {
         System.out.println("Error: invalid symbol " + symbol);
         return false;
     }

     if (!states.contains(fromState)) {
         System.out.println("Error: invalid state " + fromState);
         return false;
     }

     if (!states.contains(toState)) {
         System.out.println("Error: invalid state " + toState);
         return false;
     }

     Pair<String, String> key = new Pair<>(symbol, fromState);
     if (transitions.containsKey(key)) {
         System.out.println("Warning: transition already exists for <" + symbol + "," + fromState + ">. It will be overridden.");
     }

     transitions.put(key, toState);
     return true;
 }

     @Override
     public String getCurrentState() {
         return "";
     }

     @Override
     public List<String> execute(String input) {
         return List.of();
     }

     @Override
     public void clear() {

     }

     @Override
     public Set<String> getSymbols() {
         return Set.of();
     }

     @Override
     public Set<String> getStates() {
         return Set.of();
     }

     @Override
     public Set<String> getFinalStates() {
         return Set.of();
     }

     @Override
     public Map<Pair<String, String>, String> getTransitions() {
         return Map.of();
     }
 }




 //Transition class
 class Transition {
    private String currentState;
    private char inputSymbol;
    private String nextState;

    public Transition(String currentState, char inputSymbol, String nextState) {
        this.currentState = currentState.toUpperCase();
        this.inputSymbol = inputSymbol;
        this.nextState = nextState.toUpperCase();
    }

    public String getCurrentState() {
        return currentState;
    }

    public char getInputSymbol() {
        return inputSymbol;
    }

    public String getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "(" + currentState + ", " + inputSymbol + ") → " + nextState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transition)) return false;
        Transition t = (Transition) o;
        return inputSymbol == t.inputSymbol &&
               currentState.equalsIgnoreCase(t.currentState) &&
               nextState.equalsIgnoreCase(t.nextState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentState.toLowerCase(), inputSymbol, nextState.toLowerCase());
    }
}
class State {
    private final String name;

    public State(String name) {
        this.name = name.toUpperCase(); // case-insensitive
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State))
            return false;
        State state = (State) o;
        return name.equalsIgnoreCase(state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
 class FSMCommandHandler {

    private FSM fsm;

    public FSMCommandHandler(FSM fsm) {
        this.fsm = fsm;
    }

    //setters and getters
    public FSM getFSM() {
        return fsm;
    }

    public void setFSM(FSM fsm) {
        this.fsm = fsm;
    }
    // custom exceptions
    public static class InvalidSymbolException extends Exception {
        public InvalidSymbolException(String msg) { super(msg); }
    }

    public static class InvalidStateException extends Exception {
        public InvalidStateException(String msg) { super(msg); }
    }

    public static class TransitionException extends Exception {
        public TransitionException(String msg) { super(msg); }
    }
}


