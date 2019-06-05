## l2fwd-int-collector

This app targets for processing INT packets based on DPDK 16.07. It can run outside the DPDK directory. And I add ```-R TS``` in this app, in order to automatically quit when it has processed INT packets for ```TS``` seconds, and the start time is from the first INT packet.

### How to run?

- Environment set
```
export RTE_SDK=/path/to/rte_sdk # dpdk16.07 installation directory
export RTE_SDK= x86_64-native-linuxapp-gcc
```

- App compilation 
```
cd l2fwd/
make
```

- App running
```
./build/l2fwd [EAL options] -- -p PORTMASK [-q NQ -T t -R TS]
```

The CLI can be such as ```./build/l2fwd -c f -n 4 -- -q 4 -p ffff -T 1 -R 10 > test.txt```.