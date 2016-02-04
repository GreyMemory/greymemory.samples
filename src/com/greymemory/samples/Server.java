/*
 * Copyright(c) 2015 Mindmick Corp. to present
 * Anton Mazhurin & Nawwaf Kharma
 */
package com.greymemory.samples;

import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.greymemory.evolution.Population;

/**
 * 
 * @author amazhurin
 */
public class Server {
    public void run(String log_file, IndividualServer.ChannelType type) {
        Scanner in = new Scanner(System.in);
        System.out.println("Equalite sample.");
        
        String dir = log_file.substring(0, log_file.lastIndexOf("."));
        String output_file = dir + "_prediction.csv";
        
        Random rnd = new Random(33);
        
        IndividualServer individual;
        individual = new IndividualServer(log_file, "", type);
        
        System.out.print("Enter population size : ");
        String s = in.nextLine();
        int population_size = Integer.parseInt(s);
        
        if(population_size > 0){
            // EVOLUTION
            individual.genome.randomize(rnd);
            Population population;
            population = new Population(individual, population_size, 8);
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

            individual = (IndividualServer)population.get_best_individual();
        }
        
        if(individual != null){
            System.out.println("Running the best..." + output_file);
            individual.set_file_output(output_file);
            individual.calculate_cost();
            System.out.println("Fitness = " + individual.get_cost());
            individual.genome.print();
        }
        
        System.out.println("Done.");
        
    }    
}
