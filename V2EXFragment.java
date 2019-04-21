package com.example.greeknews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.greeknews.R;
import com.example.greeknews.activity.JieDianActivity;
import com.example.greeknews.adapter.VpAdapterV2ex;
import com.example.greeknews.base.BaseFragment;
import com.example.greeknews.bean.V2exBeanTabs;
import com.example.greeknews.presenter.V2EXPresenter;
import com.example.greeknews.v2exfragments.AllFragment;
import com.example.greeknews.view.V2EXView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by 孤辟 on 2019/4/3.
 */

public class V2EXFragment extends BaseFragment<V2EXView, V2EXPresenter> implements V2EXView {

    // 徐志岐  1808b
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.iv)
    ImageView iv;
    private String mUrl = "https://www.v2ex.com/";
    private static final String TAG = "V2EXFragment";
    private ArrayList<String> mtitles;
    private VpAdapterV2ex mAdapterV2ex;
    private ArrayList<Fragment> mFragments;
    private VpAdapterV2ex mVpAdapterV2ex;

    @Override
    protected V2EXPresenter initPresenter() {
        return new V2EXPresenter();
    }

    @Override
    protected int getlayoutid() {
        return R.layout.fragment_v2ex;
    }

    @Override
    protected void initData() {
        super.initData();
        final ArrayList<V2exBeanTabs> tabslist = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(mUrl).get();
                    //查找id是Tabs的div元素,因为只有一个,直接调用了first()
                    Element tabs = doc.select("div#Tabs").first();
                    //查找带有href属性的a元素
                    Elements allTabs = tabs.select("a[href]");
                    for (Element element : allTabs) {
                        String linkHref = element.attr("href");
                        String tab = element.text();
                        //Log.d(TAG, "linkHref: " + linkHref + ",tab:" + tab);
                        V2exBeanTabs bean = new V2exBeanTabs(linkHref, tab);
                        tabslist.add(bean);
                        Log.d(TAG, "linkHref: " + tabslist.toString());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFragments = new ArrayList<>();
                            for (int i = 0; i < tabslist.size(); i++) {
                                mFragments.add(AllFragment.newInstance(tabslist.get(i).linkHref));
                            }
                            mVpAdapterV2ex = new VpAdapterV2ex(getChildFragmentManager(), mFragments, tabslist);
                            vp.setAdapter(mVpAdapterV2ex);
                            tab.setupWithViewPager(vp);
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), JieDianActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
