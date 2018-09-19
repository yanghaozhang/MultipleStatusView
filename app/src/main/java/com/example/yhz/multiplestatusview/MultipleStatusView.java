package com.example.yhz.multiplestatusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MultipleStatusView extends FrameLayout {
    private static final String TAG = "MultipleStatusView";

    public static final LayoutParams DEFAULT_LAYOUT_PARAMS =
            new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);

    public static final int STATUS_CONTENT = 0x00;
    public static final int STATUS_LOADING = 0x01;
    public static final int STATUS_EMPTY = 0x02;
    public static final int STATUS_ERROR = 0x03;
    public static final int STATUS_NO_NETWORK = 0x04;

    private int mEmptyViewResId;
    private int mErrorViewResId;
    private int mLoadingViewResId;
    private int mNoNetworkViewResId;

    private int mViewStatus;
    private LayoutInflater mInflater;
    private OnClickListener mOnRetryClickListener;

    public MultipleStatusView(Context context) {
        this(context, null);
    }

    public MultipleStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusView, defStyleAttr, 0);
        mEmptyViewResId = a.getResourceId(R.styleable.MultipleStatusView_emptyView, R.layout.empty_view);
        mErrorViewResId = a.getResourceId(R.styleable.MultipleStatusView_errorView, R.layout.error_view);
        mLoadingViewResId = a.getResourceId(R.styleable.MultipleStatusView_loadingView, R.layout.loading_view);
        mNoNetworkViewResId = a.getResourceId(R.styleable.MultipleStatusView_noNetworkView, R.layout.no_network_view);
        a.recycle();
        mInflater = LayoutInflater.from(getContext());
    }

    /**
     * 获取当前状态
     */
    public int getViewStatus() {
        return mViewStatus;
    }

    /**
     * 设置重试点击事件
     *
     * @param onRetryClickListener 重试点击事件
     */
    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        this.mOnRetryClickListener = onRetryClickListener;
    }

    /**
     * 显示空视图
     */
    public void showEmpty() {
        showEmpty(mEmptyViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    /**
     * 显示空视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public void showEmpty(int layoutId, ViewGroup.LayoutParams layoutParams) {
        showStatusView(layoutId, layoutParams, STATUS_EMPTY);
    }

    /**
     * 显示错误视图
     */
    public void showError() {
        showError(mErrorViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    /**
     * 显示错误视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public void showError(int layoutId, ViewGroup.LayoutParams layoutParams) {
        showStatusView(layoutId, layoutParams, STATUS_ERROR);
        changeErrorMsg("加载失败");
    }

    /**
     * 显示错误视图
     */
    public void showErrorWithMsg(String msg) {
        showStatusView(mErrorViewResId, DEFAULT_LAYOUT_PARAMS, STATUS_ERROR);
        changeErrorMsg(msg);
    }

    private void changeErrorMsg(String msg) {
        View statusView = getStatusView(mErrorViewResId);
        if (statusView != null) {
            View tvView = statusView.findViewById(R.id.error_view_tv);
            if (tvView != null && tvView instanceof TextView) {
                TextView view = (TextView) tvView;
                if (!view.getText().toString().equals(msg) && msg != null) {
                    view.setText(msg);
                }
            }
        }
    }

    /**
     * 显示加载中视图
     */
    public void showLoading() {
        showLoading(mLoadingViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    /**
     * 显示加载中视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public void showLoading(int layoutId, ViewGroup.LayoutParams layoutParams) {
        showStatusView(layoutId, layoutParams, STATUS_LOADING);
    }

    /**
     * 显示无网络视图
     */
    public void showNoNetwork() {
        showNoNetwork(mNoNetworkViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    /**
     * 显示无网络视图
     *
     * @param layoutId     自定义布局文件
     * @param layoutParams 布局参数
     */
    public void showNoNetwork(int layoutId, ViewGroup.LayoutParams layoutParams) {
        showStatusView(layoutId, layoutParams, STATUS_NO_NETWORK);
    }

    public void showStatusView(int layoutId, ViewGroup.LayoutParams layoutParams, int status) {
        View view = getStatusView(layoutId);
        if (view == null) {
            view = inflateView(layoutId);
            view.setTag(layoutId);
            showStatusView(view, layoutParams, status);
        } else {
            mViewStatus = status;
            if (layoutParams != null && !layoutParams.equals(view.getLayoutParams())) {
                view.setLayoutParams(layoutParams);
            }
            showViewById(view);
        }
    }

    public void showStatusView(View view, ViewGroup.LayoutParams layoutParams, int status) {
        if (layoutParams == null) {
            layoutParams = DEFAULT_LAYOUT_PARAMS;
        }
        checkNull(view, "view is null.---status::" + status);
        mViewStatus = status;
        if (getStatusView(view) == null) {
            if (mOnRetryClickListener != null) {
                View clickView = getRetryView(view, status);
                if (clickView != null && !clickView.hasOnClickListeners()) {
                    clickView.setOnClickListener(mOnRetryClickListener);
                }
            }
            addView(view, 0, layoutParams);
        }
        showViewById(view);
    }

    private View getRetryView(View view, int status) {
        switch (status) {
            case STATUS_EMPTY:
                return view.findViewById(R.id.empty_retry_view);
            case STATUS_ERROR:
                return view.findViewById(R.id.error_retry_view);
            case STATUS_NO_NETWORK:
                return view.findViewById(R.id.no_network_retry_view);
            default:
                break;
        }
        return null;
    }

    private View inflateView(int layoutId) {
        return mInflater.inflate(layoutId, null);
    }

    private View getStatusView(View v) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null && view == v) {
                return view;
            }
        }
        return null;
    }

    private View getStatusView(int viewId) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null && view.getTag() instanceof Integer && ((Integer) view.getTag()) == viewId) {
                return view;
            }
        }
        return null;
    }

    private void showViewById(View v) {
        setVisibility(VISIBLE);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.setVisibility(view == v ? VISIBLE : GONE);
        }
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            mViewStatus = STATUS_CONTENT;
        }
    }

    private void checkNull(Object object, String hint) {
        if (null == object) {
            throw new NullPointerException(hint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOnRetryClickListener = null;
        mInflater = null;
    }

    public void setEmptyViewResId(int emptyViewResId) {
        this.mEmptyViewResId = emptyViewResId;
    }

    public void setErrorViewResId(int errorViewResId) {
        this.mErrorViewResId = errorViewResId;
    }

    public void setLoadingViewResId(int loadingViewResId) {
        this.mLoadingViewResId = loadingViewResId;
    }

    public void setNoNetworkViewResId(int noNetworkViewResId) {
        this.mNoNetworkViewResId = noNetworkViewResId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }
}
