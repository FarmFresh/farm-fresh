package com.farmfresh.farmfresh.fragments.ui.models;

import com.github.siyamed.shapeimageview.RoundedImageView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by pbabu on 8/27/16.
 */
public class ImageViewWithProgressBar {
    private RoundedImageView imageView;
    private CircularProgressBar progressBar;
    public ImageViewWithProgressBar(RoundedImageView imageView, CircularProgressBar progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    public RoundedImageView getImageView() {
        return imageView;
    }

    public void setImageView(RoundedImageView imageView) {
        this.imageView = imageView;
    }

    public CircularProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(CircularProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
