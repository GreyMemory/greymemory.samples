package com.greymemory.samples;

import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author amazhurin
 */
public class Main { 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        Scanner in = new Scanner(System.in);
        System.out.println("GreyMem (c) 2015 Anton Mazhurin");
        
        while(true){
            try{
                System.out.println("1 - General");
                System.out.println("2 - Random Series");
                System.out.println("3 - Gym");
                System.out.println("4 - Server");
                System.out.println("5 - OCR");
                System.out.println("q - quit");

                String s = in.nextLine();
                if(s.equals("q"))
                    break;

                switch (s){
                    case "1" :  {
                        com.greymemory.samples.General test;
                        test = new General();
                        test.run();
                    } break;
                    case "2" :  {
                        com.greymemory.samples.RandomSeries test;
                        test = new RandomSeries();
                        test.run();
                    } break;
                    case "3" :  {
                        com.greymemory.samples.Gym test;
                        test = new Gym();
                        test.run(System.getProperty("user.dir") + 
                                //"/data/gym/gym.csv"
                                //"/data/gym/gym_simulation.csv"
                                "/data/gym/gym_tuesdays.csv"
                        );
                    } break;
                    case "4" :  {
                        com.greymemory.samples.Server test;
                        test = new Server();

                        //test.run(System.getProperty("user.dir") + "/data/equalite/chhost2_CONNECTIONS.csv",
                        //        IndividualServer.ChannelType.CONNECTIONS);


                        test.run(System.getProperty("user.dir") + 
                                //"/data/equalite/ovh4_TCPTRAFFIC.csv",
                                //"/data/equalite/cymru1_TCPTRAFFIC.csv",

                                //"/data/equalite/linode4_TCPTRAFFIC.csv",
                                //"/data/equalite/prometeus1_TCPTRAFFIC.csv",
                                //"/data/equalite/veeble2_TCPTRAFFIC.csv",
                                //"/data/equalite/seflow1_TCPTRAFFIC.csv",


                                "/data/equalite/cool12_TCPTRAFFIC.csv",
                                //"/data/equalite/kimsufi3_TCPTRAFFIC.csv",
                                IndividualServer.ChannelType.TCP_TRAFFIC);


                        /*
                        test.run(System.getProperty("user.dir") + 
                                "/data/equalite/one2_Current Load.csv",
                                //"/data/equalite/test1.csv",
                                IndividualServer.ChannelType.CPU_LOAD
                        );*/

                    } break;
                    case "5" :  {
                        com.greymemory.samples.OCR test;
                        test = new OCR();
                        test.run(System.getProperty("user.dir") + 
                            "/data/ocr/train.csv");
                    } break;
                }

                //break;
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
}

/*
java -cp ./GreyMemory.samples.jar com.greymemory.samples.Main
*/