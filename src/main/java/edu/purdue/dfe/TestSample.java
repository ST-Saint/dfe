package edu.purdue.dfe;

import java.util.Random;

public class TestSample {

    static final String a = "const string";

    public static String testString(){
        if( a.equals(a) ){

        }
        return a;
    }

    public void testIf() {
        Random rnd = new Random();
        if (rnd.nextBoolean()) {
            System.out.println("first 1");
        } else {
            System.out.println("first 0");
        }

        if (rnd.nextBoolean()) {
            System.out.println("second 1");
        } else {
            System.out.println("second 0");
        }
    }

    public void testSwtich() {
        Random rnd = new Random();
        int i = rnd.nextInt(5);
        switch (i) {
        case 1: {
            System.out.println("1");
            break;
        }
        case 2: {
            System.out.println("2");
            break;
        }
        case 3: {
            System.out.println("3");
            break;
        }
        default: {
            System.out.println("sth");
        }
        }
    }
}
