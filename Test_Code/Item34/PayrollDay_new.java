enum PayrollDay_new {
    MONDAY(WEEKDAY), TUSEDAY(WEEKDAY), WEDNESDAY(WEEKDAY), THURSDAY(WEEKDAY), FRIDAY(WEEKDAY), SATURDAY(WEEKEND),
    SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay_new(PayType payType) {
        this.payType = payType;
    }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    enum PayType {
        WEEKDAY {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked <= MINS_PER_SHIFT ? 0 : (MINS_PER_SHIFT - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int minutesWorked, int payRate);

        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minWorked, int payRate){
            int basePay = minWorked * payRate;
            return basePay + overtimePay(minWorked, payRate)
        }
    }
}
// 전략 열거 타입 패턴은 switch문 보다 복잡하지만 더 안전하고 유연하다.