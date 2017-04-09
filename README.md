# ReadMark

- 简介

  这个app是自己练习和学习的地方，会尝试一些实用or新鲜的效果。在这里记录一些用到的知识点和问题。

- Retrofit + OkHttp + RxJava 网络请求框架

  ~~~java
      private RetrofitSingleton(){
          Executor executor = Executors.newCachedThreadPool();

          Gson gson = new GsonBuilder().create();

          HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
          interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

          OkHttpClient okHttpClient = new OkHttpClient.Builder()
                  .addInterceptor(interceptor)
                  .retryOnConnectionFailure(true)
                  .connectTimeout(15, TimeUnit.SECONDS)
                  .build();

          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .client(okHttpClient)
                  .addConverterFactory(GsonConverterFactory.create(gson))
                  .callbackExecutor(executor)
                  .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                  .build();
          service = retrofit.create(BookListService.class);
      }
  ~~~

- 搜索界面自定义流式标签布局FlowLayout展示热门标签，带长按拖动动效。

  ![p11.gif](https://github.com/chengkun123/ReadMark/blob/master/ScreenShots/p11.gif?raw=true)

  利用BaseAdapter和监听者模式分离数据和布局。

  ~~~java
  public class TagAdapter<T> extends BaseAdapter 
  ~~~

  ~~~java
  //为FlowLayout设置新Adapter
      public void setAdapter(TagAdapter<String> adapter){
          //注销监听者
          if(mAdapter != null && mDataSetObserver != null){
              mAdapter.unregisterDataSetObserver(mDataSetObserver);
          }
          removeAllViews();
          mAdapter = adapter;
          mOnTagMovedListener = (OnTagMovedListener)mAdapter;
          //注册监听者
          if(mAdapter != null){
              //在FlowLayout内部创建内部类实现DataSetObserver
              mDataSetObserver = new AdapterDataSetObserver();
              mAdapter.registerDataSetObserver(mDataSetObserver);
          }
      }

      public ListAdapter getAdapter(){
          return mAdapter;
      }
      /*
      * 此内部类作为一个Adapter的监听者，在Adapter调用notifyDataChanged时
      完成View重置的功能
      * */

      class AdapterDataSetObserver extends DataSetObserver{
          @Override
          public void onChanged() {
              super.onChanged();
              reloadViews();
          }

          @Override
          public void onInvalidated() {
              super.onInvalidated();
          }
      }
  ~~~

  利用Window和Animation产生拖动和标签移位的效果

  ~~~java
  if(mWindowLayoutParams == null){
              mWindowLayoutParams = new WindowManager.LayoutParams();
              //根据原TextView形成新的Window
              mWindowLayoutParams.width = mOldView.getWidth();
              mWindowLayoutParams.height = mOldView.getHeight();
              
              mWindowLayoutParams.x = mOldView.getLeft() + this.getLeft();
              mWindowLayoutParams.y = mOldView.getTop() + this.getTop();

              mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
              mWindowLayoutParams.format = PixelFormat.RGBA_8888;
              mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
              mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
              //mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
              //mOldView.setVisibility(INVISIBLE);
   }

   mWindowManager.addView(mDragView, mWindowLayoutParams);
  ~~~

  ~~~java
  private void startMove(int dropPosition){
          TranslateAnimation animation;
          //后移
          if(dropPosition < mTempPosition){
              for(int i=dropPosition; i<mTempPosition; i++){
                  View preView = getChildAt(i);
                  View nextView = getChildAt(i+1);
                  //移动效果理想
                  float xTranslation = (nextView.getLeft() - preView.getLeft()) * 1f / preView.getWidth();
                  float yTranslation = (nextView.getTop() - preView.getTop()) * 1f / preView.getHeight();

                  animation = new TranslateAnimation(
                          Animation.RELATIVE_TO_SELF
                          , 0
                          , Animation.RELATIVE_TO_SELF
                          , xTranslation
                          , Animation.RELATIVE_TO_SELF
                          , 0
                          , Animation.RELATIVE_TO_SELF
                          , yTranslation);

                  animation.setInterpolator(new LinearInterpolator());
                  animation.setDuration(100);
                  animation.setFillAfter(true);

                  if(i == mTempPosition - 1){
                      animation.setAnimationListener(animationListener);
                  }
                  preView.startAnimation(animation);
              }
          }
    
    	.......
  }
  ~~~



- 夜间模式切换框架，实验了一下工厂方法模式和builder模式。

  ​

  ![p12.gif](https://github.com/chengkun123/ReadMark/blob/master/ScreenShots/p12.gif?raw=true)

  ~~~java
  public abstract class ViewSetter {
      protected View mTargetView;
      protected int mTargetViewId;
      protected int mAttrId;

      public ViewSetter(int targetViewId, int attrId){
          mTargetViewId = targetViewId;
          mAttrId = attrId;
      }

      public ViewSetter(View targetView, int attrId){
          mTargetView = targetView;
          mAttrId = attrId;
      }

      public abstract void setValue(Resources.Theme theme, int themeId);

      //通过属性id得到某Theme下的具体颜色值
      protected int getColor(Resources.Theme theme){
          TypedValue typedValue = new TypedValue();
          theme.resolveAttribute(mAttrId, typedValue, true);
          return typedValue.data;
      }
  }
  ~~~

  ​

- 仿知乎悬浮功能按钮，自定义ViewGroup，利用了属性动画。

  ![p10.gif](https://github.com/chengkun123/ReadMark/blob/master/ScreenShots/p10.gif?raw=true)

  介绍在[这里](http://blog.csdn.net/chengkun_123/article/details/69938550)

- 水波纹进度条效果，自定义View，利用贝塞尔曲线达到水波效果。

  ![p13.gif](https://github.com/chengkun123/ReadMark/blob/master/ScreenShots/p13.gif?raw=true)

  ​

  ~~~java
  protected void onDraw(Canvas canvas){
  		...
          //画了一个以方块为底，三阶贝塞尔曲线为顶的图形
          //Path在每次onDraw的时候重置
          mPath.reset();
          mPath.moveTo(0, y);
          mPath.cubicTo(mWidth/4 + 2 * x, y + mWidth/12, mWidth/4 + 2 * x, y - mWidth/12, mWidth, y);
          mPath.lineTo(mWidth, mWidth);
          mPath.lineTo(0, mWidth);
          mPath.close();
          ...

          //先画圆dst
          mCanvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mCirclePaint);
          //模式设置为src_in
          mWavePaint.setXfermode(mMode);
          //后画水波矩形src
          mCanvas.drawPath(mPath, mWavePaint);

      	....

          //进行重绘
          super.onDraw(canvas);
          //10ms重绘一次，形成波动效果。
          postInvalidateDelayed(10);
      }
  ~~~

  ​

- TO-DO

  1.因为豆瓣读书API给的并不全（热门标签都是硬编码进去的，汗），有时间想学习爬虫并爬取Top10等信息~