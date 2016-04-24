/*
 * Copyright(c) 2015 Anton Mazhurin to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.greymemory.anomaly.AnomalyCalculator;
import com.greymemory.core.SliderRead;
import com.greymemory.core.SliderRead.FutureSample;
import com.greymemory.core.SliderWrite;
import com.greymemory.core.XDM;
import com.greymemory.core.XDMParameters;
import com.greymemory.evolution.Gene;
import com.greymemory.evolution.Individual;

/**
 *
 * @author amazhurin
 */
public class IndividualGym extends Individual{
    private final String file_input;
    private String file_output;

    public IndividualGym(String file_input, String file_output){
        this.file_input = file_input;
        this.file_output = file_output;
      
        //0
        genome.genes.add(new Gene("value_resolution", 0.01f, 3f));
        genome.genes.get(genome.genes.size()-1).value = 3.01f;
        
        //1
        genome.genes.add(new Gene("value_radius", 5f, 30f));
        genome.genes.get(genome.genes.size()-1).value = 35f;
        
        //2
        genome.genes.add(new Gene("dow_resolution",1f, 0f));
        genome.genes.get(genome.genes.size()-1).value = 1f;
        
        //3
        genome.genes.add(new Gene("dow_radius", 0f, 3f));
        genome.genes.get(genome.genes.size()-1).value =0.96f;
        
        //4
        genome.genes.add(new Gene("hour_resolution", 1f, 0f));
        genome.genes.get(genome.genes.size()-1).value = 0.9f;
        
        //5
        genome.genes.add(new Gene("hour_radius", 0f, 3f));
        genome.genes.get(genome.genes.size()-1).value = 0.9f;
        
        //6
        genome.genes.add(new Gene("window", 2f, 12f));
        genome.genes.get(genome.genes.size()-1).value = 10.9f;
        
        
        //7
        genome.genes.add(new Gene("num_hl", 7f, 230f));
        genome.genes.get(genome.genes.size()-1).value = 41;
        
        //8
        genome.genes.add(new Gene("forgetting_rate", 10f, 2000f));
        genome.genes.get(genome.genes.size()-1).value = 82f;
        
    }
    
    
    @Override
    public Individual create() {
        Individual individual = new IndividualGym(file_input, get_file_output());
        return individual;
    }

    private XDM xdm;
    private void create_xdm() throws Exception{
        
        XDMParameters param;
        param = new XDMParameters();
        
        param.window = new int[(int)(genome.genes.get(6).value)];
        param.predict = true;
        param.num_channels = 3;
        param.num_channels_prediction = 1;
        param.max_storage_size_in_mb = 1200;

        param.activation_radius = new double[param.num_channels];
        param.resolution = new double[param.num_channels];
        param.prediction_radius = new double[param.num_channels_prediction];
        param.medians = new double[param.num_channels];
        
        param.classes = new int[1];
        param.classes[0] = 0;
        
        int i = 0;
        // value
        param.resolution[i] = genome.genes.get(0).value;
        param.activation_radius[i] = 0;//genome.genes.get(1).value;
        param.prediction_radius[i] = 0;//100;
        param.medians[i] = 15; 
        
        // dow
        i++;
        param.resolution[i] = genome.genes.get(2).value;
        param.activation_radius[i] = genome.genes.get(3).value;
        param.medians[i] = 4; 
        
        // hour
        i++;
        param.resolution[i] = genome.genes.get(4).value;
        param.activation_radius[i] = genome.genes.get(5).value;
        param.medians[i] = 12; 
        
        param.min_num_hard_location = (int)genome.genes.get(7).value;
        
        param.forgetting_rate = (int)genome.genes.get(8).value;
        
        xdm = new XDM(param);
    }
    
    private int dayofweek(int d, int m, int y){
        int t[] = { 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };
        int y1 = y;
        if(m < 3) 
            y1--;
        return (y1 + y1 / 4 - y1 / 100 + y1 / 400 + t[m - 1] + d) % 7;
    }    

    private void read_data(String line, double[] data){
        String[] parts0 = line.split(",");
        String stime = parts0[0];

	// day of week
	String [] parts = stime.split(" ");
	String[] date = parts[0].split("/");
	double dow = dayofweek(Integer.parseInt(date[1]), 
                Integer.parseInt(date[0]), Integer.parseInt(date[2]));
	data[1] = dow;

	// hour
	String[] hours = parts[1].split(":");
	double hr = Integer.parseInt(hours[0]);
        data[2] = hr;

	double value = Double.parseDouble(parts0[1]);
        data[0] = value;
    }
    
    @Override
    public void calculate_cost() {
        double cost = 0.0f;
        BufferedReader reader = null;
        BufferedWriter writer = null;
	String line;
        double[] data = new double[3];
        FutureSample prediction = null;
        int num_points = 0;
        double average_error = 0f;
        
        try {
            create_xdm();
              
            SliderWrite trainer;
            trainer = new SliderWrite(xdm);
            
            SliderRead predictor;
            predictor = new SliderRead(xdm);

            if(get_file_output() != null && get_file_output().length() > 0){
                writer = new BufferedWriter(new FileWriter(new File(get_file_output())));
            }

            AnomalyCalculator anomaly;
            anomaly = new AnomalyCalculator(800);
            
            reader = new BufferedReader(new FileReader(file_input));
            int num_lines = 0;
            while ((line = reader.readLine()) != null) {
                if(Thread.interrupted()){
                    break;
                }
                
                read_data(line, data);
                num_lines++;
                
                double predicted_value = 0f;
                double error = 0f;
                
                // calculate previous line prediction error 
                if(num_lines > 300){
                    error = 1f;
                    predicted_value = 0f;
                    if(prediction != null){
                        error = xdm.get_normalized_error(data, prediction.data);
                        predicted_value = prediction.data[0];
                    } 
                    
                    anomaly.process(error);
                    
                    average_error += error;
                    num_points++;
                }
                
                // write the data to the output file
                if(writer != null)
                    writer.write(
                        Double.toString(data[1]) + "," +
                        Double.toString(data[2]) + "," +
                        Double.toString(data[0]) + "," +
                        Double.toString(anomaly.get_anomaly()) + "," +
                        "1," +
                        Double.toString(predicted_value) + "," +
                        "\n" );
                
                // train
                trainer.train(data, 0);
                
                // predict
                predictor.process(data);
                prediction = predictor.predict();
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
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}           
        
        if(num_points > 0)
            cost = average_error / num_points;
        
        set_cost(cost*100);
        
        System.out.printf("*");
        
        xdm = null;
    }

    /**
     * @return the file_output
     */
    public String get_file_output() {
        return file_output;
    }

    public void set_file_output(String file_output) {
        this.file_output = file_output;
    }
    
}
