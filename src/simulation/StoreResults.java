package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StoreResults<E> implements Runnable {
	String filePath;
	ArrayList<E> data2BS;
	
	public StoreResults(String filePath, ArrayList<E> data2BS) {
		this.filePath=filePath;
		this.data2BS=data2BS;
	}
	
	@Override
	public void run() {
		storeData();
	}
	void storeData() {
		File file2 = new File(filePath);
		try {
			if (!file2.exists()) {
				file2.createNewFile();
			}
			FileWriter stream = new FileWriter(file2);
			for (int j = 0; j < data2BS.size(); j++) {
				stream.write(data2BS.get(j).toString() + "\r\n");
			}
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
