import edu.princeton.cs.algs4.MinPQ;
import java.util.Scanner;
import java.util.Stack;

public class Solver {
    private Node solutionNode;

    // create a node class, aka a state in the puzzle
    private class Node implements Comparable<Node> {

        // it'll have the current board config
        // what number move it's at
        // the previous state
        // the boards priority
        private final Board board;
        private final int moves;
        private final Node previous;
        private final int priority;

        public Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            this.priority = board.manhattan() + moves;
        }

        // need something to compare with
        @Override
        public int compareTo(Node other) {
            return this.priority - other.priority;
        }
    }

    // Find a solution for the initial board
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board can't be null");

        // two priority queues, one for initial board and other from twin
        MinPQ<Node> pq = new MinPQ<>();
        pq.insert(new Node(initial, 0, null));

        // the purpose of a twin board it to check whether the board is solvable
        // for example, if the initial board finds a solution before the twin board then it's solvable, but if the...
        // ...twin board also finds a solution, then it's unsolvable
        // puzzle theory...
        MinPQ<Node> twinPq = new MinPQ<>();
        twinPq.insert(new Node(initial.twin(), 0, null));

        // we're repeatably running one iteration of a-star on both the initial board and the twin board
        // seeing which ones find a solution and setting solutionNode accordingly
        while (true) {

            Node result = solve(pq);
            if (result != null) {
                solutionNode = result;
                break;
            }
            if (solve(twinPq) != null) {
                // puzzle theory
                solutionNode = null;
                break;
            }
        }
    }

    // a star algo
    private Node solve(MinPQ<Node> pq) {
        if (pq.isEmpty()) return null;
        // grab the board with the smallest priority (which is manhattan + moves)
        Node node = pq.delMin();

        //check if it's done and then return it
        if (node.board.isGoal()) return node;

        // otherwise, find its neighbors, makes sure none of them are the previous ones, then adds them to the queue
        for (Board neighbor : node.board.neighbors()) {
            if (node.previous == null || !neighbor.equals(node.previous.board)) {
                pq.insert(new Node(neighbor, node.moves + 1, node));
            }
        }
        return null;
    }

    // Is the initial board even solvable?
    public boolean isSolvable() {
        return solutionNode != null;
    }

    // minimum number of moves to solve initial board, -1 if unsolvable
    public int moves() {
        return isSolvable() ? solutionNode.moves : -1;
    }

    // create a sequence of boards that lead to the solution, otherwise return null
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> solution = new Stack<>();

        // starting from the solution board, keep adding the previous ones to from a sequence of them
        for (Node node = solutionNode; node != null; node = node.previous) {
            solution.push(node.board);
        }

        // I have to reverse them
        Stack<Board> correctOrderSolution = new Stack<>();
        while (!solution.isEmpty()) {
            correctOrderSolution.push(solution.pop());
        }
        return correctOrderSolution;
    }

    // just for testing. check PuzzleSolver for official
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Read board size
        int n = sc.nextInt();
        int[][] tiles = new int[n][n];

        // Read the tiles
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = sc.nextInt();
            }
        }
        Board initial = new Board(tiles);

        // solve it solve it solve it solve it!!!!
        Solver solver = new Solver(initial);

        // print it
        if (!solver.isSolvable()) {
            System.out.println("No solution possible");
        } else {
            System.out.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                System.out.println(board);
            }
        }

    }
}
