import java.io.*;
import java.util.*;

class macro1 {
    Vector<String> mnt = new Vector<>();
    Vector<String> mdt = new Vector<>();
    Vector<String> ala = new Vector<>();
    int prev = 0;
    int hit = 0;
    String line;
    int length = 0;
    int p = 0;
    int k = 0;
    int flag = 0;

    void pass1() {
        try (Scanner scanner = new Scanner(new FileReader("input.txt"))) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                length++;
                String[] temp = line.split("[,\\s++]");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[0].equals("MACRO") || temp[0].equals("MEND")) {
                        length--;
                    }
                    if (i == 0 && temp[0].equals("MACRO")) {
                        p = 1;
                        break;
                    }
                    if (p == 1) {
                        prev = hit;
                        mnt.add(temp[0]);
                        mnt.add(Integer.toString(length));
                        for (int j = 1; j < temp.length; j++) {
                            ala.add(temp[j]);
                            hit++;
                        }
                        p = 0;
                    }
                    if (i == 0 && temp[0].equals("MEND")) {
                        mdt.add(temp[0]);
                        flag = 1;
                    }
                    if (i > 0 && !temp[i - 1].equals("MACRO") && !mnt.contains(temp[i - 1])
                            && temp[i].startsWith("&")) {
                        StringBuilder t = new StringBuilder();
                        int l = 0;
                        for (int h = 0; h < temp.length; h++) {
                            if (mnt.contains(temp[0])) {// 0
                                l = 1;
                            }
                            if (!temp[h].startsWith("&") && l == 0) {// h
                                t.append(temp[h]);
                            } else {
                                if (l == 0) {
                                    if (flag == 0) {
                                        for (int g = 0; g < ala.size(); g++) {
                                            if (ala.get(g).equals(temp[h])) {
                                                k = g + 1;
                                            }
                                        }
                                    } else {
                                        for (int g = prev; g < ala.size(); g++) {
                                            if (ala.get(g).equals(temp[h])) {
                                                k = g + 1;
                                            }
                                        }
                                    }

                                    t.append("\t#").append(k);
                                }

                            }
                        }
                        if (t.length() > 1) {
                            mdt.add(t.toString());
                        }
                        i++;
                    }
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("mdt.txt"))) {
                for (int i = 0; i < mdt.size(); i++) {
                    writer.write((i + 1) + "\t" + mdt.get(i) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("mnt.txt"))) {
                int j = 1;
                for (int i = 0; i < mnt.size(); i = i + 2) {
                    writer.write(j + "\t" + mnt.get(i) + "\t" + mnt.get(i + 1) + "\n");
                }
                j++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("ala.txt"))) {
                for (int i = 0; i < ala.size(); i++) {
                    writer.write((i + 1) + "\t" + ala.get(i) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("file input.txt not found");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        macro1 m = new macro1();
        m.pass1();
    }

}