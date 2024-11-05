import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;

class MnemonicTable {
    String mnemonic;
    String opcode;
    int length;

    public MnemonicTable(String mnemonic, String opcode, int length) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.length = length;
    }
}

public class PassOne {

    Hashtable<String, MnemonicTable> is = new Hashtable<>();
    ArrayList<String> symtab = new ArrayList<>();
    ArrayList<Integer> symaddr = new ArrayList<>();
    ArrayList<String> littab = new ArrayList<>();
    ArrayList<Integer> litaddr = new ArrayList<>();
    ArrayList<Integer> pooltab = new ArrayList<>();
    int LC = 0;

    public void createIS() {
        MnemonicTable m = new MnemonicTable("STOP", "00", 0);
        is.put("STOP", m);
        m = new MnemonicTable("ADD", "01", 0);
        is.put("ADD", m);
        m = new MnemonicTable("SUB", "02", 0);
        is.put("SUB", m);
        m = new MnemonicTable("MULT", "03", 0);
        is.put("MULT", m);
        m = new MnemonicTable("MOVER", "04", 0);
        is.put("MOVER", m);
        m = new MnemonicTable("MOVEM", "05", 0);
        is.put("MOVEM", m);
        m = new MnemonicTable("COMP", "06", 0);
        is.put("COMP", m);
        m = new MnemonicTable("BC", "07", 0);
        is.put("BC", m);
        m = new MnemonicTable("DIV", "08", 0);
        is.put("DIV", m);
        m = new MnemonicTable("READ", "09", 0);
        is.put("READ", m);
        m = new MnemonicTable("PRINT", "10", 0);
        is.put("PRINT", m);
        m = new MnemonicTable("LT", "03", 0);
        is.put("LT", m);
    }

    public void generateIC() throws Exception {
        BufferedWriter wr = new BufferedWriter(new FileWriter("ic.txt"));
        BufferedReader br = new BufferedReader(new FileReader("input.asm"));
        String line = " ";
        pooltab.add(0, 0);
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\\s+");
            if (split[0].length() > 0) {
                // it is a label
                if (!symtab.contains(split[0])) {
                    symtab.add(split[0]);
                    symaddr.add(LC);
                } else {
                    int index = symtab.indexOf(split[0]);
                    symaddr.remove(index);
                    symaddr.add(index, LC);
                }
            }

            if (split[1].equals("START")) {
                LC = Integer.parseInt(split[2]);
                wr.write("(AD,01)(C," + split[2] + ")\n");
            } else if (split[1].equals("ORIGIN")) {
                if (split[2].contains("+") || split[2].contains("-")) {
                    LC = getAddress(split[2]);
                } else {
                    LC = symaddr.get(symtab.indexOf(split[2]));
                }
            } else if (split[1].equals("EQU")) {
                int addr = 0;
                if (split[2].contains("+") || split[2].contains("-")) {
                    addr = getAddress(split[2]);
                } else {
                    addr = symaddr.get(symtab.indexOf(split[2]));
                }
                if (!symtab.contains(split[0])) {
                    symtab.add(split[0]);
                    symaddr.add(addr);
                } else {
                    int index = symtab.indexOf(split[0]);
                    symaddr.remove(index);
                    symaddr.add(index, addr);
                }
            } else if (split[1].equals("LTORG") || split[1].equals("END")) {
                if (litaddr.contains(0)) {
                    for (int i = pooltab.get(pooltab.size() - 1); i < littab.size(); i++) {
                        if (litaddr.get(i) == 0) {
                            litaddr.remove(i);
                            litaddr.add(i, LC);
                            LC++;
                        }
                    }
                    if (!split[1].equals("END")) {
                        pooltab.add(littab.size());
                        wr.write("(AD,05)\n");
                    } else
                        wr.write("(AD,04)\n");
                }
            } else if (split[1].contains("DS")) {
                LC += Integer.parseInt(split[2]);
                wr.write("(DL,01) (C," + split[2] + ")\n");
            } else if (split[1].equals("DC")) {
                LC++;
                wr.write("(DL,02) (C," + split[2].replace("'", "").replace("'", "") + ")\n");
            } else if (is.containsKey(split[1])) {
                wr.write("(IS," + is.get(split[1]).opcode + ") ");
                if (split.length > 2 && split[2] != null) {
                    String rg = split[2].replace(",", "");
                    if (rg.equals("AREG")) {
                        wr.write("(RG,01) ");
                    } else if (rg.equals("BREG")) {
                        wr.write("(RG,02) ");
                    } else if (rg.equals("CREG")) {
                        wr.write("(RG,03) ");
                    } else if (rg.equals("DREG")) {
                        wr.write("(RG,04) ");
                    } else {
                        if (symtab.contains(rg)) {
                            wr.write("(S," + symtab.indexOf(rg) + ")\n");
                        } else {
                            symtab.add(rg);
                            symaddr.add(0);
                            wr.write("(S," + symtab.indexOf(rg) + ")\n");
                        }
                    }
                }

                if (split.length > 3 && split[3] != null) {
                    if (split[3].contains("=")) {
                        String norm = split[3].replace("=", "").replace("'", "").replace("'", "");
                        if (!littab.contains(norm)) {
                            littab.add(norm);
                            litaddr.add(0);
                            wr.write("(L," + littab.indexOf(norm) + ")\n");
                        } else {
                            wr.write("(L," + littab.indexOf(norm) + ")\n");
                        }

                    } else if (symtab.contains(split[3])) {
                        wr.write("(S," + symtab.indexOf(split[3]) + ")\n");
                    } else {
                        symtab.add(split[3]);
                        symaddr.add(0);
                        wr.write("(S," + symtab.indexOf(split[3]) + ")\n");
                    }
                }
                LC++;
            }
        }
        wr.flush();
        wr.close();
        br.close();

        // Print Symbol Table to console and file
        try (BufferedWriter symWriter = new BufferedWriter(new FileWriter("sym.txt"))) {
            System.out.println("\nSymbol Table:");
            for (int i = 0; i < symtab.size(); i++) {
                String entry = symtab.get(i) + "\t\t" + symaddr.get(i)+"\t\t1";
                System.out.println(entry);
                symWriter.write(entry + "\n");
            }
        }

        // Print Literal Table to console and file
        try (BufferedWriter litWriter = new BufferedWriter(new FileWriter("lit.txt"))) {
            System.out.println("\nLiteral Table:");
            for (int i = 0; i < littab.size(); i++) {
                String entry = littab.get(i) + "\t\t" + litaddr.get(i);
                System.out.println(entry);
                litWriter.write(entry + "\n");
            }
        }

        // Print Pool Table to console and file
        try (BufferedWriter poolWriter = new BufferedWriter(new FileWriter("pool.txt"))) {
            System.out.println("\nPool Table:");
            for (int i = 0; i < pooltab.size(); i++) {
                String entry = String.valueOf(pooltab.get(i));
                System.out.println(entry);
                poolWriter.write(entry + "\n");
            }
        }
    }

    private int getAddress(String string) {
        int temp = 0;
        if (string.contains("+")) {
            String[] sp = string.split("\\+");
            int ad = symaddr.get(symtab.indexOf(sp[0]));
            temp = ad + Integer.parseInt(sp[1]);
        } else if (string.contains("-")) {
            String[] sp = string.split("\\-");
            int ad = symaddr.get(symtab.indexOf(sp[0]));
            temp = ad - Integer.parseInt(sp[1]);
        }
        return temp;
    }

    public static void main(String[] args) throws Exception {
        PassOne p = new PassOne();
        p.createIS();
        p.generateIC();
    }
}
