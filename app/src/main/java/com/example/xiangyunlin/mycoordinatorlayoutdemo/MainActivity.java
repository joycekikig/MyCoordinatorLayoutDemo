package com.example.xiangyunlin.mycoordinatorlayoutdemo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout mTabs;
    private ViewPager mViewPager;
    private ArrayList<String> myDataset;
    private FloatingActionButton mFabAdd;
    private FloatingActionButton mFabRightNext;
    private FloatingActionButton mFabLeftNext;
    private boolean doNotifyDataSetChangedOnce = false;
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        initFab();

    }

    private void initData() {
        myDataset = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            myDataset.add(i + "");
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setText("Tab 1"));
        mTabs.addTab(mTabs.newTab().setText("Tab 2"));

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
                doRightFAB(tab.getPosition());
                doLeftFAB(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs) {
            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                animateFab(position);
                doRightFAB(position);
                doLeftFAB(position);
            }
        });
    }

    private void initFab() {
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        mFabRightNext = (FloatingActionButton) findViewById(R.id.fab_rightnext);
        mFabLeftNext = (FloatingActionButton) findViewById(R.id.fab_leftnext);
        mFabLeftNext.hide();

        doAddFAB();
        doRightFAB(mTabs.getTabCount());
        doLeftFAB(0);
    }

    private class SamplePagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            // 記得要確認有沒有新修改過Tab總數，無論如何都要記得 notifyDataSetChanged !
            if(doNotifyDataSetChangedOnce) {
                doNotifyDataSetChangedOnce = false;
                notifyDataSetChanged();
            }
            return mTabs.getTabCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           view = new View(MainActivity.this);

            switch(position){
                case 0:
                    view = getLayoutInflater().inflate(R.layout.pager_item1, container, false);
                    MyAdapter myAdapter = new MyAdapter(myDataset);
                    RecyclerView mList = view.findViewById(R.id.list_view);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mList.setLayoutManager(layoutManager);
                    mList.setAdapter(myAdapter);
                    break;
                default:
                    view = getLayoutInflater().inflate(R.layout.pager_item2, container, false);
                    container.addView(view);
                    TextView title = view.findViewById(R.id.item_title);
                    title.setText(String.valueOf(position + 1));
            }
            // ERROR : The specified child already has a parent. You must call removeView() on the child's parent first.
            if(view.getParent() != null)
                container.removeView(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public ViewHolder(View v) {
                super(v);
                mTextView = v.findViewById(R.id.info_text);
            }
        }

        public MyAdapter(List<String> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mData.get(position));

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    // Add Fab Function
    private void doAddFAB() {
        // 點擊浮動+鈕，增加Tab
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numTabs = mTabs.getTabCount();
                mTabs.addTab(mTabs.newTab().setText("Tab " + String.valueOf(numTabs+1)));
                doNotifyDataSetChangedOnce = true;
                mViewPager.setCurrentItem(mTabs.getTabCount());
                Log.d("test", "Add");
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs){
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                    }
                });
            }
        });

    }

    // 點擊浮動>鈕，往下一個Tab
    private void doRightFAB(final int position) {
        mFabRightNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNotifyDataSetChangedOnce = true;
                mViewPager.setCurrentItem(position+1);
                Log.d("test", "Right");
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs){
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                    }
                });
            }
        });

    }

    // 點擊浮動<鈕，往上一個Tab
    private void doLeftFAB(final int position) {
        mFabLeftNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNotifyDataSetChangedOnce = true;
                mViewPager.setCurrentItem(position-1);
                Log.d("test", "Left");
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs){
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                    }
                });
            }
        });

    }

    private void animateFab(int position) {
        switch (position) {
            case 0:
                mFabRightNext.show();
                mFabLeftNext.hide();
                break;
            default:
                mFabRightNext.show();
                mFabLeftNext.show();
                break;
        }
    }
}
