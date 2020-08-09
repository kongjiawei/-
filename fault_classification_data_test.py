from keras.models import load_model
import pandas as pd
import numpy as np
#from Classify_Training import

Prefix = 'dataset_train'
File = 'dataset_test'
FileName = File + '.csv'
ModelName= 'train_result'

'''读取测试集的数据'''
DataX = []
DataY = []
DataX = pd.read_csv(FileName, usecols = [1, 3, 5, 11, 12])
DataY = pd.read_csv(FileName, usecols = [13])
DataX = DataX.values.tolist()
DataY = DataY.values.tolist()
DataX = np.array(DataX)
DataY = np.array(DataY)

'''载入模型并预测'''
model = load_model(ModelName + '.h5')
predict = model.predict(DataX)

'''计算精确度'''
predict_to_value = np.argmax(predict,axis = 1)
print(predict_to_value)
count_right = 0
count_sum = len(predict_to_value)
for i in range(count_sum):
    if(predict_to_value[i] == DataY[i][0]):#DataY是嵌套的
        count_right = count_right + 1

accuracy = count_right / count_sum
print('accuracy:',accuracy)
resultfile = open("predict_results.dat", "w+")
for elements in predict_to_value:
    resultfile.write(str(elements)+'\n')
