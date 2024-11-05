import java.util.*;

public class prac {
 
    public static void main(String[] args) {
    }

    public static int fifo(int pages[],int framecnt)
    {
        Queue<Integer>frame = new LinkedList<>();
        int pagefaults = 0; 

        for(int page : pages)
        {
            boolean isHit = frame.contains(page);


            if(!isHit)
            {
                pagefaults++;
                if(frame.size()==framecnt)
                {
                    frame.poll();
                }
                frame.add(page);
            }

            for(Integer framecontent: frame)
            {
                System.out.print(framecontent+" ");
            }
            for(int i=frame.size();i<framecnt;i++)
            {
                System.out.print("- ");
            }

            if(isHit)
            {
                System.out.println("(hit)");
            }
            else{
                System.out.println("(miss)");
            }
        }
    }
}
