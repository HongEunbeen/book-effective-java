public enum School {
    FIRST(100, 100, 10), SECOND(80, 120, 11), THIRD(90, 90, 9), FOURTH(100, 130, 13), FIFTH(54, 70, 7),
    SIXTH(78, 89, 8)
    
    public final int girls;
    public final int boys;
    public final int teacher;
    public final int students;

    School(int girls, int boys, int teacher) {
        this.girls = girls;
        this.boys = boys;
        this.teacher = teacher;
        students = girls + boys;
    }

    public int transferStudents(String type) {
        if (type.equals("come")) {
            return students + 1;
        } else {
            return students - 1;
        }

    }
}
