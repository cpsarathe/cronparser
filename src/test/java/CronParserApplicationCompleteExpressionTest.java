import org.junit.jupiter.api.Test;
import org.loop.test.CronExpressionOutputDTO;
import org.loop.test.CronParserApplication;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class CronParserApplicationCompleteExpressionTest {

    @Test
    public void test_5DaysAWeek_AllMonths_1And15thDayOfMonth_0Hour_Every15Minute() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "*/15 0 1,15 * 1-5 /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> minutesExpected = new TreeSet<Integer>();
        int x = 15;
        minutesExpected.add(0);
        for (int i = 15; i < CronParserApplication.MINUTE_MAX_RANGE; i += x) {
            minutesExpected.add(i);
        }
        assertEquals(outputDTO.getMinutes(), minutesExpected);

        Set<Integer> hoursExpected = new TreeSet<Integer>();
        hoursExpected.add(0);
        assertEquals(outputDTO.getHours(), hoursExpected);

        Set<Integer> dayOfMonthExpected = new TreeSet<Integer>();
        dayOfMonthExpected.add(1);
        dayOfMonthExpected.add(15);
        assertEquals(outputDTO.getDayOfMonth(), dayOfMonthExpected);

        Set<Integer> monthExpected = new TreeSet<Integer>();
        for (int i = 1; i <= CronParserApplication.MONTH_MAX_RANGE; i++) {
            monthExpected.add(i);
        }
        assertEquals(outputDTO.getMonths(), monthExpected);

        Set<Integer> dayOfWeekExpected = new TreeSet<Integer>();
        for (int i = 1; i <= 5; i++) {
            dayOfWeekExpected.add(i);
        }
        assertEquals(outputDTO.getDayOfWeek(), dayOfWeekExpected);
    }
}
