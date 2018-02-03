package com.example.nikhilanj.oopomo_new;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nikhilanj on 03-02-18.
 */

class appimer {
    static int getSeconds(){
        Random r = new Random();
        //System.out.println(System.currentTimeMillis());
        //int t = (int)(System.currentTimeMillis()%100);
        int t = (int)((TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())%100)%60);
        //System.out.println(t);
        return t;
        //return r.nextInt(60);
    }

    static int getMinutes(){
        //Random r2 = new Random();
        //return r2.nextInt(60);
        int t2 = (int)((TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()))%100)%60;
        return t2;
    }

}
