package pidev.javafx.tools.marketPlace;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;

public class ChatGPTAPIDescriber {
    public static String chatGPT(String title)  {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://localhost:5000/get-descreptionJava");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"title\": \"" + title + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.append("Error: ").append(e.getMessage());
        }
        String[] substringsToRemove = {"\\\"", "\"\"\\\\", "\"\\n", "\"", "\\n", "\\ \\u","\\u",":", "Title"};

        String newResponse=response.toString();
        // Remove each substring
        for (String substring : substringsToRemove) {
            newResponse = newResponse.replace(substring, "");
        }
    
        return newResponse;
    }








//
//    public class ChatGPTAPIDescriber {
//        public static String chatGPT(String prompt) {
//            String url = "https://api.openai.com/v1/chat/completions";
//            String apiKey = "sk-proj-25zJjGtVKNQ82MUQnRvfT3BlbkFJFpNUAuTX2F7JAkAmYx8A";
//            String model = "gpt-3.5-turbo";
////        String model = "gpt-3.5-turbo-instruct-0914";
//
//            System.out.println(prompt);
//            try {
//                URL obj = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
//                connection.setRequestProperty("Content-Type", "application/json");
//
//                // Create the request body
//                String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \""+prompt+" \"}]}";
//                connection.setDoOutput(true);
//
//
//                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//                writer.write(body);
//                writer.flush();
//                writer.close();
//
//                // Read the response
//                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                StringBuffer response = new StringBuffer();
//                while ((line = br.readLine()) != null) {
//                    response.append(line);
//                }
//                br.close();
//
//                // Extract the message from the JSON response
//                return extractMessageFromJSONResponse(response.toString());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        public static String extractMessageFromJSONResponse(String response) {
//            JSONObject jsonObject = new JSONObject(response);
//            System.out.println(jsonObject);
//            return jsonObject.getJSONArray( "choices" ).getJSONObject( 0 ).getJSONObject( "message" ).getString( "content" );
//        }

//    public static void main(String[] args) {
////        String prompt = "Hello, how can I assist you?";
//        String prompt = "describe mercides benz s class car for sale specified its benefits";
//        System.out.println(chatGPT(prompt));
//    }
}

