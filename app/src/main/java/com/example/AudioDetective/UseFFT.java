package com.example.AudioDetective;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;


//references
//https://stackoverflow.com/questions/41587275/fft-result-interpretation

public class UseFFT {

    public void computeFFT(double[] input) {

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexResults = transformer.transform(input, TransformType.FORWARD);





        for (Complex c : complexResults) {


            Log.i("fft val", String.valueOf(c.getReal()));
        }

            Log.i("fft size", String.valueOf(complexResults.length));

        //In the theoretical sense, an FFT maps complex[N] => complex[N]. However, if your data is just
        // an audio file, then your input will be simply complex numbers with no imaginary component. Thus
        // you will map real[N] =>complex[N]. However, with a little math, you see that the format of the output
        // will always be output[i]==complex_conjugate(output[N-i]). Thus you really only need to look at the first N/2+1 samples.
        // Additionally, the complex output of the FFT gives you information about both phase and magnitude.
        // If all you care about is how much of a certain frequency is in your audio, you only need to look at the magnitude,
        // which can be calculated as square_root(imaginary^2+real^2), for each element of the output.
        //
        //Of course, you'll need to look at the documentation of whatever library you use to understand which array element
        // corresponds to the real part of the Nth complex output, and likewise to find the imaginary part of the Nth
        // complex output.



    }








}
