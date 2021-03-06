package com.tiggerbiggo.prima.core;
import com.tiggerbiggo.prima.processing.fragment.Fragment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Stack;

/**
 * The core class for building and rendering Fragment based images.
 */
public class Builder implements Runnable
{
    public static final int THREADNUM = 6;

    private Thread[] threads;
    private boolean setup = false;
    private boolean isDone = false;
    private Stack<Vector2> fragList;
    private Fragment<Color[]>[][] fragMap;
    private BufferedImage[] imgs = null;
    private int w, h, n;
    private int current, max;

    /**Initialises the Builder object
     *
     * @param fragMap A 2D array of Color[] type fragments, to make up a 2D image with many frames
     * @param shuffle Whether or not to shuffle the render order
     */
    public Builder(Fragment<Color[]>[][] fragMap, boolean shuffle)
    {
        //Check for dangerous null values
        try {
            if (fragMap == null || fragMap.length <= 0 || fragMap[0].length <= 0) {
                throw new IllegalArgumentException("Invalid FragMap");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException("FragMap is either null or otherwise invalid");
        }

        //Create a stack to store the fragment objects in for easy retrieval by the threads.
        fragList = new Stack<>();

        this.fragMap = fragMap;

        //Store variables for width, height and length
        w = fragMap.length;
        h = fragMap[0].length;
        n = fragMap[0][0].get().length;

        //Iterate over the ranges 0 > w and 0 > h to add an entry for every fragment in the map.
        //This is needed so each thread knows which pixel to access when writing to the image.
        for(int i=0; i<w; i++)
            for (int j=0; j<h; j++)
                fragList.add(new Vector2(i, j));

        max = fragList.size();
        current = 0;

        //If desired, shuffles the list to evenly distribute rendering across entire image.
        //Useful for real time rendering so the user can see the image progressively render evenly.
        if(shuffle) Collections.shuffle(fragList);

        //Creates a new array of images and populates it.
        imgs = new BufferedImage[n];
        for(int i=0; i<n; i++)
        {
            imgs[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }
    }

    /**
     * Optional callback method, can be overridden to call back after each rendered pixel completed
     * @param x the current x position in the array
     * @param y the current y position in the array
     */
    public void callback(int x, int y){}

    /**
     * Creates an array of threads and starts them running, effectively starting the render process.
     */
    public void startBuild()
    {
        threads = new Thread[THREADNUM];
        for(int i=0; i<THREADNUM; i++)
        {
            threads[i] = new Thread(this);
        }
        setup = true;
        for(Thread t : threads)
        {
            t.start();
        }
    }

    /**
     * The main run method. This should not be called from outside this class.
     * Keeps rendering each pixel until completed.
     */
    @Override
    public void run() {
        //Check if setup has been done
        if(setup)
        {
            //Get coordinate from the stack to populate variable before loop
            Vector2 pos = getNext();

            //Repeat until no more elements are available
            while(pos != null) {
                current++;

                int x, y;
                x=pos.iX();
                y=pos.iY();

                //Calculates the array of colours from the next fragment
                Color[] colors = fragMap[x][y].get();

                //if invalid number of colours returned, break
                if(colors.length != n) {
                    break;
                }
                else
                {
                    //Else render the colours to the image array
                    //iterates over each image and sets the colour of the pixel at (x,y)
                    for(int i=0; i<n; i++)
                    {
                        imgs[i].setRGB(x, y, colors[i].getRGB());
                    }
                }
                //get next position vector
                pos = getNext();

                //Optional callback
                callback(x, y);
            }
        }
    }

    /**
     * Synchronized pop from the list of positions
     * @return Next element if exists, if stack empty returns null
     */
    private synchronized Vector2 getNext()
    {
        if(fragList.isEmpty())
            return null;
        return fragList.pop();
    }

    /**
     *
     * @return The current value of the internal counter.
     */
    public int getCurrent()
    {
        return current;
    }

    /**Joins all currently working threads in this object to the thread that called this method.
     * This effectively results in the thread waiting until the build operation has completed.
     */
    public void joinAll()
    {
        if(setup)
        {
            for(Thread t : threads)
            {
                try {
                    t.join();
                }
                catch(InterruptedException e) { }
            }
        }
    }

    /**
     * @return Boolean value for whether or not the calculation has finished.
     */
    public boolean isDone()
    {
        if (!isDone) {
            if (!setup) {
                return false;
            }
            for (int i = 0; i < THREADNUM; i++) {
                try {
                    if (threads[i].isAlive())
                        return false;
                } catch (Exception e) {
                    return false;
                }
            }
            isDone = true;
            return true;
        }
        else return true;
    }

    /**
     * @return The image array. Can be used even if calculation unfinished, resulting in viewing the images as they are rendering.
     */
    public BufferedImage[] getImgs() {
        return imgs;
    }
}
