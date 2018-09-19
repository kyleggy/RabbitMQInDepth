import rabbitpy

with rabbitpy.Connection() as connection:
    with connection.channel() as channel:
        channel.prefetch_count(10)
        for message in rabbitpy.Queue(channel, 'test-messages'):
            message.pprint()
            message.ack()
