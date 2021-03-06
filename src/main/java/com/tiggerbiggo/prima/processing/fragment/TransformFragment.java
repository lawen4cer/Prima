package com.tiggerbiggo.prima.processing.fragment;

import com.tiggerbiggo.prima.core.Vector2;
import com.tiggerbiggo.prima.exception.IllegalMapSizeException;
import com.tiggerbiggo.prima.presets.Transform;

import java.util.ArrayList;

public class TransformFragment implements Fragment<Vector2>
{
    private Fragment<Vector2> in;
    private Transform t;

    private ArrayList<TransformFragment> myList;

    public TransformFragment(Fragment<Vector2> in, Transform t)
    {
        this.in = in;
        this.t = t;
    }

    @Override
    public Vector2 get() {
        return transform(in.get());
    }

    @Override
    public Fragment<Vector2>[][] build(int xDim, int yDim) throws IllegalMapSizeException {
        Fragment<Vector2>[][] map;
        try
        {
            map = in.build(xDim, yDim);
        }
        catch (IllegalMapSizeException ex)
        {
            throw ex;
        }

        if(Fragment.checkArrayDims(map, xDim, yDim))
        {
            TransformFragment[][] thisArray = new TransformFragment[xDim][yDim];
            for(int i=0; i<xDim; i++)
            {
                for(int j=0; j<yDim; j++)
                {
                    thisArray[i][j] = new TransformFragment(map[i][j], t);
                }
            }
            return thisArray;
        }

        throw new IllegalMapSizeException();
    }

    private Vector2 transform(Vector2 in)
    {
        return t.doFormula(in);
    }
}
