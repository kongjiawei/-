import socket
import numpy as np
import time
import threading

'''socket 连接collector'''
host_collector = '192.168.109.229'
port_collector = 8881
sock_collector = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock_collector.connect((host_collector, port_collector))
print("connect collector success")
output = 0
latest_out = 0
send_time = 0
start_time = time.time()
print_time = start_time + 1;
standard_fwd_acts = {98 : 64} #ufid:fwd_acts
standard_path = {98: [7]};
path = []
statisticdata = ['SwitchID', 'Inport', 'Outport', 'IngressTime', 'HopLatency', 'Bandwidth', 'n_packets', 'n_bytes', 'queue_len', 'fwd_acts']
data = sock_collector.recv(1000)
DL_DATA = data
map_info = DL_DATA[0] #A corresponding bit of 1 indicates that the bit has data transmission
bit1 = DL_DATA[1] #Represents how many sets of data a node will have
ttl = DL_DATA[2] #
ufid = DL_DATA[4]
for i in range(ttl):
    node_data = DL_DATA[i * bit1 + 5: i * bit1 + 14]
    if(map_info & 1 == 1):
        path.append(node_data[0])
    if(map_info >> 9 & 1 == 1):
        fwd_acts = node_data[8]
if(path != standard_path):
    output = 1
if(fwd_acts != standard_fwd_acts):
    output = 2

# while(True):
#     data = sock_collector.recv(1000)
#     send_time = send_time + 1
#     data_transform = np.fromstring(data, dtype = 'float64')
#     current_time = time.time()
#     if(current_time > print_time):
#         print(len(data_transform))
#         print(data_transform)
#         print("send_time: ", send_time, " time: ", time.time() - start_time)
#         print_time = current_time + 0.5
#     time.sleep(0.01)

def socket_collector():
    host_collector = '192.168.109.229'
    port_collector = 8881
    sock_collector = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock_collector.connect((host_collector, port_collector))
    print("connect collector success")
    while (True):
        data = sock_collector.recv(1000)
        send_time = send_time + 1
        data_transform = np.fromstring(data, dtype='float64')
        current_time = time.time()
        if (current_time > print_time):
            print(len(data_transform))
            print(data_transform)
            print("send_time: ", send_time, " time: ", time.time() - start_time)
            print_time = current_time + 0.5
        time.sleep(0.01)


if __name__ == '__main__':
    proc = threading.Thread(target = socket_collector, args = ())
    proc.start()

