/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player {
    
    //получим виртуальные координаты середины виртуальной доски
    private int x = (int) Math.round(Game.FIELD_SIZE / 2);
    private int y = (int) Math.round(Game.FIELD_SIZE / 2);
    private int speed  = 1;
    public int length  = 1;
    
    enum Direction {UP, DOWN, LEFT, RIGHT, NONE};
    private Direction playerDirection = Direction.NONE;
    
    
    ArrayList<Body> snakeBody = new ArrayList<>();

    //создали класс Body
    public class Body{
        public int x,y;
    }

    //при создании игрока, поместим координаты головы змеи в snakeBody
    public Player(int x, int y) {

        this.x = x;
        this.y = y;

        Body segment = new Body();

        segment.x = x;
        segment.y = y;

        snakeBody.add(segment);   
    }
    

    public void move() {
        switch(playerDirection) {
        case UP:
            y-=speed;
            break;
        case DOWN:
            y+=speed;
            break;
        case LEFT:
            x-=speed;
            break;
        case RIGHT:
            x+=speed;
            break;
        default:
            break;
        }
    }

    //при каждом движении головы - пересчитвыаем координты всех сегментов змеи
    public void recountCoords(int x, int y) {
        //добавим новую координату головы змеи            
        Body segment = new Body();
        segment.x = x;
        segment.y = y;       
        snakeBody.add(0, segment);
    }
    
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_UP) {
                playerDirection = Direction.UP;
        }
        if(key == KeyEvent.VK_DOWN) {
                playerDirection = Direction.DOWN;
        }
        if(key == KeyEvent.VK_LEFT) {
                playerDirection = Direction.LEFT;
        }
        if(key == KeyEvent.VK_RIGHT) {
                playerDirection = Direction.RIGHT;
        }
    }
    
    public void addSegment(int x, int y) {
        Body segment = new Body();
        segment.x = x;
        segment.y = y;
     
        if (snakeBody.size() == 1) {
            snakeBody.add(segment);
        } else {
            snakeBody.add(1, segment);
        }
        length+=1;
    }
    
    public void delLastSegment() {
        snakeBody.remove(snakeBody.size()-1);
        length-=1;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHeadX() {
        return x;
    }

    public int getHeadY() {
        return y;
    }

    public int getLastX() {
        return snakeBody.get(length).x;
    }

    public int getLastY() {
        return snakeBody.get(length).y;
    }
}