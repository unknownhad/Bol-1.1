package com.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.BolApp;
import com.bol.app.R;
import com.model.classes.DataObject;

public class GridViewAdapter extends BaseAdapter {

	ArrayList<DataObject> list;
	Context context;
	Resources res;
	int count;
	boolean fromTopList;
	

	public GridViewAdapter(Context context, ArrayList<DataObject> list,boolean fromTopList) {

		this.context = context;
		this.list = list;
		res = context.getResources();
		count = list.size();
		this.fromTopList = fromTopList;

	}

	@Override
	public int getCount() {
		
		return count;
	}

	@Override
	public Object getItem(int position) {
		
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.gridview_layout, null);
			holder = new ViewHolder();
			holder.imgview = (ImageView) convertView.findViewById(R.id.imgview);
			holder.deleteImg = (ImageView) convertView
					.findViewById(R.id.delete);
			holder.catName = (TextView) convertView.findViewById(R.id.name);
			if(BolApp.sp.getBoolean("lock_locked", false))
			{
				holder.deleteImg.setVisibility(View.INVISIBLE);
			}
			else
				holder.deleteImg.setVisibility(View.VISIBLE);

			convertView.setTag(holder);

		} else {
			
			holder = (ViewHolder) convertView.getTag();
			if(BolApp.sp.getBoolean("lock_locked", false))
			{
				holder.deleteImg.setVisibility(View.INVISIBLE);
			}
			else
				holder.deleteImg.setVisibility(View.VISIBLE);
		}

		holder.deleteImg.setTag(list.get(position));
		System.out.println(list.get(position).getImg_path()+" Position is " + position + " and name is "
				+ list.get(position).getImg_name());

		if (list.get(position).getImg_name() != null) {
			int drawableID = res.getIdentifier(
					list.get(position).getImg_name(), "drawable",
					context.getPackageName());
			
			
			if (drawableID == 0) {
				drawableID = res.getIdentifier("not_available", "drawable",
						context.getPackageName());

			}
			 if(list.get(position).getImg_path()!=null)
			{
				holder.imgview.setImageBitmap(BitmapFactory.decodeFile(list.get(position).getImg_path()));
				
			}
			 else
			holder.imgview.setImageResource(drawableID);
		}

		else {
			int drawableID = res.getIdentifier("not_available", "drawable",
					context.getPackageName());
			holder.imgview.setImageResource(drawableID);
		}
		
		if ((list!=null && list.get(position).getImg_name()!=null && list.get(position).getImg_name()
				.equalsIgnoreCase("add_category_img"))|| fromTopList) {
			holder.deleteImg.setVisibility(View.INVISIBLE);
		}
		

		holder.catName.setText(list.get(position).getCategoryName());

		return convertView;
	}

	private static class ViewHolder {
		ImageView imgview;
		TextView catName;
		ImageView deleteImg;
	}

}
