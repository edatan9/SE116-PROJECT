import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.io.*;

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

public class FileManager {
    public void writeToTextFile(FSM fsm, String fileName) throws {
        try(BufferedWriter bw=new BufferedWriter(new FileWriter(fileName))) {
            bw.write("SYMBOLS");
            for(String symbol : fsm.getSymbols()) {
                bw.write(" " + symbol);
            }

            bw.write(";\n");

            bw.write("STATES");
            for(String state : fsm.getStates()) {
                bw.write(" " + state);
            }

            bw.write(";\n");

            bw.write("INITIAL-STATE" + fsm.getInitialState() + ";\n");

            bw.write("FINAL-STATES");
            for(String finalState : fsm.getFinalStates()) {
                bw.write(" " + finalState);
            }

            bw.write("TRANSITIONS");
            for (Transition transition : fsm.getTransitions()){
                bw.write(" " + transition.getSymbol()) + " " + transition.getCurrentState() + " " + transition.getNextState() + ", ");
            }

            bw.write(";\n");
        } catch(IOException e) {
            System.err.println("There is something gone wrong when writing the file: " + e.getMessage());
        }
    }

    public List<String> readCommandsFromFile(String filename) throws FileOperationException {
        List<String> commands = new ArrayList<>();
        StringBuilder currentCommand = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Handle comment lines
                if (line.trim().startsWith(";")) {
                    continue;
                }

                // Process the line character by character
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    if (c == ';') {
                        // End of command
                        currentCommand.append(';');
                        commands.add(currentCommand.toString().trim());
                        currentCommand = new StringBuilder();

                        // Skip the rest of the line (comments)
                        break;
                    } else {
                        currentCommand.append(c);
                    }
                }

                // If no semicolon was found, add a space to continue to next line
                if (currentCommand.length() > 0 && !line.contains(";")) {
                    currentCommand.append(" ");
                }
            }

            // Check if there's a command without semicolon at the end of file
            if (currentCommand.length() > 0) {
                throw new FileOperationException("Command at the end of file missing semicolon: " + currentCommand);
            }

        } catch (IOException e) {
            throw new FileOperationException("Error reading file " + filename + ": " + e.getMessage());
        }

        return commands;
    }
}



