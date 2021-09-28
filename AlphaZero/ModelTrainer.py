import json
import os
import numpy as np
from tensorflow import keras
import tensorflow as tf


def policy_loss(y_true, y_pred):
    policy_true = y_true[:, 1:]
    policy_pred = y_pred[:, 1:]
    return keras.losses.categorical_crossentropy(policy_true, policy_pred)


def value_loss(y_true, y_pred):
    value_true = y_true[:, :1]
    value_pred = y_pred[:, :1]
    return keras.losses.mean_squared_error(value_true, value_pred)


def main():
    input_layer = keras.layers.Input(shape=(42,))
    reshape = keras.layers.Reshape((6, 7, 1), input_shape=(42,))(input_layer)
    conv1 = keras.layers.Conv2D(filters=20, kernel_size=4, strides=1,
                                  padding='valid', input_shape=(6, 7, 1))(reshape)
    conv2 = keras.layers.Conv2D(filters=81, kernel_size=(4, 1), strides=1,
                                padding='valid', input_shape=(6, 7, 1))(reshape)
    conv3 = keras.layers.Conv2D(filters=81, kernel_size=(1, 4), strides=1,
                                padding='valid', input_shape=(6, 7, 1))(reshape)
    flatten1 = keras.layers.Flatten()(conv1)
    flatten2 = keras.layers.Flatten()(conv2)
    flatten3 = keras.layers.Flatten()(conv3)
    concat1 = keras.layers.concatenate([input_layer, flatten1, flatten2, flatten3])
    dense1 = keras.layers.Dense(200, activation='tanh')(concat1)
    concat2 = keras.layers.concatenate([concat1, dense1])
    dense2 = keras.layers.Dense(200, activation='tanh')(concat2)
    concat3 = keras.layers.concatenate([concat2, dense2])
    dense3 = keras.layers.Dense(200, activation='tanh')(concat3)
    concat4 = keras.layers.concatenate([concat3, dense3])
    dense4 = keras.layers.Dense(200, activation='tanh')(concat4)
    concat5 = keras.layers.concatenate([concat4, dense4])
    dense5 = keras.layers.Dense(200, activation='tanh')(concat5)
    concat6 = keras.layers.concatenate([concat5, dense5])
    dense6 = keras.layers.Dense(200, activation='tanh')(concat6)
    concat7 = keras.layers.concatenate([concat6, dense6])
    dense7 = keras.layers.Dense(200, activation='tanh')(concat7)
    concat8 = keras.layers.concatenate([concat7, dense7])
    dense8 = keras.layers.Dense(200, activation='tanh')(concat8)
    concat9 = keras.layers.concatenate([concat8, dense8])
    dense9 = keras.layers.Dense(200, activation='tanh')(concat9)
    concat10 = keras.layers.concatenate([concat9, dense9])
    dense10 = keras.layers.Dense(200, activation='tanh')(concat10)
    concat11 = keras.layers.concatenate([concat10, dense10])
    dense11 = keras.layers.Dense(200, activation='tanh')(concat11)
    concat12 = keras.layers.concatenate([concat11, dense11])
    dense12 = keras.layers.Dense(200, activation='tanh')(concat12)
    concat13 = keras.layers.concatenate([concat12, dense12])
    dense13 = keras.layers.Dense(200, activation='tanh')(concat13)
    concat14 = keras.layers.concatenate([concat13, dense13])
    dense14 = keras.layers.Dense(200, activation='tanh')(concat14)
    concat15 = keras.layers.concatenate([concat14, dense14])
    dense15 = keras.layers.Dense(200, activation='tanh')(concat15)
    concat16 = keras.layers.concatenate([concat15, dense15])
    dense16 = keras.layers.Dense(200, activation='tanh')(concat16)
    concat17 = keras.layers.concatenate([concat16, dense16])
    dense17 = keras.layers.Dense(200, activation='tanh')(concat17)
    concat18 = keras.layers.concatenate([concat17, dense17])
    value = keras.layers.Dense(1, activation='tanh')(concat18)
    policy = keras.layers.Dense(7, activation='softmax')(concat18)

    player = keras.Model(inputs=[input_layer], outputs=[value, policy])
    player.compile(loss=['mse', 'categorical_crossentropy'],
                   optimizer='sgd')
    tf.saved_model.save(player, 'player')
    unique = dict()
    ls = os.listdir('/Users/brennandury/ConnectFourData/')
    for name in ls:
        if (name != '.DS_Store'):
            f = open('/Users/brennandury/ConnectFourData/' + name)
            obj = json.load(f)
            f.close()
            for item in obj:
                tup = tuple(item[:42])
                if tup not in unique:
                    unique[tup] = np.zeros(9)
                unique[tup] += np.array(item[42:])
    X = np.array([])
    y1 = np.array([])
    y2 = np.array([])
    for features, labels in unique.items():
        X = np.append(X, features)
        score = labels[0] / (labels[0] + labels[1])
        policy_average = labels[2:] / sum(labels[2:])
        y1 = np.append(y1, score)
        y2 = np.append(y2, policy_average)
    X = X.reshape(-1, 42, 1)
    y1 = y1.reshape(-1, 1)
    y2 = y2.reshape(-1, 7)
    player.fit(X, (y1, y2), workers=8, use_multiprocessing=True)
    tf.saved_model.save(player, 'player')


if __name__ == '__main__':
    main()
