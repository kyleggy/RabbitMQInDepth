import stomp

conn = stomp.Connection()
conn.start()
conn.connect()
conn.send(body='Example Message', destination='/amq/queue/custom-queue')
conn.disconnect()
