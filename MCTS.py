# For performing a MCTS backed by a neural network. The only method intended to be public is get_move.

import sys
import numpy as np
from scipy.stats import dirichlet
from ConnectFour import ConnectFour, ROWS, COLS, GAMENOTOVER, PLAYERNONE

# Exploration constant; higher values favor more exploration
C = 1

# Turn number at which the MCTS becomes deterministic
TAO = 10

# Number of MCTS simulations to choose a move
SIMULATIONS = 800

# Controls the dirichlet noise
EPSILON = 0.2  # Contribution of noise to the weighted average
ALPHA = np.full(COLS, 1.75)  # Shape of distribution, slightly prefers dense vectors

# ADT representing statistics about a game state that MCTS uses to evaluate moves to explore.
class Node:
  def __init__(self):
    self.q = 0  # Quality of the board from the perspective of the player who just played in the range (-1, 1)
    self.n = 0  # Visits to this node
    self.probabilities = np.zeros(COLS)  # Probability of each next move


# Calculate the initial statistics for a node.
def first_visit(g, nodes, network):
  board = g.get_board()
  node = nodes[board]
  output = network.predict(board.get_arr().reshape((1, ROWS * COLS)))
  p_vector = np.array(output[1][0], dtype=float)
  if g.is_symmetrical():
    p_vector = (p_vector + np.flip(p_vector)) / 2
  node.probabilities = p_vector
  node.q = output[0][0][0]
  # Add each child
  for move in range(COLS):
    if g.move(move):
      move_board = g.get_board()
      if not move_board in nodes.keys():
        child = Node()
        flip_board = g.get_flip()
        nodes[move_board] = child
        nodes[flip_board] = child
      g.undo()
  node.n = 1
  return node.q

# Updates the quality of a node and increment its visit count
def update(g, nodes, v):
  node = nodes[g.get_board()]
  node.q = (node.q * node.n + v) / (node.n + 1);
  node.n += 1

# Returns an array of the number of visits to each child node
def get_visits(g, nodes):
  visits = np.zeros(COLS)
  for i in range(COLS):
    if g.move(i):
      child = nodes[g.get_board()]
      visits[i] = child.n
      g.undo()    
  return visits

# Chooses what move is most worthy of exploration.
def choose_move_search(g, nodes):
  bounds = np.zeros(COLS)
  node = nodes[g.get_board()]
  for i in range(COLS):
    if g.move(i):
      child = nodes[g.get_board()]
      # Get statistics
      q_action = child.q
      n_action = child.n
      p_action = node.probabilities[i]

      # PUCT: Balances exploration vs exploitation
      score = q_action + C * p_action * node.n ** (1/2) / (n_action + 1)
      bounds[i] = score
      g.undo()
    else:
      bounds[i] = -sys.float_info.max
  return np.argmax(bounds)

# Adds dirichlet noise to the probability vector
def noise(node, rng):
  p_vector = node.probabilities
  noise_vector = dirichlet.rvs(ALPHA, size=1, random_state=rng)[0]
  node.probabilities = ((1 - EPSILON) * p_vector) + ((EPSILON) * noise_vector)

# Performs one MCTS simulation
def search(g, nodes, network, training):
  result = g.get_winner()
  node = nodes[g.get_board()]
  if result != GAMENOTOVER:
    # Game end state, backpropegate the result
    update(g, nodes, 1)
    if result == PLAYERNONE:
      return 0
    else:
      return -1
  elif node.n == 0:
    # New game state, backpropegate the result
    q = first_visit(g, nodes, network)
    return -q
  else:
    # Seen this game state before, pick the best move to explore and continue building the tree
    move = choose_move_search(g, nodes)
    g.move(move)
    v = search(g, nodes, network, training)
    g.undo()
    update(g, nodes, v)
    return -v

# Evaluates the move to play based on the search results. Before turn TAO, samples from the visits to each child
# node as a probability vector. After turn TAO, chooses the most visited move
def final_move(g, nodes, rng, training):
  visits = get_visits(g, nodes)    
  if g.get_turn() < TAO and training:
    if g.is_symmetrical():
      return rng.choice(4, p=visits[:4] / sum(visits[:4])), visits
    return rng.choice(COLS, p=visits / sum(visits)), visits
  else:
    return np.argmax(visits), visits

# Performs a MCTS to choose a move. Takes a ConnectFour game in any state and a neural network.
def get_move(g, network, rng, training, simulations=SIMULATIONS):
  root = Node()
  nodes = dict()
  nodes[g.get_board()] = root
  nodes[g.get_flip()] = root
  initial_q = first_visit(g, nodes, network)
  initial_p = root.probabilities
  if training:
    noise(root, rng)
  for i in range(simulations):
    search(g, nodes, network, training)
  move, visits = final_move(g, nodes, rng, training)
  if training:
    print('board, turn:')
    print(np.flip(g.get_board().get_arr().reshape((6, 7)), axis=0).astype(int) % 3, g.get_turn())
  print('initial p, z:')
  print(initial_p, initial_q)
  if training:
    print('noisy p:')
    print(root.probabilities)
  print('improved p, q:')
  print(visits / np.sum(visits), root.q)
  return move, visits / np.sum(visits), root.q