import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing various animals and some plants.
 * 
 * @author Daniel Ratiu and Michael Jacob
 * @version 22/02/2018
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 200;
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 250;
    // The coefficient that a fox will be created in any given grid position.
    private static final double FOX_CREATION_COEFFICIENT = 0.06;
    // The coefficient that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_COEFFICIENT = 0.11;  
    // The coefficient that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_COEFFICIENT = 0.05;
    // The coefficient that a cow will be created in any given grid position.
    private static final double COW_CREATION_COEFFICIENT = 0.07;
    // The coefficient that an elephant will be created in any given grid position.
    // This is split evenly between male and female elephants.
    private static final double ELEPHANT_CREATION_COEFFICIENT = 0.1;
    // The coefficient that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_COEFFICIENT = 0.15;
    // A shared random number generator.
    private static final Random rand = Randomizer.getRandom();
    // The probability that some animal will catch a disease on each step.
    private static final double DISEASE_PROBABILITY = 0.07;
    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The number of completed steps of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // List of plants in the field.
    private List<Plant> plants;
    // The weather conditions for the next step.
    private Weather currentWeather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);

        // Randomises weather in accordance with their probabilties.
        currentWeather = Weather.randomWeightedWeather();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Cow.class, Color.BLACK);
        view.setColor(MaleElephant.class, Color.MAGENTA);
        view.setColor(FemaleElephant.class, Color.MAGENTA);
        view.setColor(Eagles.class, Color.RED);
        view.setColor(Plant.class, Color.GREEN);
        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
                
        // Run the effects of weather conditions.
        processWeather();

        // See if an animal becomes infected.
        checkDisease();

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Provide space for new plants.
        List<Plant> newPlants = new ArrayList<>();
        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if (isNight()){
                animal.nightAct(newAnimals);
            }
            else{
                animal.dayAct(newAnimals);
            }
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants);
            if(! plant.isAlive()) {
                it.remove();
            }
        }

        
        // Add the newly born organisms to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);
        
        step++;
        
        // Randomise the weather again for the next step.
        currentWeather = Weather.randomWeightedWeather();
        
        showInfo();
        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
        showInfo();
    }

    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    { 
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    animals.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    animals.add(rabbit);
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    Eagles eagle = new Eagles(true, field, location);
                    animals.add(eagle);
                }
                else if(rand.nextDouble() <= COW_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    Cow cow = new Cow(true, field, location);
                    animals.add(cow);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant( field, location);
                    plants.add(plant);
                }
                else if(rand.nextDouble() <= ELEPHANT_CREATION_COEFFICIENT) {
                    Location location = new Location(row, col);
                    int genderDecider = rand.nextInt(2);
                    if (genderDecider == 0){
                        FemaleElephant elephant = new FemaleElephant (true, field, location);
                        animals.add(elephant);
                    }
                    else{
                        MaleElephant elephant = new MaleElephant(true, field, location);
                        animals.add(elephant);
                    }

                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Process the effect of the current weather condition.
     */
    private void processWeather()
    {
        switch(currentWeather){
            case RAINING: raining();
            break;
            case SUNNY: sunny();
            break;
            case WINDY: windy();
            break;
            case SNOWING: snowing();
            break;
        }
    }

    /**
     * Actions to take when raining.
     */
    private void raining()
    {
        // Plants grow more.
        for (Plant plant : plants){
            plant.grow();
        }
        // Eagles struggle to find food in rain.
        for (Animal animal : animals){
            if( animal instanceof Eagles){
                animal.incrementHunger();
            }
        }
    }

    /**
     * Actions to take when sunny.
     */
    private void sunny()
    {
        // Plants grow more when its sunny.
        for (Plant plant : plants){
            plant.grow();
        }
    }

    /**
     * Actions to take when windy.
     */
    private void windy()
    {
        // Wind can destroy plants.
        for (Plant plant : plants){
            if(rand.nextDouble() > plant.calculateWindSurvivalProbability()) {
                plant.setDead();
            }
        }
    }

    /**
     * Actions to take when snowing.
     */
    private void snowing()
    {
        // Plants and animals can be killed by snow. 
        for (Plant plant : plants){
            if(rand.nextDouble() > plant.calculateSnowSurvivalProbability()) {
                plant.setDead();
            }
        }
        for (Animal animal: animals){
            if(rand.nextDouble() > animal.calculateSnowSurvivalProbability()) {
                animal.setDead();
            }
        }
    }

    /**
     * Infect a random animal at the disease probability rate.
     */
    private void checkDisease()
    {
        if(rand.nextDouble() <= DISEASE_PROBABILITY){
            findRandomAnimal().infect();
        }
    }

    /**
     * Find a random animal in the field.
     *  @return A random animal in the field.
     */
    private Animal findRandomAnimal()
    {
        return animals.get(rand.nextInt(animals.size()));
    }
    
    /**
     * Check if it is night. Night occurs every four steps, first ocurring on the fourth step.
     * @return True if it is night.
     */

    private boolean isNight()
    {
        if((step % 4 ) == 3){
            return true;
        }
        return false;
    }
    
    /**
     * Show time of day and weather information for the next step to be executed.
     */
    private void showInfo(){
        String dayNight = "Time: ";
        if (isNight()){
            dayNight += "night";
        }
        else{
            dayNight += "day";
        }
        String weatherString = "Weather: " + currentWeather.toString().toLowerCase();
        view.setInfoText(dayNight + "   " + weatherString);
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
