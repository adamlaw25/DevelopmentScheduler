import java.util.LinkedList;

public class AssignmentNode {

	private int id;
	// The time of the node in the schedule
	private int time;
	private boolean isStart;

	LinkedList<AssignmentEdge> inEdges;
	LinkedList<AssignmentEdge> outEdges;

	public AssignmentNode(int id, boolean isStart) {
		this.id = id;
		this.time = 0;
		this.isStart = isStart;
		this.inEdges = new LinkedList<AssignmentEdge>();
		this.outEdges = new LinkedList<AssignmentEdge>();
	}

	public final int getId() {
		return id;
	}

	public final boolean isStart() {
		return isStart;
	}

	public final int getTime() {
		return time;
	}

	public final void setTime(int time) {
		this.time = time;
	}

	public LinkedList<AssignmentEdge> getInEdges() {
		return inEdges;
	}

	public void addInEdge(AssignmentEdge edge) {
		this.getInEdges().add(edge);
	}

	public LinkedList<AssignmentEdge> getOutEdges() {
		return outEdges;
	}

	public void addOutEdge(AssignmentEdge edge) {
		this.getOutEdges().add(edge);
	}

}
