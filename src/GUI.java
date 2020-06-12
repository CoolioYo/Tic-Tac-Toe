import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI extends JFrame {
    private Container pane;
    private JButton[][] board;
    private JMenu menu;
    private JMenuBar menuBar;
    private JMenuItem quit;
    private JMenuItem newGame;

    private String currentPlayer;
    private boolean hasWinner;

    private String[][] boardAI = {
            {"", "", ""},
            {"", "", ""},
            {"", "", ""}
    };


    ArrayList<Integer> available;

    public GUI() {
        super();

        pane = getContentPane();
        pane.setLayout(new GridLayout(3, 3));
        setTitle("Tic Tac Toe");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        board = new JButton[3][3];
        hasWinner = false;

        initializeBoard();
        initializeMenuBar();
        randomPlayer();

    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        menu = new JMenu("Options");

        newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("<<NEW GAME>>");
                resetBoard();
            }
        });

        quit = new JMenuItem("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(newGame);
        menu.add(quit);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText("");
                boardAI[i][j] = ("");
            }
        }

        available = findAvailable();
        randomPlayer();
        hasWinner = false;
    }

    private void initializeBoard() {
        available = findAvailable();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton();
                button.setFont(new Font(Font.DIALOG, Font.PLAIN, 100));

                board[i][j] = button;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (((JButton) e.getSource()).getText().equals("") && hasWinner == false) {
                            button.setText("X");
                            for(int i = 0; i < 3; i++){
                                for(int j = 0; j < 3; j++){
                                    if(board[i][j] == e.getSource()){
                                        playerMove(i, j);
/*                                        System.out.println("i: "+i);
                                        System.out.println("j: "+i);*/
                                    }
                                }
                            }
                            available = findAvailable();
                            togglePlayer();
                        }
                    }
                });
                pane.add(button);
            }
        }
    }

    private void randomPlayer() {
        int randomPlayer = (int) (Math.random() * 2);
        currentPlayer = "X";

        if (randomPlayer != 1) {
            togglePlayer();
        }else{
            System.out.println("Current Player: "+currentPlayer);
        }
    }

    private void togglePlayer() {
        System.out.println("Current Player: "+currentPlayer);

        String result = checkWin();
        System.out.println("result: "+result);
        if(!result.equals("none")){
            hasWinner = true;
            if(!result.equals("tie")){
                System.out.println("<<"+result+" wins>>");
                JOptionPane.showMessageDialog(null, result+" wins!");
            }else{
                System.out.println("<<Tie>>");
                JOptionPane.showMessageDialog(null, "It was a tie!");
            }
        }else{
            if (currentPlayer.equals("X")) {
                currentPlayer = "O";
                enemyTurn();
            } else {
                currentPlayer = "X";
            }

            printBoard();
        }
    }

    private void printBoard(){
        System.out.println("BOARD:");
        for(int i = 0; i < boardAI.length; i++){
            for(int j = 0; j < boardAI.length; j++){
                System.out.print(boardAI[i][j]);
            }
            System.out.println("");
        }
    }

    private ArrayList<Integer> findAvailable() {
        ArrayList<Integer> available = new ArrayList<Integer>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardAI[i][j].equals("")) {
                    available.add(i * 3 + j + 1);
                }
            }
        }

        return available;
    }

    private void playerMove(int row, int col){
        boardAI[row][col] = "X";
    }

    private void testMove(String player, int index){
        int row = (index-1)/3;
        int col = Math.max(0,(index-1))%3;

        boardAI[row][col] = player;
    }

    private void enemyTurn() {
        int index = 0;
        int bestMove = 0;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        available = findAvailable();
        ArrayList<Integer> a = available;

        for (int i = 0; i < available.size(); i++) {
            index = available.get(i);
            testMove("O", index);
            available = findAvailable();
            int score = minimax(0, alpha, beta, false);

            testMove("", index);
            available = a;

            if (score > bestScore) {
                bestScore = score;
                bestMove = index;
            }
        }

        testMove("O", bestMove);
        int row = (bestMove-1)/3;
        int col = Math.max(0,(bestMove-1))%3;
        board[row][col].setText("O");

/*        System.out.println("bestMove: "+bestMove);
        System.out.println("row: "+row);
        System.out.println("col: "+col);*/

        togglePlayer();
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        int index;
        int bestScore;
        ArrayList<Integer> a = available;

        String result = checkWin();
        if (result.equals("X")) {
            return -1;
        } else if (result.equals("O")) {
            return 1;
        } else if (result.equals("tie")) {
            return 0;
        }

        if (isMaximizing) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < available.size(); i++) {
                index = available.get(i);
                testMove("O", index);
                available = findAvailable();
                int score = minimax(depth+1, alpha, beta, false);
                alpha = Integer.max(alpha, score);

                testMove("", index);
                available = a;

                bestScore = Integer.max(score, bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
        }else{
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < available.size(); i++) {
                index = available.get(i);
                testMove("X", index);
                available = findAvailable();
                int score = minimax(depth+1, alpha, beta, true);
                beta = Integer.min(beta, score);

                testMove("", index);
                available = a;

                bestScore = Integer.min(score, bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestScore;
    }

    private String checkWin() {
        //check rows
        for (int row = 0; row < 3; row++) {
            if (check3(boardAI[row][0], boardAI[row][1], boardAI[row][2])) {
                return boardAI[row][0];
            }
        }

        //check columns
        for (int col = 0; col < 3; col++) {
            if (check3(boardAI[0][col], boardAI[1][col], boardAI[2][col])) {
                return boardAI[0][col];
            }
        }

        //check diagonals
        if (check3(boardAI[0][0], boardAI[1][1], boardAI[2][2])) {
            return boardAI[0][0];
        }

        if (check3(boardAI[2][0], boardAI[1][1], boardAI[0][2])) {
            return boardAI[2][0];
        }

        if (available.size() == 0) {
            return "tie";
        }

        return "none";
    }

    private boolean check3(String a, String b, String c) {
        return (!a.equals("") && a.equals(b) && b.equals(c));
    }
}