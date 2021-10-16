import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class SnakeGame extends JFrame implements ActionListener {

    Timer timer = new Timer();

    JButton startButton = new JButton();
    JButton setupButton = new JButton();
    JButton quitButton = new JButton();

    JButton mainMenu = new JButton();

    String[] rowList = {"7", "9", "11", "13", "15"};
    String[] columnList = {"7", "9", "11", "13", "15"};
    String[] difficultyList = {"Easy", "Medium", "Hard", "Extreme"};

    JComboBox setRows = new JComboBox(rowList);
    JComboBox setColumns = new JComboBox(columnList);
    JComboBox setDifficulty = new JComboBox(difficultyList);

    JLabel gameBoard = new JLabel();
    JLabel scoreLabel = new JLabel();
    JLabel gameOverLabel = new JLabel();

    StringBuilder boardToString = new StringBuilder();

    JFrame snakeFrame = new JFrame();

    public static int rows = 7;
    public static int columns = 7;
    String[][] board;
    int goalPosX;
    int goalPosY;
    int score = 0;
    int length = 1;
    boolean addSegment = false;
    Random random = new Random();

    boolean gameOver;
    int currentDirection = 0;
    String inputDifficulty;
    int difficulty = 2000;

    static int maxSize = 225; //max rows * max columns

    SnakeSegment headSegment;
    SnakeSegment[] snake = new SnakeSegment[maxSize];

    public static void main(String[] args) {

        SnakeGame game = new SnakeGame();

        game.firstLaunch();

    }

    void firstLaunch() {

        startButton.setText("Start");
        setupButton.setText("Setup");
        quitButton.setText("Quit");

        startButton.addActionListener(this);
        setupButton.addActionListener(this);
        quitButton.addActionListener(this);

        startButton.setBounds(200, 100, 200, 100);
        setupButton.setBounds(200, 250, 200, 100);
        quitButton.setBounds(200, 400, 200, 100);

        snakeFrame.setSize(600, 600);
        snakeFrame.setLayout(null);
        snakeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        snakeFrame.setBackground(Color.lightGray);

        startButton.setVisible(true);
        setupButton.setVisible(true);
        quitButton.setVisible(true);

        startButton.setFocusPainted(false);
        setupButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);

        snakeFrame.add(startButton);
        snakeFrame.add(setupButton);
        snakeFrame.add(quitButton);

        snakeFrame.setVisible(true);
    }

    void subsequentMainMenu() {

        snakeFrame.getContentPane().removeAll();
        snakeFrame.getContentPane().revalidate();
        snakeFrame.getContentPane().repaint();

        snakeFrame.add(startButton);
        snakeFrame.add(setupButton);
        snakeFrame.add(quitButton);
    }

    void frameSetup() {

        snakeFrame.getContentPane().removeAll();
        snakeFrame.getContentPane().revalidate();
        snakeFrame.getContentPane().repaint();

        JLabel rowsLabel = new JLabel();
        JLabel columnsLabel = new JLabel();
        JLabel difficultyLabel = new JLabel();

        rowsLabel.setText("<html>" + "Select the number of rows on your board:" + "</html>");
        columnsLabel.setText("<html>" + "Select the number of columns on your board:" + "</html>");
        difficultyLabel.setText("<html>" + "Select your difficulty:" + "</html>");
        mainMenu.setText("Return to Main Menu");

        rowsLabel.setBounds(100, 50, 150, 100);
        columnsLabel.setBounds(100, 200, 150, 100);
        difficultyLabel.setBounds(100, 350, 150, 100); //TODO: Fix cutoff

        setRows.setBounds(350, 75, 150, 50);
        setColumns.setBounds(350, 225, 150, 50);
        setDifficulty.setBounds(350, 375, 150, 50);

        mainMenu.setBounds(225, 475, 150, 50);
        mainMenu.addActionListener(this);
        mainMenu.setFocusPainted(false);

        rowsLabel.setVisible(true);
        columnsLabel.setVisible(true);
        difficultyLabel.setVisible(true);
        setRows.setVisible(true);
        setColumns.setVisible(true);
        setDifficulty.setVisible(true);
        mainMenu.setVisible(true);

        snakeFrame.add(rowsLabel);
        snakeFrame.add(columnsLabel);
        snakeFrame.add(difficultyLabel);
        snakeFrame.add(setRows);
        snakeFrame.add(setColumns);
        snakeFrame.add(setDifficulty);
        snakeFrame.add(mainMenu);

    }

    void frameBoard() {

        snakeFrame.getContentPane().removeAll();
        snakeFrame.getContentPane().revalidate();
        snakeFrame.getContentPane().repaint();

        gameBoard.setFont(new Font("Consolas", Font.PLAIN, 20));
        gameBoard.setHorizontalAlignment(SwingConstants.CENTER);
        gameBoard.setVerticalAlignment(SwingConstants.CENTER);
        gameBoard.setBounds(100, 100, 400, 400);

        scoreLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        scoreLabel.setText("Current score: " + score);
        scoreLabel.setBounds(50, 50, 150, 50);

        gameOverLabel.setFont(new Font("Consolas", Font.PLAIN, 20));
        gameOverLabel.setText("Game over!");
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setBounds(200, 50, 200, 50);

        gameOverLabel.setVisible(false);
        gameBoard.setVisible(true);
        scoreLabel.setVisible(true);
        snakeFrame.add(gameBoard);
        snakeFrame.add(scoreLabel);
        snakeFrame.add(gameOverLabel);

        snakeFrame.validate();
        snakeFrame.setFocusable(true);
        snakeFrame.requestFocus();
        snakeFrame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case 87 : currentDirection = 1; break;
                    case 65 : currentDirection = 2; break;
                    case 83 : currentDirection = 3; break;
                    case 68 : currentDirection = 4; break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

    }

    void start() {

        boardSetup();
        frameBoard();
        gameOver = false;
        timer.scheduleAtFixedRate(iterateBoard, 0, difficulty);

    }

    void boardSetup() {
        board = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(board[i], "O");
        }
        headSegment = new SnakeSegment(columns / 2, rows / 2);
        board[headSegment.y][headSegment.x] = "X";
        snake[0] = headSegment;
        generateGoal();
        board[goalPosY][goalPosX] = "$";
    }

    void resetBoard() {
        for (int i = 0; i < rows; i++) {
            Arrays.fill(board[i], "O");
        }

        for (int i = 0; i < length; i++) {
            board[snake[i].y][snake[i].x] = "x";
        }

        board[headSegment.y][headSegment.x] = "X";

        if(addSegment) {
            length++;
            generateGoal();
            addSegment = false;
        }
        board[goalPosY][goalPosX] = "$";
    }

    void printBoard() {
        boardToString.setLength(0);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                boardToString.append(board[r][c] + " ");
            }
            boardToString.append("<br/>");
        }
    }

    void selectDifficulty() {
        if (inputDifficulty.equals("Easy")) {
            difficulty = 2000;
        } else if (inputDifficulty.equals("Medium")) {
            difficulty = 1000;
        } else if (inputDifficulty.equals("Hard")) {
            difficulty = 500;
        } else if (inputDifficulty.equals("Extreme")) {
            difficulty = 250;
        }
        subsequentMainMenu();
    }

    void generateGoal() {

        //This is horrible optimized especially when the snake gets big, but it works for now.
        System.out.println("Generated goal");
        goalPosY = random.nextInt(0, rows - 1);
        goalPosX = random.nextInt(0, columns - 1);

        if(board[goalPosY][goalPosX] == "X") {
            generateGoal();
        }
    }

    TimerTask iterateBoard = new TimerTask() {
        @Override
        public void run() {
            System.out.println("New iteration");
            moveSnake();
            moveHead();
            score();
            resetBoard();
            printBoard();
            gameBoard.setText("<html>" + boardToString.toString() + "</html>");
            scoreLabel.setText("Current score: " + score);
        }
    };

    void moveHead() {
        if ((currentDirection == 1 && headSegment.y == 0) || (currentDirection == 2 && headSegment.x == 0) || (currentDirection == 3 && headSegment.y == rows) || (currentDirection == 4 && headSegment.x == columns)) {
            // above line checks to see if the snake runs into a wall
            iterateBoard.cancel();
            gameOver();
        } else if ((currentDirection == 1 && board[headSegment.y - 1][headSegment.x] == "x") || (currentDirection == 2 && board[headSegment.y][headSegment.x - 1] == "x") || (currentDirection == 3 && board[headSegment.y + 1][headSegment.x] == "x") || (currentDirection == 4 && board[headSegment.y][headSegment.x + 1] == "x")) {
            // above line checks to see if the snake runs into itself
            iterateBoard.cancel();
            gameOver();
        } else if (currentDirection == 1) {
            // up
            headSegment.y = headSegment.y - 1;
        } else if (currentDirection == 2) {
            // left
            headSegment.x = headSegment.x - 1;
        } else if (currentDirection == 3) {
            // down
            headSegment.y = headSegment.y + 1;
        } else if (currentDirection == 4) {
            // right
            headSegment.x = headSegment.x + 1;
        } else if (currentDirection == 0) {
            // stationary
        }
    }

    void score() {
        if(headSegment.y == goalPosY && headSegment.x == goalPosX) {
            score++;
            addSegment = true;
        }
    }

    void moveSnake() {
        for (int i = length - 1; i > 0; i--) {
            SnakeSegment nextSegment = new SnakeSegment(snake[i - 1].y, snake[i - 1].x);
            snake[i] = nextSegment;
        }
        snake[0] = headSegment;
    }

    void gameOver() {
        gameOverLabel.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            start();
        }
        if (e.getSource() == setupButton) {
            frameSetup();
        }
        if (e.getSource() == quitButton) {
            System.exit(0);
        }
        if (e.getSource() == mainMenu) {
            rows = Integer.parseInt((String) setRows.getSelectedItem());
            columns = Integer.parseInt((String) setColumns.getSelectedItem());
            inputDifficulty = (String) setDifficulty.getSelectedItem();
            selectDifficulty();
        }
    }
}

