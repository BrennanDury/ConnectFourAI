import tensorflow as tf
from tensorflow import keras
from keras import regularizers
from keras import optimizers
from keras.layers import *
import tensorflow_model_optimization as tfmot
import numpy as np
from ConnectFour import ROWS, COLS
from DataManager import DataManager

# Neural network hyperparameters
FILTERS = 64
L2PENALTY = 10e-5
MOMENTUM = 0.9  # Addition of momentum improves performance massively because the data is noisy in early stages
LR = 0.01
BATCHSIZE = 8
EPOCHS = 3
RESIDUALS = 2

# The first layers of the network.
def conv_layer(input_layer):
  reshape = Reshape((ROWS, COLS, 1), input_shape=(ROWS * COLS,))(input_layer)
  conv = Conv2D(filters=FILTERS, kernel_size=4, strides=1,
                             padding='same', input_shape=(ROWS, COLS, 1),
                             kernel_regularizer=regularizers.l2(L2PENALTY))(reshape)
  norm = BatchNormalization()(conv)
  rect = PReLU()(norm)
  return rect

# A repeated structure of layers with the same input and output shape.
def res_layer(input_layer):
  conv1 = Conv2D(filters=FILTERS, kernel_size=4, strides=1,
                              padding='same', input_shape=(ROWS, COLS, FILTERS),
                              kernel_regularizer=regularizers.l2(L2PENALTY))(input_layer)
  norm1 = BatchNormalization()(conv1)
  rect1 = PReLU()(norm1)
  conv2 = Conv2D(filters=FILTERS, kernel_size=4, strides=1,
                              padding='same', input_shape=(ROWS, COLS, FILTERS),
                              kernel_regularizer=regularizers.l2(L2PENALTY))(rect1)
  norm2 = BatchNormalization()(conv2)
  concat = concatenate([input_layer, norm2])
  rect2 = PReLU()(concat)
  return rect2

# Outputs the q value.
def value_head(input_layer):
  conv = Conv2D(filters=1, kernel_size=1, strides=1,
                             padding='same', input_shape=(ROWS, COLS, FILTERS),
                             kernel_regularizer=regularizers.l2(L2PENALTY))(input_layer)
  norm = BatchNormalization()(conv)
  rect = PReLU()(norm)
  flat = Flatten()(rect)
  dense = Dense(20, kernel_regularizer=regularizers.l2(L2PENALTY))(flat)
  rect2 = PReLU()(dense)
  value = Dense(1, kernel_regularizer=regularizers.l2(L2PENALTY),
                             activation='tanh')(rect2)
  return value

# Outputs the probability vector.
def policy_head(input_layer):
  conv = Conv2D(filters=2, kernel_size=1, strides=1,
                             padding='same', input_shape=(ROWS, COLS, FILTERS),
                             kernel_regularizer=regularizers.l2(L2PENALTY))(input_layer)
  norm = BatchNormalization()(conv)
  rect = PReLU()(norm)
  flat = Flatten()(rect)
  policy = Dense(COLS, kernel_regularizer=regularizers.l2(L2PENALTY), activation='softmax')(flat)
  return policy

# Defines the neural network architecture.
def initialize_network(deploy=True, *args):
  input_layer = Input(shape=(ROWS * COLS,))
  prev = conv_layer(input_layer)
  for i in range(RESIDUALS):
    curr = res_layer(prev)
    prev = curr
  value = value_head(prev)
  policy = policy_head(prev)
  network = keras.Model(inputs=[input_layer], outputs=[value, policy])

  optimizer = tf.keras.optimizers.SGD(learning_rate=LR, momentum=MOMENTUM)
  network.compile(loss=['mean_squared_error', 'categorical_crossentropy'],
                  optimizer=optimizer)

  if deploy:
    network.save('trained')
  for name in args:
    network.save(name)
  return network

# Trains the model.
def train_network(X, y1, y2, validation_data=None, deploy=True, *args):
  loaded = initialize_network(False)
  loaded.fit(X, (y1, y2), validation_data=validation_data, epochs=EPOCHS, batch_size=BATCHSIZE)
  if deploy:
    loaded.save('trained')
  for name in args:
    loaded.save(name)
    
def get_network(name='trained'):
  loaded_model = tf.keras.models.load_model(name)
  return loaded_model