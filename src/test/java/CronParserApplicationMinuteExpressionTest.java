import org.junit.jupiter.api.Test;
import org.loop.test.*;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CronParserApplicationMinuteExpressionTest {

    @Test
    public void test_Minute_Valid_Number() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "5 * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        expected.add(5);
        assertEquals(outputDTO.getMinutes(), expected);
    }

    @Test
    public void test_Minute_InValid_Number_Every_120_Minute() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "120 * * * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void test_Minute_Valid_All_Possible_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "* * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        for (int i = 0; i < CronParserApplication.MINUTE_MAX_RANGE; i++) {
            expected.add(i);
        }
        assertEquals(outputDTO.getMinutes(), expected);
    }

    @Test
    public void test_Minute_Valid_List_Of_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "5,10 * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        expected.add(5);
        expected.add(10);
        assertEquals(outputDTO.getMinutes(), expected);
    }

    @Test
    public void test_Minute_Valid_Range_Of_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "5-10 * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        for (int i = 5; i <= 10; i++) {
            expected.add(i);
        }
        assertEquals(outputDTO.getMinutes(), expected);
    }

    @Test
    public void test_Minute_Valid_All_Possible_With_Skip() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "*/15 * * * * /usr/bin/find";
        CronExpressionOutputDTO outputDTO = cronParserApplication.parseExpression(expression);
        Set<Integer> expected = new TreeSet<Integer>();
        int x = 15;
        expected.add(0);
        for (int i = x; i < CronParserApplication.MINUTE_MAX_RANGE; i += x) {
            expected.add(i);
        }
        assertEquals(outputDTO.getMinutes(), expected);
    }

    @Test
    public void test_Minute_InValid_All_Possible_Values() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "*** * * * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.equals(expectedMessage));
    }

    @Test
    public void test_Minute_InValid_Number_Character() {
        CronParserApplication cronParserApplication = new CronParserApplication();
        String expression = "a12 * * * * /usr/bin/find";
        Exception exception = assertThrows(CronParsingException.class, () -> {
            cronParserApplication.parseExpression(expression);
        });
        String expectedMessage = "Error in parsing cron expression " + expression;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.equals(expectedMessage));
    }

}
