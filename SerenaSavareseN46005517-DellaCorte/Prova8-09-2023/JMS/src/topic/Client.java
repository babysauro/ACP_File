package topic;

import java.util.Hashtable;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Client{

    private static final int N = 20; //Numero richieste da inviare

    public static void main(String[] args) {

        //Il tipo di richiesta è fornito da terminale
        String type = args[0];
        
        Hashtable<String, String> prop = new Hashtable<String, String>();

        prop.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        prop.put("java.naming.provider.url", "tcp://127.0.0.1:61616");

        prop.put("topic.data", "data");

        System.out.println("[CLIENT]Client avviato");

        try {
            
            Context context = new InitialContext(prop);
            TopicConnectionFactory tcf = (TopicConnectionFactory) context.lookup("TopicConnectionFactory");

            Topic data = (Topic) context.lookup("data");
            TopicConnection tc = tcf.createTopicConnection();

            //Publisher
            TopicSession ts = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher = ts.createPublisher(data);

            MapMessage message = ts.createMapMessage();

            tc.start();

            int valore;
            Random rand = new Random();

            /*
             * Vengono effettuate 20 richieste
             * type può essere 'temperature' con valore tra 0 e 100 
             * o 'pressure' con valore tra 1000 e 1050
             */

            for(int i=0; i<N; i++){

                if(type.compareTo("temperature") == 0){

                    //CASO TEMPERATURE
                    valore = rand.nextInt(100)+1; //0 -> 100
                    message.setInt("valore", valore);
                    message.setString(type, "tipo");

                    System.out.println("[CLIENT]Invio richiesta 'temperature' ....");
                    publisher.publish(data, message);
                    System.out.println("[CLIENT]Messaggio inviato");

                    Thread.sleep(2000); //sleep 2 secondi

                }else if (type.compareTo("pressure")== 0){

                    //CASO PRESSURE
                    valore = rand.nextInt(51)+1000; //1000 -> 1050
                    message.setInt("valore", valore);
                    message.setString(type, "tipo");

                    System.out.println("[CLIENT]Invio richiesta 'pressure' ....");
                    publisher.publish(data, message);
                    System.out.println("[CLIENT]Messaggio inviato");
                    
                    Thread.sleep(2000);//sleep 2 secondi

                }else{
                    //CASO ERRORE
                    System.out.println("[CLIENT]ERRIORE: Tipo di richiesta nonn valido.");
                }

            }

        } catch (JMSException | NamingException |InterruptedException e) {
            e.printStackTrace();
        }

    }

}