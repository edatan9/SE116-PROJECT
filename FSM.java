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
    Map<Pair>
}

class FSM implements interFSM {
    public boolean addState(String state) {
        if (state == null || state.isBlank()) {
            return false;
        }
        state=state.toUpperCase();
        else if (!state.matches()) {

        }
    }

    public boolean addSymbol(String symbol) {
        if(symbol==null || symbol.isBlank()) {
            return false;
        } else if(symbol.matches("[a-zA-Z0-9]")) {
            return false;
        }
      symbols.add(symbol);
        return true;
    }
}
