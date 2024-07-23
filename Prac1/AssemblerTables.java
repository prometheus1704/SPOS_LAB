import java.io.*;
import java.util.*;

class AssemblerTables {
    public static void main(String[] args) throws Exception {
        List<String> code = readFile("program.txt");

        System.out.println("Assembly language program:");
        System.out.println("--------------------------");
        for (String line : code) {
            System.out.println(line);
        }

        Map<String, SymbolEntry> symbolTable = new LinkedHashMap<>();
        Map<String, LiteralEntry> literalTable = new LinkedHashMap<>();
        createTables(code, symbolTable, literalTable);

        printSymbolTable(symbolTable);
        printLiteralTable(literalTable);
    }

    private static List<String> readFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        }
        return lines;
    }

    private static void createTables(List<String> code, Map<String, SymbolEntry> symbolTable, Map<String, LiteralEntry> literalTable) {
        int LOC = 200;
        int literalAddress = 0;
        boolean programStarted = false;

        for (int i = 0; i < code.size(); i++) {
            String line = code.get(i);
            String[] parts = line.split("\\s+");
            
            if (parts[0].equals("START")) {
                LOC = Integer.parseInt(parts[1]);
                programStarted = true;
                continue;
            }

            if (!programStarted) continue;
            if (parts[0].equals("END")) break;

            if (parts[0].equals("ORIGIN")) {
                LOC = evaluateExpression(parts[1], symbolTable);
                continue;
            }

            if (parts[0].equals("EQU")) {
                int value = evaluateExpression(parts[2], symbolTable);
                symbolTable.put(parts[0], new SymbolEntry(parts[0], value, 0));
                continue;
            }

            if (parts[0].equals("LTORG")) {
                for (LiteralEntry entry : literalTable.values()) {
                    if (entry.address == -1) {
                        entry.address = LOC;
                        LOC++;
                    }
                }
                continue;
            }

            if (!isInstruction(parts[0]) && !parts[0].equals("DS")) {
                symbolTable.put(parts[0], new SymbolEntry(parts[0], LOC, getLength(parts)));
            }

            for (String part : parts) {
                if (part.startsWith("='") && part.endsWith("'")) {
                    String literal = part;
                    if (!literalTable.containsKey(literal)) {
                        literalTable.put(literal, new LiteralEntry(literal, -1));
                    }
                }
            }

            LOC += getLength(parts);
        }

        // Assign addresses to remaining literals
        for (LiteralEntry entry : literalTable.values()) {
            if (entry.address == -1) {
                entry.address = LOC;
                LOC++;
            }
        }
    }

    private static boolean isInstruction(String opcode) {
        return opcode.equals("MOVER") || opcode.equals("MOVEM") || opcode.equals("ADD") || 
               opcode.equals("SUB") || opcode.equals("MULT") || opcode.equals("BC") || 
               opcode.equals("STOP");
    }

    private static int getLength(String[] parts) {
        if (parts[0].equals("DS")) {
            return Integer.parseInt(parts[1]);
        }
        return 1; // Assume all other instructions take 1 word
    }

    private static int evaluateExpression(String expression, Map<String, SymbolEntry> symbolTable) {
        String[] parts = expression.split("\\+");
        int result = 0;
        for (String part : parts) {
            if (symbolTable.containsKey(part)) {
                result += symbolTable.get(part).address;
            } else {
                result += Integer.parseInt(part);
            }
        }
        return result;
    }

    private static void printSymbolTable(Map<String, SymbolEntry> symbolTable) {
        System.out.println("\nSYMBOL TABLE:");
        System.out.println("-------------------------------------------");
        System.out.println("SYMBOL\tADDRESS\tLENGTH");
        System.out.println("-------------------------------------------");
        for (SymbolEntry entry : symbolTable.values()) {
            System.out.printf("%s\t%d\t%d%n", entry.symbol, entry.address, entry.length);
        }
    }

    private static void printLiteralTable(Map<String, LiteralEntry> literalTable) {
        System.out.println("\nLITERAL TABLE:");
        System.out.println("-------------------------------------------");
        System.out.println("LITERAL\tADDRESS");
        System.out.println("-------------------------------------------");
        for (LiteralEntry entry : literalTable.values()) {
            System.out.printf("%s\t%d%n", entry.literal, entry.address);
        }
    }
}

class SymbolEntry {
    String symbol;
    int address;
    int length;

    public SymbolEntry(String symbol, int address, int length) {
        this.symbol = symbol;
        this.address = address;
        this.length = length;
    }
}

class LiteralEntry {
    String literal;
    int address;

    public LiteralEntry(String literal, int address) {
        this.literal = literal;
        this.address = address;
    }
}
