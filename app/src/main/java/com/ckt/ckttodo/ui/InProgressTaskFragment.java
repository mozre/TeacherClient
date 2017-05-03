package com.ckt.ckttodo.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttodo.Base.CommonFragmentView;
import com.ckt.ckttodo.R;
import com.ckt.ckttodo.database.DatabaseHelper;
import com.ckt.ckttodo.database.EventTask;
import com.ckt.ckttodo.database.PostTaskData;
import com.ckt.ckttodo.databinding.FragmentTaskBinding;
import com.ckt.ckttodo.databinding.TaskListItemBinding;
import com.ckt.ckttodo.presenter.PostDetailPresenter;
import com.ckt.ckttodo.util.TranserverUtil;
import com.ckt.ckttodo.widgt.TaskDividerItemDecoration;
import com.ckt.ckttodo.widgt.TimeWatchDialog;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;

/**
 * Created by mozre
 */
public class InProgressTaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EndlessRecyclerView.OnLoadMoreListener, CommonFragmentView {

    private static final String TAG = "InProgressTaskFragment";
    private FragmentTaskBinding mFragmentTaskBinding;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerView mRecyclerView;
    private TaskRecyclerViewAdapter mAdapter;
    private RealmResults<PostTaskData> mTasks;
    private LinkedList<PostTaskData> mShowTasks;
    private LinkedList<PostTaskData> mTopTasks = new LinkedList<>();
    private static boolean isShowCheckBox = false;
    private Map<Integer, Boolean> mItemsSelectStatus = new HashMap<>();
    private ShowMainMenuItem mShowMenuItem;
    private DatabaseHelper mHelper;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

        try {
            MainActivity activity = (MainActivity) context;
            this.mShowMenuItem = (ShowMainMenuItem) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Cast Exception");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return init(inflater);
    }


    private View init(LayoutInflater inflater) {
        mShowTasks = new LinkedList<>();
        mHelper = DatabaseHelper.getInstance(getContext());
        screenTask();
        mFragmentTaskBinding = FragmentTaskBinding.inflate(inflater);
        mRecyclerView = mFragmentTaskBinding.recyclerTaskList;
        mSwipeRefreshLayout = mFragmentTaskBinding.commonHomeFragmentRefresh;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);
        mAdapter = new TaskRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new TaskDividerItemDecoration(getContext(),
                TaskDividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        return mFragmentTaskBinding.getRoot();
    }


    //     过滤 Task
    private void screenTask() {
        if (mShowTasks == null) {
            mShowTasks = new LinkedList<>();
        }
        mShowTasks.clear();
        mTasks = mHelper.findAll(PostTaskData.class);
        mTopTasks.clear();
        long now = Calendar.getInstance().getTimeInMillis();
        for (PostTaskData task : mTasks) {
            if (task.getExam_deadline() > now && task.getStatus() == PostTaskData.STATUS_DATA_PASS) {
                if (task.getTopNumber() > 0) {
                    mTopTasks.add(task);
                    continue;
                }
                mShowTasks.addLast(task);
            }
        }
        sortTop(mTopTasks);
    }

