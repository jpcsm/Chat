package com.test.js.chat;


import android.app.Activity;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class FBtn_libraryTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbtn_library_test);

//        findViewById(R.id.pink_icon).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FBtn_libraryTestActivity.this, "Clicked pink Floating Action Button", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.setter);
//        button.setSize(FloatingActionButton.SIZE_MINI);
//        button.setColorNormalResId(R.color.pink);
//        button.setColorPressedResId(R.color.pink_pressed);
//        button.setIcon(R.drawable.ic_fab_star);
//        button.setStrokeVisible(false);
//
//        final View actionB = findViewById(R.id.action_b);
//
//        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
//        actionC.setTitle("Hide/Show Action above");
//        actionC.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//            }
//        });
//
//        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
//        menuMultipleActions.addButton(actionC);



//        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        drawable.getPaint().setColor(getResources().getColor(R.color.white));
//        ((FloatingActionButton) findViewById(R.id.setter_drawable)).setIconDrawable(drawable);

//        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
//        actionA.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                actionA.setTitle("Action A clicked");
//            }
//        });

        // Test that FAMs containing FABs with visibility GONE do not cause crashes
//        findViewById(R.id.button_gone).setVisibility(View.GONE);
//
//        final FloatingActionButton actionEnable = (FloatingActionButton) findViewById(R.id.action_enable);
//        actionEnable.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                menuMultipleActions.setEnabled(!menuMultipleActions.isEnabled());
//            }
//        });

        FloatingActionsMenu rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        FloatingActionButton addedOnce = new FloatingActionButton(this);
        addedOnce.setTitle("Added once");
        rightLabels.addButton(addedOnce);

        FloatingActionButton addedTwice = new FloatingActionButton(this);
        addedTwice.setTitle("Added twice");
        rightLabels.addButton(addedTwice);
        rightLabels.removeButton(addedTwice);
        rightLabels.addButton(addedTwice);
    }
}