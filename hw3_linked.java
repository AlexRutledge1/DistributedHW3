import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

class LL<T>
{
	AtomicMarkableReference<Node<T>> head;

	public LL()
	{
		Node<T> headnode = new Node<T>(null, null);
		head = new AtomicMarkableReference<Node<T>>(headnode, false);
	}

	public LL(Node<T> n)
	{
		head = new AtomicMarkableReference<Node<T>>(n, false);
	}

	public boolean add(T item)
	{
		int key = item.hashCode();
		while (true)
		{
			Window<T> window = find(head.getReference(), key);
			Node<T> pred = window.pred, curr = window.curr;
			if (curr != null && curr.key == key)
			{
				return false;
			}
			else
			{
				Node<T> node = new Node<T>(item, curr);
				if (pred.next.compareAndSet(curr, node, false, false)) return true;
			}
		}
	}

	public boolean remove(T item)
	{
		int key = item.hashCode();
		boolean snip;
		while (true)
		{
			Window<T> window = find(head.getReference(), key);
			Node<T> pred = window.pred, curr = window.curr;
			if (curr.key != key)
			{
				return false;
			}
			else
			{
				Node<T> succ = curr.next.getReference();
				snip = curr.next.attemptMark(succ, true);
				if (!snip) continue;
				pred.next.compareAndSet(curr, succ, false, false);
				return true;
			}
		}
	}

	public boolean contains(T item)
	{
		boolean[] marked =
		{ false };
		int key = item.hashCode();
		Node<T> curr = head.getReference();
		while (curr.key < key)
		{
			curr = curr.next.getReference();
			Node<T> succ = curr.next.get(marked);
		}
		return (curr.key == key && !marked[0]);
	}

	public Window<T> find(Node<T> head, int key)
	{
		Node<T> pred = null, curr = null, succ = null;
		boolean[] marked =
		{ false };
		boolean snip;
		retry: while (true)
		{
			pred = head;
			curr = head.next.getReference();
			while (true)
			{
				if (curr == null)
				{
					return new Window<T>(pred, curr);
				}
				succ = curr.next.get(marked);
				while (marked[0])
				{
					snip = pred.next.compareAndSet(curr, succ, false, false);
					if (!snip) continue retry;
					curr = succ;
					if (curr == null) continue retry;
					succ = curr.next.get(marked);
				}
				if (curr.key >= key) return new Window<T>(pred, curr);
				pred = curr;
				curr = succ;
			}
		}
	}
}

class Node<T>
{
	AtomicMarkableReference<Node<T>> next;
	T val;
	int key;

	public Node(T value, Node<T> next)
	{
		this.next = new AtomicMarkableReference<Node<T>>(next, false);
		this.val = value;
		if (value != null) this.key = value.hashCode();
	}
}

class Window<T>
{
	Node<T> pred, curr;

	public Window(Node<T> mypred, Node<T> mycurr)
	{
		pred = mypred;
		curr = mycurr;
	}
}

public class hw3_linked extends Thread
{
	private static int thank_yous = 0;
	static final int numLetters = 500000;
	static LL<Integer> list;
	static ArrayList<Integer> unordered;

	public static void main(String[] args)
	{
		unordered = new ArrayList<>();
		boolean[] toInsert = new boolean[numLetters];
		while (unordered.size() < numLetters)
		{
			int rand = (int) (Math.random() * numLetters);
			while (toInsert[rand])
				rand = (int) (Math.random() * numLetters);
			unordered.add(rand);
			toInsert[rand] = true;
		}
		// System.out.println("Line 147");
		list = new LL<Integer>();

		Thread[] thread = new Thread[4];
		for (int i = 0; i < 4; i++)
		{
			thread[i] = new hw3_linked();
			thread[i].start();
		}
		for (int i = 0; i < 4; i++)
		{
			try
			{
				thread[i].join();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(thank_yous);
	}

	private synchronized static int getFromBag()
	{
		if (unordered.size() == 0) return -1;
		int temp = unordered.remove(0);
		return temp;
	}

	private synchronized static void addThankU()
	{
		thank_yous++;
	}

	public void run()
	{
		while (list.head.getReference().next != null && !unordered.isEmpty())
		{
			if (list.head == null)
			{

				int curr = getFromBag();
				if (curr > -1) list.add(curr);

			}
			else
			{
				if (unordered.isEmpty())
				{
					while (list.head.getReference().next != null
							&& list.head.getReference().next.isMarked())
						;
					while (!list.remove(list.head.getReference().next.getReference().val))
						;
					addThankU();
				}
				else
				{
					int rand = (int) Math.random() * 2;
					if (rand == 0)
					{
						int curr = getFromBag();
						if (curr > -1) list.add(curr);

						while (list.head.getReference().next != null
								&& list.head.getReference().next.isMarked())
							;
						while (!list.remove(list.head.getReference().next.getReference().val))
							;
						addThankU();
					}
					else
					{
						while (list.head.getReference().next != null
								&& list.head.getReference().next.isMarked())
							;
						while (!list.remove(list.head.getReference().next.getReference().val))
							;
						addThankU();
						int curr = getFromBag();
						if (curr > -1) list.add(curr);

					}
				}
			}
		}

	}

}
