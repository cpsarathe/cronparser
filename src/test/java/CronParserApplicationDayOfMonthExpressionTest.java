import org.junit.jupiter.api.Test;
import org.loop.test.CronExpressionOutputDTO;
import org.loop.test.CronParserApplication;
import org.loop.test.CronParsingException;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CronParserApplicationDayOfMonthExpressionTest {

    @Test
    public void test_DayOfMonth_Valid_Number() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * 5 * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        expected.add(5);
        assertEquals(outputDTO.getDayOfMonth(), expected);
    }

    @Test
    public void test_DayOfMonth_InValid_Number_35thDay() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * 35 * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void test_DayOfMonth_Valid_All_Possible_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        for (int i = 1; i <= CronParserApplication.DAY_OF_MONTH_MAX_RANGE; i++) {
            expected.add(i);
        }
        assertEquals(outputDTO.getDayOfMonth(), expected);
    }

    @Test
    public void test_DayOfMonth_Valid_List_Of_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * 2,12 * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        expected.add(2);
        expected.add(12);
        assertEquals(outputDTO.getDayOfMonth(), expected);
    }

    @Test
    public void test_DayOfMonth_Valid_Range_Of_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * 3-7 * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        for (int i = 3; i <= 7; i++) {
            expected.add(i);
        }
        assertEquals(outputDTO.getDayOfMonth(), expected);
    }

    @Test
    public void test_DayOfMonth_Valid_All_Possible_With_Skip() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * */2 * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        int x = 2;
        expected.add(1);
        for (int i = 1 + x ; i < CronParserApplication.DAY_OF_MONTH_MAX_RANGE; i += x) {
            expected.add(i);
        }
        assertEquals(outputDTO.getDayOfMonth(), expected);
    }

    @Test
    public void test_DayOfMonth_InValid_All_Possible_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * *** * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.equals(expectedMessage));
    }

    @Test
    public void test_DayOfMonth_InValid_Number_Character() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * xa1(2 * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.equals(expectedMessage));
    }

}
