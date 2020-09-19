import socket
import numpy as np
import time
import threading
from keras.models import load_model

'''create test_inputdata'''
def creat_testdata(data):
    test_input = []
    test_input.append(data[:])
    return np.array(test_input)


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
        current_time1 = time.time()
        if (current_time1 > print_time):  #Print once per second
            # print(len(data_transform))
            #print(data_transform)
            #print("recv_time: ", recv_time, " time: ", current_time - start_time)
            print_time = current_time1 + 1



if __name__ == '__main__':
    proc = threading.Thread(target=socket_collector, args=())
    proc.start()


    output = 0
    latest_output = 0
    data_transform = []

    standard_fwd_acts = {98: 64}  # ufid:fwd_acts, Ufid is the ID identity of the flow
    standard_path = {98:[2]} #ufid: correct path

    #print_time1 = time.time()
    input_len = 168 * 2
    output_len = 24
    list_input = []
    last_bandwidth = 0
    offset = 4000 #Mb
    normalization = 10000 #Mb
    FLAG = 0
    actual_Mb = []
    actual_01 = []
    predict_Mb = []
    predict_01 = []
    list_delta_time = []
    threshold = 9500

    last_time = 0
    model = load_model('Traffic2L' + '.h5')
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
        total = 0
        right_cnt = 0
        index = 0
        for i in range(ttl):
            node_data = DL_DATA[i * bit1 + 5: i * bit1 + 14]
            #print('node_data:', node_data, 'ttl', i)
            if (map_info & 1 == 1):
                path.append(node_data[0])
            if (map_info >> 9 & 1 == 1):
                fwd_acts = node_data[8]
            if(map_info >> 6 & 1 == 1):    #bandwidth
                if(i == ttl - 1):
                    current_time = time.time()
                    if((len(list_input) < (input_len + output_len))): #and current_time > last_time):
                        list_input.append((node_data[5] - offset) / normalization) #append bandwidth and normalization
                        last_bandwidth = node_data[5] #The data is inserted when the bandwidth changes
                        #print('node_data[5]:', node_data[5])
                        if(FLAG == 1):
                            actual_Mb.append(node_data[5])
                            if(node_data[5] > threshold):
                                actual_01.append(1)
                            else:
                                actual_01.append(0)
                        last_time = current_time + 1

                        if (len(actual_01) > 1000):  #output wait 1000s
                            for i in range(len(actual_01)):
                                if (actual_01[i] == predict_01[i]):
                                    right_cnt = right_cnt + 1
                            right_rate = (right_cnt + 1) / len(actual_01)
                            print('right_rate:', right_rate)
                        print("predict:", predict_01)
                        print("actual:", actual_01)
                    elif(len(list_input) == (input_len + output_len)):
                        current_time_before = time.time()
                        print('len:', len(list_input), 'list_input:', list_input)
                        list_input = list_input[output_len:]
                        input = creat_testdata(list_input)
                        print("input:", input, 'input.shape:', input.shape,input.shape[0],input.shape[0])
                        input = np.reshape(input, (input.shape[0], 1, input.shape[1]))
                        predict = model.predict(input)
                        current_time_after = time.time()
                        list_delta_time.append(current_time_after - current_time_before)
                        print("process time:", list_delta_time)
                        FLAG = 1
                        for i in range(len(predict[0])):
                            actual_value_Mb = predict[0][i] * normalization + offset
                            predict_Mb.append(actual_value_Mb)
                            if(actual_value_Mb > threshold):
                                predict_01.append(1)
                            else:
                                predict_01.append(0)





        # if (path != standard_path[ufid]):
        #     output = 1
        # if (fwd_acts != standard_fwd_acts[ufid]):
        #     output = 2
        # current_time1 = time.time()
        # print("fault type:",output)
        # print(node_data)
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

