package it.polimi.ingsw.model.playerset;

import it.polimi.ingsw.model.utilities.components.Component;
import it.polimi.ingsw.enums.ComponentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * this class is used to model the RocketshipBoard as a graph in order to check its integrity
 */
public class Graph {
    private Map<Component, HashSet<Component>> adj;

    public Graph(){
        adj = new HashMap<>();
    }

    /**
     * addNode() simply adds the node (representing a component) to the graph (setting zero arches backward and forward)
     * @param c is the component we want to add to the graph
     */
    public void addNode(Component c){
        if(c.getComponentType().equals(ComponentType.EMPTY_COMPONENT)) {
            throw new RuntimeException("There must be a mistake");
        }
        else adj.putIfAbsent(c, new HashSet<>());

    }

    /**
     * addArch() adds an arch between two nodes and also adds the two nodes to each other adjacency set
     */
    public void addArch(Component a, Component b){
        adj.putIfAbsent(a, new HashSet<>());
        adj.putIfAbsent(b, new HashSet<>());
        adj.get(a).add(b);
        adj.get(b).add(a);
    }

    /**
     * getGraph() simply returns the graph. Each component has a set containing his linked components
     */
    public Map<Component, HashSet<Component>> getGraph(){
        return adj;
    }
}
