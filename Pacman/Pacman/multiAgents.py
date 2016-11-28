# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"


        return successorGameState.getScore()

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '10'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
  """
    Minimax agent (question 2)
  """

  def getAction(self, gameState):

    def max_value(state, depth):
      if state.isWin() or state.isLose() or depth == self.depth:
        return self.evaluationFunction(state)

      v = float('-Inf')
      for a in state.getLegalActions():
        v = max(v, min_value(state.generateSuccessor(self.index, a), depth, self.index+1))
      return v

    def min_value(state, depth, opponentIndex):
      if state.isWin() or state.isLose():
        return self.evaluationFunction(state)
      v = float('Inf')
      for a in state.getLegalActions(opponentIndex):
        if opponentIndex == gameState.getNumAgents() - 1:
          v = min(v, max_value(state.generateSuccessor(opponentIndex, a), depth+1))
        else:
          v = min(v, min_value(state.generateSuccessor(opponentIndex, a), depth, opponentIndex + 1))
      return v
      
    maxUtility = float('-Inf')
    max_a = ''
    legalActions = gameState.getLegalActions(self.index)

    for a in legalActions:
      tempMaxUtility =  min_value(gameState.generateSuccessor(self.index, a), 0, self.index+1)
      if tempMaxUtility > maxUtility:
        maxUtility = tempMaxUtility
        max_a = a

    return max_a
    

class AlphaBetaAgent(MultiAgentSearchAgent):
  """
    Minimax agent with alpha-beta pruning (question 3)
  """
  def getAction(self, gameState):
   
    def max_value(state, alpha, beta, depth):
      
      if state.isWin() or state.isLose() or depth == self.depth:
        return self.evaluationFunction(state)

      v = float('-Inf')
      for a in state.getLegalActions(self.index):
        v = max(v, min_value(state.generateSuccessor(self.index, a), alpha, beta, depth, self.index+1))
        if v >= beta:
          return v
        alpha = max(alpha, v)
      return v

    def min_value(state, alpha, beta, depth, opponentIndex):
      if state.isWin() or state.isLose():
        return self.evaluationFunction(state)
      v = float('Inf')
      for a in state.getLegalActions(opponentIndex):
        if opponentIndex == gameState.getNumAgents() - 1:
          v = min(v, max_value(state.generateSuccessor(opponentIndex, a), alpha, beta, depth+1))
        else:
          v = min(v, min_value(state.generateSuccessor(opponentIndex, a), alpha, beta, depth, opponentIndex + 1))
        if v <= alpha:
          return v
        beta = min(beta, v)
      return v
    

    alpha = float('-Inf')
    beta = float('Inf')
    v = max_value(gameState, alpha, beta, 0)
    max_a = ''
  
    for action in gameState.getLegalActions(self.index):
      tempV = min_value(gameState.generateSuccessor(0, action), alpha, beta, 0, self.index+1)
      if tempV == v:
        max_a = action
        return max_a
    
    

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction

