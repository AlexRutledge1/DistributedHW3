import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class hw3_mars extends Thread
{
	int index;
	static ArrayList<Pair<Integer, Long>>[] readings;
	static boolean cont = true;
	static boolean onlyWait = false;
	static final int ONE_MINUTE = 50;

	static private long start_time;

	public hw3_mars(int i)
	{
		index = i;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException
	{
		Date date = new Date();
		readings = (ArrayList<Pair<Integer, Long>>[]) new ArrayList[8];
		for (int i = 0; i < 8; i++)
		{
			readings[i] = new ArrayList<>();
		}
		while (cont)
		{
			start_time = date.getTime();
			Thread[] threads = new Thread[8];
			for (int i = 0; i < 8; i++)
			{
				threads[i] = new hw3_mars(i);
				threads[i].start();
			}
			for (int i = 0; i < 8; i++)
				threads[i].join();
		}

	}

	private void printToFile(int hour) throws IOException
	{
		TreeSet<Integer> highest = new TreeSet<>(), lowest = new TreeSet<>();
		Pair<Integer, Long> lGap = readings[0].get((hour - 1) * 10),
				rGap = readings[0].get((hour - 1) * 10);
		int gap_max = 0;
		for (int i = (hour - 1) * 60; i < hour * 50; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				Pair<Integer, Long> lowest_gap = readings[j].get(i),
						highest_gap = readings[j].get(i);
				for (int h = 0; h < 10; h++)
				{
					if (lowest_gap.left > readings[j].get(i + h).left)
					{
						lowest_gap = readings[j].get(i + h);
						if (highest_gap.left - lowest_gap.left > gap_max)
						{
							gap_max = highest_gap.left - lowest_gap.left;
							lGap = lowest_gap;
							rGap = highest_gap;
						}
					}
					else if (highest_gap.left < readings[j].get(i + h).left)
					{
						highest_gap = readings[j].get(i + h);
					}
					if (highest.size() < 10 || readings[j].get(i).left > highest.first())
					{
						highest.add(readings[j].get(i).left);
					}
					if (lowest.size() < 10 || readings[j].get(i).left < lowest.last())
					{
						lowest.add(readings[j].get(i).left);
					}
				}

			}
		}
		File outFile = new File("out" + hour + ".txt");
		outFile.createNewFile();
		FileWriter out = new FileWriter(outFile);
		for (Integer a : highest)
			out.write(a + " ");
		out.write("\n");
		for (Integer a : lowest)
			out.write(a + " ");
		out.write("\n");
		out.write("start_time: " + lGap.right + " temp: " + lGap.left + "F\n");
		out.flush();
		out.close();
	}

	private void checkIn()
	{

	}

	private boolean printReady(int h)
	{
		if (h == 0) return false;
		for (int i = 0; i < 8; i++)
			if (readings[i].size() < 60 * h) return false;
		return true;
	}

	ReentrantLock lock = new ReentrantLock();

	public void run()
	{
		Scanner scan = new Scanner(System.in);
		while (cont)
		{

			try
			{
				Thread.sleep(ONE_MINUTE);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (onlyWait) continue;
			Date date = new Date();
			int reading = (int) (Math.random() * 170) - 100;
			// System.out.println(index);
			long curr_time = date.getTime();
			readings[index].add(new Pair<Integer, Long>(reading, curr_time - start_time));

			if (index == 0 && printReady((int) ((curr_time - start_time) / (ONE_MINUTE * 60))))
			{
				onlyWait = true;
				// System.out.println("maintenance");
				try
				{
					printToFile((int) ((curr_time - start_time) / (ONE_MINUTE * 60)));
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.print("%d hour(s) have passed, would you like to continue(Y/N):");
				System.out.flush();

				String a = scan.next();

				if (a.length() > 0 && a.charAt(0) == 'Y')
				{
					cont = true;
					onlyWait = false;
				}
				else
					cont = false;

			}

		}
		scan.close();
	}
}

class Pair<T1, T2>
{
	T1 left;
	T2 right;

	public Pair(T1 a, T2 b)
	{
		left = a;
		right = b;
	}
}