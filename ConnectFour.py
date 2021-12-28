import numpy as np

# Constants for the game
ROWS = 6
COLS = 7
GAMENOTOVER = -2
PLAYER1 = 1
PLAYER2 = -1
PLAYERNONE = 0
MASK = np.array([3 ** i for i in range(42)])  # For efficiently hashing game states
HORIZONTAL = 0
VERTICAL = 1
FORWARD = 2
BACKWARD = 3

# Hashable wrapper class for a np array representing a board
class HashableArray:
  def __init__(self, arr):
    self._arr = arr

  def get_arr(self):
    return self._arr

  def __hash__(self):
    return int(np.dot(MASK, self._arr + 1))

  def __eq__(self, other):
    return np.all(self._arr == other._arr)


# Manages game state.
class ConnectFour: 
  def __init__(self):
    self._board = np.zeros((ROWS, COLS))
    self._flip = np.zeros((ROWS, COLS))
    self._reverse_board = np.zeros((ROWS, COLS))
    self._reverse_flip = np.zeros((ROWS, COLS))

    self._height = np.zeros(COLS, dtype=np.int8)
    self._next = 1
    self._undo_list = np.zeros((ROWS * COLS, 2))
    self._turn = 0
    self._winner = -2

  # Makes a move in the column.
  def move(self, col):
    col = int(col)
    if self._height[col] >= ROWS:
      return False
    else:
      self._board[int(self._height[col])][col] = self._next
      self._flip[int(self._height[col])][COLS - col - 1] = self._next

      self._reverse_board[int(self._height[col])][col] = self._next * -1
      self._reverse_flip[int(self._height[col])][COLS - col - 1] = self._next * -1
      self._undo_list[self._turn] = np.array([self._height[col], col])

      checks = [self._check4(int(self._height[col]), col, dir) for dir in range(4)]
      self._turn += 1
      self._height[col] += 1
      self._next *= -1
      if np.sum(checks) > 0:
        self._winner = PLAYER1
      elif np.sum(checks) < 0:
        self._winner = PLAYER2
      elif np.sum(self._height) == ROWS * COLS:
        self._winner = PLAYERNONE
      return True

  # Restores the game state before the last move.
  def undo(self):
    row, col = tuple(self._undo_list[self._turn - 1])
    row = int(row)
    col = int(col)
    self._board[row][col] = PLAYERNONE
    self._flip[row][COLS - col - 1] = PLAYERNONE
    self._reverse_board[row][col] = PLAYERNONE
    self._reverse_flip[row][COLS - col - 1] = PLAYERNONE
    
    self._undo_list[self._turn - 1] = np.zeros(2)
    self._turn -= 1
    self._height[col] -= 1
    self._next *= -1
    self._winner = GAMENOTOVER

  # Returns PLAYER1 if player 1 is the winner in the current game state, PLAYER2 if
  # player 2 is the winner in the current game state, PLAYERNONE if the game is a draw, GAMENOTOVER if the game is not
  # decided yet.
  def get_winner(self):
    return self._winner

  # Returns the current turn number, starting with 0
  def get_turn(self):
    return self._turn

  # Returns the board as a HashableArray. The board returned appears to the next player as if they
  # are player 1. So if the next player is player 2, the pieces are flipped. This is done so the neural network
  # can always understand itself as player 1.
  def get_board(self):
    if (self._next == PLAYER1):
      return HashableArray(self._board.flatten())
    else:
      return HashableArray(self._reverse_board.flatten())

  # Returns the board reflected horizontally. Connect Four is the same game reflected horizontally, so the neural
  # network benefits from augmenting the data by flipping all boards.
  def get_flip(self):
    if (self._next == PLAYER1):
      return HashableArray(self._flip.flatten())
    else:
      return HashableArray(self._reverse_flip.flatten())

  # Private helper function checks a particular game location for a 4 in a row. Checks both directions around the
  # input row and col in the orientation given by the input direction.
  def _check4(self, row, col, direction):
    row = int(row)
    col = int(col)
    if direction == HORIZONTAL:
      dr = 0
      dc = 1
    elif direction == VERTICAL:
      dr = 1
      dc = 0
    elif direction == FORWARD:
      dr = 1
      dc = -1
    else: # direction == BACKWARD
      dr = 1
      dc = 1
    player_at = self._board[row][col]
    length = 1
    i = 1
    j = -1
    while 0 <= row + i * dr < ROWS and 0 <= col + i * dc < COLS \
          and self._board[row + i * dr][col + i * dc] == player_at:
      i += 1
      length += 1
      if length == 4:
        return player_at
    while 0 <= row + j * dr < ROWS and 0 <= col + j * dc < COLS \
          and self._board[row + j * dr][col + j * dc] == player_at:
      j -= 1
      length += 1
      if length == 4:
        return player_at
    return PLAYERNONE

  def is_symmetrical(self):
    return np.array_equal(self._board[:, :3], self._flip[:, :3])