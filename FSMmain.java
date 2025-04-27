import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

// custom exceptions
class InvalidSymbolException extends Exception {
    public InvalidSymbolException(String msg) { super(msg); }
}

class InvalidStateException extends Exception {
    public InvalidStateException(String msg) { super(msg); }
}

class TransitionException extends Exception {
    public TransitionException(String msg) { super(msg); }
}

class InvalidInputException extends Exception{
    public InvalidInputException(String message) {
        super(message);
    }
}

class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
class InvalidFileNameException extends Exception {
    public InvalidFileNameException(String message) {
        super(message);
    }
}
class InvalidFilePathException extends Exception {
    public InvalidFilePathException(String message) {
        super(message);
    }
}

class FileOperationException extends Exception {
    public FileOperationException(String message) {
        super(message);
    }
}

interface InterFSM {
    boolean addSymbol(String symbol);
    boolean addState(String state);
    boolean setInitialState(String state);
    boolean setCurrentState(String state);
    boolean addFinalState(String state);
    boolean addTransition(String symbol, String fromState, String toState);
    boolean addNextState(String state);
    String getCurrentState();
    List<String> execute(String input);
    void clear();
    List<String> traceFSM(String input);

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
     @Override
     public boolean setCurrentState(String state) {
         state = state.toUpperCase();
         if (!states.contains(state)) {
             System.out.println("Error: state " + state + " was not declared.");
             return false;
         }
         currentState = state;
         return true;
     }
     @Override
     public boolean addNextState(String state) {
         return addFinalState(state); 
     }
     @Override
     public List<String> traceFSM(String input) {
         return execute(input);
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
//FSMCommandHandler class
class FSMCommandHandler {

    private InterFSM fsm;

    public FSMCommandHandler(FSM fsm) {
        this.fsm = fsm;
    }

    //setters and getters
    public InterFSM getFSM() {
        return fsm;
    }

    public void setFSM(InterFSM fsm) {
        this.fsm = fsm;
    }

    public void handleSymbolsCommand(String[] tokens) throws InvalidSymbolException {
        for (String symbol : tokens) {
            if (!symbol.matches("[a-zA-Z0-9]")) {
                throw new InvalidSymbolException("Invalid symbol: " + symbol);
            }
            fsm.addSymbol(symbol.toUpperCase());
        }
    }

    public void handleStatesCommand(String[] tokens) throws InvalidStateException {
        for (String state : tokens) {
            if (!state.matches("[a-zA-Z0-9]+")) {
                throw new InvalidStateException("Invalid state: " + state);
            }
            fsm.addState(state.toUpperCase());
        }
    }

    public void handleInitialStateCommand(String state) throws InvalidStateException {
        if (!fsm.setInitialState(state)) {
            throw new InvalidStateException("Initial state must be a declared state: " + state);
        }
    }

    public void handleFinalStatesCommand(String[] states) throws InvalidStateException {
        for (String state : states) {
            if (!fsm.addFinalState(state)) {
                throw new InvalidStateException("Final state must be a declared state: " + state);
            }
        }
    }

    public void handleTransitionsCommand(String[] transitionStrings) throws TransitionException {
        for (String transition : transitionStrings) {
            String[] parts = transition.trim().split("\\s+");
            if (parts.length != 3) {
                throw new TransitionException("Invalid transition format: " + transition);
            }

            String symbol = parts[0];
            String from = parts[1];
            String to = parts[2];

            if (!fsm.addTransition(symbol, from, to)) {
                throw new TransitionException("Transition invalid: " + transition);
            }
        }
    }

    public void handlePrintCommand(String filename) {
        System.out.println("SYMBOLS: " + fsm.getSymbols());
        System.out.println("STATES: " + fsm.getStates());
        System.out.println("INITIAL STATE: " + fsm.getCurrentState());
        System.out.println("FINAL STATES: " + fsm.getFinalStates());
        // İleride: Transitions map'ini yazdırmak için FSM'e getter eklersin
    }

    public String executeFSM(String input) throws InvalidInputException {
        if(input==null || input.isEmpty()) {
            throw new InvalidInputException("Input cannot be null or empty");
        }
        ArrayList<String> trace = (ArrayList<String>) fsm.traceFSM(input);
        StringBuilder result = new StringBuilder();
        for (String state : trace) {
            result.append(state).append(" ");
        }
        String finalState = trace.get(trace.size() - 1);
        result.append(fsm.getFinalStates().contains(finalState) ? "YES" : "NO");
        return result.toString();
    }

    public boolean isAcceptedState(String state) {
        return fsm.getFinalStates().contains(state.toUpperCase());
    }
}

class FileManager {
    private FSM fsm; //fsm sinifina ulasmak icin fsm reference'i tutuyoruz

    public FileManager(FSM fsm) {
        this.fsm = fsm;
    }

    public FSM getFsm() {
        return fsm;
    }

    public void setFsm(FSM fsm) {
        this.fsm = fsm;
    }

    public void writeToFile(String filename) throws FileOperationException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("SYMBOLS: ");
            for (String symbol : fsm.getSymbols()) {
                writer.write(" " + symbol);
            }
            writer.write(";");
            writer.newLine();

            writer.write("STATES: ");
            for (String state : fsm.getStates()) {
                writer.write(" " + state);
            }
            writer.write(";");
            writer.newLine();

            writer.write("INITIAL STATE: " + fsm.getCurrentState() + ";");
            writer.newLine();

            writer.write("FINAL-STATES: ");
            for (String finalState : fsm.getFinalStates()) {
                writer.write(" " + finalState);
            }
            writer.write(";");
            writer.newLine();

            writer.write("TRANSITIONS: ");
            for (Map.Entry<Pair<String, String>, String> entry : fsm.getTransitions().entrySet()) {
                Pair<String, String> key = entry.getKey();
                String symbol = key.getFirst();
                String fromState = key.getSecond();
                String toState = entry.getValue();

                writer.write("TRANSITIONS " + symbol + " " + fromState + " " + toState + ";");
                writer.newLine();
            }

        } catch (IOException e) {
            throw new FileOperationException("Error with writing the file: " + e.getMessage());
        }
    }

