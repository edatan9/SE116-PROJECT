import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.List;

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



class FSM implements interFSM {
    private Set<String> states = new HashSet<>();
    private Set<String> symbols = new HashSet<>();
    private String initialState;
    private String currentState;
    private Set<String> finalStates = new HashSet<>();
    private Map<Pair<String, String>, String> transitions = new HashMap<>();

    public boolean resetFSM() {
        if (initialState == null) return false;
        currentState = initialState;
        return true;
    }

    public boolean stepFSM(String inputSymbol) {
        Pair<String, String> key = new Pair<>(currentState, inputSymbol);
        if (!transitions.containsKey(key)) return false;
        currentState = transitions.get(key);
        return true;
    }

    public ArrayList<String> traceFSM(String inputLine) {
        ArrayList<String> visitedStates = new ArrayList<>();
        resetFSM();
        visitedStates.add(currentState);
        for (char c : inputLine.toCharArray()) {
            String sym = String.valueOf(c);
            if (!stepFSM(sym)) {
                break;
            }
            visitedStates.add(currentState);
        }
        return visitedStates;
    }

    public boolean saveFSM(String filename) {
   
        return true;
    }

    public boolean loadFSM(String filename) {
       
        return true;
    }

    @Override
    public void clear() {
    symbols.clear();
    states.clear();
    transitions.clear();
    finalStates.clear();
    initialState = null;
    currentState = null;
   }
    
    @Override
    public boolean addTransition(String symbol, String fromState, String toState) {
       symbol = symbol.toUpperCase();
       fromState = fromState.toUpperCase();
       toState = toState.toUpperCase();

    if (!symbols.contains(symbol) || !states.contains(fromState) || !states.contains(toState)) {
        return false;
    }

    Pair<String, String> key = new Pair<>(fromState, symbol);
        
    transitions.put(key, toState);
    return true;
}


}
    
    
    public boolean addState(String state) {
        if (state == null || state.isBlank()) {
            return false;
        }
        state=state.toUpperCase();
       if (!state.matches("[A-Z0-9]+")) { 
        return false;
    }

         return states.add(state);
    }

    public boolean addSymbol(String symbol) {
        if(symbol==null || symbol.isBlank()) {
            return false;
        } else if(symbol.matches("^[a-zA-Z0-9]+$")) {
            return false;
        }
      symbols.add(symbol);
        return true;
    }
}
@Override
public boolean setCurrentState(String state) {
    if (!states.contains(state)) return false;
    currentState = state.toUpperCase();
    return true;
}

@Override
public boolean addNextState(String state) {
    if (!states.contains(state)) return false;
    return finalStates.add(state.toUpperCase());
}
//Transition class
public class Transition {
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
public class FSMCommandHandler {

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
class FSMTest {
    public static void main(String[] args) {
        FSM fsm = new FSM();

        // SYMBOLS: 0, 1, 2, 3
        fsm.addSymbol("0");
        fsm.addSymbol("1");
        fsm.addSymbol("2");
        fsm.addSymbol("3");

        // STATES: Q0, Q1, Q2
        fsm.addState("Q0");
        fsm.addState("Q1");
        fsm.addState("Q2");

        // INITIAL STATE: Q0
        fsm.setCurrentState("Q0");

        // FINAL STATE: Q2
        fsm.addNextState("Q2");

        // TRANSITIONS
        fsm.addTransition("0", "Q0", "Q0");
        fsm.addTransition("0", "Q1", "Q1");
        fsm.addTransition("0", "Q2", "Q2");
        fsm.addTransition("1", "Q0", "Q1");
        fsm.addTransition("1", "Q1", "Q2");
        fsm.addTransition("1", "Q2", "Q0");
        fsm.addTransition("2", "Q0", "Q2");
        fsm.addTransition("2", "Q1", "Q0");
        fsm.addTransition("2", "Q2", "Q1");
        fsm.addTransition("3", "Q0", "Q0");
        fsm.addTransition("3", "Q1", "Q1");
        fsm.addTransition("3", "Q2", "Q2");

        // EXECUTE
        System.out.println("Trace FSM for input '123':");
        System.out.println(fsm.traceFSM("123"));
    }
}

