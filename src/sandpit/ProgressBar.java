package sandpit;

import java.io.IOException;

public class ProgressBar {
	public void progressBar() throws InterruptedException, IOException {
			int progress = 0;
	
			while (progress <= 100) {
	
				StringBuffer progressBar = new StringBuffer();
	
				for (int x = 0; x < 10; x++) {
					int positionToOutputTo = (int) Math.ceil(progress / 10);
	
					if (x < positionToOutputTo) {
						progressBar.append("# ");
					} else {
						progressBar.append("- ");
					}
				}
				String output = "\r" + progressBar.toString() + progress + "%";
				System.out.write(output.getBytes());
	
				progress += Math.random() * 10;
	
				Thread.sleep((long) (Math.random() * 1000));
			}
	
			System.out.println("");
		}
}

