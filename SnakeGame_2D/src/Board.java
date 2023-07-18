import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board  extends JPanel implements ActionListener {

//1.) initialize sizes
    int B_HEIGHT = 400;
    int B_WIDTH = 400;
    int DOT_SIZE = 10;// here 10
    int MAX_DOTS = 1600;//(int) (int) (int) Math.pow(((double) B_HEIGHT /DOT_SIZE),2); // here 1600

    int[] x = new int[MAX_DOTS];
    int[] y = new int[MAX_DOTS];

    int DOTS;
    // coordinates of apple
    int apple_x;
    int apple_y;
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// 2.) Images
    Image body;
    Image head;
    Image apple;    // images will be loaded in after position initialization of game in initiateGame method
    Timer timer;
    int DELAY = 150;    //300 mili secs i.e 0.3secs

    // arrow key action mapped with boolean variable
    boolean leftDirection = true;
    boolean rightDirection = false;
    boolean upDirection = false;
    boolean downDirection = false;
    boolean inGame = true;
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // constructor for board JPanel
    Board(){
        //  control using keys
        TAdapter tAdapter = new TAdapter();     // extended custom class of keyAdapter class
        addKeyListener(tAdapter);
        setFocusable(true);


        setPreferredSize(new Dimension(B_WIDTH,B_HEIGHT));
        setBackground(Color.BLACK);
        loadImages();
        initiateGame();

    }

    //2.) load images from resources folder to image object
    public void loadImages(){       // ImageIcon is a mathod of java swing
        // for body
        ImageIcon bodyIcon = new ImageIcon("src/resources/dot.png");
        body = bodyIcon.getImage();
        // for head
        ImageIcon headIcon = new ImageIcon("src/resources/head.png");
        head = headIcon.getImage();
        // for apple
        ImageIcon appleIcon = new ImageIcon("src/resources/apple.png");
        apple = appleIcon.getImage();
    }


    //3.) method to initialize co ordinates of snake and apple on board
    public void initiateGame(){
        DOTS = 3;// initially snake size will be three dots
        // initialize snakes start position/co ordinates
        x[0] = 250;// set first element ourselves
        y[0] = 250;
        for(int i=1; i<DOTS; i++){
            x[i] = x[0] + DOT_SIZE*i;
            y[i] = y[0];

        }

        // initialize co ordinates of apple(randomizer)
        locateApple();
        //  apple_x = 150;
        //  apple_y = 150;
        timer = new Timer(DELAY, this); //this - ActionListner; //
        timer.start();

    }

    //4.) Draw images at their current particular  positions using PaintComponent() of JPanel class
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);

    }

    //5.)   Method for draw image
    public void doDrawing( Graphics g ){    //here observer is the Board class
        if(inGame){
            g.drawImage(apple, apple_x, apple_y, this);
            for (int i = 0; i < DOTS; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], this);

                } else {
                    g.drawImage(body, x[i], y[i], this);
                }
            }

        }
        else{
            gameOver(g);
            timer.stop();
        }
    }

    //5.) Randomize the apple position
    //the indexexs of apple are in multiple of 10s i.e. Dot size, where the last possible index will be 390 i.e.,
    // 0 - b_width - 390; // 0 - b_height - 390
    public void locateApple(){
        apple_x = ((int)(Math.random()*39))*DOT_SIZE;   // Math.random provides random num from 0-1, in this case from 0 to 39.
        apple_y = ((int)(Math.random()*39))*DOT_SIZE;
    }

// 7.) Action performed on each input
    @Override
    public void actionPerformed(ActionEvent actEvent) {
        if(inGame){
            move();
            eatAppleCheck();
            checkCollison();

        }
        repaint();

    }

    // 8.) Movement of snake
    public void move(){
        // dots except first will follow the previous dots positions
        // the loops runs from the last dot towards the second
        for( int i=DOTS-1; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];

        }

        if(leftDirection){
            x[0] -= DOT_SIZE;
        }
        if(rightDirection){
            x[0] += DOT_SIZE;
        }
        if(upDirection){
            y[0] -= DOT_SIZE;
        }
        if(downDirection){
            y[0] += DOT_SIZE;
        }
    }

    // 7.)Make snake eat apple
    public void eatAppleCheck(){     // checkApple overlaps snake
        if( (apple_x == x[0]) && (apple_y == y[0]) ){
            DOTS++;
            locateApple(); // change apple position again
        }
    }

    // 8.) Check Collisons with body and border
    public void checkCollison(){
    // collison of body check;
        for( int i=1; i<DOTS; i++){
            if( i>4 && x[0]==x[i] && y[0]==y[i] ){
                inGame = false;
                break;
            }
        }
    // collison of border
        if(x[0] < 0){
            inGame = false;
        }
        if(x[0] > B_WIDTH){
            inGame = false;
        }
        if(y[0] < 0){
            inGame = false;
        }
        if(y[0] > B_HEIGHT){
            inGame = false;
        }

    }

//  9.) Display game over and score;
    public void gameOver(Graphics g){
        String msg = "Game Over";
        int score = (DOTS-3)*100;
        String scoreMsg = "Score : " + Integer.toString(score);
//  making custom font
        Font small = new Font("Helvetica", Font.BOLD, 14);
//  get the x cordinate to center the message
        FontMetrics fontMetrics = getFontMetrics(small);    // gets height and width of the array

        g.setColor(Color.RED);
        g.setFont(small);
        g.drawString(msg, ( B_WIDTH - fontMetrics.stringWidth(msg))/2, B_HEIGHT/4);
        g.drawString(scoreMsg, ( B_WIDTH - fontMetrics.stringWidth(scoreMsg))/2, 3*(B_HEIGHT/4));
    }

    //  Implement key controls

    private class TAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent keyEvent){
            int key = keyEvent.getKeyCode();

            if( key==KeyEvent.VK_LEFT && !rightDirection ){     // for left
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if( key==KeyEvent.VK_RIGHT && !leftDirection ){     // for right
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if( key==KeyEvent.VK_UP && !downDirection ){     // for up
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if( key==KeyEvent.VK_DOWN && !upDirection ){     // for down
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

        }
    }
}
