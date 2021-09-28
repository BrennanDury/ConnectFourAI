# ConnectFourAI
This project creates Connect Four AI using a variety of algorithms. AI strategies vary in sophistication from a simple minimax with a heuristic based on domain knowledge to an implementation of AlphaZero.

The AlphaZero implementation is located in the AlphaZero folder, while all other strategies are located in the OtherStrategies folder.

The AlphaZero strategy uses a Monte Carlo search tree to explore promising moves according to a Convolutional Neural Network. 200 searches are performed to calculate the best move. At the end of the searches, the most visited move is chosen. When traversing the tree, from a given board state, the most promising move to explore is the move that maximizes q(a) + c * p(a) * sqrt(n(b)) / (n(a) + 1), where q is the current estimation of the quality of a board, c is a hyperparameter controlling the amount of exploration vs exploitation, set to 4, p is the prior evaluation of a board according the neural network, n is the number of times a board has been visited, a is the board state after an action, and b is the current board state. This function balances exploration of new information vs exploitation of current information.

A single search builds a game tree until a new board state is found or the simulated game is won. If a new board state is found, the search tree inferences the neural net for a prior evaluation of the resulting board of each possible next move. The evaluation of the board after the best next move is backpropegated towards the root board, updating q for each board that led to the end of this search. If the simulated game is won, the result is backpropegated and each board and its result is added to the training data for the next neural network. The network is retrained every 10 games.

The most basic strategy uses minimax to build a search tree. Positions at the max depth are evaluated with a heuristic that counts the number of fours, open threes, open twos, and open ones. The minimax algorithm includes alpha beta pruning, a transposition table, iterative deepening, and move ordering with a cheap heuristic to improve search time and by extension, increase max depth.

A simple but effective strategy is a basic Monte Carlo search tree. Instead of applying a heuristic, run fully random simulations from a given board until the game ends and choose the move with the highest win rate. This strategy produces a reasonable human level player. It is likely to miss sequences which require precise play, but is capable of making open connections, blocking its opponent’s open connections, prioritizing the center, and ignoring unreachable connections.

The MLP and Perceptron files contain my own implementations of neural networks trained with an evolutionary algorithm in java without using libraries. I created a working neural network and later transitioned to TensorFlow for the AlphaZero implementation to take advantage of gpu optimization.

