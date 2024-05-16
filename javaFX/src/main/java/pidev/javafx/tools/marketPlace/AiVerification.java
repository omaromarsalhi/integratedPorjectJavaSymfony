package pidev.javafx.tools.marketPlace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AiVerification {


    public void run(int idProduct,String mode){
        Http(idProduct,mode);
    }

    private void Http(int idProduct,String mode){
        System.out.println("mode:; "+mode);
        try {
            URL url = new URL("http://localhost:8000/java/request/verifyProduct"); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(("idProduct="+idProduct+"&mode="+mode).getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println( STR."Server response: \{response.toString()}" );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public String HttpAiResultState(int idProduct){
        try {
            URL url = new URL("http://localhost:8000/java/request/verifyAiResultState"); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(STR."idProduct=\{idProduct}".getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

}
