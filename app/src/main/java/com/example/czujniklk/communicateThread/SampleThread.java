package com.example.czujniklk.communicateThread;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventListener;

public class SampleThread implements Runnable, PropertyChangeListener {
    AppCompatActivity mainProg;
    public SampleThread(AppCompatActivity main){mainProg=main;}

    private int fftLen = 2048;

    Object obj = new Object();
    boolean running = false;

    PropertyChangeSupport PCS = new PropertyChangeSupport(this);

    @Override
    public void run() {
        synchronized (obj) {


            while (true) {
                if(running){

                double[] x = pobierzWyniki(6100);
                PCS.firePropertyChange(new PropertyChangeEvent(this, "wyniki", null, x));

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.getCause();
                }
                }
            }
        }
    }

    private double[] pobierzWyniki(double freq) {
        int bufferSize = AudioRecord.getMinBufferSize(12000, 16, 2);
        try {


            if (ActivityCompat.checkSelfPermission(mainProg, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = new String[1];
                perms[0]=Manifest.permission.RECORD_AUDIO;
                ActivityCompat.requestPermissions(mainProg, perms,0);
            }
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 12000, 16, 2, bufferSize);
                    recorder.startRecording();
                    short bufferek[] = new short[fftLen];
                    int bufferReadResult = recorder.read(bufferek, 0, fftLen);

        double x[] = new double[fftLen];
        for(int i=0; i<fftLen &&i<bufferReadResult;i++){
            x[i]=(double)bufferek[i]/Short.MAX_VALUE;
        }
        recorder.stop();
            return x;
                }
                catch(Exception e){
                    System.out.println(e.getCause());
                    return new double[fftLen];
                }

    }


    private double[] generujWyniki(double freq) {
        double[] x = new double[fftLen];
        double sampleFreq = 12000;
        int los = (int) (Math.random() * 10);
        for (int licznik = 0; licznik < fftLen; licznik++) {
            x[licznik] = Math.sin(2*freq*Math.PI*(licznik/sampleFreq)+2);
            x[licznik] += Math.random()-0.4;        }
        return x;
    }

    public void addListener(PropertyChangeListener listener){
        PCS.addPropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        running = (boolean) propertyChangeEvent.getNewValue();
    }
}
