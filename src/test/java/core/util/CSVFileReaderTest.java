package core.util;

import java.io.IOException;

public class CSVFileReaderTest {
    public static void main(String[] args) {
        String userHome = System.getProperty("user.home");
        String filePath = userHome + "/FinGen/Test_Data/usd-coin_2018-10-08_2024-04-21.csv";

        CSVFileReader reader = new CSVFileReader(filePath);

        try {
            reader.openStream();
            String[] record;
            while ((record = reader.readNext()) != null) {
                System.out.println(String.join(", ", record));
            }
        } catch (IOException e) {
            System.out.println("Error processing the CSV file: " + e.getMessage());
        } finally {
            try {
                reader.closeStream();
            } catch (IOException e) {
                System.out.println("Error closing the stream: " + e.getMessage());
            }
        }
    }
}
