import java.util.*;
import java.io.*;

class FileOperationException extends Exception {
    public FileOperationException(String message) {
        super(message);
    }
}

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
        states = new LinkedHashSet<>();
        finalStates = new HashSet<>();
        transitions = new HashMap<>();
        initialState = null;
        currentState = null;
    }
     @Override
     public Set<String> getSymbols() {
         return new LinkedHashSet<>(symbols); // kopya döndürüyoruz, dışardan değiştirilmesin
     }

     @Override
     public Set<String> getStates() {
         return new LinkedHashSet<>(states);
     }

     @Override
     public Set<String> getFinalStates() {
         return new LinkedHashSet<>(finalStates);
     }

     @Override
     public Map<Pair<String, String>, String> getTransitions() {
         return new HashMap<>(transitions);
     }

     @Override
     public String getCurrentState() {
         return currentState;
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
            states.add(state);
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
     public List<String> execute(String input) {
         List<String> stateSequence = new ArrayList<>();

         if (initialState == null) {
             System.out.println("Error: initial state not set.");
             return stateSequence;
         }

         String current = initialState;
         stateSequence.add(current);

         for (char ch : input.toCharArray()) {
             String symbol = String.valueOf(ch).toUpperCase();
             if (!symbols.contains(symbol)) {
                 System.out.println("Error: invalid input symbol '" + symbol + "'");
                 return stateSequence;
             }

             Pair<String, String> key = new Pair<>(symbol, current);
             if (!transitions.containsKey(key)) {
                 System.out.println("NO");
                 return stateSequence;
             }

             current = transitions.get(key);
             stateSequence.add(current);
         }

         if (finalStates.contains(current)) {
             System.out.println("YES");
         } else {
             System.out.println("NO");
         }

         return stateSequence;
     }
     @Override
     public void clear() {
         symbols.clear();
         states.clear();
         finalStates.clear();
         transitions.clear();
         initialState = null;
         currentState = null;
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



class Serializer implements Serializable{
    public void serializeFSM(FSM fsm, String filename) throws FileOperationException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            // Store the FSM's state instead of the FSM itself
            SerializableFSMState state = new SerializableFSMState(fsm);
            oos.writeObject(state);
        } catch (IOException e) {
            throw new FileOperationException("Error serializing FSM to file " + filename + ": " + e.getMessage());
        }
    }

    public FSM deserializeFSM(String filename) throws FileOperationException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof SerializableFSMState) {
                SerializableFSMState state = (SerializableFSMState) obj;
                return state.toFSM();
            } else {
                throw new FileOperationException("File " + filename + " does not contain a valid FSM object");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new FileOperationException("Error deserializing FSM from file " + filename + ": " + e.getMessage());
        }
    }

    private static class SerializableFSMState implements Serializable {
        private static final long serialVersionUID = 1L;

        private Set<String> symbols;
        private Set<String> states;
        private Set<String> finalStates;
        private String initialState;
        private Map<SerializablePair<String, String>, String> transitions;

        public SerializableFSMState(FSM fsm) {
            this.symbols = fsm.getSymbols();
            this.states = fsm.getStates();
            this.finalStates = fsm.getFinalStates();
            this.initialState = fsm.getCurrentState();

            // Convert transitions to serializable format
            this.transitions = new HashMap<>();
            Map<Pair<String, String>, String> fsmTransitions = fsm.getTransitions();
            for (Map.Entry<Pair<String, String>, String> entry : fsmTransitions.entrySet()) {
                SerializablePair<String, String> key = new SerializablePair<>(
                        entry.getKey().getFirst(), entry.getKey().getSecond());
                transitions.put(key, entry.getValue());
            }
        }

        public FSM toFSM() {
            FSM fsm = new FSM();

            // Add symbols
            for (String symbol : symbols) {
                fsm.addSymbol(symbol);
            }

            // Add states
            for (String state : states) {
                fsm.addState(state);
            }

            // Set initial state
            if (initialState != null) {
                fsm.setInitialState(initialState);
            }

            // Add final states
            for (String finalState : finalStates) {
                fsm.addFinalState(finalState);
            }

            // Add transitions
            for (Map.Entry<SerializablePair<String, String>, String> entry : transitions.entrySet()) {
                fsm.addTransition(
                        entry.getKey().getFirst(),
                        entry.getKey().getSecond(),
                        entry.getValue()
                );
            }

            return fsm;
        }

        private static class SerializablePair<F, S> implements Serializable {
            private static final long serialVersionUID = 1L;

            private final F first;
            private final S second;

            public SerializablePair(F first, S second) {
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
                if (!(o instanceof SerializablePair)) return false;
                SerializablePair<?, ?> pair = (SerializablePair<?, ?>) o;
                return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
            }

            @Override
            public int hashCode() {
                return Objects.hash(first, second);
            }
        }
    }
}



public class FSMmain {
    public static void main (String[] args){

    }
}
