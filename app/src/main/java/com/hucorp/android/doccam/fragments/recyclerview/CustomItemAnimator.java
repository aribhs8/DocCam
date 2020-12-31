package com.hucorp.android.doccam.fragments.recyclerview;

import android.util.Log;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.hucorp.android.doccam.Constants;
import com.hucorp.android.doccam.R;

public class CustomItemAnimator extends DefaultItemAnimator
{
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder)
    {
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.viewholder_remove_animation
        ));

        return super.animateRemove(holder);
    }
}
