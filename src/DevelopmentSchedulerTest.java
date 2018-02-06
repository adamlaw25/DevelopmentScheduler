import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class DevelopmentSchedulerTest {

	private DevelopmentScheduler testScheduler;
	private DevelopmentScheduler.TestHook testHook;

	@Before
	public void setUp() throws Exception {
		/* Set up the object to be tested */
		testScheduler = new DevelopmentScheduler();
		testHook = testScheduler.new TestHook();
	}

	// test assignmentNodeInitializer
	// Boundary: no assignment
	// Compound Boundary: no assignment
	@Test
	public void test_assignmentNodeInitializer_none() {
		int assignmentsList[][] = {};
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		assertTrue(assignmentsMap.isEmpty());
	}

	// Boundary: only 1 assignment
	@Test
	public void test_assignmentNodeInitializer_only_one() {
		int assignmentsList[][] = { { 1, 3 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		assertTrue(assignmentsMap.containsKey(1));
		assertEquals(assignmentsMap.get(1).getDuration(), 3);
	}

	// Structured basis: a case with some random assignments
	// Good data: some expected good data
	@Test
	public void test_assignmentNodeInitializer_good_data() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		assertTrue(assignmentsMap.containsKey(1));
		assertTrue(assignmentsMap.containsKey(2));
		assertTrue(assignmentsMap.containsKey(3));
		assertTrue(assignmentsMap.containsKey(4));
		// not contain
		assertFalse(assignmentsMap.containsKey(5));
		assertEquals(assignmentsMap.get(1).getDuration(), 3);
		assertEquals(assignmentsMap.get(2).getDuration(), 2);
		assertEquals(assignmentsMap.get(3).getDuration(), 4);
		assertEquals(assignmentsMap.get(4).getDuration(), 4);
	}

	// Compound Boundary test: a great amount of input for the 2d array
	// Stress test: a big load of number
	@Test
	public void test_assignmentNodeInitializer_stress() {
		int assignmentsList[][] = new int[100000][2];
		int durationList[] = new int[100000];
		for (int i = 0; i < 100000; i++) {
			Random ran = new Random();
			int k = ran.nextInt(500) + 1;
			assignmentsList[i] = new int[] { i + 1, k };
			durationList[i] = k;
		}
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();

		for (int i = 1; i <= 100000; i++) {
			assertTrue(assignmentsMap.containsKey(i));
			assertEquals(assignmentsMap.get(i).getDuration(), durationList[i - 1]);
		}
	}

	// test addAssignment
	// Structured basis: id and duration are both greater than 0
	// Boundary: id and duration are 1 more than 0
	// Good Data: expected data
	// Data flow for assignmentsMap
	@Test
	public void test_addAssignment_both_positive() {
		assertEquals(testHook.addAssignment(new int[] { 1, 1 }), 1);
	}

	// Structured basis: id and duration are both less than 0
	// Boundary: id and duration are 1 less than 0
	// Bad data: illegal data
	// Data-flow:
	@Test(expected = IllegalArgumentException.class)
	public void test_addAssignment_both_negative() {
		testHook.addAssignment(new int[] { -1, -1 });
	}

	// Structured basis: id and duration are both less than 0
	// Boundary: id and duration are 0
	// Bad data: illegal data
	@Test(expected = IllegalArgumentException.class)
	public void test_addAssignment_both_zero() {
		testHook.addAssignment(new int[] { 0, 0 });
	}

	// Compound Boundary: negative big numbers
	// Bad data: illegal data
	@Test(expected = IllegalArgumentException.class)
	public void test_addAssignment_both_neg_big() {
		testHook.addAssignment(new int[] { -1000000000, -1000000000 });
	}

	// Compound Boundary: positive big numbers
	// Bad data: extreme data
	// Data flow for assignmentsMap
	@Test
	public void test_addAssignment_both_pos_big() {
		assertEquals(testHook.addAssignment(new int[] { 1000000000, 1000000000 }), 1000000000);
	}

	// Boundary: no assignment
	// Compound Boundary: no assignment
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void test_addAssignment_no_assignment() {
		testHook.addAssignment(new int[] {});
	}

	// test addRelations
	// Structured basis: a random list of input
	// Good data
	// Data flow for targetAssignment and sourceAssignment
	@Test
	public void test_addRelations_good_data() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		// check if map is empty
		assertFalse(assignmentsMap.isEmpty());
		// check the duration of the in edge of end node #1
		assertEquals(assignmentsMap.get(1).getEndNode().getInEdges().get(0).getDuration(), 3);
		// check the connected node id of the end node #2
		assertEquals(assignmentsMap.get(2).getEndNode().getOutEdges().get(0).getConnectedNode().getId(), 3);
		// check the duration of the out edge of end node #4
		assertEquals(assignmentsMap.get(4).getEndNode().getOutEdges().get(0).getDuration(), 0);
		// check if the in edge list is empty for start node #4
		assertTrue(assignmentsMap.get(4).getStartNode().getInEdges().isEmpty());
	}

	// Bad data: no data
	// Boundary: no data
	// Compound boundary
	@Test
	public void test_addRelations_no_assignment_relation() {
		int assignmentsList[][] = {};
		int relationsList[][] = {};
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		// check if map is empty
		assertTrue(assignmentsMap.isEmpty());
	}

	// Boundary: 1 relation
	// Compound boundary: 1 relation
	// Data flow for targetAssignment and sourceAssignment
	@Test
	public void test_addRelations_one_relation() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 } };
		int relationsList[][] = { { 2, 1, 2 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		HashMap<Integer, Assignment> assignmentsMap = schedule.getAssignmentsMap();
		// check if map is empty
		assertFalse(assignmentsMap.isEmpty());
		// check the connected node id of the start node #2
		assertEquals(assignmentsMap.get(2).getStartNode().getInEdges().get(0).getConnectedNode().getId(), 1);
	}

	// test edgeCreator
	// Structured basis
	// Good data: expected data
	// Data flow for targetNode and SourceNode
	@Test
	public void test_edgeCreator() {

		int assignmentsList[][] = { { 1, 3 }, { 2, 2 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		Assignment targetAssignment = testScheduler.getAssignmentsMap().get(2);
		Assignment sourceAssignment = testScheduler.getAssignmentsMap().get(1);
		int relationship = 2;

		testHook.edgesCreator(targetAssignment, sourceAssignment, relationship);

		// test the connected node of the start node of 2
		assertEquals(targetAssignment.getStartNode().getInEdges().get(0).getConnectedNode().getId(), 1);
		// test the connected node of the start node of 1
		// note: get(0).getConnectedNode() would be the end node of the sourceAssignment
		assertEquals(sourceAssignment.getStartNode().getOutEdges().get(1).getConnectedNode().getId(), 2);

	}

	// Bad data: relation that is not 1, 2, 3 or 4
	@Test(expected = NullPointerException.class)
	public void test_edgeCreator_bad_data() {

		int assignmentsList[][] = { { 1, 3 }, { 2, 2 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		Assignment targetAssignment = testScheduler.getAssignmentsMap().get(2);
		Assignment sourceAssignment = testScheduler.getAssignmentsMap().get(1);
		int relationship = 5;

		testHook.edgesCreator(targetAssignment, sourceAssignment, relationship);

	}

	// test getInEdgesCountMap
	// Structured basis: a random case
	// Good data: expected data
	// Boundary test for stack(include == and not == cases)
	// Data flow for stack and inEdgesCountMap
	@Test
	public void test_getInEdgesCountMap() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testScheduler.addRelations(relationsList);
		testHook.getInEdgesCountMap(testScheduler.getAssignmentsMap());
		// test the in edges number of the start node of #1
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(1).getStartNode()),
				Integer.valueOf(0));
		// test the in edges number of the start node of #2
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(2).getStartNode()),
				Integer.valueOf(2));
		// test the in edges number of the start node of #3
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(3).getStartNode()),
				Integer.valueOf(1));
		// test the in edges number of the start node of #4
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(4).getStartNode()),
				Integer.valueOf(0));

		// test the in edges number of the end node of #1
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(1).getEndNode()),
				Integer.valueOf(1));
		// test the in edges number of the end node of #2
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(2).getEndNode()),
				Integer.valueOf(1));
		// test the in edges number of the end node of #3
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(3).getEndNode()),
				Integer.valueOf(2));
		// test the in edges number of the end node of #4
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(4).getEndNode()),
				Integer.valueOf(2));

		// the nodes inside stack
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(4).getStartNode());
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(1).getStartNode());
	}

	// Bad data: no relation
	// Boundary: no relation
	// Boundary test for stack(include == and not == cases)
	// Data flow for stack and inEdgesCountMap
	@Test
	public void test_getInEdgesCountMap_no_relation() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = {};
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testScheduler.addRelations(relationsList);
		testHook.getInEdgesCountMap(testScheduler.getAssignmentsMap());

		// test the in edges number of the start node of #1
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(1).getStartNode()),
				Integer.valueOf(0));
		// test the in edges number of the start node of #2
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(2).getStartNode()),
				Integer.valueOf(0));
		// test the in edges number of the start node of #3
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(3).getStartNode()),
				Integer.valueOf(0));
		// test the in edges number of the start node of #4
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(4).getStartNode()),
				Integer.valueOf(0));

		// test the in edges number of the end node of #1
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(1).getEndNode()),
				Integer.valueOf(1));
		// test the in edges number of the end node of #2
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(2).getEndNode()),
				Integer.valueOf(1));
		// test the in edges number of the end node of #3
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(3).getEndNode()),
				Integer.valueOf(1));
		// test the in edges number of the end node of #4
		assertEquals(testHook.getInEdgesCountMap().get(testScheduler.getAssignmentsMap().get(4).getEndNode()),
				Integer.valueOf(1));

		// the nodes inside stack
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(4).getStartNode());
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(3).getStartNode());
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(2).getStartNode());
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(1).getStartNode());
	}

	// test sortOutNodes
	// Structured basis: a random normal case
	// Boundary: there is a node satisfies the inEdgesCountMap.get(outNode) == 0
	// statement and can be check by popping the stack once
	// Good data: expected data
	@Test
	public void test_sortOutNodes() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testScheduler.addRelations(relationsList);
		// sort the out nodes of the start node of #4
		testHook.sortOutNodes(testScheduler.getAssignmentsMap(),
				testScheduler.getAssignmentsMap().get(1).getStartNode());
		// the last added object should be the end node of assignment #1
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(1).getEndNode());
	}

	// Boundary: no out connected node -> no node pushed to the stack
	// Bad data: no out connected node
	@Test
	public void test_sortOutNodes_no_out() {
		int assignmentsList[][] = { { 1, 3 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testHook.sortOutNodes(testScheduler.getAssignmentsMap(), testScheduler.getAssignmentsMap().get(1).getEndNode());
		// pop the first object in the stack and it will be the start node of the
		// assignment
		assertEquals(testHook.getStack().pop(), testScheduler.getAssignmentsMap().get(1).getStartNode());
		// the stack will be empty now since there is no more out edge
		assertTrue(testHook.getStack().isEmpty());
	}

	// test topologicalSort
	// Structured basis: a random list of input
	// Boundary: sorted Node size == 2 * assignment
	// Data-flow: sortedNodeList and stack
	// Good data: expected data
	@Test
	public void test_topologicalSort() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		LinkedList<AssignmentNode> sortedNodeList = DevelopmentScheduler.topologicalSort(schedule.getAssignmentsMap());
		assertEquals(sortedNodeList.get(0).getId(), 4);
		assertTrue(sortedNodeList.get(0).isStart());

		assertEquals(sortedNodeList.get(5).getId(), 1);
		assertEquals(sortedNodeList.get(5).getTime(), 0);

		assertEquals(sortedNodeList.get(7).getId(), 3);
	}

	// Boundary: no assignment & boundary is empty all the time
	// Bad data: no assignment
	@Test
	public void test_topologicalSort_no_assignment() {
		int assignmentsList[][] = {};
		int relationsList[][] = {};
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		LinkedList<AssignmentNode> sortedNodeList = DevelopmentScheduler.topologicalSort(schedule.getAssignmentsMap());
		assertTrue(sortedNodeList.isEmpty());
	}

	// Structured basis: sorted Node size != 2 * assignment(a cycle)
	// Boundary: sorted Node size != 2 * assignment
	// Bad data: a cycle
	@Test(expected = IllegalStateException.class)
	public void test_topologicalSort_cycle() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 4 } };
		int relationsList[][] = { { 1, 2, 3 }, { 2, 1, 1 } };
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		DevelopmentScheduler.topologicalSort(schedule.getAssignmentsMap());

	}

	// Stress test: 100000 assignments and relations. Will definitely have a cycle.
	// Compound Boundary test: a great amount of assignments and relations
	@Test(expected = IllegalStateException.class)
	public void test_topologicalSort_stress() {
		int assignmentsList[][] = new int[100000][2];
		int relationsList[][] = new int[100000][3];
		int durationList[] = new int[100000];
		for (int i = 0; i < 100000; i++) {
			Random ran = new Random();
			int duration = ran.nextInt(5000) + 1;
			assignmentsList[i] = new int[] { i + 1, duration };
			durationList[i] = duration;
		}
		for (int i = 0; i < 100000; i++) {
			Random ran = new Random();
			int firstId = ran.nextInt(100000);
			int secondId = ran.nextInt(100000);
			while (firstId == secondId) {
				secondId = ran.nextInt(100000);
			}
			int relation = ran.nextInt(4) + 1;
			relationsList[i] = new int[] { assignmentsList[firstId][1], assignmentsList[secondId][1], relation };

		}
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		DevelopmentScheduler.topologicalSort(schedule.getAssignmentsMap());
	}

	// test getMaxDuration
	// Structured basis: a random case
	// Good data: expected data
	// Data-flow for maxDuration
	@Test
	public void test_getMaxDuration() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testScheduler.addRelations(relationsList);
		LinkedList<AssignmentNode> sortedNodeList = DevelopmentScheduler
				.topologicalSort(testScheduler.getAssignmentsMap());
		// check the max duration for each node in the order of the sorted node
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(0)), 0);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(1)), 0);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(2)), 4);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(3)), 0);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(4)), 2);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(5)), 3);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(6)), 0);
		assertEquals(testHook.getMaxDuration(sortedNodeList.get(7)), 4);

	}

	// Boundary: no data
	// Bad data: no data
	@Test(expected = NullPointerException.class)
	public void test_getMaxDuration_no_node() {
		testHook.getMaxDuration(null);
	}

	// Boundary: no relation
	@Test
	public void test_getMaxDuration_no_relation() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 } };
		int relationsList[][] = {};
		testScheduler.assignmentNodeInitializer(assignmentsList);
		testScheduler.addRelations(relationsList);
		assertEquals(testHook.getMaxDuration(testScheduler.getAssignmentsMap().get(1).getStartNode()), 0);
		assertEquals(testHook.getMaxDuration(testScheduler.getAssignmentsMap().get(1).getEndNode()), 3);
		assertEquals(testHook.getMaxDuration(testScheduler.getAssignmentsMap().get(2).getStartNode()), 0);
		assertEquals(testHook.getMaxDuration(testScheduler.getAssignmentsMap().get(2).getEndNode()), 2);
	}

	// test deliveryTimeCalculator
	// Structured basis: a random case
	// Good data: expected data
	@Test
	public void test_deliveryTimeCalculator() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 }, { 3, 4 }, { 4, 4 } };
		int relationsList[][] = { { 2, 1, 2 }, { 2, 4, 1 }, { 3, 2, 4 }, { 3, 1, 1 }, { 4, 1, 3 } };
		assertEquals(DevelopmentScheduler.deliveryTimeCalculator(assignmentsList, relationsList), Integer.valueOf(7));
	}

	// Boundary: no assignment and relation
	// Bad data: no assignment and relation
	// Compound boundary
	@Test
	public void test_deliveryTimeCalculator_no_assignment_relation() {
		int assignmentsList[][] = {};
		int relationsList[][] = {};
		assertEquals(DevelopmentScheduler.deliveryTimeCalculator(assignmentsList, relationsList), Integer.valueOf(0));
	}

	// Boundary: no relation
	// Bad data: no relation
	// Compound boundary
	// expect to be the maximum duration out the the 2 assignments
	@Test
	public void test_deliveryTimeCalculator_no_relation() {
		int assignmentsList[][] = { { 1, 3 }, { 2, 2 } };
		int relationsList[][] = {};
		assertEquals(DevelopmentScheduler.deliveryTimeCalculator(assignmentsList, relationsList), Integer.valueOf(3));
	}

	// Stress test: 100000 assignments and relations
	// cycle expected
	@Test(expected = IllegalStateException.class)
	public void test_deliveryTimeCalculator_stess() {
		int assignmentsList[][] = new int[100000][2];
		int relationsList[][] = new int[100000][3];
		int durationList[] = new int[100000];
		for (int i = 0; i < 100000; i++) {
			Random ran = new Random();
			int duration = ran.nextInt(5000) + 1;
			assignmentsList[i] = new int[] { i + 1, duration };
			durationList[i] = duration;
		}
		for (int i = 0; i < 100000; i++) {
			Random ran = new Random();
			int firstId = ran.nextInt(100000);
			int secondId = ran.nextInt(100000);
			while (firstId == secondId) {
				secondId = ran.nextInt(100000);
			}
			int relation = ran.nextInt(4) + 1;
			relationsList[i] = new int[] { assignmentsList[firstId][1], assignmentsList[secondId][1], relation };

		}
		DevelopmentScheduler schedule = new DevelopmentScheduler();
		schedule.assignmentNodeInitializer(assignmentsList);
		schedule.addRelations(relationsList);
		DevelopmentScheduler.deliveryTimeCalculator(assignmentsList, relationsList);
	}
}
