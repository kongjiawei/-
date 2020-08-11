import socket
import numpy as np
import time
import threading


'''connect to collector'''
def socket_collector():
    host_collector = '192.168.109.229'
    port_collector = 8880
    sock_collector = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock_collector.connect((host_collector, port_collector))
    print("connect collector success")

    start_time = time.time()
    print_time = start_time + 1
    recv_time = 0
    global data_transform


    while (True):
        data = sock_collector.recv(1000)
        time.sleep(0.0008)
        recv_time = recv_time + 1
        data_transform = np.fromstring(data, dtype='float64')
        current_time = time.time()
        if (current_time > print_time):
            # print(len(data_transform))
            #print(data_transform)
            print("recv_time: ", recv_time, " time: ", current_time - start_time)
            print_time = current_time + 1



if __name__ == '__main__':
    proc = threading.Thread(target=socket_collector, args=())
    proc.start()


    output = 0
    latest_output = 0
    data_transform = []

    standard_fwd_acts = {98: 64}  # ufid:fwd_acts
    standard_path = {98:[2]} #ufid: correct path
    #print_time1 = time.time()

    time.sleep(1)
    '''Analyze and classify the received data, output 0: normal, 1: path,  2:fwd_acts'''
    while(True):
        DL_DATA = data_transform
        time.sleep(0.0001)
        #print('DL_DATA:', DL_DATA)

        '''numpy.float64 to int'''
        #try:
        map_info = int(DL_DATA[0])
        #print(map_info)# A corresponding bit of 1 indicates that the bit has data transmission
        bit1 = int(DL_DATA[1])  # Represents how many sets of data a node will have
        ttl = int(DL_DATA[2])  # Represents the number of hops that pass through the switch
        ufid =int(DL_DATA[4]) # The id of the stream distinguishes the different streams
        path = []


        for i in range(ttl):
            node_data = DL_DATA[i * bit1 + 5: i * bit1 + 14]
            if (map_info & 1 == 1):
                path.append(node_data[0])
            if (map_info >> 9 & 1 == 1):
                fwd_acts = node_data[8]

        if (path != standard_path[ufid]):
            output = 1
        if (fwd_acts != standard_fwd_acts[ufid]):
            output = 2
        current_time1 = time.time()
        print("fault type:",output)
        print(node_data)
    # #except Exception as e:
    #
    #
        # if(output != latest_output):
        #     print("output: ", output)
        #     latest_output = output
        # else:
        #     current_time1 = time.time()
        #     # if (current_time1 > print_time1):
        #     #     print("output:", output)
        #     #     print_time1 = current_time1 + 1
        #     #     print(node_data)

