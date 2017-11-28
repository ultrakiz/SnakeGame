/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Food {
    
    //получим виртуальные координаты середины виртуальной доски
    private int x = (int) (Math.random() * Game.FIELD_SIZE);
    private int y = (int) (Math.random() * Game.FIELD_SIZE);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
