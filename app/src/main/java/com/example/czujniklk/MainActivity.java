package com.example.czujniklk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.czujniklk.communicateThread.SampleThread;
import com.example.czujniklk.countThread.FFTclass;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements PropertyChangeListener {
    private int fftLen=2048;
    private TextView timetext;
    private TextView ftttext;
    private ImageView imageView;

    private String Ftext = "Ftt: ";

    private wykresHandler wykresHandler;
    private FFTclass ffTclass;
    private SampleThread sThread;

    private PropertyChangeSupport PCS = new PropertyChangeSupport(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBehaviour();
        Object mutex = new Object();
        wykresHandler = new wykresHandler(imageView,mutex);
        ffTclass = new FFTclass(fftLen,wykresHandler);
        sThread = new SampleThread(this);
        PCS.addPropertyChangeListener(sThread);
        Thread watek = new Thread(wykresHandler);
        wykresHandler.dodajSledzia(this);
        watek.start();
        Thread watek2 = new Thread(sThread);
        sThread.addListener(this);
        watek2.start();
    }

    private void setBehaviour(){
        ftttext =(TextView) findViewById(R.id.ftttext);
        timetext=(TextView) findViewById(R.id.timetext);
        imageView=(ImageView) findViewById(R.id.imageView);
        String text = "Temp: ";
        timetext.setText(text);
        timetext.setTextSize(40);
        text = "Ftt: ";
        ftttext.setText(text);
        ftttext.setTextSize(15);
    }


    public void StopOnClick(View view) {
        PCS.firePropertyChange(null, null, false);
    }

    public void StartOnClick(View view) {
        PCS.firePropertyChange(null, null, true);
    }
    Object yaaaay = new Object();

    private double[] obliczX(double freq){
            double[] x = new double[fftLen];
            double sampleFreq = 4080;
            double los = (double)(Math.random()*10);
            for(int licznik = 0; licznik<fftLen;licznik++){
                x[licznik] = Math.sin(2*freq*Math.PI*(licznik/sampleFreq)+2);
                x[licznik] += Math.random()-0.4;
            }
            return x;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("frequency")){
            double val = (double) propertyChangeEvent.getNewValue();
            ftttext.setText(Ftext+Double.toString(val*12000/2048));
            double a= 0.1051;
            double b = -26.6477;
            String Ttext = "Temp: ";
            double d = 2.34568;
            DecimalFormat f = new DecimalFormat("##.00");
            timetext.setText(Ttext+f.format(val*a+b));
        }
        else {
            double[] y = new double[fftLen];
            for (double liczba : y) {
                liczba = 0;
            }
            ffTclass.fft((double[]) propertyChangeEvent.getNewValue(), y);
        }
        }
}