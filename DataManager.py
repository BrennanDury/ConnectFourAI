import numpy as np
import json
from ConnectFour import HashableArray

# Max number of board states in training data
MAXSIZE = 200000

# Chance of including each board in the data when accessed
RANDOM = 0.75

# Manages the data. Data is stored last in first out. Repetitions are removed.
class DataManager:
  def __init__(self):
    self._data = list()
    self._boards_to_indices = dict()
    with open('SolvedFormattedData.txt') as f:
      solved = np.array(json.load(f))
    X = solved[:, :42]
    y1 = solved[:, 42:43]
    y2 = solved[:, 43:]
    self._solved = (X, (y1, y2))

  def add_data_from_file(self, file_name):
    with open(file_name) as f:
      self.add_data(np.array(json.load(f)))
    f.close()

  def add_data(self, new_data):
    counts_this_iteration = dict()  # map board to number of times seen this iteration
    for game_data in new_data:
      game_data = game_data.reshape((-1, 50))
      for data_point in game_data:
        hash_array = HashableArray(data_point[:42])
        if hash_array not in self._boards_to_indices.keys():
          self._boards_to_indices[hash_array] = len(self._data)
          self._data.append(data_point)
        else:
          index = self._boards_to_indices[hash_array]
          if hash_array not in counts_this_iteration.keys():
            counts_this_iteration[hash_array] = 0
          n = counts_this_iteration[hash_array]
          average = self._data[index]
          data_point = (data_point + n * average) / (n + 1)
          self._data[index] = data_point
          counts_this_iteration[hash_array] += 1

  def get_data(self):
    start = max(0, len(self._data) - MAXSIZE)
    end = len(self._data)
    size = end - start

    include = np.random.uniform(size=size) < 0.75
    data = []
    validation = []
    for i, data_point in enumerate(self._data[start:end]):
      if include[i]:
        data.append(data_point)
      else:
        validation.append(data_point)
    data = np.array(data).reshape((-1, 50))
    validation = np.array(validation).reshape((-1, 50))
    X = data[:, :42]
    y1 = data[:, 42 :43]
    y2 = data[:, 43:]
    
    Xv = validation[:, :42]
    y1v = validation[:, 42 :43]
    y2v = validation[:, 43:]
    print(len(y1))
    return X, y1, y2, (Xv, (y1v, y2v))

  def get_solved_data(self):
    return self._solved

  def save(self, location):
    data = []
    start = max(0, len(self._data) - MAXSIZE)
    for data_point in self._data[start:]:
      data.append(data_point.tolist())
    with open(location, 'w') as f:
      json.dump(data, f)
    f.close()