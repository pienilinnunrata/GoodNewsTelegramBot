package org.goodnewstbot.news;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KeyWords {


    public static List<String> getKeyWords() {
        ArrayList<String> list = new ArrayList<>();
        try {
            Scanner s = new Scanner(new File("src/main/resources/key_words"));
            while (s.hasNext()) {
                list.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
}
