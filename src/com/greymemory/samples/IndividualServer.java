/*
 * Copyright(c) 2015 Mindmick Corp. to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException; 

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.greymemory.anomaly.AnomalyCalculator;
import com.greymemory.core.SliderRead;
import com.greymemory.core.SliderWrite;
import com.greymemory.core.XDM;
import com.greymemory.core.XDMParameters; 
import com.greymemory.evolution.Gene;
import com.greymemory.evolution.Individual;
import java.util.ArrayList;

/**
 *
 * @author amazhurin
 */
public class IndividualServer extends Individual{
    private final String file_input;
    private String file_output;

    public enum ChannelType{
        TCP_TRAFFIC,
        CONNECTIONS,
        CPU_LOAD
    }
    
    private ChannelType channel_type;    
    public IndividualServer(String file_input, String file_output, ChannelType channel_type){
        this.file_input = file_input;
        this.channel_type = channel_type;
        
        switch(channel_type){
            case TCP_TRAFFIC:
                //genome.genes.add(new Gene("value_resolution", 1, 30));
                //genome.genes.get(genome.genes.size()-1).value = 7.5f;
                //genome.genes.get(genome.genes.size()-1).value = 3f;
                
                genome.genes.add(new Gene("value_resolution", 2, 8000));
                genome.genes.get(genome.genes.size()-1).value = 2500f;// 
                
                genome.genes.add(new Gene("value_radius", 50, 5500));
                genome.genes.get(genome.genes.size()-1).value = 0f;
                
                break;
            case CONNECTIONS:
                genome.genes.add(new Gene("value_resolution", 1, 20));
                genome.genes.get(genome.genes.size()-1).value = 4.4f;

                genome.genes.add(new Gene("value_radius", 40, 320));
                genome.genes.get(genome.genes.size()-1).value = 200f;
                break;
            case CPU_LOAD:
                genome.genes.add(new Gene("value_resolution", 0.001f, 0.01f));
                genome.genes.get(genome.genes.size()-1).value = 0.002f;
                
                genome.genes.add(new Gene("value_radius", 0.01f, 0.20f));
                genome.genes.get(genome.genes.size()-1).value = 0.02f;
                break;
        }
        
        //2
        genome.genes.add(new Gene("dow_resolution",1f, 0f));
        genome.genes.get(genome.genes.size()-1).value = 1f;
                
        //3
        genome.genes.add(new Gene("dow_radius", 0f, 3f));
        genome.genes.get(genome.genes.size()-1).value = 1.2f;
        
        //4
        genome.genes.add(new Gene("hour_resolution", 1f, 0f));
        genome.genes.get(genome.genes.size()-1).value = 1f;
        
        //5
        genome.genes.add(new Gene("hour_radius", 0f, 8f));
        //genome.genes.get(genome.genes.size()-1).value = 8f;
        genome.genes.get(genome.genes.size()-1).value = 6.4f;
        
        //6
        genome.genes.add(new Gene("window", 3f, 5f));
        //genome.genes.get(genome.genes.size()-1).value = 3.8f;
        genome.genes.get(genome.genes.size()-1).value = 3f;
        
        //7
        genome.genes.add(new Gene("num_hl", 50f, 150f));
        //genome.genes.get(genome.genes.size()-1).value = 76f;
        genome.genes.get(genome.genes.size()-1).value = 70f;
        
        //8
        genome.genes.add(new Gene("forgetting_rate", 10f, 200f));
        //genome.genes.get(genome.genes.size()-1).value = 621;
        genome.genes.get(genome.genes.size()-1).value = 10;
        

        //ovh4 hi res
        genome.genes.get(0).value = 5f;
        genome.genes.get(1).value = 500f;
        genome.genes.get(8).value = 10;
        /*
        //ovh4 low res
        genome.genes.get(0).value = 3000f;
        genome.genes.get(1).value = 300000f;
        genome.genes.get(8).value = 30;
*/
        //cymru1
        //genome.genes.get(0).value = 500f;
        //genome.genes.get(1).value = 5000f;
        //genome.genes.get(8).value = 10;

        //linode4
        //genome.genes.get(0).value = 2500f;
        //genome.genes.get(1).value = 100000f;
        //genome.genes.get(8).value = 80;

        
        // seflow1
        //genome.genes.get(0).value = 8000f;
        //genome.genes.get(1).value = 150000f;
        
        // prometeus1
        //genome.genes.get(0).value = 5000f;
        //genome.genes.get(1).value = 50000f;
        
        // veeble2
        //genome.genes.get(0).value = 500f;
        //genome.genes.get(1).value = 30000f;
        //genome.genes.get(8).value = 80;
        
        
    }
    
    @Override
    public Individual create() {
        IndividualServer individual = new IndividualServer(
                file_input, file_output, channel_type);
        return individual;
    }

    public void set_file_output(String file_output) {
        this.file_output = file_output;
    }

    private ArrayList<XDM> xdm;
    
