package it.polimi.ingsw.model.playerset;

import it.polimi.ingsw.model.utilities.components.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * this class use the graph version of the board created by the Graph class in order to check how many
 * connected components the ship has
 */
public class ConnectedComponents {
    private HashSet<Component> visited;

    public ConnectedComponents(){
        visited = new HashSet<>();
    }

    /**
     * findConnectedComponents() returns a List containing all the connected components in the graph. If the
     * list size is greater than one than it means that the ship is not integer
     */
    public List<HashSet<Component>> findConnectedComponents(Graph g){
        List<HashSet<Component>> result = new ArrayList<>();
        for(Component c: g.getGraph().keySet()){
            if(!visited.contains(c)){
                HashSet<Component> component = new HashSet<>();
                depthFirst(c, g, component);
                result.add(component);
            }
        }
        return result;
    }

    /**
     * depthFirst() is an algorithm that, for each given component, finds and return the greatest connected components
     * that contains that component
     */
    private void depthFirst(Component c, Graph g, HashSet<Component> component){
        Stack<Component> stack = new Stack<>();
        stack.push(c);
        visited.add(c);

        while(!stack.isEmpty()){
            Component current = stack.pop();
            component.add(current);
            for(Component linked: g.getGraph().get(current)){
                if(!visited.contains(linked)){
                    visited.add(linked);
                    stack.push(linked);
                }
            }
        }
    }
}
