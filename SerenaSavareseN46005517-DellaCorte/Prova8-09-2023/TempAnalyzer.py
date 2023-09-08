import stomp
from matplotlib import pyplot as plt


#LISTENER
class MyListener(stomp.ConnectionListener):
    
    def __init__(self, conn):
        self.conn = conn
    
    def on_message(self, frame):
        print('[TEMP-ANALYZER]Ricevuto messaggio: "%s"'%frame.body )
        
        valore = int(frame.body) #stringa -> valore numerico

        queue = [] #coda
        queue.append(valore)
        count = count +1

        if count == 20 :
            print(plt.plot(queue))



if __name__ == "__main__":

    #Viene implementata la ricezione asincrona sul topic temp

    #auto_content_length = False perché il messaggio è TextMessage
    conn = stomp.Connection([('127.0.0.1', 61613)], auto_content_length=False)
    conn.connect()

    #Ricezione del messaggio
    conn.set_listener("listener", MyListener)
    conn.subscribe('/topic/temp', id=1, ack='auto')

    conn.disconnect()



