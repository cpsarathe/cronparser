package org.loop.test;
import lombok.Data;
import java.util.*;

@Data
public class CronExpressionOutputDTO {
    private Set<Integer> minutes = new TreeSet<Integer>();
    private Set<Integer> hours = new TreeSet();
    private Set<Integer> dayOfMonth = new TreeSet();
    private Set<Integer> dayOfWeek = new TreeSet();
    private Set<Integer> months = new TreeSet();
    private String commandLine;
}
