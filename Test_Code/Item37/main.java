import java.util.EnumMap;

public class main {
    public static void main(String args[]) {

        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

        for( Plant.LifeCycle lc : Plant.LifeCycle.values()){
            plantsByLifeCycle.put(lc, new HashSet<>);
        }

        for( Plant p : garden){
            plantsByLifeCycle.get(p.LifeCycle).add(p);
        }

        System.out.println(plantsByLifeCycle);
    }
}
