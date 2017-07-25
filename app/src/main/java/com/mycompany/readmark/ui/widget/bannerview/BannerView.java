package com.mycompany.readmark.ui.widget.bannerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycompany.readmark.R;
import com.mycompany.readmark.utils.commen.DensityUtils;


/**
 * 组合了BannerPageView、DotView（添加在Linearlayout中）、TextView
 */

public class BannerView extends RelativeLayout {
    private final static String TAG = "BannerView";

    private BannerPageView mBannerPageView;
    private TextView mBannerDesc;
    private LinearLayout mDotContainerLayout;
    private View mBottomView;

    private BannerDataAdapter mAdapter;

    private Context mContext;

    private int mCurrentDotPosition = 0;
    //默认值
    private Drawable mIndicatorFocusDrawable;
    private Drawable mIndicatorNormalDrawable;
    private int mDotLocation = -1;
    private int mDotSize = 8;
    private int mDotDistance = 8;
    private int mBottomColor = Color.TRANSPARENT;
    private int mDescColor = Color.WHITE;
    //根据接口返回的图片的宽高比自己设定宽高比
    private float mWidthProportion = 8;
    private float mHeightProportion = 3;


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        inflate(context, R.layout.ui_banner_layout, this);
        initAttributes(attrs);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * mHeightProportion / mWidthProportion);
        // 很关键，我们重新改变父VG分配给我们的MeasureSpec，
        // 因为我们高度设置的是0，这里分配给我们的MeasureSpec是（0，EXACTLY）
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec));
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));

        //用新的MeasureSpec将测量分发下去
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initAttributes(AttributeSet attrs){
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mDotLocation = array.getInt(R.styleable.BannerView_dotLocation, mDotLocation);
        mIndicatorFocusDrawable = array.getDrawable(R.styleable.BannerView_dotFocusDrawable);
        //如果没有配置点的drawable
        if(mIndicatorFocusDrawable == null){
            mIndicatorFocusDrawable = new ColorDrawable(Color.RED);
        }

        mIndicatorNormalDrawable = array.getDrawable(R.styleable.BannerView_dotNormalDrawable);
        //如果没有配置点的drawable
        if(mIndicatorNormalDrawable == null){
            mIndicatorNormalDrawable = new ColorDrawable(Color.WHITE);
        }

        mDotSize = (int) array.getDimension(R.styleable.BannerView_dotSize, DensityUtils.dp2px(mContext, mDotSize));
        mDotDistance= (int) array.getDimension(R.styleable.BannerView_dotDistance, DensityUtils.dp2px(mContext, mDotDistance));
        mBottomColor = array.getColor(R.styleable.BannerView_bottomColor, mBottomColor);
        mDescColor = array.getColor(R.styleable.BannerView_descColor, mDescColor);
        array.recycle();
    }

    private void initView(){
        mBannerPageView = (BannerPageView) findViewById(R.id.banner_page_view);
        mBannerDesc = (TextView) findViewById(R.id.desc_text);
        mDotContainerLayout = (LinearLayout) findViewById(R.id.dot_container);
        mBottomView = findViewById(R.id.banner_bottom);

        mBottomView.setBackgroundColor(mBottomColor);
        mBannerDesc.setTextColor(mDescColor);
    }

    /**
     * 设置适配器
     * @param adapter
     */
    public void setAdapter(BannerDataAdapter adapter) {
        mAdapter = adapter;
        //设置的适配器后才能设置圆点
        initDotIndicator();
        mBannerPageView.setAdapter(adapter);
        mBannerPageView.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                pageSelected(position);
            }
        });
        String firstDesc = mAdapter.getBannerDesc(0);
        mBannerDesc.setText(firstDesc);
    }

    private void pageSelected(int position){
        //改变DotView
        DotView oldDot = (DotView) mDotContainerLayout.getChildAt(mCurrentDotPosition % mAdapter.getCount());
        oldDot.setDrawable(mIndicatorNormalDrawable);
        mCurrentDotPosition = position;
        DotView currentDot = (DotView) mDotContainerLayout.getChildAt(mCurrentDotPosition % mAdapter.getCount());
        currentDot.setDrawable(mIndicatorFocusDrawable);
        //改变Desc
        String bannerDesc = mAdapter.getBannerDesc(mCurrentDotPosition % mAdapter.getCount());
        mBannerDesc.setText(bannerDesc);
    }

    /**
     *  根据Adapter中的数量，初始化圆点
     */
    private void initDotIndicator(){
        int count = mAdapter.getCount();
        //点布局的位置移动到右边
        mDotContainerLayout.setGravity(getDotGravity());
        //在容器中添加原点
        for (int i=0; i<count; i++){
            DotView indicator = new DotView(mContext);
            //大小
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(mDotSize, mDotSize);
            //间距
            params.leftMargin = params.rightMargin = mDotDistance;
            indicator.setLayoutParams(params);
            if(i == 0){
                indicator.setDrawable(mIndicatorFocusDrawable);
            }else{
                indicator.setDrawable(mIndicatorNormalDrawable);
            }
            mDotContainerLayout.addView(indicator);
        }
    }

    private int getDotGravity() {
        switch (mDotLocation){
            case 0:
                return Gravity.CENTER;
            case 1:
                return Gravity.RIGHT;
            case -1:
                return Gravity.LEFT;
        }
        return -1;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
       // Log.e(TAG, "onDetachedFromWindow");
    }




    /**
     * Pager开始滚动
     */
    public void startRoll(){
        //Log.e(TAG, "statRoll");
        mBannerPageView.startRoll();
    }
    public void setScrollCountDown(int countdown){
        mBannerPageView.setScrollerCountDown(countdown);
    }

    public void setScrollDuaration(int duaration){
        mBannerPageView.setScrollerDuaration(duaration);
    }

}
