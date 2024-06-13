package pidev.javafx.tools.marketPlace;

import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AiVerification {


    public void run(int idProduct, String mode) {
        Http( idProduct, mode );
    }

    private void Http(int idProduct, String mode) {
        try {
            URL url = new URL( "http://"+ GlobalVariables.IP +":8000/java/request/verifyProduct" ); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            OutputStream os = connection.getOutputStream();
            os.write( ("idProduct=" + idProduct + "&mode=" + mode + "&idUser=" + UserController.getInstance().getCurrentUser().getId()).getBytes() );
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public String HttpAiResultState(int idProduct) {
        try {
            URL url = new URL( "http://"+ GlobalVariables.IP +":8000/java/request/verifyAiResultState" ); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            OutputStream os = connection.getOutputStream();
            os.write( STR."idProduct=\{idProduct}".getBytes() );
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            reader.close();

            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static String HttpCinVerification(int idUser, String frontId, String backId) {
        try {
            URL url = new URL( "http://"+ GlobalVariables.IP +":8000/java/request/getCinData" ); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            OutputStream os = connection.getOutputStream();
            os.write( ("userId=" + idUser + "&frontId=" + frontId + "&backId=" + backId).getBytes() );
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            reader.close();
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static String HttpCinInfoVerification(int idUser) {
        try {
            URL url = new URL( "http://"+ GlobalVariables.IP +":8000/java/testForJava"); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            OutputStream os = connection.getOutputStream();
            os.write( ("idUser=" + idUser).getBytes() );
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            reader.close();
            System.out.println(response);
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }
    public static void counter(int idUser) {
        try {
            URL url = new URL( "http://"+ GlobalVariables.IP +":8000/java/launchCounter"); // Replace with your API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );

            OutputStream os = connection.getOutputStream();
            os.write( ("idUser=" + idUser).getBytes() );
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            System.out.println(response);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }



    public static String analyseEditedData(int idUser) {

        String result = HttpCinInfoVerification( idUser );

        if (result.equals( "success" ) || result.equals( "none" ))
            return "success";

        var splitResult = result.split( "_" );

        if (splitResult[1].equals( "dob" ))
            return "the date of birth does not match with the id card";

        if (splitResult[1].equals( "cin" ))
            return "the cin id does not match with the id card";

        return "the given location does not match with the id card location";

    }


}
