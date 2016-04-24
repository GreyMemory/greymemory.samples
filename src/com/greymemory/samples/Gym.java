/*
 * Copyright(c) 2015 Anton Mazhurin to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.greymemory.evolution.Individual;
import com.greymemory.evolution.Population;

/**
 *
 * @author amazhurin
 */
public class Gym {
    public void run(String log_file) {
        Scanner in = new Scanner(System.in);
        System.out.println("Gym sample.");
        
        String dir = log_file.substring(0, log_file.lastIndexOf("/"));
        String output_file = dir + "/gym_prediction.csv";
        
        Random rnd = new Random(33);
        
        IndividualGym individual;
        individual = new IndividualGym(log_file, null);
        //individual.genome.randomize(rnd);
        
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
            individual = (IndividualGym)population.get_best_individual();
        }
        
        System.out.println("Running the best...");
        individual.set_file_output(output_file);
        individual.calculate_cost();
        System.out.println("Fitness = " + individual.get_cost());
        individual.genome.print();
        
        System.out.println("Done.");
        
    }    
}
