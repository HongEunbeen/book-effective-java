class FigureRe {
    enum Shape {
        RECTANGEL, CIRCLE
    };

    final Shape shape;

    double length;
    double width;

    double radius;

    Figure(double length, double width) {
        shape = Shape.RECTANGEL;
        this.length = length;
        this.width = width;
    }

    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    double area() {
        switch (shpae) {
            case RECTANGEL:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }

}
