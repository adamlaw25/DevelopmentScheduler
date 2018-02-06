
public class Assignment {

	private int id;
	private int duration;
	private AssignmentNode startNode;
	private AssignmentNode endNode;

	public Assignment(int id, AssignmentNode startNode, AssignmentNode endNode, int duration) {
		this.id = id;
		this.duration = duration;
		this.startNode = startNode;
		this.endNode = endNode;
	}

	public final int getId() {
		return id;
	}

	public final int getDuration() {
		return duration;
	}

	public final AssignmentNode getStartNode() {
		return startNode;
	}

	public final AssignmentNode getEndNode() {
		return endNode;
	}


}
