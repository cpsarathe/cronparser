package org.loop.test;

/**
 * https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
 */
public class CronParserApplication {

    public static final int MONTH_MAX_RANGE = 12;
    public static final int DAY_OF_MONTH_MAX_RANGE = 31;
    public static final int DAY_OF_WEEK_MAX_RANGE = 7;
    public static final int MINUTE_MAX_RANGE = 60;
    public static final int HOUR_MAX_RANGE = 24;

    public static void main(String[] args) {
        CronParserApplication cronParserApplication = new CronParserApplication();
        CronExpressionOutputDTO cronExpressionOutputDTO = cronParserApplication.parseExpression(args[0]);
        cronParserApplication.formatAndPrint(cronExpressionOutputDTO);
    }

    public CronExpressionOutputDTO parseExpression(String expression) {
        CronExpressionOutputDTO outputDTO = null;
        try {
            String[] expressions = expression.split(" ");
            if (expressions.length < 6) {
                throw new InvalidCronExpressionException("Input should be minimum 5 expression and a command line ");
            }
            String minute = expressions[0];
            String hour = expressions[1];
            String dayOfMonth = expressions[2];
            String month = expressions[3];
            String dayOfWeek = expressions[4];

            outputDTO = new CronExpressionOutputDTO();

            buildSubExpression(minute, ExpressionType.MINUTE, outputDTO);
            buildSubExpression(hour, ExpressionType.HOUR, outputDTO);
            buildSubExpression(dayOfMonth, ExpressionType.DAY_OF_MONTH, outputDTO);
            buildSubExpression(month, ExpressionType.MONTH, outputDTO);
            buildSubExpression(dayOfWeek, ExpressionType.DAY_OF_WEEK, outputDTO);

            outputDTO.setCommandLine(expressions[5]);

        } catch (Exception ex) {
            throw new CronParsingException("Error in parsing cron expression " + expression, ex);
        }

        return outputDTO;
    }

    private void formatAndPrint(CronExpressionOutputDTO outputDTO) {
        System.out.print(String.format("%-14s", "minute"));
        System.out.println(outputDTO.getMinutes());
        System.out.print(String.format("%-14s", "hour"));
        System.out.println(outputDTO.getHours());
        System.out.print(String.format("%-14s", "day of month"));
        System.out.println(outputDTO.getDayOfMonth());
        System.out.print(String.format("%-14s", "months"));
        System.out.println(outputDTO.getMonths());
        System.out.print(String.format("%-14s", "day of week"));
        System.out.println(outputDTO.getDayOfWeek());
        System.out.print(String.format("%-14s", "command"));
        System.out.println(outputDTO.getCommandLine());
    }

    private void buildSubExpression(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expression.length() == 0) {
            throw new InvalidCronExpressionException(expressionType.name() + " expression should be minimum length 1");
        } else if (expression.length() > 1) {
            //possibly expression has sub type or ranges or bigger number
            if (expression.charAt(0) == '*' && expression.charAt(1) != '/') {
                throw new InvalidCronExpressionException(expressionType.name() + " invalid cron expression");
            }
            //skip number on every posisble time (hour, minute, day , week , month etc)
            else if (expression.charAt(0) == '*' && expression.charAt(1) == '/') {
                String ss = expression.substring(2);
                if (ss.trim().length() == 0) {
                    throw new InvalidCronExpressionException(expressionType.name() + "invalid cron expression");
                }
                formOuputWithInterval(ss, expressionType, outputDTO);
            }
            //either range of values or list of values
            else if (expression.indexOf(",") != -1 || expression.indexOf("-") != -1) {
                formOuputWithInterval(expression, expressionType, outputDTO);
            }
            //only number if present
            else if (Integer.parseInt(expression) > 9) {
                formOutputWithGivenNumber(expression, expressionType, outputDTO);
            }
        }
        //if want all possible values
        else if (expression.length() == 1 && expression.charAt(0) == '*') {
            formOutputWithCompleteRange(expression, expressionType, outputDTO);
        }
        //if reached here most probably its valid number interval
        else if (expression.charAt(0) != '*') {
            formOutputWithGivenNumber(expression, expressionType, outputDTO);
        }
    }

    private boolean isAllowedChars(String expression) {
        for (char c : expression.toCharArray()) {
            if (!(c == '*' || c == ',' || c == '/' || c == '-' || (c >= '0' && c <= '9'))) {
                return false;
            }
        }
        return true;
    }

