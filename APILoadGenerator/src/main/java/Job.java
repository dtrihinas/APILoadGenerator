import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Job implements Runnable {

	private String url;
	private boolean debug;
	
	public Job(String url, boolean debug) {
		this.url = url;
		this.debug = debug;
	}
	
	public void run() {
		try {
			URL obj = new URL(this.url);
			
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");	
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			System.out.println("Thread: " + Thread.currentThread().getName() + " status >> " + conn.getResponseCode());
			
			if (this.debug) {
				String line = null;
				while ((line=in.readLine()) != null)
					System.out.println (line);
			}
			in.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
