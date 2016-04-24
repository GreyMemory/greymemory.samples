/*
 * Copyright(c) 2015 Anton Mazhurin to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import com.greymemory.evolution.Population;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amazhurin
 */ 
public class OCR {
    public void run(String train_file) {
        Scanner in = new Scanner(System.in);
        System.out.println("OCR sample.");
        
        Random rnd = new Random(33);
        
        IndividualOCR individual;
        individual = new IndividualOCR("./data/ocr/train.csv");
        
        System.out.print("Enter population size : ");
        String s = in.nextLine();
        int population_size = Integer.parseInt(s);
        
        if(population_size > 0){
            Population population;
            population = new Population(individual, population_size, 4);
            population.start();

            s = in.nextLine();
            System.out.println("Stopping evolution...");
            if(population.isAlive())
                population.interrupt();
            try {
                population.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Gym.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Stopped.");
            individual = (IndividualOCR)population.get_best_individual();
        }
        
        System.out.println("Running the best...");
        //individual.set_file_output(output_file);
        individual.calculate_cost();
        System.out.println("Fitness = " + individual.get_cost());
        individual.genome.print();
        
        System.out.println("Done.");
        
    }    
    
}
