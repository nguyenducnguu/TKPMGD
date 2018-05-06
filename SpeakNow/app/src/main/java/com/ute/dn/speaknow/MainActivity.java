package com.ute.dn.speaknow;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.ute.dn.speaknow.adapters.SwipeAdapter;
import com.ute.dn.speaknow.common.Const;
import com.ute.dn.speaknow.fragments.MainOfflineFragment;
import com.ute.dn.speaknow.fragments.MainOnlineFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView img_savedlist, img_info;
    ViewPager mViewPager;
    SwipeAdapter mSwipeAdapter;
    List<Fragment> lstFragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstFragment.add(new MainOnlineFragment());
        lstFragment.add(new MainOfflineFragment());

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(1);
        mSwipeAdapter = new SwipeAdapter(getSupportFragmentManager(), lstFragment);
        mViewPager.setAdapter(mSwipeAdapter);
        mViewPager.setCurrentItem(0);

        img_savedlist = findViewById(R.id.img_savedlist);
        img_savedlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SavedListActivity.class);
                startActivity(intent);
            }
        });

        img_info = findViewById(R.id.img_info);
        img_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInfo();
            }
        });
    }

    private void showDialogInfo(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_info);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Const.PICKFILE_VIDEO_REQUEST_CODE
                || requestCode == Const.PICKFILE_TRANSCRIPT_REQUEST_CODE) {
            lstFragment.get(1).onActivityResult(requestCode, resultCode, data);
        }
        else if(requestCode == Const.MAIN_ONLINE_PICKFILE_TRANSCRIPT_REQUEST_CODE){
            lstFragment.get(0).onActivityResult(requestCode, resultCode, data);
        }
    }
}
