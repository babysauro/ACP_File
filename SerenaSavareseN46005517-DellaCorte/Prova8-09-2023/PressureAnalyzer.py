import stomp

#LISTENER
class MyListener(stomp.ConnectionListener):
    
    def __init__(self, conn):
        self.conn = conn
    
    def on_message(self, frame):
        print('[PRESSURE-ANALYZER]Ricevuto messaggio: "%s"'%frame.body )
        count = count + 1

        valore = int(frame.body) #Stringa -> valore numerico

        #File
        #Ho utilizzato with in modo da non scrivere file_press.close()
        with open("press.csv", "w") as file_press:
            file_press.write('Valore: "%s"'%valore)
            file_press.write(count)


if __name__ == "__main__":

    #Viene implementata la ricezione asincrona sul topic press

    #auto_content_length = False perché il messaggio è TextMessage
    conn = stomp.Connection([('127.0.0.1', 61613)], auto_content_length=False)
    conn.connect()

    #Ricezione del messaggio
    conn.set_listener("listener", MyListener)
    conn.subscribe('/topic/press', id=1, ack='auto')

    conn.disconnect()