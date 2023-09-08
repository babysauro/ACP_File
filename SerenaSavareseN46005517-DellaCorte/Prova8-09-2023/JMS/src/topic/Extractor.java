package topic;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Extractor {
    public static void main(String[] args) {
        
        Hashtable<String, String> prop = new Hashtable<String, String>();

        prop.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        prop.put("java.naming.provider.url", "tcp://127.0.0.1:61616");

        prop.put("topic.data", "data");
        prop.put("topic.temp", "temp");
        prop.put("topic.press", "press");

        System.out.println("[EXTRACTOR]Extractor avviato");

        try {
            
            Context context = new InitialContext(prop);
            TopicConnectionFactory tcf = (TopicConnectionFactory) context.lookup("TopicConnectionFactory");

            /*
             * La ricezione dei messaggi inviati da Client avviene sul topic data
             * Sulla coda temp verranno inviati i messaggi al TempAnalyzer nel caso di TextMessage
             * pari a 'temperature', oppure verranno inviati i TextMessage al PressAnalyzer in caso
             * di richiesta 'pressure'
             */
            Topic data = (Topic) context.lookup("data");
            Topic temp = (Topic) context.lookup("temp");
            Topic press = (Topic) context.lookup("press"); 

            TopicConnection tc = tcf.createTopicConnection();

            TopicSession ts = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            
            TopicSubscriber subscriber = ts.createSubscriber(data);

            //Passo al MyListener la sessione ed i due topic (temp, press)
            MyListener listener = new MyListener(ts, temp, press);
            subscriber.setMessageListener(listener);

            //MapMessage message = ts.createMapMessage();

            tc.start();

        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }

    }
}