//    private boolean isValidNumberRange(int start, int end, ExpressionType expressionType) {
//        if (expressionType.equals(ExpressionType.MINUTE) || expressionType.equals(ExpressionType.HOUR)) {
//            return ((start < end) && ((start >= 0 && start <= MINUTE_MAX_RANGE) && (end >= 0 && end <= MINUTE_MAX_RANGE)));
//        }
//        if (expressionType.equals(ExpressionType.MONTH)) {
//            return ((start < end) && ((start >= 1 && start <= MONTH_MAX_RANGE) && (end >= 1 && end <= MONTH_MAX_RANGE)));
//        }
//        if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
//            return ((start < end) && ((start >= 1 && start <= DAY_OF_MONTH_MAX_RANGE) && (end >= 1 && end <= DAY_OF_MONTH_MAX_RANGE)));
//        }
//        if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
//            return ((start < end) && ((start >= 1 && start <= DAY_OF_WEEK_MAX_RANGE) && (end >= 1 && end <= DAY_OF_WEEK_MAX_RANGE)));
//        }
//        return false;
//    }

//    private boolean isValidNumberRange(int start, ExpressionType expressionType) {
//        if (expressionType.equals(ExpressionType.MINUTE) || expressionType.equals(ExpressionType.HOUR)) {
//            return isValidNumberRange(start, MINUTE_MAX_RANGE, expressionType);
//        }
//        if (expressionType.equals(ExpressionType.MONTH)) {
//            return isValidNumberRange(start, MONTH_MAX_RANGE, expressionType);
//        }
//        if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
//            return isValidNumberRange(start, DAY_OF_MONTH_MAX_RANGE, expressionType);
//        }
//        if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
//            return isValidNumberRange(start, DAY_OF_WEEK_MAX_RANGE, expressionType);
//        }
//        return false;
//    }

