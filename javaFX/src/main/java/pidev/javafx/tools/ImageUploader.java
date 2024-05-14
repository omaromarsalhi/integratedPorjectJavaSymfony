package pidev.javafx.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUploader {
    public static void upload(String imagePath) {
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpPost request = new HttpPost("http://127.0.0.1:8000/api/upload-image");
//            StringEntity params = new StringEntity("{\"image_url\": \"" + imagePath + "\"}");
//            request.addHeader("content-type", "application/json");
//            request.setEntity(params);
//
//            System.out.printf( "parms: "+request.getEntity().toString());
//
//            HttpResponse response = httpClient.execute(request);
//            // Handle the response (e.g., check status code)
//            // Get the response content as a string
//            String responseBody = EntityUtils.toString(response.getEntity());
//            System.out.println("Response: " + responseBody);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            // Create an HttpUrlConnection instance
            URL url = new URL( "http://127.0.0.1:8000/api/upload-image" );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            // Read the image file
            File imageFile = new File( imagePath );
            FileInputStream fileInputStream = new FileInputStream( imageFile );

            // Set content type and other headers
            connection.setRequestProperty( "Content-Type", "image/jpeg" );
            connection.setRequestProperty( "Content-Length", String.valueOf( imageFile.length() ) );

            // Write image data to the server
            DataOutputStream dataOutputStream = new DataOutputStream( connection.getOutputStream() );
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read( buffer )) != -1) {
                dataOutputStream.write( buffer, 0, bytesRead );
            }

            dataOutputStream.flush();
            dataOutputStream.close();
            fileInputStream.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println( "Image uploaded successfully!" );

            } else {
                System.out.println( "Error uploading image. Response code: " + responseCode );
                System.out.println( "Error uploading image. Response code: " + connection.getResponseMessage() );
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

