import java.io.*;
import java.util.*;

class AssemblerTables {
    public static void main(String[] args) throws Exception {
        List<String> code = readFile("program.txt");

        // Display the assembly language program
        System.out.println("Assembly language program:");
        System.out.println("--------------------------");
        for (String line : code) {
            System.out.println(line);
        }

        // Create the symbol table, literal table, and pool table
        Map<String, SymbolEntry> symbolTable = new LinkedHashMap<>();
        List<LiteralEntry> literalTable = new ArrayList<>();
        List<PoolEntry> poolTable = new ArrayList<>();
        createTables(code, symbolTable, literalTable, poolTable);

        // Print the symbol table, literal table, and pool table
        printSymbolTable(symbolTable);
        printLiteralTable(literalTable);
        printPoolTable(poolTable);

        // Generate and print intermediate code
        List<String> intermediateCode = generateIntermediateCode(code, symbolTable, literalTable);
        System.out.println("\nINTERMEDIATE CODE:");
        System.out.println("-------------------------------------------");
        for (String line : intermediateCode) {
            System.out.println(line);
        }
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

    private static void createTables(List<String> code, Map<String, SymbolEntry> symbolTable,
            List<LiteralEntry> literalTable, List<PoolEntry> poolTable) {
        int LOC = 200;
        boolean programStarted = false;
        List<LiteralEntry> pendingLiterals = new ArrayList<>();
        int literalIndex = 0;
        int poolStartLiteralIndex = -1;

        for (int i = 0; i < code.size(); i++) {
            String line = code.get(i);
            String[] parts = line.split("\\s+");

            if (parts.length == 0)
                continue; // Skip empty lines

            if (parts[0].equals("START")) {
                LOC = parts.length > 1 ? Integer.parseInt(parts[1]) : LOC;
                programStarted = true;
                continue;
            }

            if (!programStarted)
                continue;
            if (parts[0].equals("END"))
                break;

            if (parts[0].equals("ORIGIN")) {
                LOC = parts.length > 1 ? evaluateExpression(parts[1], symbolTable) : LOC;
                continue;
            }

            if (parts.length > 1 && parts[1].equals("EQU")) {
                if (parts.length > 2) {
                    int value = evaluateExpression(parts[2], symbolTable);
                    symbolTable.put(parts[0], new SymbolEntry(parts[0], value));
                }
                continue;
            }

            if (parts[0].equals("LTORG") || i == code.size() - 1) { 
                if (!pendingLiterals.isEmpty()) {
                    if (poolStartLiteralIndex == -1) {
                        poolStartLiteralIndex = literalTable.size();
                    }
                    for (LiteralEntry entry : pendingLiterals) {
                        entry.address = LOC;
                        entry.index = literalIndex++;
                        literalTable.add(entry);
                        LOC++;
                    }
                    poolTable.add(new PoolEntry(poolStartLiteralIndex));
                    poolStartLiteralIndex = -1;
                    pendingLiterals.clear();
                }
                if (parts[0].equals("LTORG")) {
                    continue;
                }
            }

            if (!isInstruction(parts[0]) && !parts[0].equals("DS")) {
                symbolTable.put(parts[0], new SymbolEntry(parts[0], LOC));
            }

            for (String part : parts) {
                if (part.startsWith("='") && part.endsWith("'")) {
                    boolean exists = false;
                    for (LiteralEntry literal : pendingLiterals) {
                        if (literal.literal.equals(part)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        pendingLiterals.add(new LiteralEntry(part, -1, -1));
                    }
                }
            }

            LOC += getLength(parts);
        }

        // Handle any remaining literals at the end of the program without adding to pool table again
        if (!pendingLiterals.isEmpty()) {
            if (poolStartLiteralIndex == -1) {
                poolStartLiteralIndex = literalTable.size();
            }
            for (LiteralEntry entry : pendingLiterals) {
                entry.address = LOC;
                entry.index = literalIndex++;
                literalTable.add(entry);
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
        if (parts.length > 1 && parts[1].equals("DS")) {
            return Integer.parseInt(parts[2]);
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

    private static List<String> generateIntermediateCode(List<String> code,
            Map<String, SymbolEntry> symbolTable, List<LiteralEntry> literalTable) {
        List<String> intermediateCode = new ArrayList<>();
        Map<String, MOTEntry> motTable = initializeMotTable();
        int LOC = 200;

        for (String line : code) {
            String[] parts = line.split("\\s+");
            if (parts.length == 0) continue;

            StringBuilder codeLine = new StringBuilder();
            codeLine.append(String.format("%04d ", LOC));  // Add location counter
            
            if (parts[0].equals("START") || parts[0].equals("END") || parts[0].equals("LTORG")) {
                MOTEntry entry = motTable.get(parts[0]);
                codeLine.append(String.format("(%s, %s) ", entry.classType, entry.opcode));
                if (parts[0].equals("START") && parts.length > 1) {
                    codeLine.append("(C, ").append(parts[1]).append(")");
                }
                intermediateCode.add(codeLine.toString().trim());
                if (parts[0].equals("START")) LOC = Integer.parseInt(parts[1]);
                continue;
            }
            
            if (parts[0].equals("ORIGIN")) {
                MOTEntry entry = motTable.get(parts[0]);
                codeLine.append(String.format("(%s, %s) ", entry.classType, entry.opcode));
                codeLine.append("(C, ").append(evaluateExpression(parts[1], symbolTable)).append(")");
                intermediateCode.add(codeLine.toString().trim());
                LOC = evaluateExpression(parts[1], symbolTable);
                continue;
            }
            
            if (parts.length > 1 && parts[1].equals("EQU")) {
                MOTEntry entry = motTable.get("EQU");
                codeLine.append(String.format("(%s, %s) ", entry.classType, entry.opcode));
                codeLine.append("(S, ").append(parts[0]).append(") ");
                codeLine.append("(C, ").append(evaluateExpression(parts[2], symbolTable)).append(")");
                intermediateCode.add(codeLine.toString().trim());
                continue;
            }
            
            // Handle instructions and symbols
            if (!isInstruction(parts[0])) {
                if (parts.length > 1 && parts[1].equals("DS")) {
                    MOTEntry entry = motTable.get("DS");
                    codeLine.append(String.format("(%s, %s) ", entry.classType, entry.opcode));
                    codeLine.append("(C, ").append(parts[2]).append(")");
                    symbolTable.put(parts[0], new SymbolEntry(parts[0], LOC));
                    intermediateCode.add(codeLine.toString().trim());
                    LOC += Integer.parseInt(parts[2]);
                    continue;
                } else {
                    symbolTable.put(parts[0], new SymbolEntry(parts[0], LOC));
                }
            }
            
            // Generate code for instructions
            for (int i = 0; i < parts.length; i++) {
                if (motTable.containsKey(parts[i])) {
                    MOTEntry entry = motTable.get(parts[i]);
                    codeLine.append(String.format("(%s, %s) ", entry.classType, entry.opcode));
                } else if (symbolTable.containsKey(parts[i])) {
                    codeLine.append("(S, ").append(parts[i]).append(") ");
                } else {
                    boolean literalFound = false;
                    for (LiteralEntry literal : literalTable) {
                        if (literal.literal.equals(parts[i])) {
                            codeLine.append("(L, ").append(literal.index).append(") ");
                            literalFound = true;
                            break;
                        }
                    }
                    if (!literalFound) {
                        codeLine.append("(C, ").append(parts[i]).append(") ");
                    }
                }
            }
            intermediateCode.add(codeLine.toString().trim());
            LOC += getLength(parts);
        }
        
        return intermediateCode;
    }

    private static Map<String, MOTEntry> initializeMotTable() {
        Map<String, MOTEntry> motTable = new HashMap<>();
        motTable.put("STOP", new MOTEntry("STOP", "IS", "00"));
        motTable.put("ADD", new MOTEntry("ADD", "IS", "01"));
        motTable.put("SUB", new MOTEntry("SUB", "IS", "02"));
        motTable.put("MULT", new MOTEntry("MULT", "IS", "03"));
        motTable.put("MOVER", new MOTEntry("MOVER", "IS", "04"));
        motTable.put("MOVEM", new MOTEntry("MOVEM", "IS", "05"));
        motTable.put("COMP", new MOTEntry("COMP", "IS", "06"));
        motTable.put("BC", new MOTEntry("BC", "IS", "07"));
        motTable.put("DIV", new MOTEntry("DIV", "IS", "08"));
        motTable.put("READ", new MOTEntry("READ", "IS", "09"));
        motTable.put("PRINT", new MOTEntry("PRINT", "IS", "10"));
        motTable.put("START", new MOTEntry("START", "AD", "01"));
        motTable.put("END", new MOTEntry("END", "AD", "02"));
        motTable.put("ORIGIN", new MOTEntry("ORIGIN", "AD", "03"));
        motTable.put("EQU", new MOTEntry("EQU", "AD", "04"));
        motTable.put("LTORG", new MOTEntry("LTORG", "AD", "05"));
        motTable.put("DS", new MOTEntry("DS", "DL", "01"));
        motTable.put("DC", new MOTEntry("DC", "DL", "02"));
        motTable.put("AREG", new MOTEntry("AREG", "RG", "01"));
        motTable.put("BREG", new MOTEntry("BREG", "RG", "02"));
        motTable.put("CREG", new MOTEntry("CREG", "RG", "03"));
        return motTable;
    }

    private static void printSymbolTable(Map<String, SymbolEntry> symbolTable) {
        System.out.println("\nSYMBOL TABLE:");
        System.out.println("-------------------------------------------");
        System.out.println("SYMBOL\tADDRESS");
        System.out.println("-------------------------------------------");
        for (SymbolEntry entry : symbolTable.values()) {
            System.out.printf("%s\t\t%d%n", entry.symbol, entry.address);
        }
    }

    private static void printLiteralTable(List<LiteralEntry> literalTable) {
        System.out.println("\nLITERAL TABLE:");
        System.out.println("-------------------------------------------");
        System.out.println("INDEX\tLITERAL\tADDRESS");
        System.out.println("-------------------------------------------");
        for (LiteralEntry entry : literalTable) {
            System.out.printf("%d\t%s\t%d%n", entry.index, entry.literal, entry.address);
        }
    }

    private static void printPoolTable(List<PoolEntry> poolTable) {
        System.out.println("\nPOOL TABLE:");
        System.out.println("-------------------------------------------");
        System.out.println("LITERAL INDEX");
        System.out.println("-------------------------------------------");
        for (PoolEntry entry : poolTable) {
            System.out.printf("%d%n", entry.literalIndex);
        }
    }
}

class SymbolEntry {
    String symbol;
    int address;

    public SymbolEntry(String symbol, int address) {
        this.symbol = symbol;
        this.address = address;
    }
}

class LiteralEntry {
    String literal;
    int address;
    int index;

    public LiteralEntry(String literal, int address, int index) {
        this.literal = literal;
        this.address = address;
        this.index = index;
    }
}

class PoolEntry {
    int literalIndex;

    public PoolEntry(int literalIndex) {
        this.literalIndex = literalIndex;
    }
}

class MOTEntry {
    String name;
    String classType;  // Changed from 'type' to 'class'
    String opcode;  // Changed from 'code' to 'opcode'

    public MOTEntry(String name, String classType, String opcode) {
        this.name = name;
        this.classType = classType;
        this.opcode = opcode;
    }
}
