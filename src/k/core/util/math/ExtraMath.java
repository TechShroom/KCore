package k.core.util.math;

import static k.core.util.math.Operation.NUM_REGEX;
import static k.core.util.math.Operation.OPERATIONS_REGEX;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtraMath {

    public static UnlimitedDouble factorial(UnlimitedDouble num1) {
        UnlimitedDouble one = UnlimitedDouble.one(), fact = one.clone();
        for (UnlimitedDouble i = new UnlimitedDouble("2"); i.lessThan(num1); i
                .add(one)) {
            fact = fact.multiply(i);
        }
        return fact;
    }

    public static String solveExpression(String expr) {
        // replace known values (pi, E)
        expr = expr.replace("pi", Double.toString(Math.PI));
        expr = expr.replace("e", Double.toString(Math.E));
        // solve for lazy with two first, then for strong.
        Matcher m = Pattern.compile("\\((.+)\\)" + OPERATIONS_REGEX + "?")
                .matcher(expr), dualM = Pattern.compile(
                "\\((.+?)\\)" + OPERATIONS_REGEX + "\\((.+?)\\)").matcher(expr);
        while (dualM.find()) {
            String one = dualM.group(1), two = dualM.group(2);
            String solve1 = solveExpression(one), solve2 = solveExpression(two);
            expr = expr.replaceFirst("\\Q(" + one + ")\\E", solve1);
            expr = expr.replaceFirst("\\Q(" + two + ")\\E", solve2);
            dualM.reset(expr);
        }
        while (m.find()) {
            String one = m.group(1);
            String solve1 = solveExpression(one);
            expr = expr.replaceFirst("\\Q(" + one + ")\\E", solve1);
            m.reset(expr);
        }
        // Apply regexes for user laziness (.x -> 0.x)
        Matcher lazyDecimal = Pattern.compile("(?<!\\d)(\\.\\d+)")
                .matcher(expr);
        expr = lazyDecimal.replaceAll("0$1");
        // Remove all .0's
        expr = expr.replaceAll("\\.0(?!\\d+)", "");
        // Convert negatives to n<number>, leaving positives as <number>
        Matcher neg = Pattern.compile(
                "(?<=" + OPERATIONS_REGEX + ")(\\-)(" + NUM_REGEX + ")")
                .matcher(expr);
        expr = neg.replaceAll("n$2");
        expr = expr.charAt(0) == '-' ? expr.replaceFirst("-", "n") : expr;

        // factorial finder? fix later
        // neg = Pattern.compile("(\\d+\\.?\\d*)(?=!)").matcher(line);
        // line = neg.replaceAll("n$1");

        // Done, compute and return
        boolean solved = false;
        String last = "";
        while (!solved) {
            Pattern pattOp = Pattern
                    .compile("(n?\\d+\\.?\\d*\\^n?\\d+\\.?\\d*)");
            Matcher op = pattOp.matcher(expr);
            ArrayList<Operation> opsE = new ArrayList<Operation>();
            ArrayList<Operation> opsMD = new ArrayList<Operation>();
            ArrayList<Operation> opsAS = new ArrayList<Operation>();
            while (op.find()) {
                String mt = op.group();
                String[] match = mt.split(OPERATIONS_REGEX);
                try {
                    Operation o = new Operation(
                            UnlimitedDouble.parseUD(match[0].replace('n', '-')),
                            UnlimitedDouble.parseUD(match[1].replace('n', '-')),
                            EOperation.getOp(mt), mt);
                    opsE.add(o);
                    // System.out.println(o + "match:" + mt); *DEBUG*
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException(
                            "Error creating Operation for input string " + mt,
                            nfe);
                }
                String[] res = Operation
                        .compute(opsE.toArray(new Operation[0]));
                int i = 0;
                for (String s : res) {
                    expr = expr.replace(opsE.get(i).getOrig(), s);
                    i++;
                }
                op.reset(expr);
            }
            pattOp = Pattern.compile("n?\\d+\\.?\\d*[\\/\\*]n?\\d+\\.?\\d*");
            op = pattOp.matcher(expr);
            while (op.find()) {
                String mt = op.group();
                String[] match = mt.split(OPERATIONS_REGEX);
                try {
                    Operation o = new Operation(
                            UnlimitedDouble.parseUD(match[0].replace('n', '-')),
                            UnlimitedDouble.parseUD(match[1].replace('n', '-')),
                            EOperation.getOp(mt), mt);
                    opsMD.add(o);
                    // System.out.println(o + "match:" + mt); *DEBUG*
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException(
                            "Error creating Operation for input string " + mt,
                            nfe);
                }
                String[] res = Operation.compute(opsMD
                        .toArray(new Operation[0]));
                int i = 0;
                for (String s : res) {
                    expr = expr.replace(opsMD.get(i).getOrig(), s);
                    i++;
                }
                op.reset(expr);
            }
            pattOp = Pattern.compile("n?\\d+\\.?\\d*[\\+\\-]n?\\d+\\.?\\d*");
            op = pattOp.matcher(expr);
            while (op.find()) {
                String mt = op.group();
                String[] match = mt.split(OPERATIONS_REGEX);
                try {
                    Operation o = new Operation(
                            UnlimitedDouble.parseUD(match[0].replace('n', '-')),
                            UnlimitedDouble.parseUD(match[1].replace('n', '-')),
                            EOperation.getOp(mt), mt);
                    opsAS.add(o);
                    // System.out.println(o + "match:" + mt); *DEBUG*
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException(
                            "Error creating Operation for input string " + mt,
                            nfe);
                }
                String[] res = Operation.compute(opsAS
                        .toArray(new Operation[0]));
                int i = 0;
                for (String s : res) {
                    expr = expr.replace(opsAS.get(i).getOrig(), s);
                    i++;
                }
                op.reset(expr);
            }
            solved = EOperation.getOp(expr) == null;
            if (expr.equals(last)) {
                throw new RuntimeException(
                        "Invalid expression "
                                + expr
                                + ", attemped solving failed. (last line was also this line)");
            }
            last = expr;
        }
        try {
            return UnlimitedDouble.parseUD(
                    expr.replaceAll("n(" + NUM_REGEX + ")", "-$1")).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error returning value " + expr, e);
        }
    }

}
