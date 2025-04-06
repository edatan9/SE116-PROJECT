import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;


interface interFSM {
    boolean addSymbol(String symbol);
    boolean addState(String state);
    boolean setCurrentState(String state);
    boolean addNextState(String state);
    boolean addTransition(String symbol, String fromState, String toState);

    ArrayList<String> execute(String input);
    void clear();

    Set<String> getSymbols();
    Set<String> getStates();
    String getCurrentState();
    Set<String> getNextState();
    Map<Pair<String, String>, String> transitions;
}

class FSM implements interFSM {
    private Set<String> states = new HashSet<>();
    private Set<String> symbols = new HashSet<>();
    private String currentState;
    private Set<String> finalStates = new HashSet<>();

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
        this.currentState = currentState;
        this.inputSymbol = inputSymbol;
        this.nextState = nextState;
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
