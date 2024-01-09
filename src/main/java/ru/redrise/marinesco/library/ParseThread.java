package ru.redrise.marinesco.library;

import org.springframework.beans.factory.DisposableBean;

public class ParseThread implements DisposableBean, Runnable{
    private static volatile boolean running;

    @Override
    public void run() {
        running = true;
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void destroy(){
        running = false;
    }
}
