package com.mpk.melwin.patientapp;

/**
 * Created by Melwin on 3/22/2018.
 */

public class Treshholdvalue {
    public static int Max,Min,PuValue;
    public static String PValue;
   public Treshholdvalue(){
       this.Max = 100;
       this.Min = 60;
       this.PuValue= 70;
   }

    public Treshholdvalue(int Max,int Min){
       this.Max = Max;
       this.Min = Min;
    }

    public static int getPuValue() {
        return PuValue;
    }

    public static void setPuValue(int PuValue) {
        Treshholdvalue.PuValue = PuValue;

    }

    public static String getPValue() {
        return PValue;
    }

    public static void setPValue(String PValue) {
       // this.PValue = PValue;
    }

    public static int getMax() {
        return Max;
    }

    public static void setMax(int max) {
        Max = max;
    }

    public static int getMin() {
        return Min;
    }

    public static void setMin(int min) {
        Min = min;
    }
}