    private XDMParameters create_param(){
        XDMParameters param;
        param = new XDMParameters();
        
        param.window = new int[(int)(genome.genes.get(6).value)];
        param.predict = true;
        param.num_channels = 3;
        param.num_channels_prediction = 1;
        param.max_storage_size_in_mb = 2000;

        param.activation_radius = new double[param.num_channels];
        param.resolution = new double[param.num_channels];
        param.prediction_radius = new double[param.num_channels_prediction];
        param.medians = new double[param.num_channels];
        
        param.classes = new int[1];
        param.classes[0] = 0;
        
        int i = 0;
        // value
        if(channel_type == channel_type.TCP_TRAFFIC){
            param.medians[i] = 10000f; 
            //param.prediction_radius[i] = 10000;
            //param.prediction_radius[i] = 100000;
        } else
        if(channel_type == channel_type.CONNECTIONS){
            param.medians[i] = 400; 
            param.prediction_radius[i] = 100000;
        } if(channel_type == channel_type.CPU_LOAD){
            param.medians[i] = 0.1f; 
        }
        
        param.resolution[i] = genome.genes.get(0).value;
        param.activation_radius[i] = genome.genes.get(1).value;
        
        // dow
        i++;
        param.resolution[i] = genome.genes.get(2).value;
        param.activation_radius[i] = 0;//genome.genes.get(3).value;
        param.medians[i] = 4; 
        
        // hour
        i++;
        param.resolution[i] = genome.genes.get(4).value;
        param.activation_radius[i] = genome.genes.get(5).value;
        param.medians[i] = 12; 
        
        param.min_num_hard_location = (int)genome.genes.get(7).value;
        
        param.forgetting_rate = (int)genome.genes.get(8).value;
        
        return param;
    }
    
    private void create_xdm() throws Exception{
        xdm = new ArrayList<>();
        for(int i = 0; i < 4; i++)
        {
            XDMParameters param = create_param();
            param.resolution[0] = param.resolution[0] * Math.pow(10, i);
            param.activation_radius[0] = 0;
            xdm.add(new XDM(param));
        }
    }
    
    private java.util.Date read_data(String line, double[] data){
        String[] parts0 = line.split(",");
        String stime = parts0[0];
        
        long timeStamp = Long.parseLong(stime);

        java.util.Date time = new java.util.Date((long)timeStamp*1000);
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        data[1] = c.get(Calendar.DAY_OF_WEEK);        

	// hour
        data[2] = c.get(Calendar.HOUR);

	double value = Double.parseDouble(parts0[1]);
        data[0] = value;
        return time;
    }

    @Override
    public void calculate_cost() {
        double cost = 0.0f;
        BufferedReader reader = null;
        BufferedWriter writer = null;
	String line;
        double[] data = new double[3];
        ArrayList<SliderRead.FutureSample> predictions = new ArrayList<>();
        int num_points = 0;
        double average_error = 0f;
        
        try {
            create_xdm();
              
            ArrayList<SliderWrite> trainers = new ArrayList<>();
            ArrayList<SliderRead> predictors = new ArrayList<>();

            for(int i = 0; i < xdm.size(); i++){
                trainers.add(new SliderWrite(xdm.get(i)));
                predictors.add(new SliderRead(xdm.get(i)));
            }

            if(get_file_output() != null && get_file_output().length() > 0){
                writer = new BufferedWriter(new FileWriter(new File(get_file_output())));
            }

            reader = new BufferedReader(new FileReader(file_input));
            int num_lines = 0;
            
            AnomalyCalculator anomaly;
            anomaly = new AnomalyCalculator(2*7*24*12);

            while ((line = reader.readLine()) != null) {
                if(Thread.interrupted()){
                    break;
                }
                
                java.util.Date date = read_data(line, data);
                num_lines++;
                
                double predicted_value = 0f;
                
                double min_error =
                    xdm.get(xdm.size()-1).param.prediction_radius[0]*
                        xdm.get(xdm.size()-1).param.resolution[0];
                
                // calculate previous line prediction error 
                if(num_lines > 300){
                    predicted_value = 0f;
                    
                    // find the minimum prediction error
                    for(SliderRead.FutureSample p: predictions){
                        if(p != null){
                            double e = Math.abs(data[0] - p.data[0]);
                            if(min_error > e){
                                min_error = e;
                                predicted_value = p.data[0];
                            }
                        }
                    }
                    anomaly.process(min_error);
                    average_error += min_error;
                    num_points++;
                }
                
                // write the data to the output file
                if(writer != null)
                    writer.write(
                        date.toString() + "," +
                        Double.toString(data[1]) + "," +
                        Double.toString(data[2]) + "," +
                        Double.toString(data[0]) + "," +
                        Double.toString(anomaly.get_anomaly()) + "," +
                        "1," +
                        Double.toString(predicted_value) + "," +
                        Double.toString(min_error) + 
                        "\n" 
                    );
                
                // train
                for(SliderWrite trainer: trainers){
                    trainer.train(data, 0);
                }
                
                // predict
                predictions.clear();
                for(SliderRead predictor: predictors){
                    predictor.process(data);
                    predictions.add(predictor.predict());
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
   
}
