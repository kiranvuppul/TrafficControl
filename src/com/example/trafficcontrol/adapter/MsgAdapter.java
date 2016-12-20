package com.example.trafficcontrol.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.trafficcontrol.BaseActivity;
import com.example.trafficcontrol.R;
import com.example.trafficcontrol.model.TrfcViaolator;
import com.example.trafficcontrol.utils.GenericUtility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MsgAdapter extends ArrayAdapter<TrfcViaolator>
{
	@SuppressWarnings("unused")
	private List<TrfcViaolator> mList;
	
	@SuppressWarnings("unused")
	private final BaseActivity mActivity;
	
	public MsgAdapter(BaseActivity activity, int ResourceId, List<TrfcViaolator> list) 
	{
		super(activity, R.layout.msg_list_row, list);
		this.mActivity = activity;
		this.mList = list;
	}
	
	protected static class ViewHolder
	{
		protected TextView tvImgPath;
		protected TextView tvDesc;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view = null;
		if (convertView == null) 
		{
			final LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflator.inflate(R.layout.msg_list_row, null);
			final ViewHolder holder = new ViewHolder();

			holder.tvImgPath = (TextView) view.findViewById(R.id.tvImgPath);
			holder.tvDesc = (TextView) view.findViewById(R.id.tvDesc);
			
			view.setTag(holder);
		} 
		else 
		{
			view = convertView;
		}
		
		final ViewHolder holder = (ViewHolder) view.getTag();
		
		TrfcViaolator trfcviolator = this.mList.get(position);
		String[] strData = trfcviolator.getmPicPath().split("/");
		holder.tvImgPath.setText(strData[strData.length - 1]);
		
		if(trfcviolator.getmMessage() != null && trfcviolator.getmMessage() != "" && !trfcviolator.getmMessage().contains("null"))
			holder.tvDesc.setText(trfcviolator.getmMessage());
		else
			holder.tvDesc.setText("");
		
		if(view!=null)
		{
			if (((ListView)parent) != null && position == GenericUtility.list_sel) 
			{
				view.setSelected(true);
				view.setPressed(true);			
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.listViewCellHilited));
				
				//....Set color
				int seletectTextColor = mActivity.getResources().getColor(R.color.listViewTextSelected);
				holder.tvImgPath.setTextColor(seletectTextColor);
				holder.tvDesc.setTextColor(seletectTextColor);
	        }
			else
			{				
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
				
				//....Set color
				int textColor = mActivity.getResources().getColor(R.color.listViewText);
				holder.tvImgPath.setTextColor(mActivity.getResources().getColor(R.color.listViewTextTitle));
				holder.tvDesc.setTextColor(textColor);
			}
		}
		
		return view;
	}

}
