/*
 * Copyright(c) 2015 Anton Mazhurin to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import com.greymemory.core.Sample;
import com.greymemory.core.XDM;
import com.greymemory.core.XDMParameters;
import com.greymemory.evolution.Gene;
import com.greymemory.evolution.Individual;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amazhurin
 */
public class IndividualOCR extends Individual {
 
    private XDM xdm;
    
    private final String file_input;
    public IndividualOCR(String file_input){
        this.file_input = file_input;
        
        //0
        genome.genes.add(new Gene("value_resolution", 1.00f, 10f));
        genome.genes.get(genome.genes.size()-1).value = 
                5f;
                //16f; // best 6?  12   15,20 
        
        //1
        genome.genes.add(new Gene("value_radius", 5f, 40f));
        genome.genes.get(genome.genes.size()-1).value = 
                60f;
                //60f;// best 60   60   60
        
        //2
        genome.genes.add(new Gene("num_hl", 7f, 90f));
        genome.genes.get(genome.genes.size()-1).value = 
                25f;
                //30;// best 80   20    15
                // Accuracy       90                               

        //3
        genome.genes.add(new Gene("read_acrivation_factor", 1f, 5f));
        genome.genes.get(genome.genes.size()-1).value = 
                5f;
    }
    
    @Override
    public Individual create() {
        Individual individual = new IndividualOCR(file_input);
        return individual;
    }
    
    private void create_xdm() throws Exception{
        
        XDMParameters param;
        param = new XDMParameters();
        
        param.window = new int[1];
        param.predict = false;
        param.num_channels = 784;
        param.num_channels_prediction = 0;
        param.max_storage_size_in_mb = 16200;
        param.classes = new int[10];
        for(int i = 0; i < 10; i++)
            param.classes[i] = i;

        param.activation_radius = new double[param.num_channels];
        param.resolution = new double[param.num_channels];
        param.medians = new double[param.num_channels];
        
        for(int i = 0; i < param.num_channels; i++){
            param.resolution[i] = genome.genes.get(0).value;
            param.activation_radius[i] = genome.genes.get(1).value;
            param.medians[i] = 120; 
        }
        
        param.min_num_hard_location = (int)genome.genes.get(2).value;
        
        param.forgetting_rate = 9999999;
        
        param.radius_multiplier_for_read = 
                genome.genes.get(3).value;
        
        xdm = new XDM(param);
    }
    
    private int read_data(String line, double[] data){
        String[] parts0 = line.split(",");
        String stime = parts0[0];

        for(int i = xdm.param.num_channels / 3; i < xdm.param.num_channels; i++){
            double value = Double.parseDouble(parts0[i+1]);
            data[i] = value;
        }
        
        for(int i = 0; i < xdm.param.num_channels / 3; i++){
            double value = Double.parseDouble(parts0[i+1]);
            data[i] = value;
        }
        return Integer.parseInt(parts0[0]);
    }
    
    
    @Override
    public void calculate_cost() {
        
        try {
            create_xdm();
        } catch (Exception ex) {
            Logger.getLogger(IndividualOCR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double fitness = 0.0f;
        BufferedReader reader = null;
	String line;
        int num_points = 0;
        int num_correct_points = 0;
        
        Sample sample = new Sample();
        sample.data = new double[xdm.param.num_channels];
        
        int num_train = 5000;
        int num_zero_hl = 0;
        int num_wrong_class = 0;
        
        try {
            reader = new BufferedReader(new FileReader(file_input));
            int num_lines = 0;
            while ((line = reader.readLine()) != null) {
                if(Thread.interrupted()){
                    break;
                }
                
                line = reader.readLine();
                line = reader.readLine();
                //line = reader.readLine();
                //line = reader.readLine();
                //line = reader.readLine();
                
                num_lines++;
                if(num_lines == 1)
                    continue;
                
                int label = read_data(line, sample.data);
                
                if(num_lines < num_train){
                    if(num_lines % 100 == 0)
                        System.out.println("Training : " + 100.0*num_lines/num_train + "%");
                    // train
                    sample.class_value = label;
                    xdm.write(sample);
                    continue;
                }

                num_points++;
                Sample sampleRead;
                sampleRead = xdm.read(sample);
                if(sampleRead.error == Sample.Error.OK){
                    if(sampleRead.class_value == label)
                        num_correct_points++;
                    else
                        num_wrong_class++;
                    
                }  else {
                    num_zero_hl++;
                }
                
                if(num_correct_points % 10 == 0){
                    System.out.println("Accuracy : " + 
                            100.0*num_correct_points/num_points + "%" +
                            ". Zero HL " + 100f * num_zero_hl/(num_points - num_correct_points) + 
                            ". Wrong class " + 100f*num_wrong_class/(num_points - num_correct_points));
                }
                
            } 

        } catch (Exception ex) {
            Logger.getLogger(IndividualGym.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}           
        /*
        // evaluation
        try {
            reader = new BufferedReader(new FileReader(file_input));
            int num_lines = 0;
            while ((line = reader.readLine()) != null) {
                if(Thread.interrupted()){
                    break;
                }
                
                num_lines++;
                if(num_lines == 1)
                    continue;
                
                int label = read_data(line, sample.data);
                
                num_points++;
                Sample sampleRead;
                sampleRead = xdm.read(sample);
                if(sampleRead.class_value == label){
                    num_correct_points++;
                    continue;
                }
                break;
            }

        } catch (Exception ex) {
            Logger.getLogger(IndividualGym.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	} */          

        if(num_points > 0)
            fitness = num_correct_points / num_points;
        
        set_cost(100 - fitness*100);
        
        System.out.printf("*");
        
        xdm = null;
    }
    
    
    
        
}
 