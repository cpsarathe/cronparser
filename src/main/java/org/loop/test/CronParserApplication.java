package org.loop.test;

public final class CronParserApplication {

    public static final int MONTH_MAX_RANGE = 12;
    public static final int DAY_OF_MONTH_MAX_RANGE = 31;
    public static final int DAY_OF_WEEK_MAX_RANGE = 7;
    public static final int MINUTE_MAX_RANGE = 60;
    public static final int HOUR_MAX_RANGE = 24;
    public static final String INVALID_CRON_EXPRESSION = " invalid cron expression";
    public static final String FORMAT = "%-14s";


    public static void main(String[] args) {
        CronParserApplication cronParserApplication = new CronParserApplication();
        CronExpressionOutputDTO cronExpressionOutputDTO = cronParserApplication.parseExpression(args[0]);
        cronParserApplication.formatAndPrint(cronExpressionOutputDTO);
    }

    /**
     * parse expressions  , validates , and build output
     *
     * @param expression
     * @return
     */
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
            System.out.println("Invalid cron expression detected " + expression);
            throw new CronParsingException("Error in parsing cron expression " + expression, ex);
        }

        return outputDTO;
    }

    /**
     * Cron expression is formed with sub expression of minutes, hours , day of month , month , day of week
     * We shall process subexpression which guides us if one of subexpression rules are working , others will also work
     * There are special character * , - consideration which affects output generation
     *
     * @param expression
     * @param expressionType
     * @param outputDTO
     */
    private void buildSubExpression(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expression.length() == 0) {
            throw new InvalidCronExpressionException(expressionType.name() + " expression should be minimum length 1");
        } else if (expression.length() > 1) {
            buildSubExpressionWithMeta(expression, expressionType, outputDTO);
        }
        //if want all possible values
        else if (expression.length() == 1 && expression.charAt(0) == '*') {
            formOutputWithAllPossibleValues(expression, expressionType, outputDTO);
        }
        //if reached here most probably it's valid number interval
        else if (expression.charAt(0) != '*') {
            formOutputWithGivenNumericValue(expression, expressionType, outputDTO);
        }
    }

    private void buildSubExpressionWithMeta(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        //possibly expression has sub type or ranges or bigger number
        if (expression.charAt(0) == '*' && expression.charAt(1) != '/') {
            throw new InvalidCronExpressionException(expressionType.name() + INVALID_CRON_EXPRESSION);
        }
        //skip number on every possible time (hour, minute, day , week , month etc)
        else if (expression.charAt(0) == '*' && expression.charAt(1) == '/') {
            String ss = expression.substring(2);
            if (ss.trim().length() == 0) {
                throw new InvalidCronExpressionException(expressionType.name() + INVALID_CRON_EXPRESSION);
            }
            formOuputWithSkipInterval(ss, expressionType, outputDTO);
        }
        //either range of values or list of values
        else if (expression.indexOf(',') != -1 || expression.indexOf('-') != -1) {
            formOuputWithListOrRangeValues(expression, expressionType, outputDTO);
        }
        //only number if present
        else if (Integer.parseInt(expression) > 9) {
            formOutputWithGivenNumericValue(expression, expressionType, outputDTO);
        }
    }

    private void formatAndPrint(CronExpressionOutputDTO outputDTO) {
        System.out.print(String.format(FORMAT, "minute"));
        System.out.println(outputDTO.getMinutes());
        System.out.print(String.format(FORMAT, "hour"));
        System.out.println(outputDTO.getHours());
        System.out.print(String.format(FORMAT, "day of month"));
        System.out.println(outputDTO.getDayOfMonth());
        System.out.print(String.format(FORMAT, "months"));
        System.out.println(outputDTO.getMonths());
        System.out.print(String.format(FORMAT, "day of week"));
        System.out.println(outputDTO.getDayOfWeek());
        System.out.print(String.format(FORMAT, "command"));
        System.out.println(outputDTO.getCommandLine());
    }

    private void formOuputWithListOrRangeValues(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        this.formOuputWithSkipInterval(expression, expressionType, outputDTO);
    }

    private void formOuputWithSkipInterval(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expression.indexOf('-') != -1) {
            String[] st = expression.split("-");
            int sNum = Integer.parseInt(st[0]);
            int eNum = Integer.parseInt(st[1]);
            this.buildRangeValues(expressionType, outputDTO, sNum, eNum);
        } else if (expression.indexOf(',') != -1) {
            String[] st = expression.split(",");
            this.buildMultipleValues(expressionType, outputDTO, st);
        } else {
            this.buildNumericValues(expressionType, expression, outputDTO);
        }
    }

    private void formOutputWithAllPossibleValues(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
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

    private void formOutputWithGivenNumericValue(String expression, ExpressionType expressionType, CronExpressionOutputDTO outputDTO) {
        if (expressionType.equals(ExpressionType.MINUTE)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > MINUTE_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + INVALID_CRON_EXPRESSION);
            }
            outputDTO.getMinutes().add(val);

        } else if (expressionType.equals(ExpressionType.HOUR)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > HOUR_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + INVALID_CRON_EXPRESSION);
            }
            outputDTO.getHours().add(val);
        } else if (expressionType.equals(ExpressionType.MONTH)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > MONTH_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + INVALID_CRON_EXPRESSION);
            }
            outputDTO.getMonths().add(val);
        } else if (expressionType.equals(ExpressionType.DAY_OF_MONTH)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > DAY_OF_MONTH_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + INVALID_CRON_EXPRESSION);
            }
            outputDTO.getDayOfMonth().add(val);
        } else if (expressionType.equals(ExpressionType.DAY_OF_WEEK)) {
            int val = Integer.parseInt(expression);
            if (val < 0 || val > DAY_OF_WEEK_MAX_RANGE) {
                throw new InvalidCronExpressionException(expressionType + INVALID_CRON_EXPRESSION);
            }
            outputDTO.getDayOfWeek().add(val);
        }
    }

    private void buildRangeValues(ExpressionType expressionType, CronExpressionOutputDTO outputDTO, int sNum, int eNum) {
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
    }

    private void buildMultipleValues(ExpressionType expressionType, CronExpressionOutputDTO outputDTO, String[] st) {
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
    }

    private void buildNumericValues(ExpressionType expressionType, String expression, CronExpressionOutputDTO outputDTO) {
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
