package com.androidfuture.frames.data;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.androidfuture.frames.ui.AFCellView;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.AFLog;




import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AFListAdapter extends BaseAdapter {
	 private List<AFData> mObjects;
	    private Context mContext;
	    private Class mClass;
	
	    public AFListAdapter(Context context, Class classObject) {
	    	super();
	    	mClass = classObject;
	        init(context);
	       
	    }

	    /**
	     * Adds the specified object at the end of the array.
	     *
	     * @param object The object to add at the end of the array.
	     */
	    public void add(AFData object) {
	    			mObjects.add(object);
	 	        
	        }

	    /**
	     * Adds the specified Collection at the end of the array.
	     *
	     * @param collection The Collection to add at the end of the array.
	     */
	    public void addAll(Collection<? extends AFData> collection) {
	        mObjects.addAll(collection);
	    }

	    /**
	     * Adds the specified items at the end of the array.
	     *
	     * @param items The items to add at the end of the array.
	     */
	    public void addAll(AFData ... items) {
	   
	                for (AFData item : items) {
	                	mObjects.add(item);
	                }
	    }

	    /**
	     * Inserts the specified object at the specified index in the array.
	     *
	     * @param object The object to insert into the array.
	     * @param index The index at which the object must be inserted.
	     */
	    public void insert(AFData object, int index) {
	            mObjects.add(index, object);
	            
	    }

	    /**
	     * Removes the specified object from the array.
	     *
	     * @param object The object to remove.
	     */
	    public void remove(AFData object) {
	    		mObjects.remove(object);
	    }
	    
	    public void clear() {
	         
	            	mObjects.clear();
	          
	    }
	    
	    public List<AFData> getAll()
	    {
	    	return mObjects;
	    }

	    
	    private void init(Context context) {
	        mContext = context;
	        mObjects = new ArrayList<AFData>();
	    }

	
	    public Context getContext() {
	        return mContext;
	    }

	    public int getCount() {
	    	//WWLog.d("size: " + mObjects.size());
	        return mObjects.size();
	    }


	    public AFData getItem(int position) {
	        return mObjects.get(position);
	    }

	    public int getPosition(AFData item) {
	        return mObjects.indexOf(item);
	    }

	   
	    public long getItemId(int position) {
	        return position;
	    }

	  
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	AFCellView itemView = (AFCellView) convertView;
	        //WWLog.d("get view: " + position);
	    	if(itemView == null )
	        {

				Class argClass[] = {Context.class};
	    		Constructor<AFCellView> cons;
				try {
					cons = mClass.getConstructor(argClass);
					itemView  = cons.newInstance(mContext);
				} catch (NoSuchMethodException e) {
					AFLog.e("no such method");
					e.printStackTrace();
				}catch (IllegalArgumentException e) {
					AFLog.e("illegal argument");
					e.printStackTrace();
				} catch (InstantiationException e) {
					AFLog.e("illegal exception");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					AFLog.e("illegal access");
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					AFLog.e("illegal exception");
					e.printStackTrace();
				}
	    	}
	    	if(itemView == null)
	    		return null;
	    	((AFCellView)itemView).update(mObjects.get(position));
	    	return itemView;
	    }
}
