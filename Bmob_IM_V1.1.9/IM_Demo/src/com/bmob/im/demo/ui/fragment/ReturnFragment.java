package com.bmob.im.demo.ui.fragment;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.adapter.ReturnAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.util.TimeUtil;
import com.bmob.im.demo.view.ClearEditText;

public class ReturnFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	
	TextView tv_recent_name,tv_recent_msg,tv_recent_time;
	
	ImageView iv_recent_avatar;
	
	Button tv_recent_renew;

	ClearEditText mClearEditText;
	
	ListView listview;
	
	ReturnAdapter adapter;
	
	User user;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.item_return, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	
	private void initView() {
		// TODO 自动生成的方法存根
		initTopBarForOnlyTitle("归还");
//		listview = (ListView)findViewById(R.id.list);
//		listview.setOnItemClickListener(this);
//		listview.setOnItemLongClickListener(this);
//		adapter = new ReturnAdapter(getActivity(), R.layout.item_return, BmobDB.create(getActivity()).queryRecents());
//		listview.setAdapter(adapter);
		
		iv_recent_avatar = (ImageView) findViewById( R.id.iv_recent_avatar);
		tv_recent_name = (TextView) findViewById(R.id.tv_recent_name);
		tv_recent_msg = (TextView) findViewById(R.id.tv_recent_msg);
		tv_recent_time = (TextView) findViewById(R.id.tv_recent_time);
		tv_recent_renew = (Button) findViewById(R.id.tv_recent_renew);
		
//		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
//		mClearEditText.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				adapter.getFilter().filter(s);
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//		});
	}
	
	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}
	
	private void updateUser(final User user) {
		// 更改
		
		String machineID = user.getMachineID();
		
		if(machineID.equals("0")){
			tv_recent_msg.setText("无借用信息");	
			tv_recent_name.setText(user.getName());
			iv_recent_avatar.setImageResource(R.drawable.machine);
			//让续借按钮消失
			tv_recent_renew.setVisibility(View.GONE);
		}else{
			tv_recent_msg.setText("编号："+user.getMachineID());	
			tv_recent_name.setText(user.getUsername());
			
			final Boolean isRenew =  user.getIsRenew();
			final Long borrowTime = Long.parseLong(user.getBorrowTime());
			tv_recent_renew.setOnClickListener(new OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	            	if(isRenew){
	            		ShowToast("失败！已续借一次");
	            	}else{
	            		updateInfo(borrowTime);
	            		//ShowToast("续借成功！");
	            		//值还没上传
	            		//refresh();
	            	}
	            }
	        });
			tv_recent_time.setText("日期："+TimeUtil.getReturnTime(borrowTime,isRenew));
			iv_recent_avatar.setImageResource(R.drawable.machine);
		}
		
	}
	
	private void updateInfo(final Long borrowTime) {
		final User u = new User();
		
		u.setIsRenew(true);
		
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("续借成功！");
				getRemoteData();
				//tv_recent_time.setText("日期："+TimeUtil.getReturnTime(borrowTime,u.getIsRenew()));
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("续借失败:" + arg1);
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(getActivity(),listener);
	}

	@Override
	public void onResume() {
		super.onResume();
		getRemoteData();
	}
	
	private void getRemoteData(){
		User user = userManager.getCurrentUser(User.class);
		initOtherData(user.getUsername());
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 自动生成的方法存根
		
	}
}
