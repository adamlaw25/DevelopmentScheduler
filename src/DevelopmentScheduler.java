import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class DevelopmentScheduler {

	private HashMap<Integer, Assignment> assignmentsMap = new HashMap<Integer, Assignment>();

	public final HashMap<Integer, Assignment> getAssignmentsMap() {
		return assignmentsMap;
	}

	// a map to represent the relation between the int relation and its enum
	// relation
	private static Map<Integer, AssignmentEdge.Relationship> relationshipMap;
	static {
		relationshipMap = new HashMap<Integer, AssignmentEdge.Relationship>();
		relationshipMap.put(1, AssignmentEdge.Relationship.END_BEGIN);
		relationshipMap.put(2, AssignmentEdge.Relationship.BEGIN_BEGIN);
		relationshipMap.put(3, AssignmentEdge.Relationship.BEGIN_END);
		relationshipMap.put(4, AssignmentEdge.Relationship.END_END);
	}

	public void assignmentNodeInitializer(int[][] assignmentsList) {
		assignmentsMap.clear();
		for (int[] assignment : assignmentsList) {
			addAssignment(assignment);
		}
	}

	private void addAssignment(int[] assignment) {
		int assignmentId = assignment[0];
		int assignmentDuration = assignment[1];

		if (assignmentId < 0 || assignmentDuration <= 0) {
			throw new IllegalArgumentException();
		}

		AssignmentNode startNode = new AssignmentNode(assignmentId, true);
		AssignmentNode endNode = new AssignmentNode(assignmentId, false);
		// add out edge to startnode
		startNode.addOutEdge(new AssignmentEdge(endNode, assignmentDuration));
		// add in edge to endnode
		endNode.addInEdge(new AssignmentEdge(startNode, assignmentDuration));
		Assignment currentAssignment = new Assignment(assignmentId, startNode, endNode, assignmentDuration);
		assignmentsMap.put(assignmentId, currentAssignment);
	}

	public void addRelations(int[][] relationsList) {
		for (int[] relation : relationsList) {
			Assignment targetAssignment = assignmentsMap.get(relation[0]);
			Assignment sourceAssignment = assignmentsMap.get(relation[1]);
			int relationship = relation[2];
			edgesCreator(targetAssignment, sourceAssignment, relationship);
		}
	}

	private static void edgesCreator(Assignment targetAssignment, Assignment sourceAssignment, int relationship) {

		// get the relationship type out of the map
		AssignmentEdge.Relationship relation = relationshipMap.get(relationship);

		AssignmentNode targetNode = relation.targetIsStart() ? targetAssignment.getStartNode()
				: targetAssignment.getEndNode();
		AssignmentNode sourceNode = relation.sourceIsStart() ? sourceAssignment.getStartNode()
				: sourceAssignment.getEndNode();
		targetNode.addInEdge(new AssignmentEdge(sourceNode, 0));
		sourceNode.addOutEdge(new AssignmentEdge(targetNode, 0));

	}

	public static LinkedList<AssignmentNode> topologicalSort(HashMap<Integer, Assignment> assignmentsMap) {
		LinkedList<AssignmentNode> sortedNodeList = new LinkedList<AssignmentNode>();
		// the stack to handle the order of the nodes
		Stack<AssignmentNode> stack = new Stack<AssignmentNode>();
		// the map to store the assignment nodes and their number of in edges
		HashMap<AssignmentNode, Integer> inEdgesCountMap = getInEdgesCountMap(assignmentsMap, stack);

		// go through all the nodes in the map
		while (!stack.empty()) {
			// get the first node in the stack and add it to the sorted list
			AssignmentNode currentNode = stack.pop();
			// System.out.println(currentNode.toString());
			sortedNodeList.add(currentNode);
			sortOutNodes(inEdgesCountMap, currentNode, stack);
		}

		/*
		 * if the count of node in the sorted list is 2 * number of assignment, the list
		 * is correct otherwise there is a circular requirement since the list adds at
		 * least 1 node more than 1 time to the list
		 */
		if (sortedNodeList.size() == 2 * assignmentsMap.size()) {
			return sortedNodeList;
		} else {
			throw new IllegalStateException();
		}
	}

	private static HashMap<AssignmentNode, Integer> getInEdgesCountMap(HashMap<Integer, Assignment> assignmentsMap,
			Stack<AssignmentNode> stack) {
		HashMap<AssignmentNode, Integer> inEdgesCountMap = new HashMap<AssignmentNode, Integer>();
		for (int id : assignmentsMap.keySet()) {
			Assignment currentAssignment = assignmentsMap.get(id);

			AssignmentNode startNode = currentAssignment.getStartNode();
			AssignmentNode endNode = currentAssignment.getEndNode();

			// put the 2 nodes in the map
			inEdgesCountMap.put(startNode, startNode.getInEdges().size());
			inEdgesCountMap.put(endNode, endNode.getInEdges().size());
			// System.out.println("start: " + startNode + " " +
			// inEdgesCountMap.get(startNode));
			// System.out.println("end:" + endNode + " " + inEdgesCountMap.get(endNode));
			// if the node has no in edge which means it will be put to the beginning of the
			// sorted list, push it to the stack
			if (inEdgesCountMap.get(startNode) == 0) {
				stack.push(startNode);
			}

			if (inEdgesCountMap.get(endNode) == 0) {
				stack.push(endNode);
			}
		}

		return inEdgesCountMap;
	}

	// sort the out connected nodes of the current node
	private static void sortOutNodes(HashMap<AssignmentNode, Integer> inEdgesCountMap, AssignmentNode currentNode,
			Stack<AssignmentNode> stack) {
		// check each out edge
		for (AssignmentEdge outEdge : currentNode.getOutEdges()) {
			// The connected out node of the current node
			AssignmentNode outNode = outEdge.getConnectedNode();
			// System.out.println("in: " + currentNode.toString() + "out: " +
			// outNode.toString());
			// update the in edges count to the current count - 1
			inEdgesCountMap.put(outNode, inEdgesCountMap.get(outNode) - 1);

			// check if the new in edge count is 0 and if it is, push the node to the stack
			if (inEdgesCountMap.get(outNode) == 0) {
				stack.push(outNode);
			}
		}
	}

	public static Integer deliveryTimeCalculator(int[][] assignmentsList, int[][] relationsList) {
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);

		int endTime = 0;
		LinkedList<AssignmentNode> sortedNodeList = topologicalSort(schedule.getAssignmentsMap());

		// go through the sorted list
		for (AssignmentNode node : sortedNodeList) {
			if (!node.getInEdges().isEmpty()) { // if it does not have in edge, time will be 0 (parallel starting at the
												// beginning)
				// get the maximum duration
				int maxDuration = getMaxDuration(node);
				// set the time of the node to be maxDuration
				node.setTime(maxDuration);

				if (maxDuration > endTime) {
					endTime = maxDuration;
				}
			}
			// print out the schedule
			//System.out.println(sortedNodeList.indexOf(node) + ": " + node.toString());
		}

		return endTime;
	}

	private static int getMaxDuration(AssignmentNode node) {
		int maxDuration = 0;
		for (AssignmentEdge edge : node.getInEdges()) {
			// compare with connectedNode.time + edge.duration
			if (edge.getDuration() + edge.getConnectedNode().getTime() > maxDuration) {
				maxDuration = edge.getDuration() + edge.getConnectedNode().getTime();
			}
		}
		return maxDuration;
	}

	/**
	 * Inner class that will be used to test the private method
	 */
	public class TestHook {

		HashMap<AssignmentNode, Integer> inEdgesCountMap;
		Stack<AssignmentNode> stack;

		public final HashMap<AssignmentNode, Integer> getInEdgesCountMap() {
			return inEdgesCountMap;
		}

		public final Stack<AssignmentNode> getStack() {
			return stack;
		}

		public int addAssignment(int[] assignment) {
			DevelopmentScheduler.this.addAssignment(assignment);
			HashMap<Integer, Assignment> assignmentsMap = DevelopmentScheduler.this.getAssignmentsMap();
			return assignmentsMap.get(assignment[0]).getId();
		}

		public void edgesCreator(Assignment targetAssignment, Assignment sourceAssignment, int relationship) {

			DevelopmentScheduler.edgesCreator(targetAssignment, sourceAssignment, relationship);

		}

		public void getInEdgesCountMap(HashMap<Integer, Assignment> assignmentsMap) {
			stack = new Stack<AssignmentNode>();
			inEdgesCountMap = DevelopmentScheduler.getInEdgesCountMap(assignmentsMap, stack);
		}

		public void sortOutNodes(HashMap<Integer, Assignment> assignmentsMap, AssignmentNode currentNode) {
			getInEdgesCountMap(assignmentsMap);
			DevelopmentScheduler.sortOutNodes(inEdgesCountMap, currentNode, stack);

		}

		public int getMaxDuration(AssignmentNode node) {
			return DevelopmentScheduler.getMaxDuration(node);
		}

	}

}
