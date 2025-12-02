package com.example.rpn_calc;

import java.util.Stack;

public class RPNUtils
{
    public static int priority(char c)
    {
        return switch (c) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    public static String toRPN(String expr)
    {
        if (expr == null || expr.trim().isEmpty()) {
            return "empty";
        }

        StringBuilder rpn = new StringBuilder();
        Stack<Character> ops = new Stack<>();
        int sk = 0;
        int op = 1;

        for (int i = 0; i < expr.length(); i++)
        {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) continue;

            if (Character.isDigit(c) || c == '.')
            {
                op -= 1;
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    rpn.append(expr.charAt(i++));
                }
                rpn.append(' ');
                i--;
            }
            else if (c == '(') {
                sk++;
                ops.push(c);
            }
            else if (c == ')')
            {
                sk--;
                while (!ops.isEmpty() && ops.peek() != '(')
                {
                    rpn.append(ops.pop()).append(' ');
                }
                if (!ops.isEmpty()) {
                    ops.pop();
                } else {
                    return "z"; // Не хватает открывающей скобки
                }
            }
            else if ("+-*/".indexOf(c) != -1)
            {
                op++;
                while (!ops.isEmpty() && priority(ops.peek()) >= priority(c))
                {
                    rpn.append(ops.pop()).append(' ');
                }
                ops.push(c);
            }
        }

        if (sk > 0) return "s"; // Не хватает закрывающей скобки
        if (sk < 0) return "z"; // Не хватает открывающей скобки
        if (op < 0) return "o"; // Не хватает оператора
        if (op > 0) return "c"; // Не хватает числа

        while (!ops.isEmpty())
        {
            rpn.append(ops.pop()).append(' ');
        }

        return rpn.toString().trim();
    }

    public static Double calcRPN(String rpn)
    {
        if (rpn.isEmpty() || "szocempty".contains(rpn)) {
            return null;
        }

        Stack<Double> nums = new Stack<>();
        String[] tokens = rpn.split(" ");

        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?"))
            {
                nums.push(Double.parseDouble(token));
            }
            else if ("+-*/".contains(token))
            {
                if (nums.size() < 2) {
                    return null; // Недостаточно операндов
                }
                double b = nums.pop();
                double a = nums.pop();
                switch (token)
                {
                    case "+" -> nums.push(a + b);
                    case "-" -> nums.push(a - b);
                    case "*" -> nums.push(a * b);
                    case "/" ->
                    {
                        if(b != 0)
                            nums.push(a / b);
                        else
                            return null;
                    }
                }
            }
        }

        if (nums.size() != 1) {
            return null; // Некорректное выражение
        }

        return nums.pop();
    }
}