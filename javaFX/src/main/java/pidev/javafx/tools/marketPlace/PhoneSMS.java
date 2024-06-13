package pidev.javafx.tools.marketPlace;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class PhoneSMS {

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    private static PhoneSMS instance;

    private PhoneSMS() {
        Twilio.init( ACCOUNT_SID, AUTH_TOKEN );
    }

    public static PhoneSMS getInstance() {
        if (instance == null)
            instance = new PhoneSMS();
        return instance;
    }


    public void sendSMS(String reciver, String body) {
        Message message = Message
                .creator(
                        new PhoneNumber( reciver ),
                        new PhoneNumber( "+16097704463" ),
                        body
                ).create();
    }
}
