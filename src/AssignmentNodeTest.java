import static org.junit.Assert.*;

import org.junit.Test;

public class AssignmentNodeTest {

	//Structured basis test
	//Good data
	//test addInEdge(Same as addOutEdge)
	@Test
	public void testAddInEdge() {
		AssignmentNode testNode1 = new AssignmentNode(1, true);
		AssignmentNode testNode2 = new AssignmentNode(2, true);
		testNode1.addInEdge(new AssignmentEdge(testNode2, 0));
		assertEquals(testNode1.getInEdges().get(0).getConnectedNode().getId(), 2);
	}

}
