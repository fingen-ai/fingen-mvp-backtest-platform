package util;

import org.apache.commons.lang3.ArrayUtils;

public class AppendArray {

    public double[] appendDoubleArray(double[] array, double x, int length){

        double[] newArray = new double[array.length + 1];
        for(int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        newArray[newArray.length - 1] = x;
        if(newArray.length > length) {
            newArray = ArrayUtils.remove(newArray, 0);
        }
        return newArray;
    }
}
