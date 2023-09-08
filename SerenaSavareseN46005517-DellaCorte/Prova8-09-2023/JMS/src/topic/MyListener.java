package topic;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;


public class MyListener implements MessageListener{

    private Topic press;
    private Topic temp;
    private TopicSession ts;

    public MyListener(TopicSession ts, Topic temp, Topic press){
        this.press = press;
        this.temp = temp;
        this.ts =ts;
    }

    public void onMessage(Message message){

        MapMessage msg = (MapMessage) message;
        
        try {
            
            System.out.println("[LISTENER]Ricevuto messaggio. Richiesta: "+msg.getString("tipo")+", valore: "+msg.getInt("valore"));

            TopicPublisher publisher_t = ts.createPublisher(temp);
            TopicPublisher publisher_p = ts.createPublisher(press);

            TextMessage text = ts.createTextMessage();
            String type = msg.getString("tipo");
            int valore = msg.getInt("valore");

            /*
             * Nel caso di richiesta di tipo 'temperature', TextMessage viene 
             * inviato sul topic 'temp', altrimenti sul topic 'press'
             */
            if(type.compareTo("temperature") == 0){

                //CASO TEMPERATURE
                text.setText(String.valueOf(valore));
                
                System.out.println("[LISTENER]Invio messaggio...");
                publisher_t.publish(temp, text);
                System.out.println("[LISTENER]Messaggio inviato");

            }else {

                //CASO PRESSURE
                text.setText(String.valueOf(valore));
                
                System.out.println("[LISTENER]Invio messaggio...");
                publisher_p.publish(press, text);
                System.out.println("[LISTENER]Messaggio inviato");

            }

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
    
}