    private void sortTop(LinkedList<PostTaskData> list) {
        if (list.size() == 0) {
            return;
        }
        if (list.size() == 1) {
            mShowTasks.addFirst(list.get(0));
            return;
        }
        PostTaskData tmpTask;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); ++j) {
                if (list.get(j).getTopNumber() > list.get(i).getTopNumber()) {
                    tmpTask = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, tmpTask);
                }
            }
        }
        mShowTasks.addAll(0, list);

    }

    public void notifyData() {
        screenTask();
        mAdapter.customNotifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        PostDetailPresenter presenter = new PostDetailPresenter(mContext, this);
        long seconds = 0;
        if (mShowTasks.size() > 0) {
//            seconds = mData.get(0).getSeconds();
        }
        presenter.postArtcleData(seconds);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMore(EndlessRecyclerView view) {
        Long seconds = null;
        PostDetailPresenter presenter = new PostDetailPresenter(mContext, this);
        if (mShowTasks.size() > 0) {
//            Log.d(TAG, "onLoadMore: max = " + mData.get(0).getSeconds());
//            seconds = mData.get(mData.size() - 1).getSeconds();
            Log.d(TAG, "onLoadMore: seconds = " + seconds);
            try {
                presenter.loadDetailData(seconds);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mRecyclerView.setLoading(false);
        }
        if (seconds != null && seconds > 0) {

        }
    }

    @Override
    public void noMoreNewMessage() {
        mRecyclerView.setLoading(false);
        Toast.makeText(mContext, "已经到最下面了！", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void noMoreMessage() {

    }

    @Override
    public void notifyNewData(List<PostTaskData> mData) {
        for (int i = 0; i < mData.size(); ++i) {
            mShowTasks.addFirst(mData.get(i));
        }
        Log.d(TAG, "notifyNewData: mdata = " + mShowTasks.size());
        if (mShowTasks.size() > 0) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyMoreData(List<PostTaskData> mData) {
        for (int i = 0; i < mData.size(); ++i) {
            mShowTasks.addLast(mData.get(i));
        }
        if (mShowTasks.size() > 0) {
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setLoading(false);
        }
    }


    private class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewHolder> {

        @Override
        public TaskRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TaskListItemBinding taskListItemBinding = DataBindingUtil.inflate(LayoutInflater.
                    from(getContext()), R.layout.task_list_item, parent, false);


            return new TaskRecyclerViewHolder(taskListItemBinding);
        }

        /**
         * clear mItemsSelectStatus before notifyDataSetChanged
         */

        public void customNotifyDataSetChanged() {
            resetItemSelectStatus(mItemsSelectStatus);
            notifyDataSetChanged();
        }


        /**
         * if delete data,show update mTasks data
         */

        public void customDeleteNotifyDataSetChanged() {
            screenTask();
            resetItemSelectStatus(mItemsSelectStatus);
            notifyDataSetChanged();
        }


        private void resetItemSelectStatus(Map<Integer, Boolean> map) {
            map.clear();
            for (int i = 0; mShowTasks.size() > i; ++i) {
                map.put(i, false);
            }

        }

        @Override
        public void onBindViewHolder(TaskRecyclerViewHolder holder, int position) {

            holder.setData(mShowTasks.get(position));
            holder.container.setTag(position);
            if (isShowCheckBox) {
                holder.checkBox.setChecked(mItemsSelectStatus.get(position));
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.imageButtonStatus.setVisibility(View.INVISIBLE);
                holder.textViewToTop.setVisibility(View.VISIBLE);
                if (mShowTasks.get(position).getTopNumber() > 0) {
                    holder.textViewToTop.setText(getResources().getString(R.string.cancel_top));
                }

            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.textViewToTop.setVisibility(View.GONE);
                holder.imageButtonStatus.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mShowTasks.size();
        }
    }


    private class TaskRecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private TimeWatchDialog timeWatchDialog;
        RelativeLayout container;
        TextView textViewPlan;
        TextView textViewPlanTime;
        TextView textViewSpendTime;
        ImageButton imageButtonStatus;
        CheckBox checkBox;
        PostTaskData mTask;
        TextView textViewToTop;
        private TaskListItemBinding mBinding;

        public TaskRecyclerViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            textViewPlan = binding.textTaskListPlan;
            textViewPlanTime = binding.textTaskListPlanTime;
            imageButtonStatus = binding.imageTaskStatus;
            checkBox = binding.checkTaskSelect;
            container = binding.relativeContainer;
            textViewToTop = binding.textTaskListTop;
            container.setOnLongClickListener(this);
            container.setOnClickListener(this);
            imageButtonStatus.setOnClickListener(this);
            textViewToTop.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mItemsSelectStatus.put((Integer) container.getTag(), isChecked);
                }
            });

        }


        public void setData(PostTaskData data) {
            this.mTask = data;
            mBinding.setTask(data);
            mBinding.executePendingBindings();
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == container) {
                itemContainerLongClickedEvent();
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            if (v == container) {
                if (isShowCheckBox) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        mItemsSelectStatus.put((Integer) container.getTag(), false);
                    } else {
                        checkBox.setChecked(true);
                        mItemsSelectStatus.put((Integer) container.getTag(), true);
                    }
                } else {

                    Intent intent = new Intent(getContext(),NewExamActivity.class);
                    intent.putExtra(NewExamActivity.PASS_PROTAL,NewExamActivity.SHOW_EXAM);
                    intent.putExtra(NewExamActivity.PASS_ID,mTask.getExam_id());
                    startActivityForResult(intent,MainActivity.IN_PROGRESS_TO_NEW_EXAM_REQUEST_CODE);


                }

            } else if (v == textViewToTop) {
                int position = (Integer) container.getTag();
                if (mShowTasks.get(position).getTopNumber() > 0) {
                    setTaskCancelTop(position);
                } else {
                    setTaskToTop(position);
                }
            }
        }

        /**
         * show about tomato time
         */
        private void showTomatoDialog() {
            if (timeWatchDialog == null) {
                timeWatchDialog = new TimeWatchDialog(getContext());
                timeWatchDialog.setOnCancelClickedListener(new TimeWatchDialog.CancelClickedListener() {
                    @Override
                    public void onCancelClickedListener() {
                        imageButtonStatus.setSelected(false);
                        timeWatchDialog.stop();
//                        long spendTime = timeWatchDialog.stop();
//                        Log.d("TTT", "onCancelClickedListener: " + spendTime);
                    }
                });
            }
            timeWatchDialog.show();
            timeWatchDialog.start();

        }
    }

    private void setTaskCancelTop(int position) {

        PostTaskData eventTask = copyTask(mShowTasks.get(position));
        eventTask.setTopNumber(EventTask.TOP_NORMAL);
        mHelper.update(eventTask);
        mShowMenuItem.setShowMenuItem(false);
        isShowCheckBox = false;
        mAdapter.customDeleteNotifyDataSetChanged();

    }


    /**
     * set task to top
     *
     * @param position
     */
    private void setTaskToTop(Integer position) {
        List<PostTaskData> adjustList = null;
        PostTaskData newTopTask = copyTask(mShowTasks.get(position));
        newTopTask.setTopNumber(EventTask.TOP_THREE);
        adjustList = adjustOrder(mShowTasks.get(position).getTopNumber());
        adjustList.add(newTopTask);
        for (int i = 0; i < adjustList.size(); ++i) {
            mHelper.update(adjustList.get(i));
        }
        mShowMenuItem.setShowMenuItem(false);
        isShowCheckBox = false;
        mAdapter.customDeleteNotifyDataSetChanged();
    }

    private List<PostTaskData> adjustOrder(Integer topNumber) {
        List<PostTaskData> tmpList = new ArrayList<>();
        PostTaskData tmpTask;
        PostTaskData resultTask = null;
        for (int i = 0; i < mTopTasks.size(); ++i) {
            tmpTask = mTopTasks.get(i);
            if (tmpTask.getTopNumber() == topNumber) {
                continue;
            }
            if (tmpTask.getTopNumber() > 0) {
                resultTask = copyTask(tmpTask);
                if (resultTask.getTopNumber() == EventTask.TOP_ONE) {
                    resultTask.setTopNumber(EventTask.TOP_NORMAL);

                } else {

                    resultTask.setTopNumber(tmpTask.getTopNumber() - 1);
                }
                tmpList.add(resultTask);
            }
        }

        return tmpList;
    }


    private PostTaskData copyTask(PostTaskData tmpTask) {
        PostTaskData result = new PostTaskData();
        result.setExam_id(tmpTask.getExam_id());
        result.setExam_title(tmpTask.getExam_title());
        result.setExam_content(tmpTask.getExam_content());
        result.setExam_lan(tmpTask.getExam_lan());
        result.setExam_in_arg(tmpTask.getExam_in_arg());
        result.setExam_out_arg(tmpTask.getExam_out_arg());
        result.setExam_deadline(tmpTask.getExam_deadline());
        result.setExam_update_time(tmpTask.getExam_update_time());
        result.setExam_remark(tmpTask.getExam_remark());
        result.setExam_tatal(tmpTask.getExam_tatal());
        result.setExam_commit_count(tmpTask.getExam_commit_count());
        result.setExam_correct_count(tmpTask.getExam_correct_count());
        result.setStatus(tmpTask.getStatus());
        result.setTopNumber(tmpTask.getTopNumber());
        return result;
    }

    /**
     * control about the delete checkbox visible or not
     */

    private void itemContainerLongClickedEvent() {
        isShowCheckBox = true;
        mShowMenuItem.setShowMenuItem(true);
        mAdapter.customNotifyDataSetChanged();
    }

    /**
     * control delete task listl
     *
     * @param isDelete
     */

    public void finishDeleteAction(boolean isDelete) {
        isShowCheckBox = false;
        if (isDelete) {
            List<PostTaskData> tasks = new ArrayList<>();
            for (int position : mItemsSelectStatus.keySet()) {
                if (mItemsSelectStatus.get(position)) {
                    tasks.add(mShowTasks.get(position));
                }
            }
            for (PostTaskData task1 : tasks) {
                mHelper.delete(task1);
            }
            mAdapter.customDeleteNotifyDataSetChanged();
        }

        mAdapter.customNotifyDataSetChanged();
    }

    public void finishTaskAction() {
        isShowCheckBox = false;
        List<PostTaskData> tasks = new ArrayList<>();
        for (int position : mItemsSelectStatus.keySet()) {
            if (mItemsSelectStatus.get(position)) {
                tasks.add(mShowTasks.get(position));
            }
        }
        PostTaskData upDateTask = new PostTaskData();
        for (PostTaskData task1 : tasks) {
            TranserverUtil.transPostTask(upDateTask, task1);
            upDateTask.setStatus(PostTaskData.STATUS_DATA_PASS);
            mHelper.update(upDateTask);
        }
        mAdapter.customDeleteNotifyDataSetChanged();
    }


    public interface ShowMainMenuItem {
        void setShowMenuItem(boolean isShow);
    }

}
