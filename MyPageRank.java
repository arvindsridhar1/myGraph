package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import support.graph.CS16Edge;
import support.graph.CS16Vertex;
import support.graph.Graph;
import support.graph.PageRank;

/**
 * In this class you will implement one of many different versions
 * of the PageRank algorithm. This algorithm will only work on
 * directed graphs. Keep in mind that there are many different ways
 * to handle sinks.
 *
 * Make sure you review the help slides and handout for details on
 * the PageRank algorithm.
 *
 */
public class MyPageRank<V> implements PageRank<V> {
	private Graph<V> _g;
	private List<CS16Vertex<V>> _vertices;
	private Map<CS16Vertex<V>, Double> _vertsToRanks;
	private List<CS16Vertex<V>> _graphSinks;
	private List<Integer> _numOutgoingEdges;
	private List<Double> _previousPageRank;
	private List<Double> _currentPageRank;
	private double _numRounds = 0;
 	private static final double _dampingFactor = 0.85;
	private static final int _maxIterations = 100;
	private static final double _error = 0.01;

	/**
	 * TODO: Feel free to add in anything else necessary to store the information
	 * needed to calculate the rank. Maybe make something to keep track of sinks,
	 * your ranks, and your outgoing edges?
	 */

	/**
	 * The main method that does the calculations! You'll want to call the methods
	 * that initialize your variables here. You'll also want to decide on a
	 * type of loop - for loop, do while, or while loop - for your calculations.
	 *
	 * @return A Map of every Vertex to its corresponding rank
	 *
	 */
	@Override
	public Map<CS16Vertex<V>, Double> calcPageRank(Graph<V> g) {
		_g = g;
		_vertices = new ArrayList<>();
		_vertsToRanks = new HashMap<>();
		_previousPageRank = new ArrayList<>();
		_currentPageRank = new ArrayList<>();
		_graphSinks = new ArrayList<>();
		_numOutgoingEdges = new ArrayList<>();


		int numVertices = g.getNumVertices();
		Iterator<CS16Vertex<V>> graphVertices = g.vertices();
		while(graphVertices.hasNext()){
			CS16Vertex<V> next = graphVertices.next();
			_vertices.add(next);
			_currentPageRank.add(1.0/numVertices);
			_numOutgoingEdges.add(g.numOutgoingEdges(next));
			if(g.numOutgoingEdges(next) == 0){
				_graphSinks.add(next);
			}
		}

		do{
			for(int i= 0; i < _vertices.size(); i++){
				_previousPageRank.add(i, _currentPageRank.get(i));
			}
			this.handleSinks();
			for(int i = 0; i < _vertices.size(); i++){
				double updatedRank = this.updatePageRank(_vertices.get(i));
				_currentPageRank.set(i, updatedRank);
				_vertsToRanks.put(_vertices.get(i), updatedRank);
			}
			_numRounds ++;
		} while(!checkForStoppage());

		return _vertsToRanks;
	}

	/**
	 * Method used to account for sink pages (those with no outgoing
	 * edges). There are multiple ways you can implement this, check
	 * the lecture and help slides!
	 */
	private void handleSinks() {
		double rankSum = 0;
		for (int i = 0; i < _vertices.size(); i++){
			if(_numOutgoingEdges.get(i) == 0){
				rankSum += _previousPageRank.get(i) / _g.getNumVertices();
			}
		}
		for(int i = 0; i < _vertices.size(); i++){
			_currentPageRank.set(i, rankSum);
		}
	}

	//makeCheckStoppage method
	private boolean checkForStoppage(){
		for (int i = 0; i < _vertices.size(); i++){
			if(Math.abs(_currentPageRank.get(i) - _previousPageRank.get(i)) > _error || _numRounds > _maxIterations){
				return false;
			}
		}
		_numRounds ++;
		return true;
	}

	private double updatePageRank(CS16Vertex<V> vertex){
		Iterator<CS16Edge<V>> incomingEdges = _g.incomingEdges(vertex);
		while(incomingEdges.hasNext()){
			CS16Edge<V> edge = incomingEdges.next();
			CS16Vertex<V> oppositeVertex = _g.opposite(vertex, edge);
			_currentPageRank.set(_vertices.indexOf(vertex), _currentPageRank.get(_vertices.indexOf(vertex)) +
					_previousPageRank.get(_vertices.indexOf(oppositeVertex))/_numOutgoingEdges.get(_vertices.indexOf(oppositeVertex)));
		}

		double updatedRank = ((1-_dampingFactor) / _g.getNumVertices()) +
				(_dampingFactor * _currentPageRank.get(_vertices.indexOf(vertex)));
		return updatedRank;
	}

}
