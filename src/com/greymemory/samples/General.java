/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greymemory.samples;

import java.util.ArrayList;
import java.util.Random;
import com.greymemory.core.Sample;
import com.greymemory.core.XDM;
import com.greymemory.core.XDMParameters;

/**
 *
 * @author amazhurin 
 */
public class General {
    
    public void run(){
        try{
            XDMParameters param = new XDMParameters(
                3, 0, 1, 0.0f, 1000.0f, 1.0f, 20.0f, 20.0f, 2);

            XDM xdm = new XDM(param);
            
            Random random = new Random(33); 
            
            Sample s1 = new Sample();
            s1.data = new double[param.num_channels];
            for(int i = 0; i < param.num_channels; i++)
                s1.data[i] = random.nextDouble() * 1000;
            s1.class_value = 0;
            
            Sample s2 = new Sample();
            s2.data = new double[param.num_channels];
            for(int i = 0; i < param.num_channels; i++)
                s2.data[i] = random.nextDouble() * 1000;
            s2.class_value = 1;
            
            xdm.write(s1);
            xdm.write(s2);
            
            // modify a bit
            //for(int i = 0; i < param.num_channels; i++)
            //    s2.data[i] += random.nextDouble() * 20;

            Sample read1 = xdm.read(s1);
            Sample read2 = xdm.read(s2);
            
            if(read1.class_value == s1.class_value &&
               read2.class_value == s2.class_value)
                System.out.println("Passed");
            else
                System.out.println("Failed");
                
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
