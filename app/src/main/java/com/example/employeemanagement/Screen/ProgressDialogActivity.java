package com.example.employeemanagement.Screen;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.example.employeemanagement.R;

public class ProgressDialogActivity {

    public static Dialog progressBar;

    public static void ShowProgreeBar(Context mContext)
    {
        //Dialog progressBar;
        progressBar = new Dialog(mContext);
        progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.layout_progressbar);
        //return progressBar;
    }

    public static void show()
    {
        progressBar.show();
    }

    public static void hide()
    {
        progressBar.dismiss();
    }

}
