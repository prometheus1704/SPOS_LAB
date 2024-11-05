import java.io.*;
import java.util.*;

class macro2 {
    Vector<String> mnt = new Vector<>();
    Vector<String> mdt = new Vector<>();
    Vector<String> ala = new Vector<>();
    Vector<Integer> argu = new Vector<>();
    Vector<String> argm = new Vector<>();
    int s = 1;
    int o = 0;
    int h = 0;
    int t = 0;
    String line;
    String l1;
    String l2;
    String l3;

    void initialize() {
        try (Scanner sc = new Scanner(new FileReader("mnt.txt"))) {
            while (sc.hasNextLine()) {
                l1 = sc.nextLine();
                String[] t1 = l1.split("\\s++");
                for (int i = 1; i < t1.length; i++) {
                    mnt.add(t1[i]);
                    // System.out.println(t1[i]);
                }
            }
            for (int i = 0; i < mnt.size(); i++) {
                System.out.println(mnt.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (Scanner sc2 = new Scanner(new FileReader("mdt.txt"))) {
            while (sc2.hasNextLine()) {
                mdt.add(sc2.nextLine().split("\\s++", 2)[1]);
                // System.out.println(sc.nextLine().split("\t", 2)[1]);
            }
            for (int i = 0; i < mdt.size(); i++) {
                System.out.println(mdt.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (Scanner sc3 = new Scanner(new FileReader("ala.txt"))) {
            while (sc3.hasNextLine()) {
                l3 = sc3.nextLine();
                String[] t3 = l3.split("\t");
                ala.add(t3[1]);
            }
            for (int i = 0; i < ala.size(); i++) {
                System.out.println(ala.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void pass2() {
        try (Scanner scanner = new Scanner(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("expmac.txt"))) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] temp = line.split("[,\\s++]");
                if (temp[0].equals("START")) {
                    t = 1;
                }
                if (t == 1) {
                    if (mnt.contains(temp[0])) {
                        for (int i = 1; i < temp.length; i++) {
                            argm.add(temp[i]);
                        }
                        int g = Integer.parseInt(mnt.get(s));
                        g = g - 1;
                        while (!mdt.get(g).equals("MEND")) {
                            String line1 = mdt.get(g);
                            String[] temp1 = line1.split("[,\\s++]");
                            writer.write(temp1[0] + "\t");
                            for (int i = 1; i < temp1.length; i++) {
                                temp1[i] = temp1[i].substring(1);
                                int v = Integer.parseInt(temp1[i]) - 1;
                                writer.write(argm.get(v) + "\t");
                            }
                            writer.write("\n");
                            g++;
                        }
                        s = s + 2;
                    } else {
                        writer.write(line + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("error occured while writing to file macexp.txt");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        macro2 t = new macro2();
        t.initialize();
        t.pass2();
    }
}
