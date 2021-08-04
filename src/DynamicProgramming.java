import src.Solution;

import java.util.*;

public class DynamicProgramming implements Algorithm{

    private enum People{
        A,B,C,D,N;

        Integer getSpeed(){

            switch (this){
                case A: return 1;
                case B: return 2;
                case C: return 7;
                case D: return 10;
                case N: return 0;
            }
            throw new IllegalArgumentException();
        }
    }


    @Override
    public void execute() {
        bridgeCrossingProblem();  }

    public void testMapKeyChangeBehaviour(){
        DynamicStorage s= new DynamicStorage();
    }

    DynamicStorage storage;
    ArrayList<People> start;
    ArrayList<People> ziel;
    Boolean isFackelAmStart=true;
    public void bridgeCrossingProblem(){
        Solution solution= new Solution();
        storage=new DynamicStorage();

        start = new ArrayList<>(Arrays.asList(People.A,People.B,People.C,People.D));
        ziel=new ArrayList<>();
        ArrayList<Vector<People>> allPossibleMoves = new ArrayList<>();

        for (People p1: People.values()){
            for (People p2: People.values()){
                Vector<People> move= new Vector();

                if (p1.equals(p2)){
                    move.add(p1);
                    move.add(People.N);
                    allPossibleMoves.add(move);
                    move= new Vector();
                    move.add(People.N);
                    move.add(p2);
                    allPossibleMoves.add(move);
                }
                else{
                    move.add(p1);
                    move.add(p2);
                    allPossibleMoves.add(move);
                }

            }
        }
        Vector<People> t= new Vector<>();
        solveRecursively(allPossibleMoves);
    }
    private void init(){
        start = new ArrayList<>(Arrays.asList(People.A,People.B,People.C,People.D));
        ziel=new ArrayList<>();
        isFackelAmStart=true;
    }

    private void solveRecursively(ArrayList<Vector<People>> allPossibleMoves) {
        Integer solution=-1;
        HashMap<People,Integer> tabu=new HashMap<>();
        Integer tabuCounter=2;
        Random r=new Random();

        for (People p:People.values()){
            tabu.put(p,tabuCounter);
        }
        while(true){
            HashMap<People,Integer> prevTabu=new HashMap<>();
            init();
            while(ziel.size()!=4){
                if (prevTabu.equals(tabu)){
                    for (People p: tabu.keySet()){
                        if (tabu.get(p)<tabuCounter)
                            tabu.put(p,tabu.get(p)+1);
                    }
                }
                prevTabu=tabu;
                for (Vector<People> move: allPossibleMoves)
                {
                    if (20<r.nextInt(100))continue;//randomness for each solution
                    if (tabu.get(move.get(0))!=tabuCounter|| tabu.get(move.get(1))!=tabuCounter)continue;

                    if ((start.contains(move.get(0))&&start.contains(move.get(1))
                            ||start.contains(move.get(0))&&move.get(1).equals(People.N)
                            ||start.contains(move.get(1))&&move.get(0).equals(People.N))
                        &&isFackelAmStart){ //wenn beide Personen auf der selben Seite
                        if (ziel.contains(move.get(0))&&move.get(0)!=People.N||ziel.contains(move.get(1))&&move.get(1)!=People.N)throw new RuntimeException("messed up the positioning");
                         //wenn tabu aus
                        isFackelAmStart=false;
                        tabu.put(move.get(0),0);// dann setze Tabu
                        start.remove(move.get(0)); // Bewege vom start
                        if (move.get(0)!=People.N)
                            ziel.add(move.get(0)); // zum ziel

                        tabu.put(move.get(1),0);
                        start.remove(move.get(1));
                        if (move.get(1)!=People.N)
                            ziel.add(move.get(1));

                        storage.addMoveToSequenz(move); //speichere zug in Historie
                        for (People p: tabu.keySet()){
                                    if (tabu.get(p)<tabuCounter)
                                        tabu.put(p,tabu.get(p)+1);
                        }
                        continue;
                    }

                    if ((ziel.contains(move.get(0))&&ziel.contains(move.get(1))
                            ||ziel.contains(move.get(0))&&move.get(1).equals(People.N)
                            ||ziel.contains(move.get(1))&&move.get(0).equals(People.N))
                    &&!isFackelAmStart){
                        if ((start.contains(move.get(0))&&move.get(0)!=People.N)||(start.contains(move.get(1))&&move.get(1)!=People.N))throw new RuntimeException("messed up the positioning");
                        isFackelAmStart=true;

                        tabu.put(move.get(0),0);
                        ziel.remove(move.get(0));
                        if (move.get(0)!=People.N)
                            start.add(move.get(0));


                        tabu.put(move.get(1),0);
                        ziel.remove(move.get(1));
                        if (move.get(1)!=People.N)
                            start.add(move.get(1));

                        storage.addMoveToSequenz(move);
                        for (People p: tabu.keySet()){
                            if (tabu.get(p)<tabuCounter)
                                tabu.put(p,tabu.get(p)+1);
                        }
                    }
                    //increment Tabucoutner for every person
                   // System.out.println("Start "+start.toString());
                   // System.out.println("Ziel "+ziel.toString());


                    //kein gültiger Zug
                }
            }
            storage.newMoveSequenz();

            System.out.println("Best Solution: "+storage.getBestSolution());

        }



    }

    private class DynamicStorage {
        //stores Sequence of moves and there resulting time
        HashMap<ArrayList<Vector<People>>,Integer> moveSequenzeTimeLookUpTable=new HashMap<>();
        ArrayList<Vector<People>> currentMoveSequenz=null;
        Integer bestSolution=-1;

        private DynamicStorage() {
            currentMoveSequenz=new ArrayList<Vector<People>>();
        }

        public void newMoveSequenz(){
            if (bestSolution==-1){
                bestSolution=moveSequenzeTimeLookUpTable.get(currentMoveSequenz);
            }
            else{
                bestSolution=Math.min(moveSequenzeTimeLookUpTable.get(currentMoveSequenz),bestSolution);
                System.out.println("Solution: "+ moveSequenzeTimeLookUpTable.get(currentMoveSequenz));
            }
            currentMoveSequenz=new ArrayList<Vector<People>>();
        }

        void addMoveToSequenz(Vector<People> move){

            Integer sequenzTime= moveSequenzeTimeLookUpTable.get(currentMoveSequenz);
            if (sequenzTime==null){
                currentMoveSequenz.add(move);
                moveSequenzeTimeLookUpTable.put(currentMoveSequenz, Math.max(move.get(0).getSpeed(),move.get(1).getSpeed()));
                return;
            }
            currentMoveSequenz.add(move);
            moveSequenzeTimeLookUpTable.put(currentMoveSequenz,sequenzTime +Math.max(move.get(0).getSpeed(),move.get(1).getSpeed()));
        }

        boolean isMoveSequenzPresent(ArrayList<Vector<People>> moveSequenz){
            for(ArrayList<Vector<People>> s: moveSequenzeTimeLookUpTable.keySet()){
                if (s.containsAll(moveSequenz))return true;
            }
            return false;
        }

        Integer getTimeOfMovesequenz(ArrayList<Vector<People>> moveSequenz){
            try{
               return moveSequenzeTimeLookUpTable.get(moveSequenz);
            }catch (Exception e){
                System.out.println("Sequenz not present");
            }
            return null;
        }

        ArrayList<Vector<People>> getCurrentMoveSequenz() {
            return currentMoveSequenz;
        }
        Integer getBestSolution(){
            return bestSolution;
        }
    }
}
