class Sum {
	private long sum;

	public long getSum() {
		return sum;
	}

	public void setSum(long sum) {
		this.sum = sum;
	}
}

class Summation implements Runnable {
	private int upper;
	private int lower;
	private Sum sumValue;

	public Summation(int lower, int upper, Sum sumValue) {
		this.lower = lower;
		this.upper = upper;
		this.sumValue = sumValue;
	}

	public void run() {
		long sum = 0;
		
		System.out.println("Summing from " + lower + " to " +  upper);
		for (int i = lower; i <= upper; i++) {
			sum += i;
			for (int j = 0; j < 10000000; j++)
				;
		}
		sumValue.setSum(sum);
	}
}

public class ThreadExample {
	public static void main(String [] args) {
		if (args.length == 2) {
			if (Integer.parseInt(args[0]) < 0 || Integer.parseInt(args[1]) < 0)
				System.err.println("Parameters must be >= 0.");
			else {
				int upper   = Integer.parseInt(args[0]);
				int threads = Integer.parseInt(args[1]);
				int incr    = upper / threads;
				int lower   = 0;
				int nupper;

				Sum[] sumVector = new Sum[threads];
				Thread[] thrd   = new Thread[threads];			       				
				
				for (int i = 0; i < threads; i++) {
					sumVector[i] = new Sum();
					nupper = lower + incr;
					if (i == threads - 1)
						nupper = upper;
					thrd[i] = new Thread(new Summation(lower+1, nupper, sumVector[i]));
					lower = lower + incr;
					thrd[i].start();
				}
				try {
					long sum = 0;
					for (int i = 0; i < threads; i++) 
						thrd[i].join();
					for (int i = 0; i < threads; i++)
						sum += sumVector[i].getSum();
					System.out.println("The sum of 1 to " + upper + " is " + sum);
				}
				catch (InterruptedException ie) {}
			}
		}
		else
			System.err.println("Usage: Summation <integer value> <threads>");
	}
}