//    private boolean isValidRange(String expression, ExpressionType expressionType) {
//        String[] st = expression.split("-");
//        int sNum = Integer.valueOf(st[0]);
//        int eNum = Integer.valueOf(st[1]);
//        return isValidNumberRange(sNum, eNum, expressionType);
//    }
//
//    private boolean isValidNumbers(String expression) {
//        String[] st = expression.split(",");
//        for (String s : st) {
//            Integer.parseInt(s);
//        }
//        return true;
//    }
//
//    private boolean isValidNumberRange(char c, ExpressionType expressionType) {
//        int num = Character.getNumericValue(c);
//        return isValidNumberRange(num, expressionType);
//    }

    private void formOuputWithInterval(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expression.indexOf("-") != -1) {
            String[] st = expression.split("-");
            int sNum = Integer.valueOf(st[0]);
            int eNum = Integer.valueOf(st[1]);
            if (expressionType.equals(ExpressionType.MINUTE)) {
                for (int x = sNum; x <= eNum; x++) {
                    outputDTO.getMinutes().add(x);
                }
            } else if (expressionType.equals(ExpressionType.HOUR)) {
                for (int x = sNum; x <= eNum; x++) {
                    outputDTO.getHours().add(x);
                }
            } else if (expressionType.equals(ExpressionType.MONTH)) {
                for (int x = sNum; x <= eNum; x++) {
                    outputDTO.getMonths().add(x);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
                for (int x = sNum; x <= eNum; x++) {
                    outputDTO.getDayOfMonth().add(x);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
                for (int x = sNum; x <= eNum; x++) {
                    outputDTO.getDayOfWeek().add(x);
                }
            }
        } else if (expression.indexOf(",") != -1) {
            String[] st = expression.split(",");
            if (expressionType.equals(ExpressionType.MINUTE)) {
                for (String s : st) {
                    outputDTO.getMinutes().add(Integer.valueOf(s));
                }
            } else if (expressionType.equals(ExpressionType.HOUR)) {
                for (String s : st) {
                    outputDTO.getHours().add(Integer.valueOf(s));
                }
            } else if (expressionType.equals(ExpressionType.MONTH)) {
                for (String s : st) {
                    outputDTO.getMonths().add(Integer.valueOf(s));
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
                for (String s : st) {
                    outputDTO.getDayOfMonth().add(Integer.valueOf(s));
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
                for (String s : st) {
                    outputDTO.getDayOfWeek().add(Integer.valueOf(s));
                }
            }
        } else {
            if (expressionType.equals(ExpressionType.MINUTE)) {
                int val = Integer.parseInt(expression);
                int temp = 0;
                outputDTO.getMinutes().add(temp);
                while ((temp + val) < MINUTE_MAX_RANGE) {
                    temp = temp + val;
                    outputDTO.getMinutes().add(temp);
                }
            } else if (expressionType.equals(ExpressionType.HOUR)) {
                int val = Integer.parseInt(expression);
                int temp = 0;
                outputDTO.getHours().add(temp);
                while ((temp + val) < HOUR_MAX_RANGE) {
                    temp = temp + val;
                    outputDTO.getHours().add(temp);
                }
            } else if (expressionType.equals(ExpressionType.MONTH)) {
                int val = Integer.parseInt(expression);
                int temp = 1;
                outputDTO.getMonths().add(temp);
                while ((temp + val) < MONTH_MAX_RANGE) {
                    temp = temp + val;
                    outputDTO.getMonths().add(temp);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
                int val = Integer.parseInt(expression);
                int temp = 1;
                outputDTO.getDayOfMonth().add(temp);
                while ((temp + val) < DAY_OF_MONTH_MAX_RANGE) {
                    temp = temp + val;
                    outputDTO.getDayOfMonth().add(temp);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
                int val = Integer.parseInt(expression);
                int temp = 1;
                outputDTO.getDayOfWeek().add(temp);
                while ((temp + val) < DAY_OF_WEEK_MAX_RANGE) {
                    temp = temp + val;
                    outputDTO.getDayOfWeek().add(temp);
                }
            }
        }
    }

    private void formOutputWithCompleteRange(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expression.length() == 1 && expression.charAt(0) == '*') {
            if (expressionType.equals(ExpressionType.MINUTE)) {
                for (int x = 0; x < MINUTE_MAX_RANGE; x++) {
                    outputDTO.getMinutes().add(x);
                }
            } else if (expressionType.equals(ExpressionType.HOUR)) {
                for (int x = 0; x < HOUR_MAX_RANGE; x++) {
                    outputDTO.getHours().add(x);
                }
            } else if (expressionType.equals(ExpressionType.MONTH)) {
                for (int x = 1; x <= MONTH_MAX_RANGE; x++) {
                    outputDTO.getMonths().add(x);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
                for (int x = 1; x <= DAY_OF_MONTH_MAX_RANGE; x++) {
                    outputDTO.getDayOfMonth().add(x);
                }
            } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
                for (int x = 1; x <= DAY_OF_WEEK_MAX_RANGE; x++) {
                    outputDTO.getDayOfWeek().add(x);
                }
            }
        }
    }

    private void formOutputWithGivenNumber(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expressionType.equals(ExpressionType.MINUTE)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > MINUTE_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + " invalid cron expression");
            }
            outputDTO.getMinutes().add(val);

        } else if (expressionType.equals(ExpressionType.HOUR)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > HOUR_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + " invalid cron expression");
            }
            outputDTO.getHours().add(val);
        } else if (expressionType.equals(ExpressionType.MONTH)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > MONTH_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + " invalid cron expression");
            }
            outputDTO.getMonths().add(val);
        } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > DAY_OF_MONTH_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + " invalid cron expression");
            }
            outputDTO.getDayOfMonth().add(val);
        } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > DAY_OF_WEEK_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + " invalid cron expression");
            }
            outputDTO.getDayOfWeek().add(val);
        }

    }
}
