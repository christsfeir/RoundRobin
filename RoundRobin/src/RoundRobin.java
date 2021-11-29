import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class RoundRobin {
    static class process extends Thread{
        public Integer id;
        public Integer leftTime;
        public Integer burstTime;
        public Integer arrivalTime;
        public boolean isFirst;
        public int waitingTime;

        public process(int id, int burstTime, int arrivalTime) {
            this.id = id;
            this.leftTime = burstTime;
            this.burstTime = burstTime;
            this.arrivalTime = arrivalTime;
            isFirst=false;
            waitingTime=0;
        }
        public void reduceBustTime(int value) {
            leftTime-=value;
        }
    }

    private static int timeCount =1;
    private static ArrayList<process> queue=new ArrayList<RoundRobin.process>();
    private static ArrayList<process> finished=new ArrayList<RoundRobin.process>();
    private static int inputIndex=0;


    public static void simulate(ArrayList<process> arr) {
        //sorting according to arrival time
        Collections.sort(arr, new Comparator<process>(){
            public int compare(process s1, process s2) {
                return s1.arrivalTime.compareTo(s2.arrivalTime);
            }
        });
        //checking if any thread is ready
        while(!arr.isEmpty()) {
            Iterator<process> it = arr.iterator();
            while (it.hasNext() ) {
                process value = it.next();
                if (value.arrivalTime<=timeCount) {
                    queue.add(value);
                    it.remove();
                }
            }
            run();
        }

        //all thread started, but some of them are still not finished
        while(!queue.isEmpty()) {
            run();
        }

        //all thread finished
        System.out.println("--------------------------------------------------------------\nWaiting Times:");
        //sorting according to process number
        Collections.sort(finished, new Comparator<process>(){
            public int compare(process s1, process s2) {
                return s1.id.compareTo(s2.id);

            }
        });
        for(process p:finished) {
            System.out.println("Process "+p.id+": "+p.waitingTime);
        }
    }



    public static void run() {
        //selecting process with lowest remaining time
        process toRun = selectProcess();
        if(toRun==null) {
            timeCount++;
            return;
        }
        //first time started
        if(!toRun.isFirst) {
            System.out.println("Time "+timeCount+", Process "+toRun.id+", Started");
            toRun.isFirst = true;
        }
        System.out.println("Time "+timeCount+", Process "+toRun.id+", Resumed");

        //finding 10% quantum (at least is 1)
        int quantum=calculateQuantum(toRun.leftTime);

        //letting run the selected process
        toRun.reduceBustTime(quantum);;
        timeCount+=quantum;
        System.out.println("Time "+timeCount+", Process "+toRun.id+", Paused");

        //if process is finished
        if(toRun.leftTime==0) {
            toRun.waitingTime= timeCount - toRun.arrivalTime - toRun.burstTime;
            finished.add(toRun);
            removeProcess();
            System.out.println("Time "+timeCount+", Process "+toRun.id+", Finished");
        }
    }

    public static process selectProcess() {
        if(queue.size()==0) {
            return null;
        }
        //sorting according to remaining time. is same remaining time, old process is selected.
        Collections.sort(queue, new Comparator<process>(){
            public int compare(process s1, process s2) {
                if(s1.leftTime.equals(s2.leftTime)) {
                    return s1.id.compareTo(s2.id);
                } else
                    return s1.leftTime.compareTo(s2.leftTime);
            }
        });
        return queue.get(0);
    }

    public static void removeProcess() {
        queue.remove(0);
    }

    public static int calculateQuantum(int remainingTime) {
        return (int) Math.ceil((double)remainingTime*0.1);
    }

    public static void main(String []agrs){
        PrintStream out;
        try {
            //wrting all console output to text
            out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);

            ArrayList<process> lst = new ArrayList<RoundRobin.process>();
            int procId=1;

            //reading file
            File myObj = new File("input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                int data1 = myReader.nextInt();
                int data2 = myReader.nextInt();
                process p1=new process(procId, data2, data1);
                procId++;
                lst.add(p1);
            }
            myReader.close();

            simulate(lst);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

    }

}