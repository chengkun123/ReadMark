package com.mycompany.readmark.ui.adapter.commen;

/**
 * Created by Lenovo.
 */

public interface MultiTypeSupport<DATA> {
    /**
     * 一般是根据item里面的“标记”来判断布局的id
     * @param item
     * @return
     */
    int getLayoutId(DATA item);
}
