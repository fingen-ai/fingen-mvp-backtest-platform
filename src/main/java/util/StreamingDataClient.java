package util;

import java.io.IOException;

public interface StreamingDataClient {
    /**
     * Opens the data stream.
     * @throws IOException if there is an issue opening the data stream.
     */
    void openStream() throws IOException;

    /**
     * Reads the next record from the stream.
     * @return the next record as an array of Strings, or null if the end of the stream is reached.
     * @throws IOException if there is an issue reading the data.
     */
    String[] readNext() throws IOException;

    /**
     * Closes the data stream.
     * @throws IOException if there is an issue closing the stream.
     */
    void closeStream() throws IOException;
}
