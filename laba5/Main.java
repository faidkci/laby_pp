import TextFormatter.TextFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

class Main
{
    public static void main(String[] args)
    {
        int width;
        BufferedReader br;
        try
        {
            br = new BufferedReader(new InputStreamReader(System.in));
            width = Integer.parseInt(br.readLine());
            TextFormatter.Format(new File("input.txt"), new File("output.txt"), width);
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
    }
}
