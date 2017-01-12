import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Garfield g = new Garfield();
            g.start();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

class Garfield {
    private char[][] _world;
    private int _time;

    public void start() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int _numTasks = Integer.parseInt(br.readLine());
        if(!((_numTasks >= 1) && (_numTasks <= 1000))) System.err.print("1 <= numTasks <= 1000");
        for(int i = 0; i < _numTasks; i++){
            String[] _rowsColumnsAndTime = br.readLine().split(" ");
            int numRows = Integer.parseInt(_rowsColumnsAndTime[1]);
            int numColumns = Integer.parseInt(_rowsColumnsAndTime[0]);

            _world = new char[numRows][numColumns];
            _time = Integer.parseInt(_rowsColumnsAndTime[2]);

            for(int row = 0; row < numRows; row++){
                _world[row] = br.readLine().toCharArray();
            }

            int currTask = i + 1;
            //int foodEaten = 0;
            //System.out.println(currTask + " " + NextHouse(new ArrayList<Location>(), foodEaten, _time, findStartingPosition()));
            System.out.println(currTask + " " + findBestRoute());
        }
    }

    private int findBestRoute() {
        int foodEaten = 0;
        int timeRemaining = _time;
        Location home = findStartingPosition();
        List<Integer> lstFoodEaten = new ArrayList<Integer>();
        for(Location loc : findAllFood()) {
            if(canGetInTime(timeRemaining, home, loc)){
                int calcTime = timeRemaining - calculateElapsedTime(calculateSteps(home, loc));
                List<Location> lstVisited = new ArrayList<Location>();
                lstVisited.add(loc);
                lstFoodEaten.add(NextHouse(lstVisited, foodEaten + 1, calcTime, loc));
            }
        }

        return Collections.max(lstFoodEaten);
    }

    private int NextHouse(List<Location> lstVisited, int foodEaten, int timeRemaining, Location current) {
        if(timeRemaining >= 0) {
            Location closest = findClosestTo(lstVisited, current);
            if(closest == null) return foodEaten;
            if(canGetInTime(timeRemaining, current, closest)) {
                timeRemaining = goEatInHouse(timeRemaining, foodEaten, current, closest);
                foodEaten++;
                lstVisited.add(closest);
                foodEaten = NextHouse(lstVisited, foodEaten, timeRemaining, closest);
            } else {
                lstVisited.add(closest);
                foodEaten = NextHouse(lstVisited, foodEaten, timeRemaining, current);
            }
        }
        return foodEaten;
    }

    private int goEatInHouse(int timeRemaining, int foodEaten, Location current, Location goal) {
        timeRemaining -= calculateSteps(current, goal) + 1;
        return timeRemaining;
    }

    private int calculateElapsedTime(int numSteps) {
        return (2 * numSteps) + 1;
    }

    private int calculateSteps(Location current, Location goal){
        return Math.abs(current.getRow() - goal.getRow()) + Math.abs(current.getCol() - goal.getCol());
    }

    private boolean canGetInTime(int timeRemaining, Location current, Location goal){
        // First calculate the number of steps we will have to make C(x,y) => G(a,b) ==> Steps = abs(x-a) + abs(y-b);
        int numSteps = calculateSteps(current, goal);
        int stepsFromHome = calculateSteps(current,findStartingPosition());
        // If the cat can go and get back to the current position and eat at the goal position then return true
        if (timeRemaining - (numSteps + 1) - stepsFromHome >= 0) return true;
        return false;
    }

    private Location findClosestTo(List<Location> lstVisited, Location current){
        List<Location> lstVisitable = findAllFood();
        lstVisitable.removeAll(lstVisited);

        if(lstVisitable.size() <= 0) return null;
        //If can't find any others, return a impossible location
        Location closest = lstVisitable.get(0);
        for(Location loc : lstVisitable){
            if(calculateSteps(current, closest) > calculateSteps(current, loc))
                closest = loc;
        }
        return closest;
    }

    private List<Location> findAllFood() {
        List<Location> lstHouses = new ArrayList<>();
        for (int row = 0; row < _world.length; row++) {
            for (int col = 0; col < _world[0].length; col++){
                if(_world[row][col] == 'E') lstHouses.add(new Location(row, col));
            }
        }
        return lstHouses;
    }

    private Location findStartingPosition() {
        for(int row = 0; row < _world.length; row++){
            for(int col = 0; col < _world[0].length; col++){
                if(_world[row][col] == 'G') return new Location(row,col);
            }
        }
        // This will never happen unless there is faulty input
        return null;
    }
}

class Location {
    private int _row, _col;

    public Location (int row, int col) {
        this._row = row;
        this._col = col;
    }

    public int getRow(){
        return _row;
    }

    public int getCol(){
        return _col;
    }

    public boolean equals(Object obj){
        if(obj instanceof Location && ((Location) obj).getRow() == this._row && ((Location) obj).getCol() == this._col) return true;
        else return false;
    }
}
