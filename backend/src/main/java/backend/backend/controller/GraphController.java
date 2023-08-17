package backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.web.bind.annotation.RequestMapping;
import backend.backend.model.*;
import backend.backend.repositories.*;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private TeammateEdgeRepository teammateEdgeRepository;
    @Autowired
    private OpponentEdgeRepository opponentEdgeRepository;


    @GetMapping("/both")
    public ResponseEntity<List<GraphEdge>> findShortestPathBoth(
            @RequestParam String source,
            @RequestParam String destination) {
        Long sourceId = getIdForNode(source);
        Long destinationId = getIdForNode(destination);
        if(sourceId == null || destinationId == null){
            return ResponseEntity.notFound().build();
        }
        List<GraphEdge> shortestPath = dijkstraAlgorithm(sourceId, destinationId, "both");

        if (shortestPath == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(shortestPath);
    }

    @GetMapping("/teammates")
    public ResponseEntity<List<GraphEdge>> findShortestPathTeammates(
            @RequestParam String source,
            @RequestParam String destination) {
        Long sourceId = getIdForNode(source);
        Long destinationId = getIdForNode(destination);
        if(sourceId == null || destinationId == null){
            return ResponseEntity.notFound().build();
        }
        List<GraphEdge> shortestPath = dijkstraAlgorithm(sourceId, destinationId, "teammates");

        if (shortestPath == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(shortestPath);
    }

    @GetMapping("/opponents")
    public ResponseEntity<List<GraphEdge>> findShortestPathOpponents(
            @RequestParam String source,
            @RequestParam String destination) {
        Long sourceId = getIdForNode(source);
        Long destinationId = getIdForNode(destination);
        if(sourceId == null || destinationId == null){
            return ResponseEntity.notFound().build();
        }
        List<GraphEdge> shortestPath = dijkstraAlgorithm(sourceId, destinationId, "opponents");

        if (shortestPath == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(shortestPath);
    }

    // Dijkstra's Algorithm Implementation
    private List<GraphEdge> dijkstraAlgorithm(Long source, Long destination, String type) {
        // Priority queue for minimum distance nodes
        PriorityQueue<NodeDistance> minHeap = new PriorityQueue<>();
        minHeap.add(new NodeDistance(source, 0.0));
    
        // Maps to store distances and previous nodes
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>();
        distances.put(source, 0.0);
    
        // Create a map to store all edges associated with the neighbors
        Map<Long, List<GraphEdge>> allEdges = new HashMap<>();
    
        // Run Dijkstra's algorithm
        while (!minHeap.isEmpty()) {
            NodeDistance currentNode = minHeap.poll();
            Long node = currentNode.node;
            double distance = currentNode.distance;
    
            if (node.equals(destination)) {
                break; // Terminate if destination node is reached
            }
    
            List<GraphEdge> edges = new ArrayList<>();
            if (type.equals("teammates") || type.equals("both")) {
                List<TeammateEdge> teammateEdges = teammateEdgeRepository.getAllEdgesWithID(node);
                for (TeammateEdge edge : teammateEdges) {
                    Long neighborId = edge.getNode1().equals(node) ? edge.getNode2() : edge.getNode1();
                    edges.add(edge);
                    double edgeWeight = 1.0; // Set the appropriate edge weight here
                    double totalDistance = distance + edgeWeight;
                    if (!allEdges.containsKey(neighborId)) {
                        allEdges.put(neighborId, new ArrayList<>());
                    }
                    allEdges.get(neighborId).add(edge);
    
                    if (totalDistance < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                        distances.put(neighborId, totalDistance);
                        previousNodes.put(neighborId, node);
                        minHeap.add(new NodeDistance(neighborId, totalDistance));
                    }
                }
            }
    
            if (type.equals("opponents") || type.equals("both")) {
                List<OpponentEdge> opponentEdges = opponentEdgeRepository.getAllEdgesWithID(node);
                for (OpponentEdge edge : opponentEdges) {
                    Long neighborId = edge.getNode1().equals(node) ? edge.getNode2() : edge.getNode1();
                    edges.add(edge);
                    double edgeWeight = 1.0; // Set the appropriate edge weight here
                    double totalDistance = distance + edgeWeight;
                    if (!allEdges.containsKey(neighborId)) {
                        allEdges.put(neighborId, new ArrayList<>());
                    }
                    allEdges.get(neighborId).add(edge);
    
                    if (totalDistance < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                        distances.put(neighborId, totalDistance);
                        previousNodes.put(neighborId, node);
                        minHeap.add(new NodeDistance(neighborId, totalDistance));
                    }
                }
            }
        }
    
        // Build and return the shortest path as a list of GraphEdge objects
        List<GraphEdge> shortestPath = new ArrayList<>();
        Long currentNode = destination;
    
        while (previousNodes.containsKey(currentNode)) {
            Long prevNode = previousNodes.get(currentNode);
            System.out.println(currentNode + " " + prevNode);
            List<GraphEdge> edges = allEdges.get(prevNode);
            boolean found = false;
            if (edges != null) {
                for (GraphEdge edge : edges) {
                    if ((edge.getNode1().equals(prevNode) && edge.getNode2().equals(currentNode)) ||
                        (edge.getNode2().equals(prevNode) && edge.getNode1().equals(currentNode))) {
                        shortestPath.add(edge);
                        found = true;
                        break; // Assuming there is only one edge between nodes
                    }
                }
            }
            List<GraphEdge> edges2 = allEdges.get(currentNode);
            if (edges != null && !found) {
                for (GraphEdge edge : edges2) {
                    if ((edge.getNode1().equals(prevNode) && edge.getNode2().equals(currentNode)) ||
                        (edge.getNode2().equals(prevNode) && edge.getNode1().equals(currentNode))) {
                        shortestPath.add(edge);
                        break; // Assuming there is only one edge between nodes
                    }
                }
            }
    
            if (currentNode.equals(prevNode)) {
                System.out.println("what");
                break;
            }
    
            currentNode = prevNode;
        }
    
        translateIds(shortestPath);
        Collections.reverse(shortestPath);
    
        return shortestPath;
    }
    

    // Helper class for nodes and distances
    private static class NodeDistance implements Comparable<NodeDistance> {
        Long node;
        double distance;

        NodeDistance(Long node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
    
    private Long getIdForNode(String player_name) {
        return nodeRepository.getIdForNode(player_name);
    }
    private void translateIds(List<GraphEdge> list){
        for(int i=0; i<list.size();i++){
            list.get(i).setPlayer1(nodeRepository.getPlayerNameForNode(list.get(i).getNode1()));
            list.get(i).setPlayer2(nodeRepository.getPlayerNameForNode(list.get(i).getNode2()));
        }
    }
}
    
