package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVFileReader implements StreamingDataClient {
    private String filePath;
    private BufferedReader reader;

    public CSVFileReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void openStream() throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        // Optionally skip the header line if your CSV has headers
        // reader.readLine();
    }

    @Override
    public String[] readNext() throws IOException {
        if (reader != null) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(",");
            }
        }
        return null;  // Return null if end of file or reader not initialized
    }

    @Override
    public void closeStream() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
