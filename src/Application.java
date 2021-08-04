public class Application {

    public static void main(String[] args){
        Application app= new Application();
    }
    private Application() {
        Algorithm bridgecrossingTabusearch = new DynamicProgramming();
        bridgecrossingTabusearch.execute();
    }
}
