/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.greymemory.samples;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import com.greymemory.core.SliderRead;
import com.greymemory.core.SliderWrite;
import com.greymemory.core.SliderRead.FutureSample;
import com.greymemory.core.XDM;
import com.greymemory.core.XDMParameters;
 
/**
 *
 * @author amazhurin
 */
public class RandomSeries {
    public void run(){
	int num_series =
                97;
		//100;
	int num_points =
            //5;
	    100;
	
        int range = 256;

	// init with random data
        ArrayList<double[]> series = new ArrayList<>();
        Random random = new Random(33);
	for (int i = 0; i < num_series; i++){
            double [] s = new double[num_points];
            series.add(s);
            for (int k = 0; k < num_points; k++)
                s[k] = random.nextInt(range);
	}   
        
        Scanner in = new Scanner(System.in);
        System.out.println("Use meta data? (0/1)");
        String input = in.nextLine();
	boolean use_meta = input.equals("1");        
        
        System.out.println("Creating XDM...");
        
        XDMParameters param = new XDMParameters(
                1, // num_channels
                1, // num_channels in future
                6, // window_size
                0.0f, 
                range, 
                1.0f, 
                20.0f, // activation radius
                256.0f, // prediction radius
                1);

        param.predict = true;
        param.max_storage_size_in_mb = 100;
        param.forgetting_rate = 3000;
        param.min_num_hard_location = 7;
        if(use_meta)
            param.meta_data_range = num_series;
        
        double[] buf = new double[param.num_channels];                
        XDM xdm;
        
        try{
            xdm = new XDM(param);
            
            System.out.println("Storage has been created");
            System.out.println("Training...");

            //sample.class_value = 0;
            //sample.future = new float[param.num_channels_prediction];
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < num_series; i++){
                double [] s = series.get(i);
                SliderWrite trainer = new SliderWrite(xdm);
                
                for (int k = 0; k < s.length; k++){
                    buf[0] = s[k];
                    trainer.train(buf, i);
                }
                
                /*
                WindowBuffer buffer = new WindowBuffer(param.num_channels, param.window.length);
                sample.meta_data = i;
                for (int k = 0; k < s.length-1; k++){
                    buf[0] = s[k];
                    buffer.add(buf);
                    if(buffer.is_full()){
                        sample.data = buffer.data;
                        sample.future[0] = s[k+1];
                        xdm.write(sample);
                    }
                }*/
            }   
            
            System.out.printf("Train time = %f\n", 
                (System.currentTimeMillis() - startTime) / 1000.0f);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
                
        
	while (true){
            try{
                System.out.printf("Enter an index between 0..%d\n", num_series-1);
                input = in.nextLine();
                if ("q".equals(input))
                    break;

                int index = Integer.parseInt(input);
                if (index < 0 || index >= num_series){
                    System.out.println("wrong index.");
                    continue;
                }

                System.out.printf("Enter a starting point between 0..%d\n", 
                        num_points-1);
                input = in.nextLine();
                if ("q".equals(input))
                    break;
                int start = Integer.parseInt(input);
                if (start < 0 || start >= num_points){
                    System.out.println("wrong starting point.");
                    continue;
                }

                int noise = 0;
                System.out.printf("Noise amplitude? (0...)");
                input = in.nextLine();
                noise = Integer.parseInt(input);

                int num_total = 0;
                int num_correct = 0;
                int num_correct_meta = 0;

                //WindowBuffer buffer = new WindowBuffer(param.num_channels, param.window.length);
                
                SliderRead predictor = new SliderRead(xdm);

                for (int i = start; i < num_points-1; i++){
                    double d  = series.get(index)[i];
                    if (noise > 0){
                        d += -noise + random.nextInt(2*noise);
                    }
                    
                    buf[0] = d;
                    //buffer.add(buf);
                    //if(!buffer.is_full())
                    //    continue;

                    //sample.data = buffer.data;

                    //Sample sampleRead = xdm.read(sample);
                    predictor.process(buf);
                    FutureSample prediction = predictor.predict();
                    if(prediction == null)
                        continue;

                    num_total++;
                    double future = series.get(index)[i + 1];

                    if (prediction.data[0] != future){
                        System.out.printf("Prediction error = %f\n", 
                                prediction.data[0] - future);
                    }
                    else
                        num_correct++;

                    if (use_meta){
                        if (prediction.meta_data != index){
                            System.out.printf("Meta error = %d\n", 
                                    prediction.meta_data);
                        }
                        else
                            num_correct_meta++;
                    }
                }

                System.out.printf("Correct = %2.1f %%\n", 
                        num_correct * 100.0f / num_total);
                if (use_meta)
                    System.out.printf("Correct meta = %2.1f %%\n", 
                            num_correct_meta * 100.0f / num_total);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
