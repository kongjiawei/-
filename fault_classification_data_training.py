from keras.models import Sequential
from keras.layers.core import Dense, Activation
import numpy as np
import pandas as pd
from keras.utils import np_utils
import random

File = 'dataset_train'
FileName = File  + '.csv'
modelname= 'train_result'
N_HIDDEN = 256
INPUT_SHAPE = 5
OUTPUT_CLASS = 3
BATCH_SIZE = 40960
VALIDATION_SPLIT = 0.10

if (__name__ == '__main__'):
    ''' 读取csv文件数据,DataX为输入，DataY为标签'''
    DataX = []
    DataY = []
    DataX = pd.read_csv(FileName, usecols = [1, 3, 5, 11, 12])
    DataY = pd.read_csv(FileName, usecols = [13])
    DataX = DataX.values.tolist()
    DataY = DataY.values.tolist()

    '''以相同的顺序打乱DataX和DataY'''
    random.Random(4).shuffle(DataX)
    random.Random(4).shuffle(DataY)
    DataX = np.array(DataX)
    DataY = np.array(DataY)
    print(DataX)
    print(DataY)
    print('DataX.shape:', DataX.shape)

    '''将DataY 转化为OUTPUT类别矩阵'''
    DataY = np_utils.to_categorical(DataY, OUTPUT_CLASS)


    '''TrainX.TrainY进行训练,先定义层数'''
    X_train = DataX
    Y_train = DataY
    model = Sequential()
    model.add(Dense(N_HIDDEN, input_shape = (INPUT_SHAPE, )))
    model.add(Activation('sigmoid'))
    model.add(Dense(N_HIDDEN))
    model.add(Activation('relu'))
    model.add(Dense(OUTPUT_CLASS))
    model.add(Activation('softmax'))
    model.summary()

    '''确定损失函数，开始训练'''
    model.compile(loss = 'categorical_crossentropy', optimizer = 'Adam', metrics = ['accuracy'])
    history = model.fit(X_train, Y_train, batch_size = BATCH_SIZE,
                        epochs = 1100, verbose = 1, validation_split = VALIDATION_SPLIT)
    output = model.predict(X_train)
    print(output)
    print(output.shape)

    model.save(modelname + '.h5')