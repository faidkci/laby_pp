package TextFormatter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class TextFormatter
{

    private static ArrayList<String> Compute(Queue<String> input_data, int width)
    {
        ArrayList<String> res = new ArrayList<>();
        Queue<String> to_add = new ArrayDeque<>();

        int whitespaces;
        int left_whitespaces;
        int whitespace_index;

        int word_width;
        int word_count;

        String str_to_add;

        String whitespace_array[];

        while(true)
        {
            whitespaces = 0;
            word_width = 0;
            str_to_add = "";

            if(input_data.isEmpty())
            {
                break;
            }
            else
            {
                if(input_data.peek().length() >= width)
                {
                    str_to_add += input_data.remove();
                }
                else
                {
                    word_width += input_data.peek().length();
                    to_add.add(input_data.remove());

                    while(true)
                    {
                        if(input_data.isEmpty())
                        {
                            break;
                        }
                        else if(input_data.peek().length() + word_width + whitespaces + 1 > width)
                        {
                            break;
                        }
                        else
                        {
                            word_width += input_data.peek().length();
                            to_add.add(input_data.remove());
                            whitespaces++;
                        }
                    }

                    if(to_add.size() == 1)
                    {
                        str_to_add += to_add.remove();
                    }
                    else
                    {
                        whitespaces += width - word_width - whitespaces;
                        word_count = to_add.size();
                        whitespace_index = 0;
                        left_whitespaces = whitespaces % (word_count - 1);

                        whitespace_array = new String[word_count - 1];

                        for(int i = 0; i < word_count - 1; i++)
                        {
                            whitespace_array[i] = (new String(" ")).repeat(whitespaces / (word_count - 1));
                        }

                        int left = 0;
                        int right = word_count - 2;

                        while(left_whitespaces != 0)
                        {
                            whitespace_array[left] += " ";
                            left++;
                            left_whitespaces--;
                            if(left_whitespaces == 0)
                            {
                                break;
                            }
                            else
                            {
                                whitespace_array[right] += " ";
                                right--;
                                left_whitespaces--;
                            }
                        }

                        while(to_add.size() != 1)
                        {
                            str_to_add += to_add.remove();
                            str_to_add += whitespace_array[whitespace_index];
                            whitespace_index++;
                        }
                        str_to_add += to_add.remove();
                    }
                }
            }
            str_to_add += '\n';
            res.add(str_to_add);
        }
        return res;
    }

    public static ArrayList<String> Format(File input_file, int width)
    {
        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new FileReader(input_file));
        }
        catch(FileNotFoundException e)
        {
            System.err.println(e.toString());
        }

        Queue<String> input_data = new ArrayDeque<>();

        try
        {
            while(in.ready())
            {
                String[] strings = in.readLine().split("\\s+");
                for(String str : strings)
                {
                    if(!str.isEmpty())
                    {
                        input_data.add(str);
                    }
                }
            }
            in.close();
        }
        catch(IOException e)
        {
            System.err.println(e.toString());
        }

        return Compute(input_data, width);
    }

    public static ArrayList<String> Format(ArrayList<String> text, int width)
    {
        Queue<String> input_data = new ArrayDeque<>();
        for(String line : text)
        {
            String[] strings = line.split("\\s+");
            for(String str : strings)
            {
                if(!str.isEmpty())
                {
                    input_data.add(str);
                }
            }
        }
        return Compute(input_data, width);
    }

    public static ArrayList<String> Format(String text, int width)
    {
        Queue<String> input_data = new ArrayDeque<>();
        String[] strings = text.split("\\s+");
        for(String str : strings)
        {
            if(!str.isEmpty())
            {
                input_data.add(str);
            }
        }
        return Compute(input_data, width);
    }

    public static void Format(File input_file, File output_file, int width)
    {
        ArrayList<String> computed = Format(input_file, width);

        PrintWriter wrtr = null;

        try
        {
            wrtr = new PrintWriter(new FileWriter(output_file));
        }
        catch(IOException e)
        {
            System.err.println(e.toString());
        }

        for(String str : computed)
        {
            wrtr.print(str);
        }

        wrtr.close();
    }

    public static void Format(ArrayList<String> text, File output_file, int width)
    {
        ArrayList<String> computed = Format(text, width);

        PrintWriter wrtr = null;

        try
        {
            wrtr = new PrintWriter(new FileWriter(output_file));
        }
        catch(IOException e)
        {
            System.err.println(e.toString());
        }

        for(String str : computed)
        {
            wrtr.print(str);
        }

        wrtr.close();
    }

    public static void Format(String text, File output_file, int width)
    {
        ArrayList<String> computed = Format(text, width);

        PrintWriter wrtr = null;

        try
        {
            wrtr = new PrintWriter(new FileWriter(output_file));
        }
        catch(IOException e)
        {
            System.err.println(e.toString());
        }

        for(String str : computed)
        {
            wrtr.print(str);
        }

        wrtr.close();
    }

}


