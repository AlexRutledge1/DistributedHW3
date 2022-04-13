import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class hw3_mars extends Thread
{
	int index;
	static ArrayList<ArrayList<Pair<Integer, Long>>> readings;
	static boolean cont = true;
	static boolean onlyWait = false;
	static final int ONE_MINUTE = 50;

	static private long start_time;

	public hw3_mars(int i)
	{
		index = i;
	}

	public static void main(String[] args) throws InterruptedException
	{
		Date date = new Date();
		readings = new ArrayList<ArrayList<Pair<Integer, Long>>>();
		for (int i = 0; i < 8; i++)
		{
			readings.add(new ArrayList<>());
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
		ArrayList<Integer> highest = new ArrayList<>(), lowest = new ArrayList<>();
		Pair<Integer, Long> lGap = readings.get(0).get((hour - 1) * 10),
				rGap = readings.get(0).get((hour - 1) * 10);
		int gap_max = 0;
		for (int i = (hour - 1) * 60; i < hour * 50; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				Pair<Integer, Long> lowest_gap = readings.get(j).get(i),
						highest_gap = readings.get(j).get(i);
				for (int h = 0; h < 10; h++)
				{
					if (lowest_gap.left > readings.get(j).get(i + h).left)
					{
						lowest_gap = readings.get(j).get(i + h);
						if (highest_gap.left - lowest_gap.left > gap_max)
						{
							gap_max = highest_gap.left - lowest_gap.left;
							lGap = lowest_gap;
							rGap = highest_gap;
						}
					}
					else if (highest_gap.left < readings.get(j).get(i + h).left)
					{
						highest_gap = readings.get(j).get(i + h);
					}
					if (highest.size() < 10)
					{
						highest.add(readings.get(j).get(i).left);
						Collections.sort(highest);
						continue;
					}
					if (lowest.size() < 10)
					{
						lowest.add(readings.get(j).get(i).left);
						Collections.sort(lowest);
						continue;
					}
					if (readings.get(j).get(i).left > highest.get(0))
					{
						highest.add(readings.get(j).get(i).left);
						highest.remove(highest.get(0));
						Collections.sort(highest);
					}
					if (readings.get(j).get(i).left < lowest.get(lowest.size() - 1))
					{
						lowest.add(readings.get(j).get(i).left);
						Collections.sort(lowest);
						lowest.remove(lowest.get(lowest.size() - 1));

					}
				}

			}
		}
		System.out.print("Highest temps: ");
		for (Integer a : highest)
			System.out.print(a + "F ");
		System.out.print("\nLowest temps: ");
		for (Integer a : lowest)
			System.out.print(a + "F ");
		System.out.print("\nLargest Gap:\n");
		System.out.print("lowTemp_time: " + Math.floorDiv(lGap.right, ONE_MINUTE) + "m temp: "
				+ lGap.left + "F\n");
		System.out.print("highTemp_time: " + Math.floorDiv(rGap.right, ONE_MINUTE) + "m temp: "
				+ rGap.left + "F\n");
		System.out.flush();

	}

	private boolean printReady(int h)
	{
		if (h == 0) return false;
		for (int i = 0; i < 8; i++)
			if (readings.get(i).size() < 60 * h) return false;
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
			int reading = (int) (Math.random() * 171) - 100;
			// System.out.println(index);
			long curr_time = date.getTime();
			readings.get(index).add(new Pair<Integer, Long>(reading, curr_time - start_time));

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
				cont = false;
				return;
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