import java.util.ArrayList;
import java.util.List;

public class Board {

    private final int[][] tiles;
    private final int n;

    //the constructor
    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = new int[n][n];
        // copy the array over
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n"); //The first line contains the board size n

        // the remaining n lines contains the n-by-n grid of tiles in row-major order
        // using 0 to designate the blank square.
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(tiles[i][j]);
                s.append(" ");
            }
            s.append("\n"); // new row
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    //number of tiles out of place
    public int hamming() {
        int hammer = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // i * n + j + 1 => checks if the appropriate number is in it's right position
                if (tiles[i][j] != 0 && tiles[i][j] != i * n + j + 1) {
                    hammer++;
                }
            }
        }
        return hammer;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int ny = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0 && tiles[i][j] != i * n + j + 1) {
                    int actual = tiles[i][j]; // the value at that the tile
                    // manhattan distance formula => |x1 - x2| + |y1 - y2|
                    // (actual - 1) / n gives the goal row & (actual - 1) % n gives the goal col
                    // actual - goal
                    ny += Math.abs(i - ((actual - 1) / n)) + Math.abs(j - ((actual - 1) % n));
                }
            }
        }
        return ny;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0; // if hamming distance is 0 then all the tiles are in the right place
    }
//
    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board other = (Board) y;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tiles[i][j] != other.tiles[i][j]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> buddies = new ArrayList<>();

        int emptyTileRow = -1;
        int emptyTileCol = -1;

        // first we need to find that blank tile
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    emptyTileRow = i;
                    emptyTileCol = j;
                }
            }
        }
        // have all the possiible directions (up, down, left, right)
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : directions) {
            // for each direction,
            // calculate the new row and col for the empty tile
            int updatedEmptyTileRow = emptyTileRow + d[0];
            int updatedEmptyTileCol = emptyTileCol + d[1];

            // and then check if it's in the grid bounds
            if (updatedEmptyTileRow >= 0 && updatedEmptyTileRow < n && updatedEmptyTileCol >= 0 && updatedEmptyTileCol < n) {
                // if so, swap the empty tile and add that new board config to the bag
                int[][] dupe = copyBoard();
                dupe[emptyTileRow][emptyTileCol] = dupe[updatedEmptyTileRow][updatedEmptyTileCol];
                dupe[updatedEmptyTileRow][updatedEmptyTileCol] = 0;
                buddies.add(new Board(dupe));
            }
        }
        return buddies;
    }

    // a board that is obtained by exchanging any pair of tiles
    // used to check if the board is solvable
    public Board twin() {
        int[][] twin = copyBoard();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                // make either one of the two tiles next to eachother are not empty
                if (twin[i][j] != 0 && twin[i][j + 1] != 0) {
                    // swap
                    int temp = twin[i][j];
                    twin[i][j] = twin[i][j + 1];
                    twin[i][j + 1] = temp;
                    return new Board(twin);
                }
            }
        }
        //return error if cant create one
        throw new RuntimeException("couldn't create a twin board");
    }

    private int[][] copyBoard() {
        int[][] b = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                b[i][j] = tiles[i][j];
            }
        }
        return b;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] tiles = {
                { 0, 1, 3 },
                { 4, 2, 5 },
                { 7, 8, 6 }
        };

        Board board = new Board(tiles);
        System.out.println(board);
        System.out.println("Hamming Distance: " + String.valueOf(board.hamming()));
        System.out.println("Manhattan Distance: " + String.valueOf(board.manhattan()));
        System.out.println("Is it the goal: " + String.valueOf(board.isGoal()));
        System.out.println("Twin board:");
        System.out.println(board.twin());
        System.out.println("Neighbors:");
        for (Board neighbor : board.neighbors()) {
            System.out.println("Neighbor Manhattan Distance: " + String.valueOf(neighbor.manhattan()));
            System.out.println(neighbor);
        }
    }

}