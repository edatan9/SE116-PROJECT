import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.util.Properties;


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

 class InvalidCommandException extends Exception {
      InvalidCommandException(String message) {
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
    public boolean addSymbol(String symbol)  {
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
             String existingNextState = transitions.get(key);
             if (!existingNextState.equals(toState)) {
                 System.out.println("Warning: Transition <" + symbol + "," + fromState + "> overridden (" + existingNextState + " → " + toState + ")");
             }
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
        List<String> errorMessages = new ArrayList<>();

        for (String symbol : tokens) {
            if (symbol.length() != 1) {
                errorMessages.add(symbol + " is not allowed as a symbol, length must be 1.");
                continue;
            }

            if (!symbol.matches("[a-zA-Z0-9]")) {
                errorMessages.add(symbol + " is not allowed, not alphanumeric.");
                continue;
            }

            fsm.addSymbol(symbol.toUpperCase());
        }

        if (!errorMessages.isEmpty()) {
            throw new InvalidSymbolException(String.join(" ", errorMessages));
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
        System.out.println("TRANSITIONS:");
        for (Map.Entry<Pair<String, String>, String> entry : fsm.getTransitions().entrySet()) {
            Pair<String, String> key = entry.getKey();
            String symbol = key.getFirst();
            String from = key.getSecond();
            String to = entry.getValue();
            System.out.println(symbol + " " + from + " " + to);
        }
    }
    public String executeFSM(String input) throws InvalidInputException {
        if(input==null || input.isEmpty()) {
            throw new InvalidInputException("Input cannot be null or empty");
        }

        // FSM'in başlangıç durumu kontrol edilmeli
        if (fsm.getCurrentState() == null) {
            return "Error: FSM is not initialized properly.";
        }

        ArrayList<String> trace = (ArrayList<String>) fsm.traceFSM(input);

        // Trace boş veya null olabilir, bu durumlar kontrol edilmeli
        if (trace == null || trace.isEmpty()) {
            return "Error: Execution failed.";
        }

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
            String line;
            StringBuilder command = new StringBuilder();
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                command.append(line).append(" ");

                if (line.contains(";")) {
                    String fullCommand = command.toString().trim();
                    command.setLength(0); // StringBuilder'ı temizle

                    try {
                        System.out.println("Processing (line " + lineNumber + "): " + fullCommand);

                        // Komutu analiz et ve FSM'de uygula
                        CommandInterpreter interpreter = new CommandInterpreter();
                        interpreter.processLine(fullCommand);

                    } catch (InvalidCommandException e) {
                        System.err.println("Line " + lineNumber + ": " + e.getMessage());
                    }
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

                    File file = new File(filename);
                    if (file.exists() && !file.canWrite()) {
                        return "Error: File exists but cannot be written: " + filename; // PDF uyumlu hata mesajı
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
    private static final long serialVersionUID = 1;

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
    private CommandProcessor processor;

    public CommandInterpreter() {
        this.processor=new CommandProcessor();
    }

    private void printPrompt() {
        System.out.print("? ");
    }
    public void startREPL() throws InvalidCommandException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder buffer = new StringBuilder();
        int lineNumber = 0;
        printPrompt();
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                lineNumber++;  // Her satır için numarayı artır

                if (line.trim().isEmpty()) {
                    printPrompt();
                    continue;
                }

                // Satırda noktalı virgül var mı kontrol et
                int idx = line.indexOf(';');

                if (idx >= 0) {
                    // Noktalı virgüle kadar olan kısmı tampona ekle
                    buffer.append(line, 0, idx);
                    String command = buffer.toString().trim();

                    // Komutu işle
                    if (!command.isEmpty()) {
                        processLine(command);
                    }

                    // Tamponu temizle
                    buffer.setLength(0);

                    // Noktalı virgülden sonraki kısmı kontrol et, varsa tampona ekle
                    if (idx < line.length() - 1) {
                        buffer.append(line.substring(idx + 1));
                    }

                    if (running) printPrompt();
                } else {
                    // Noktalı virgül yok, hata mesajı göster
                    System.out.println("Line " + lineNumber + ": semicolon expected");

                    // Girdiyi tampona ekle
                    buffer.append(line).append(" ");

                    if (running) printPrompt();
                }
            }
        } catch (IOException e) {
            throw new InvalidCommandException("I/O error: " + e.getMessage());
        } finally {
            // Uygulama sonlanırken Logger'ı kapat
            if (Logger.isLoggingEnabled()) {
                Logger.stopLogging();
            }
        }
    }
    public void processLine(String line) throws InvalidCommandException {
        if (line == null || line.isEmpty()) return;

        // Komut adlarını içeren bir liste
        List<String> commandNames = Arrays.asList("SYMBOLS", "STATES", "INITIAL-STATE", "FINAL-STATES",
                "TRANSITIONS", "PRINT", "COMPILE", "LOAD", "EXECUTE",
                "CLEAR", "LOG", "EXIT");

        // Satırı tokenlara ayır
        List<String> tokens = tokenizeCommand(line);
        if (tokens == null || tokens.isEmpty()) return;

        // Tüm olası komut başlangıçlarını bul
        List<Integer> commandStartIndices = new ArrayList<>();
        commandStartIndices.add(0); // İlk komut her zaman 0. indeksten başlar

        for (int i = 1; i < tokens.size(); i++) {
            if (commandNames.contains(tokens.get(i).toUpperCase())) {
                commandStartIndices.add(i);
            }
        }

        // Eğer komut başlangıç indeksleri listesi boşsa, return
        if (commandStartIndices.isEmpty()) return;

        // Her komutu ayrı ayrı işle
        for (int i = 0; i < commandStartIndices.size(); i++) {
            int startIndex = commandStartIndices.get(i);
            // Dizin sınırları kontrolü
            if (startIndex >= tokens.size()) continue;

            int endIndex = (i < commandStartIndices.size() - 1) ?
                    commandStartIndices.get(i + 1) : tokens.size();

            // Dizin sınırları kontrolü
            if (endIndex > tokens.size()) endIndex = tokens.size();

            List<String> command = tokens.subList(startIndex, endIndex);
            if (command.isEmpty()) continue;

            String cmd = command.get(0).toUpperCase();

            // EXIT komutu için özel işleme
            if (cmd.equals("EXIT")) {
                if (Logger.isLoggingEnabled()) {
                    Logger.stopLogging();
                }
                handleExitCommand();
                break;
            }
            // TRANSITIONS komutu için özel işleme
            else if (cmd.equals("TRANSITIONS")) {
                List<String> transitionTokens = tokenizeTransitionCommand(line.substring(line.indexOf("TRANSITIONS")));
                if (transitionTokens != null && !transitionTokens.isEmpty()) {
                    String result = processor.processCommand(transitionTokens);
                    if (result != null) {
                        System.out.println(result);
                    }
                }
            }
            // LOAD komutu için özel işleme
            else if (cmd.equals("LOAD")) {
                // LOAD komutları için özel işleme
                if (command.size() > 1) {
                    // Geçerli LOAD komutu (parametrelerle birlikte)
                    String result = processor.processCommand(command);
                    if (result != null) {
                        System.out.println(result);
                    }
                }
            }
            // Diğer tüm komutlar için standart işleme
            else {
                String result = processor.processCommand(command);
                if (result != null) {
                    System.out.println(result);
                }
            }
        }
    }

    // Normal komutlar için tokenize metodu
    private List<String> tokenizeCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        for (String tok : input.trim().split("\\s+")) {
            if (!tok.isEmpty()) tokens.add(tok);
        }
        return tokens;
    }

    // TRANSITIONS komutu için özel tokenize metodu
    private List<String> tokenizeTransitionCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        String[] parts = input.trim().split("\\s+");

        if (parts.length > 0) {
            tokens.add(parts[0]); // TRANSITIONS komutunu ekle

            // Tüm geçişleri virgülle ayrılmış kabul et
            StringBuilder transitions = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                transitions.append(parts[i]).append(" ");
            }

            // Virgülle ayrılmış geçişleri işle
            String transitionsStr = transitions.toString().trim();
            if (!transitionsStr.isEmpty()) {
                tokens.add(transitionsStr); // Transitions parametresini tek parça olarak ekle
            }
        }

        return tokens;
    }

    void handleExitCommand() {
        System.out.println("TERMINATED BY USER");
        running = false;
    }
    void handleLoadCommand(String filename) {
        int lineNumber = 0;

        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder buf = new StringBuilder();

            while ((line = file.readLine()) != null) {
                lineNumber++;
                buf.append(line).append(" ");

                if (line.contains(";")) {
                    String fullLine = buf.toString().trim();
                    int semicolonIndex = fullLine.indexOf(";");
                    String command = fullLine.substring(0, semicolonIndex).trim();
                    buf.setLength(0);

                    // Önce LOAD komutunun okuduğu komutu yazdır
                    System.out.println(command + ";");

                    // Sonra komut satırını işle ve çıktısını yazdır
                    List<String> tokens = tokenizeCommand(command);
                    if (!tokens.isEmpty()) {
                        String cmd = tokens.get(0).toUpperCase();
                        if (cmd.equals("EXIT")) {
                            handleExitCommand();
                        } else {
                            try {
                                String result = processor.processCommand(tokens);
                                if (result != null) {
                                    System.out.println(result);
                                }
                            } catch (InvalidCommandException e) {
                                System.err.println("Error in command: " + e.getMessage());
                            }
                        }
                    }
                }
                else {
                    System.out.println("Line " + lineNumber + ": semicolon expected");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
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
    private void handleLoadFromTextFile(String filename) throws InvalidCommandException {
        List<String> errorMessages = new ArrayList<>();

        try (BufferedReader file = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            StringBuilder multiLineCommand = new StringBuilder();
            boolean inMultilineCommand = false;

            while ((line = file.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Boş satırları atla
                if (line.isEmpty()) {
                    continue;
                }

                // Çok satırlı komut işleme
                if (inMultilineCommand) {
                    multiLineCommand.append(" ").append(line);
                    if (line.contains(";")) {
                        inMultilineCommand = false;
                        processFileCommand(multiLineCommand.toString(), lineNumber, errorMessages);
                        multiLineCommand.setLength(0);
                    }
                    continue;
                }

                // Noktalı virgül içermeyen satırları çok satırlı komut başlangıcı olarak işle
                if (!line.contains(";")) {
                    inMultilineCommand = true;
                    multiLineCommand.append(line);
                    continue;
                }

                // Normal komut işleme (satırda noktalı virgül var)
                processFileCommand(line, lineNumber, errorMessages);
            }

            // Dosya bitti ama hala çok satırlı komut varsa
            if (inMultilineCommand && multiLineCommand.length() > 0) {
                String errorMsg = "Line " + lineNumber + ": unclosed command, semicolon missing";
                System.out.println(errorMsg);
                errorMessages.add(errorMsg);
                // Yine de komutu işlemeye çalış
                multiLineCommand.append(";"); // Eksik noktalı virgülü ekle
                processFileCommand(multiLineCommand.toString(), lineNumber, errorMessages);
            }

        } catch (IOException e) {
            throw new InvalidCommandException("Error loading file: " + e.getMessage());
        }
    }

    // Dosyadan okunan bir komut satırını işleyen yardımcı metot
    private void processFileCommand(String commandLine, int lineNumber, List<String> errorMessages) {
        // Geçerli komut anahtar kelimelerini belirle (büyük/küçük harf duyarsız)
        Set<String> commandKeywords = new HashSet<>(Arrays.asList(
                "SYMBOLS", "STATES", "INITIAL-STATE", "FINAL-STATES", "TRANSITIONS",
                "PRINT", "COMPILE", "LOAD", "EXECUTE", "CLEAR", "LOG", "EXIT"
        ));

        // Komutları ayrıştır
        List<String> commands = splitIntoSeparateCommands(commandLine, commandKeywords);

        // Her komutu ayrı ayrı işle
        for (String command : commands) {
            command = command.trim();
            if (command.isEmpty()) continue;

            boolean hasValidSemicolon = command.endsWith(";");
            String commandWithoutSemi = hasValidSemicolon ?
                    command.substring(0, command.length() - 1).trim() : command.trim();

            System.out.println(command); // Sadece komutu yazdır

            if (!hasValidSemicolon) {
                String errorMsg = "Line " + lineNumber + ": semicolon missing in command-->" + command;
                System.out.println(errorMsg);
                errorMessages.add(errorMsg);
            }

            // Boş komut kontrolü
            if (commandWithoutSemi.isEmpty()) continue;

            try {
                List<String> tokens = tokenizeCommand(commandWithoutSemi);
                if (tokens.isEmpty()) continue;

                String cmdType = tokens.get(0).toUpperCase();

                // TRANSITIONS komutu için özel işleme
                if (cmdType.equals("TRANSITIONS")) {
                    List<String> transitionTokens = tokenizeTransitionCommand(commandWithoutSemi);
                    String result = processCommand(transitionTokens);
                    if (result != null) System.out.println(result); // Sonucu doğrudan yazdır
                }
                // LOAD komutları için nested kontrolü
                if (cmdType.equals("LOAD")) {
                    // LOAD komutları için özel işleme
                    if (tokens.size() > 1) {
                        // Geçerli LOAD komutu (parametrelerle birlikte)
                        String result = processCommand(tokens);
                        if (result != null) System.out.println(result);
                    }
                } else {
                    // LOAD dışındaki tüm komutlar için standart işleme
                    String result = processCommand(tokens);
                    if (result != null) System.out.println(result);
                }
            } catch (Exception e) {
                String errorMsg = "Line " + lineNumber + ": " + e.getMessage();
                System.out.println(errorMsg);
                errorMessages.add(errorMsg);
            }
        }
    }

    // Bir satırı ayrı komutlara bölen yardımcı metot
    private List<String> splitIntoSeparateCommands(String line, Set<String> commandKeywords) {
        List<String> commands = new ArrayList<>();
        StringBuilder currentCommand = new StringBuilder();
        String[] tokens = line.split("\\s+");
        boolean inCommand = false;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) continue;

            // Noktalı virgül içeren token kontrolü
            if (token.contains(";")) {
                // Noktalı virgülü ayır
                int semicolonIndex = token.indexOf(';');
                if (semicolonIndex > 0) {
                    // Noktalı virgülden önceki kısmı mevcut komuta ekle
                    currentCommand.append(" ").append(token.substring(0, semicolonIndex));
                }

                // Mevcut komutu tamamla
                commands.add(currentCommand.toString().trim() + ";");
                currentCommand = new StringBuilder();
                inCommand = false;

                // Noktalı virgülden sonraki kısmı kontrol et
                if (semicolonIndex < token.length() - 1) {
                    String remainder = token.substring(semicolonIndex + 1).trim();
                    if (!remainder.isEmpty()) {
                        currentCommand.append(remainder);
                        inCommand = true;
                    }
                }
            }
            // Yeni bir komut başlangıcı mı kontrol et
            else if (commandKeywords.contains(token.toUpperCase()) && inCommand && !currentCommand.toString().trim().isEmpty()) {
                // Mevcut komutu noktalı virgül olmadan tamamla
                commands.add(currentCommand.toString().trim());
                currentCommand = new StringBuilder(token);
            }
            // Normal token, mevcut komuta ekle
            else {
                if (inCommand) {
                    currentCommand.append(" ");
                } else {
                    inCommand = true;
                }
                currentCommand.append(token);
            }
        }

        // Kalan komut varsa ekle
        if (inCommand && !currentCommand.toString().trim().isEmpty()) {
            commands.add(currentCommand.toString().trim());
        }

        return commands;
    }

    // TRANSITIONS komutu için özel tokenize metodu
    private List<String> tokenizeTransitionCommand(String input) {
        List<String> tokens = new ArrayList<>();
        String[] parts = input.trim().split("\\s+");

        if (parts.length > 0) {
            tokens.add(parts[0]); // TRANSITIONS komutunu ekle

            // Tüm geçişleri virgülle ayrılmış kabul et
            StringBuilder transitions = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                transitions.append(parts[i]).append(" ");
            }

            // Virgülle ayrılmış geçişleri işle
            String transitionsStr = transitions.toString().trim();
            if (!transitionsStr.isEmpty()) {
                tokens.add(transitionsStr); // Transitions parametresini tek parça olarak ekle
            }
        }

        return tokens;
    }

    // Normal komutlar için tokenize metodu
    private List<String> tokenizeCommand(String input) {
        List<String> tokens = new ArrayList<>();
        for (String tok : input.trim().split("\\s+")) {
            if (!tok.isEmpty()) tokens.add(tok);
        }
        return tokens;
    }

   public String processCommand(List<String> tokens) throws InvalidCommandException {
        if (tokens.isEmpty()) {
            throw new InvalidCommandException("No command provided");
        }
       String cmd = tokens.get(0).toUpperCase();
       String result = null;
       String commandString = String.join(" ", tokens) + ";";
        try {
            switch (cmd) {
                case "SYMBOLS":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getSymbols().toString();
                    } else {
                        String[] syms = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleSymbolsCommand(syms);
                    }break;

                case "STATES":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getStates().toString();
                    } else {
                        String[] sts = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleStatesCommand(sts);
                    }break;

                case "INITIAL-STATE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("INITIAL-STATE requires one state");
                    }
                    handler.handleInitialStateCommand(tokens.get(1));
                    break;

                case "FINAL-STATES":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getFinalStates().toString();
                    } else {
                        String[] fs = tokens.subList(1, tokens.size())
                                .toArray(new String[0]);
                        handler.handleFinalStatesCommand(fs);
                    }break;

                case "TRANSITIONS":
                    if (tokens.size() == 1) {
                        return handler.getFSM().getTransitions().toString();
                    } else {
                        String raw = String.join(" ", tokens.subList(1, tokens.size()));
                        String[] parts = raw.split("\\s*,\\s*");
                        handler.handleTransitionsCommand(parts);
                    }break;

                case "PRINT":
                    if (tokens.size() == 1) {
                        handler.handlePrintCommand(null);
                    } else {
                        try  {
                        fileManager.writeToFile(tokens.get(1));
                        }  catch(FileOperationException e){
                          result = "Error:"+ e.getMessage();
                        }
                    }break;

                case "COMPILE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("COMPILE requires filename");
                    }
                    try {
                    serializer.serializeFSM(fsm, tokens.get(1));
                    result= "Compile successful";
                    } catch (Exception e){
                        result = "Error ="+ e.getMessage();
                    }
                    break;

                case "LOAD":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("LOAD requires filename");
                    }
                    String fn = tokens.get(1);
                    try {
                    if (fn.toLowerCase().endsWith(".fs")) {
                        FSM loaded = serializer.deserializeFSM(fn);
                        this.fsm = loaded;
                        this.handler = new FSMCommandHandler(fsm);
                        this.fileManager = new FileManager(fsm);
                    } else {
                        // Dosyadan okuma ve komutları işleme
                        handleLoadFromTextFile(fn);
                    }
                    } catch (FileOperationException | InvalidFileNameException | InvalidFilePathException | InvalidFileFormatException e){
                        result = "Error: " + e.getMessage();

                    }
                    break;

                case "EXECUTE":
                    if (tokens.size() != 2) {
                        throw new InvalidCommandException("EXECUTE requires input string");
                    }
                    result= handler.executeFSM(tokens.get(1));
                    break;

                case "CLEAR":
                    fsm.clear();
                    result= "CLEARED";
                    break;

                case "LOG":
                    if (tokens.size() == 1) {
                        result = Logger.stopLogging();
                    } else if(tokens.size()==2) {
                        result = Logger.startLogging(tokens.get(1));
                    } else {
                        throw new InvalidCommandException("LOG command requires filename");
                    }break;
                default:
                    throw new InvalidCommandException("Invalid command: " + cmd);
            }
            if (Logger.isLoggingEnabled() && !cmd.equals("LOG")) {
                String logResult = "";
                if (result != null) {
                    logResult = result;
                }
                Logger.log(commandString, logResult);
            }
            return result;
        } catch (Exception e) {
            if (Logger.isLoggingEnabled() && !cmd.equals("LOG")) {
                String originalCommand = String.join(" ", tokens) + ";";
                Logger.log(originalCommand, "Error: " + e.getMessage());
            }
            throw new InvalidCommandException(e.getMessage());
        }

    }
}
class GitVersion {
    private static final Properties GIT_PROPERTIES = new Properties();
    private static final String UNKNOWN = "unknown";

    static {
        try {
            GIT_PROPERTIES.load(GitVersion.class.getClassLoader().getResourceAsStream("git.properties"));
        } catch (IOException | NullPointerException e) {
        }
    }

    public static String getVersion() {
        String commitId = GIT_PROPERTIES.getProperty("git.commit.id.abbrev", UNKNOWN);
        return "1.0-" + commitId;
    }
}


    public class FSMmain {
        private static final String VERSION = GitVersion.getVersion();  // TODO: replace with your Git version identifier

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
    }
}
