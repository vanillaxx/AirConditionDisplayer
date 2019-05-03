package app;

import app.AirConditionApplication;
import app.GIOSService;
import org.apache.commons.cli.*;

/**
 * Root of a program
 */
public class Main {
    /**
     * Apply funcionality and parametres of program
     * @param args arguments of program which can be applicated from commandline
     */
    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Option function1 = Option.builder("f1").desc("Actual air condition index for station").build();
        Option function2 = Option.builder("f2").desc("Value of parameter for station and particular date").build();
        Option function3 = Option.builder("f3").desc("Average value of parameter for particular period").build();
        Option function4 = Option.builder("f4").desc("Parameter with the most amplitude for stations from particular time").build();
        Option function5 = Option.builder("f5").desc("Parameter which value is the smallest at special time").build();
        Option function6 = Option.builder("f6").desc("Parameters which are above the norm").build();
        Option function7 = Option.builder("f7").desc("The most and the least value of parameter").build();
        Option function8 = Option.builder("f8").desc("Graph of parameter values for stations and period").build();

        Option nameOfStation = Option.builder("s").longOpt("station").hasArg(true).hasArgs().argName("nameOfStation").build();
        Option date = Option.builder("d").longOpt("date").hasArg(true).argName("startDate").build();
        Option finish = Option.builder("f").longOpt("finish").hasArg(true).argName("endDate").build();
        Option parameter = Option.builder("p").longOpt("parameter").hasArg(true).argName("nameOfParameter").build();
        Option character = Option.builder("c").longOpt("character").hasArg(true).argName("character").build();
        Option amount = Option.builder("n").longOpt("number").hasArg(true).argName("amountOfParameter").build();
        Option help = Option.builder("h").longOpt("help").hasArg(false).build();

        options.addOption(function1);
        options.addOption(function2);
        options.addOption(function3);
        options.addOption(function4);
        options.addOption(function5);
        options.addOption(function6);
        options.addOption(function7);
        options.addOption(function8);
        options.addOption(nameOfStation);
        options.addOption(date);
        options.addOption(finish);
        options.addOption(parameter);
        options.addOption(character);
        options.addOption(amount);
        options.addOption(help);

        try{
            if(args.length!=0){
                AirConditionApplication airConditionApplication = new AirConditionApplication(new GIOSService());
                CommandLine line = parser.parse(options,args);
                if(line.hasOption("h")){
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("Air Condition Application", options);
                }
                if(line.hasOption("f1")){
                    if(line.hasOption("s")){
                        String nameOfStation1 = line.getOptionValues("s")[0];
                        System.out.println(airConditionApplication.displayActualAirConditionIndex(nameOfStation1));

                    }
                    else {
                        System.out.println("Apply name of a station");
                        System.out.println();
                    }
                }
                else if(line.hasOption("f2")){
                    if(line.hasOption("s") && line.hasOption("p") && line.hasOption("d")){
                        String nameOfStation2 = line.getOptionValues("s")[0];
                        String nameOfParameter2 = line.getOptionValue("p");
                        String date2 = line.getOptionValue("d");
                        System.out.println(airConditionApplication.displayActualValueOfParameter(nameOfStation2,date2,nameOfParameter2));
                        System.out.println();
                    }
                    else {
                        System.out.println("Apply name of station, parameter and date");
                        System.out.println();
                    }
                }
                else if(line.hasOption("f3")){
                    if(line.hasOption("s") && line.hasOption("p") && line.hasOption("d") && line.hasOption("f")){
                        String nameOfStation3 = line.getOptionValues("s")[0];
                        String nameOfParameter3 = line.getOptionValue("p");
                        String date3 = line.getOptionValue("d");
                        String finish3 = line.getOptionValue("f");
                        System.out.println(airConditionApplication.displayAverageValueOfParameter(nameOfStation3,date3,finish3,nameOfParameter3));
                        System.out.println();
                    }
                    else {
                        System.out.println("Apply name of parameter and start and end date and time");
                        System.out.println();
                    }
                }
                else if(line.hasOption("f4")){
                    if(line.hasOption("s") && line.hasOption("d")){
                        String[] namesOfStations4 = line.getOptionValues("s");
                        String date4 = line.getOptionValue("d");
                        System.out.println(airConditionApplication.findTheMostAmplitude(namesOfStations4,date4));
                    }
                    else {
                        System.out.println("Apply names of stations and date of start");
                    }
                }
                else if(line.hasOption("f5")){
                    if(line.hasOption("d")){
                        String date5 = line.getOptionValue("d");
                        System.out.println(airConditionApplication.findTheSmallestValueOfParameter(date5));
                    }
                    else {
                        System.out.println("Apply date");
                    }
                }
                else if(line.hasOption("f6")){
                    if(line.hasOption("s") && line.hasOption("d") && line.hasOption("n")){
                        String nameOfStation6 = line.getOptionValues("s")[0];
                        String date6 = line.getOptionValue("d");
                        String n6string = line.getOptionValue("n");
                        int n6 = Integer.parseInt(n6string);
                        System.out.println(airConditionApplication.displaySensorsWithTheMostValueOfParameter(nameOfStation6,date6,n6));
                    }
                    else {
                        System.out.println("Apply name of station, date and amount of parameters to display");
                    }
                }
                else if(line.hasOption("f7")){
                    if(line.hasOption("p")){
                        String nameOfParameter7 = line.getOptionValue("p");
                        System.out.println(airConditionApplication.getMaximalAndMinimalValueOfParameter(nameOfParameter7));
                    }
                    else {
                        System.out.println("Apply name of parameter");
                    }
                }
                else if(line.hasOption("f8")){
                    if(line.hasOption("s") && line.hasOption("p") && line.hasOption("d") && line.hasOption("f") && line.hasOption("c")){
                        String[] nameOfStations8 = line.getOptionValues("s");
                        String nameOfParameter8 = line.getOptionValue("p");
                        String date8 = line.getOptionValue("d");
                        String finish8 = line.getOptionValue("f");
                        String character8 = line.getOptionValue("c");
                        System.out.println(airConditionApplication.displayGraph(nameOfParameter8,nameOfStations8,date8,finish8,character8));
                    }
                    else {
                        System.out.println("Apply names of stations, name of parameter, character of frame, start and end date and time");
                    }
                }
                else{
                    System.out.println("Please, choose a functionality");
                }
            }
            else {
                System.out.println("Press -h for help");
            }
        } catch (Exception e) {
            System.out.println("unexpected exception" + e);
        }
    }
}
