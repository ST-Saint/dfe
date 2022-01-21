package edu.purdue.dfe;

import java.util.Random;

public class Sample {


    public void testIf(){
        Random rnd = new Random();
        if( rnd.nextBoolean() ){
            System.out.println("first 1");
        }else{
            System.out.println("first 0");
        }

        if( rnd.nextBoolean() ){
            System.out.println("second 1");
        }else{
            System.out.println("second 0");
        }
    }
}