    public void readToFile(String filename) throws FileOperationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> allLines = new ArrayList<>();
            String line;

            // Tüm satırları listeye alıyoruz
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }

            // Sonra satırları işliyoruz
            StringBuilder command = new StringBuilder();
            for (String currentLine : allLines) {
                command.append(currentLine).append(" ");
                if (currentLine.contains(";")) {
                    String fullCommand = command.toString().trim();
                    System.out.println("Processing: " + fullCommand);
                    // Gerekli işlemi yap
                    command.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new FileOperationException("Error with reading file: " + e.getMessage());
        }
    }
}

    class Logger {
            private static BufferedWriter logWriter = null;
            private static String currentLogFile = null;

            public static String startLogging(String filename) {
                try {
                    // If already logging, close current log file
                    if (logWriter != null) {
                        stopLogging();
                    }

                    // Create new log file or overwrite existing one
                    logWriter = new BufferedWriter(new FileWriter(filename));
                    currentLogFile = filename;
                    return "Started logging to " + filename;
                } catch (IOException e) {
                    return "Error: Could not start logging to " + filename + " - " + e.getMessage();
                }
            }

            public static String stopLogging() {
                if (logWriter != null) {
                    try {
                        logWriter.close();
                        logWriter = null;
                        currentLogFile = null;
                        return "STOPPED LOGGING";
                    } catch (IOException e) {
                        return "Error while closing log file: " + e.getMessage();
                    }
                } else {
                    return "LOGGING was not enabled";
                }
            }

            public static String log(String command, String response) {
                if (logWriter == null) {
                    return null; // Not logging, no error
                }

                try {
                    logWriter.write("> " + command);
                    logWriter.newLine();
                    logWriter.write(response);
                    logWriter.newLine();
                    logWriter.flush(); // Ensure content is written immediately
                    return null;
                } catch (IOException e) {
                    return "Error writing to log file: " + e.getMessage();
                }
            }

            public static boolean isLoggingEnabled() {
                return logWriter != null;
            }

            public static String getCurrentLogFile() {
                return currentLogFile;
            }
        }


class Serializer implements Serializable {
    private static final long serialVersionUID = 1L;

    public void serializeFSM(FSM fsm, String filename) throws FileOperationException, InvalidFileNameException, InvalidFilePathException {
        // Validate file name
        if (filename == null || filename.trim().isEmpty()) {
            throw new InvalidFileNameException("File name cannot be null or empty");
        }

        // Check if file name contains invalid characters
        if (filename.matches(".*[\\\\/:*?\"<>|].*")) {
            throw new InvalidFileNameException("File name contains invalid characters");
        }

        try {
            // Validate file path
            File file = new File(filename);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                throw new InvalidFilePathException("Directory path does not exist: " + file.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                // Store the FSM's state instead of the FSM itself
                SerializableFSMState state = new SerializableFSMState(fsm);
                oos.writeObject(state);
            } catch (IOException e) {
                throw new FileOperationException("Error serializing FSM to file '" + filename + "': " + e.getMessage());
            }
        } catch (SecurityException e) {
            throw new FileOperationException("Security violation when accessing file '" + filename + "': " + e.getMessage());
        }
    }

