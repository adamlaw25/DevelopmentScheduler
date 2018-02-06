
public class AssignmentEdge {

	// the node of the edge. Its either the source node or the target node
	private AssignmentNode connectedNode;
	private int duration;

	public AssignmentEdge(AssignmentNode connectedNode, int duration) {
		this.connectedNode = connectedNode;
		this.duration = duration;
	}

	public final AssignmentNode getConnectedNode() {
		return connectedNode;
	}

	public int getDuration() {
		return duration;
	}

	public enum Relationship {
		END_BEGIN(true, false), BEGIN_BEGIN(true, true), BEGIN_END(false, true), END_END(false, false);

		private Boolean targetIsStart;
		private Boolean sourceIsStart;

		private Relationship(Boolean targetIsStart, Boolean sourceIsStart) {
			this.targetIsStart = targetIsStart;
			this.sourceIsStart = sourceIsStart;
		}

		public final Boolean targetIsStart() {
			return targetIsStart;
		}

		public final Boolean sourceIsStart() {
			return sourceIsStart;
		}

	}

}
