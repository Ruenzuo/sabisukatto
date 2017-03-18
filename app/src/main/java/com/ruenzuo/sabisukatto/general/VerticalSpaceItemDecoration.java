package com.ruenzuo.sabisukatto.general;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by ruenzuo on 25/03/16.
 * Code from: http://stackoverflow.com/a/27037230
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalSpaceHeight;
    private List<Integer> indexes;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight, List<Integer> indexes) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.indexes = indexes;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Integer position = parent.getChildAdapterPosition(view);
        if (indexes.contains(position)) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

}
