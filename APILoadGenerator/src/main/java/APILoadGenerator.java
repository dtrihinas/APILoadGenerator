import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class APILoadGenerator {

	private static final int DEFAULT_EXECUTOR_CAPACITY = 10000;
	private static final int DEFAULT_KEEP_ALIVE = 60; //in seconds

	private int thread_num;
	private long coolof;
	private long max_exec_time;
	private ExecutorService executor; 
	private boolean debug;
	private String url;
	
	public APILoadGenerator(int thread_num, long max_exec_time, long coolof, String url, boolean debug) {
		this.thread_num = thread_num;
		this.coolof = coolof; //millis
		this.max_exec_time = max_exec_time; //millis
		this.url = url;
		this.debug = debug;
		
		this.executor = new ThreadPoolExecutor(
							this.thread_num, 
							this.thread_num, 
							DEFAULT_KEEP_ALIVE, 
							TimeUnit.SECONDS,
							new ArrayBlockingQueue<Runnable>(DEFAULT_EXECUTOR_CAPACITY, true),
							new ThreadPoolExecutor.DiscardOldestPolicy()
						);	
	}
	
	public APILoadGenerator(int thread_num, long max_exec_time, long coolof, String url) {
		this(thread_num, max_exec_time, coolof, url, true);
	}
	
	public void startJob() {
		
		long cur_time = System.currentTimeMillis();
		long elapsed_time = 0;
		
		while(elapsed_time < this.max_exec_time) {
			
			this.executor.execute(new Job(this.url, this.debug));

			System.out.println("elapsed time >> " +elapsed_time);
			
			elapsed_time += System.currentTimeMillis() - cur_time;
			cur_time = System.currentTimeMillis();			
			try {
				Thread.sleep(coolof);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.term();
	}
	
	public void term() {
		this.executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				this.executor.awaitTermination(1, TimeUnit.MINUTES);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public static void main(String[] args) {
		APILoadGenerator gen = new APILoadGenerator (4, 60000, 1000, "http://localhost/", false);
		gen.startJob();

	}

}