    public FSM deserializeFSM(String filename) throws FileOperationException, InvalidFileNameException,
            InvalidFilePathException, InvalidFileFormatException {
        // Validate file name
        if (filename == null || filename.trim().isEmpty()) {
            throw new InvalidFileNameException("File name cannot be null or empty");
        }

        // Check if file name contains invalid characters
        if (filename.matches(".*[\\\\/:*?\"<>|].*")) {
            throw new InvalidFileNameException("File name contains invalid characters");
        }

        try {
            File file = new File(filename);

            // Check if file exists
            if (!file.exists()) {
                throw new InvalidFilePathException("File does not exist: " + filename);
            }

            // Check if file is readable
            if (!file.canRead()) {
                throw new FileOperationException("Cannot read file: " + filename);
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof SerializableFSMState) {
                    SerializableFSMState state = (SerializableFSMState) obj;
                    try {
                        return state.toFSM();
                    } catch (InvalidSymbolException | InvalidStateException | TransitionException e) {
                        throw new InvalidFileFormatException("File contains invalid FSM data: " + e.getMessage());
                    }
                } else {
                    throw new InvalidFileFormatException("File '" + filename + "' does not contain a valid FSM object");
                }
            } catch (InvalidClassException e) {
                throw new InvalidFileFormatException("Incompatible FSM version in file '" + filename + "'");
            } catch (ClassNotFoundException e) {
                throw new InvalidFileFormatException("Missing class definition when deserializing from '" + filename + "'");
            } catch (IOException e) {
                throw new FileOperationException("Error reading FSM from file '" + filename + "': " + e.getMessage());
            }
        } catch (SecurityException e) {
            throw new FileOperationException("Security violation when accessing file '" + filename + "': " + e.getMessage());
        }
    }

    public static class SerializableFSMState implements Serializable {
        private static final long serialVersionUID = 1L;

        private Set<String> symbols;
        private Set<String> states;
        private Set<String> finalStates;
        private String initialState;
        private Map<SerializablePair<String, String>, String> transitions;

        public SerializableFSMState(FSM fsm) {
            this.symbols = new HashSet<>(fsm.getSymbols());
            this.states = new HashSet<>(fsm.getStates());
            this.finalStates = new HashSet<>(fsm.getFinalStates());
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

        public FSM toFSM() throws InvalidSymbolException, InvalidStateException, TransitionException {
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
                String symbol = entry.getKey().getFirst();
                String currentState = entry.getKey().getSecond();
                String nextState = entry.getValue();

                // Verify symbol, current state and next state are valid before adding transition
                if (!symbols.contains(symbol)) {
                    throw new InvalidSymbolException("Invalid symbol in serialized FSM: " + symbol);
                }

                if (!states.contains(currentState)) {
                    throw new InvalidStateException("Invalid current state in serialized FSM: " + currentState);
                }

                if (!states.contains(nextState)) {
                    throw new InvalidStateException("Invalid next state in serialized FSM: " + nextState);
                }

                fsm.addTransition(symbol, currentState, nextState);
            }

            return fsm;
        }

        public static class SerializablePair<F, S> implements Serializable {
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

class CommandInterpreter {
    private boolean running = true;

    public void startREPL() throws InvalidCommandException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder buffer = new StringBuilder();
        printPrompt();
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                int idx = line.indexOf(';');
                if (idx >= 0) {
                    buffer.append(line, 0, idx);
                    String command = buffer.toString().trim();
                    buffer.setLength(0);
                    processLine(command);
                    if (running) printPrompt();
                } else {
                    buffer.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            throw new InvalidCommandException("I/O error: " + e.getMessage());
        }
    }
    public void processLine(String line) throws InvalidCommandException {
        if (line.isEmpty()) return;
        List<String> tokens = tokenizeCommand(line);
        String cmd = tokens.get(0).toUpperCase();
        switch (cmd) {
            case "EXIT":
                handleExitCommand();
                break;
            case "LOAD":
                if (tokens.size() < 2) throw new InvalidCommandException("LOAD requires filename");
                handleLoadCommand(tokens.get(1));
                break;
            case "EXECUTE":
                if (tokens.size() < 2) throw new InvalidCommandException("EXECUTE requires input string");
                handleExecute(tokens.get(1));
                break;
            case "LOG":
                handleLogging(tokens.size() > 1 ? tokens.get(1) : null);
                break;
            case "CLEAR":
                // TODO: clear FSM data
                System.out.println("CLEARED");
                break;
            default:
                throw new InvalidCommandException("Invalid command: " + cmd);
        }
    }
    public List<String> tokenizeCommand(String input) {
        List<String> parts = new ArrayList<>();
        for (String tok : input.trim().split("\\s+")) {
            if (!tok.isEmpty()) parts.add(tok);
        }
        return parts;
    }

    void handleExitCommand() {
        System.out.println("TERMINATED BY USER");
        running = false;
    }
    void handleLoadCommand(String filename) {
        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder buf = new StringBuilder();
            while ((line = file.readLine()) != null) {
                int idx = line.indexOf(';');
                if (idx >= 0) {
                    buf.append(line, 0, idx);
                    processLine(buf.toString().trim());
                    buf.setLength(0);
                } else {
                    buf.append(line).append(" ");
                }
            }
        } catch (IOException | InvalidCommandException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    void handleExecute(String input) {
        // FR14
        System.out.println("EXECUTED: " + input);
        // TODO: integrate CommandProcessor
    }
    void handleLogging(String filename) {
        if (filename == null) {
            System.out.println("STOPPED LOGGING");
        } else {
            System.out.println("LOGGING to " + filename);
            // TODO: open log file
        }
    }

    void printPrompt() {
        System.out.print("? ");
    }
}

class CommandProcessor {
    private FSM fsm;
    private FSMCommandHandler handler;
    private FileManager fileManager;
    private Serializer serializer;

    CommandProcessor() {
        this.fsm         = new FSM();
        this.handler     = new FSMCommandHandler(fsm);
        this.fileManager = new FileManager(fsm);
        this.serializer  = new Serializer();
    }

    String processCommand(List<String> tokens) throws InvalidCommandException {
        if (tokens.isEmpty()) {
            throw new InvalidCommandException("No command provided");
        }
        String cmd = tokens.get(0).toUpperCase();
        try {
            switch (cmd) {
                case "SYMBOLS":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getSymbols().toString();
                    } else {
                        String[] syms = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleSymbolsCommand(syms);
                        return null;
                    }

                case "STATES":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getStates().toString();
                    } else {
                        String[] sts = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleStatesCommand(sts);
                        return null;
                    }

                case "INITIAL-STATE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("INITIAL-STATE requires one state");
                    }
                    handler.handleInitialStateCommand(tokens.get(1));
                    return null;

                case "FINAL-STATES":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getFinalStates().toString();
                    } else {
                        String[] fs = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleFinalStatesCommand(fs);
                        return null;
                    }

                case "TRANSITIONS":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getTransitions().toString();
                    } else {
                        String raw = String.join(" ", tokens.subList(1, tokens.size()));
                        String[] parts = raw.split("\\s*,\\s*");
                        handler.handleTransitionsCommand(parts);
                        return null;
                    }

                case "PRINT":
                    if (tokens.size() == 1) {
                        handler.handlePrintCommand(null);
                    } else {
                        fileManager.writeToFile(tokens.get(1));
                    }
                    return null;

                case "COMPILE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("COMPILE requires filename");
                    }
                    serializer.serializeFSM(fsm, tokens.get(1));
                    return "Compile successful";

                case "LOAD":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("LOAD requires filename");
                    }
                    String fn = tokens.get(1);
                    if (fn.toLowerCase().endsWith(".fs")) {
                        FSM loaded = serializer.deserializeFSM(fn);
                        this.fsm = loaded;
                        this.handler = new FSMCommandHandler(fsm);
                        this.fileManager = new FileManager(fsm);
                    } else {
                        fileManager.readToFile(fn);
                    }
                    return null;

                case "EXECUTE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("EXECUTE requires input string");
                    }
                    return handler.executeFSM(tokens.get(1));

                case "CLEAR":
                    fsm.clear();
                    return "CLEARED";

                case "LOG":
                    if (tokens.size() == 1) {
                        return Logger.stopLogging();
                    } else {
                        return Logger.startLogging(tokens.get(1));
                    }

                default:
                    throw new InvalidCommandException("Invalid command: " + cmd);
            }
        } catch (Exception e) {
            // Altyapıdan gelen tüm hataları tek tip olarak sarmala
            throw new InvalidCommandException(e.getMessage());
        }
    }
}

class InvalidCommandException extends Exception {
    InvalidCommandException(String message) {
        super(message);
    }
}

    public class FSMmain {
        private static final String VERSION = "1.0";  // TODO: replace with your Git version identifier

        public static void main(String[] args) {
            // FR1: print version and current date/time
            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm"));
            System.out.println("FSM DESIGNER " + VERSION + " " + now);

            CommandInterpreter interpreter = new CommandInterpreter();

            try {
                // FR15: if a filename was passed on the command line, load it first
                if (args.length > 0) {
                    System.out.println("Loading commands from file: " + args[0]);
                    interpreter.handleLoadCommand(args[0]);
                }
                // then start interactive mode
                interpreter.startREPL();

            } catch (InvalidCommandException e) {
                System.err.println("Error: " + e.getMessage());

        }
    }}
