import rabbitpy

with rabbitpy.Connection() as connection:
    with connection.channel() as channel:
        queue = rabbitpy.Queue(channel, 'test-messages')
        queue.declare()
        while True:
            message = queue.get()
            if message:
                message.pprint()
                message.ack()
                if message.body == 'stop':
                    break
