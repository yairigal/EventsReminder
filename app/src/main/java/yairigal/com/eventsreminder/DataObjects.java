package yairigal.com.eventsreminder;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

/**
 * Created by Yair Yigal on 2018-01-07.
 */

public class DataObjects extends ArrayList<DataObject> {
    myCallback callback;
    public DataObjects(myCallback atAddRemove){
        this.callback = atAddRemove;
    }

    @Override
    public boolean add(DataObject dataObject) {
        boolean ans = super.add(dataObject);
        this.callback.onAddandRemove(this);
        return ans;
    }

    @Override
    public void add(int index, DataObject element) {
        super.add(index, element);
        this.callback.onAddandRemove(this);
    }

    @Override
    public DataObject remove(int index) {
        DataObject item = super.remove(index);
        this.callback.onAddandRemove(this);
        return item;
    }

    @Override
    public boolean remove(Object o) {
        boolean ans = super.remove(o);
        this.callback.onAddandRemove(this);
        return ans;
    }

    interface myCallback extends Callback{
        void onAddandRemove(DataObjects obj);
    }
}

