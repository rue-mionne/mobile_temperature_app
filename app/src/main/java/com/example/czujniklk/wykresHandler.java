package com.example.czujniklk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class wykresHandler implements Runnable, PropertyChangeListener {
    Object FFTMutex;
    Bitmap bitmap;
    Canvas canvas;
    int ymax;
    ArrayList<Double> wykresData=new ArrayList<Double>(2048);
    ImageView wykres;
    List<Double> listaY=new ArrayList<>();
    List<Double> listaX=new ArrayList<>();

    List<Integer> placeHolder = new ArrayList<>();

    ArrayList<Double> freqTable = new ArrayList<>();
    ArrayList<Double> freqTableMean = new ArrayList<>();
    Integer last=0;
    PropertyChangeSupport freqTableSledz = new PropertyChangeSupport(this);
    wykresHandler(ImageView wykres, Object FFTMutex){
        this.wykres = wykres;
        ymax=500;
        this.FFTMutex = FFTMutex;
    }

    public void dodajSledzia(PropertyChangeListener sledz){
        freqTableSledz.addPropertyChangeListener(sledz);
    }

    @Override
    public void run() {
        for(int i=0;i<2048;i++){
            freqTable.add(0.0);
        }
        for(int i=0; i<10;i++){freqTableMean.add(0.0);}

        repaint();
    }

    private void repaint() {
        bitmap = Bitmap.createBitmap((Integer)1024,ymax, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        if(wykresData.isEmpty()&&!listaX.isEmpty()){
            wykresData.addAll(freqTable);
        }
        Paint paint = new Paint();
        paint.setARGB(255,255,0,0);
        Paint newPaint = new Paint();
        newPaint.setARGB(255,255,255,255);
        canvas.drawPaint(newPaint);
        Double max = 0.0;
        for (Integer i=0; i<(listaX.size()/2);i++) {
            Double tempWynik = listaX.get(i)* listaX.get(i)+listaY.get(i)*listaY.get(i);
            if(max<tempWynik)
                max=tempWynik;
            wykresData.set(i, tempWynik);


        }
        double mnoznik = 500/max;
        for(Integer i=0; i<(listaX.size()/2);i++){
            canvas.drawLine(i,511-(int)(wykresData.get(i)*mnoznik),i, 511, paint);
        }
        Double suma = 0.0;
        for(Double liczba:freqTableMean){
            suma+=liczba;
        }
        Double number = (double) wykresData.indexOf(max);
        if(suma/10-number<500&&number!=0){


            freqTableMean.set(last, number);
            freqTableSledz.firePropertyChange("frequency",null, number);
        }
        if(last!=9){
            last++;
        }
        else {
            last=0;
        }
        wykres.setImageBitmap(bitmap);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("y_list")) {
            listaY.clear();
            double[] wynik = (double[]) propertyChangeEvent.getNewValue();
            for(double liczba : wynik){
                listaY.add(liczba);

            }
            listaY=listaY;
        }
        else {
            listaX.clear();
            double[] wynik = (double[]) propertyChangeEvent.getNewValue();
            for(double liczba : wynik){
                listaX.add(liczba);


            }
            listaX=listaX;
            repaint();
        }

    }

    public Object getMutex(){
        return FFTMutex;
    }


}
