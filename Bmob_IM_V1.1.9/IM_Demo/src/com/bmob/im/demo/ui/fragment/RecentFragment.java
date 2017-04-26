package com.bmob.im.demo.ui.fragment;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.util.TimeUtil;
import com.bmob.im.demo.view.ClearEditText;
import com.bmob.im.demo.view.dialog.DialogTips;

/** ����Ự
  * @ClassName: ConversationFragment
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 ����1:01:37
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	
	TextView tv_recent_name,tv_recent_msg,tv_recent_time;
	
	ImageView iv_recent_avatar;

	ClearEditText mClearEditText;
	
	ListView listview;
	
	User user;
	
	MessageRecentAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.item_borrow, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		initTopBarForOnlyTitle("����");
//		listview = (ListView)findViewById(R.id.list);
//		listview.setOnItemClickListener(this);
//		listview.setOnItemLongClickListener(this);
//		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_borrow, BmobDB.create(getActivity()).queryRecents());
//		listview.setAdapter(adapter);
		iv_recent_avatar = (ImageView) findViewById( R.id.iv_recent_avatar);
		tv_recent_name = (TextView) findViewById(R.id.tv_recent_name);
		tv_recent_msg = (TextView) findViewById(R.id.tv_recent_msg);
		tv_recent_time = (TextView) findViewById(R.id.tv_recent_time);
		
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
					Log.i("asd",user.toString());
					updateUser(user);
				} else {
					ShowLog("onSuccess ���޴���");
				}
			}
		});
	}
	
	private void updateUser(User user) {
		// ����
		tv_recent_msg.setText("��ţ�"+user.getMachineID());	
		tv_recent_name.setText(user.getName());
		
		Long borrowTime = Long.parseLong(user.getBorrowTime());
		tv_recent_time.setText("���ڣ�"+TimeUtil.getChatTime(borrowTime));
		iv_recent_avatar.setImageResource(R.drawable.machine);
	}

	
	/** ɾ���Ự
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"ɾ���Ự", "ȷ��",true,true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		//����δ����Ϣ
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//��װ�������
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
		initOtherData(user.getUsername());
		if(!hidden){
			refresh();
		}
	}
	
}
