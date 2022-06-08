public class Main {
    public static void main(String args[]) {
        String type = args[0];
        int all = School.FIRST.students();
        for (School sh : School.values()) {
            System.out.println(sh + "의 총 학생 수는 : " + all);
        }

        
    }
}
