package com.tiggerbiggo.prima.graphics;

import com.tiggerbiggo.prima.calculation.Calculation;
import com.tiggerbiggo.prima.calculation.ColorTools;
import com.tiggerbiggo.prima.core.Vector2;

import java.awt.*;

/**
 * Simple gradient for 2 colors
 */
public class SimpleGradient extends Gradient
{
    Color c1, c2;
    boolean loop;

    /**
     *
     * @param c1 The first color
     * @param c2 The second color
     * @param loop Whether or not to loop
     * @throws IllegalArgumentException if c1 or c2 are null
     */
    public SimpleGradient(Color c1, Color c2, boolean loop) throws IllegalArgumentException{
        this.c1 = c1;
        this.c2 = c2;
        this.loop = loop;
    }

    /**
     * Default constructor, defaults to black, white, and no loop
     */
    public SimpleGradient(){
        this(Color.black, Color.white, false);
    }

    /**
     * Evaluates a vector to a color
     * @param v The vector to evaluate
     * @return The evaluated color
     */
    @Override
    public Color evaluate(Vector2 v){
        double a = v.magnitude();
        a = normalise(a, loop);
        return ColorTools.colorLerp(c1, c2, a);
    }
}
