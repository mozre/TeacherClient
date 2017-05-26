package com.ckt.ckttodo.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ckt.ckttodo.database.Exam;
import com.ckt.ckttodo.databinding.FragmentTaskBinding;
import com.ckt.ckttodo.databinding.TaskListItemBinding;
import com.ckt.ckttodo.presenter.PostDetailPresenter;
import com.ckt.ckttodo.widgt.TaskDividerItemDecoration;
import com.ckt.ckttodo.widgt.TimeWatchDialog;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mozre
 */
public class InProgressTaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        EndlessRecyclerView.OnLoadMoreListener, CommonFragmentView {

    private static final String TAG = "InProgressTaskFragment";
    private FragmentTaskBinding mFragmentTaskBinding;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerView mRecyclerView;
    private TaskRecyclerViewAdapter mAdapter;
    private List<Exam> mTasks = new ArrayList<>();
    private LinkedList<Exam> mShowTasks;
    private LinkedList<Exam> mTopTasks = new LinkedList<>();
    private static boolean isShowCheckBox = false;
    private Map<Integer, Boolean> mItemsSelectStatus = new HashMap<>();
    private ShowMainMenuItem mShowMenuItem;
    private DatabaseHelper mHelper;
    private Context mContext;

    private static final int PAGE_COUNT = 30;

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
        getFistDataList();
        mFragmentTaskBinding = FragmentTaskBinding.inflate(inflater);
        mRecyclerView = mFragmentTaskBinding.recyclerTaskList;
        mSwipeRefreshLayout = mFragmentTaskBinding.commonHomeFragmentRefresh;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);

        // Recyclerview Adapter设置 及布局展示设置
        mAdapter = new TaskRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new TaskDividerItemDecoration(getContext(),
                TaskDividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        return mFragmentTaskBinding.getRoot();
    }


    //     过滤 Task 获取属于当前状态的Exam
    private void screenTask(List<Exam> tasks) {
        if (mShowTasks == null) {
            mShowTasks = new LinkedList<>();
        }
        mShowTasks.clear();
        mTopTasks.clear();
        long now = Calendar.getInstance().getTimeInMillis();
        for (Exam task : tasks) {
            if (task.getExam_deadline() > now && task.getStatus() == Exam.STATUS_DATA_PASS) {
                if (task.getTopNumber() > 0) {
                    mTopTasks.add(task);
                    continue;
                }
                mShowTasks.addLast(task);
            }
        }
        sortTop(mTopTasks);
    }

    // 置顶相关，此处未用
    private void sortTop(LinkedList<Exam> list) {
        if (list.size() == 0) {
            return;
        }
        if (list.size() == 1) {
            mShowTasks.addFirst(list.get(0));
            return;
        }
        Exam tmpTask;
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
        getFistDataList();
        mAdapter.customNotifyDataSetChanged();
    }


    private class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewHolder> {

        @Override
        public TaskRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TaskListItemBinding taskListItemBinding = DataBindingUtil.inflate(LayoutInflater.
                    from(getContext()), R.layout.task_list_item, parent, false);


            return new TaskRecyclerViewHolder(taskListItemBinding);
        }

        /**
         * clear mItemsSelectStatus before notifyDataSetChanged 更新recyler同时初始化chcekbox状态集合
         */

        public void customNotifyDataSetChanged() {
            resetItemSelectStatus(mItemsSelectStatus);
            notifyDataSetChanged();
        }


        /**
         * if delete data,show update mTasks data 删除某些item后的视图更新
         */

        public void customDeleteNotifyDataSetChanged() {
            getFistDataList();
            resetItemSelectStatus(mItemsSelectStatus);
            notifyDataSetChanged();
        }

        // 每次初始化checkbox记录
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
        Exam mTask;
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
// DataBinding相关用于实现数据填充

        public void setData(Exam data) {
            this.mTask = data;
            mBinding.setTask(data);
            mBinding.executePendingBindings();
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == container) {
//                itemContainerLongClickedEvent();
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
                    //点击进入详情Activity
                    Intent intent = new Intent(getContext(), NewExamActivity.class);
                    intent.putExtra(NewExamActivity.PASS_PROTAL, NewExamActivity.SHOW_EXAM);
                    intent.putExtra(NewExamActivity.PASS_ID, mTask.getExam_id());
                    startActivityForResult(intent, MainActivity.IN_PROGRESS_TO_NEW_EXAM_REQUEST_CODE);


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

        Exam eventTask = copyTask(mShowTasks.get(position));
        eventTask.setTopNumber(Exam.TOP_NORMAL);
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
        List<Exam> adjustList = null;
        Exam newTopTask = copyTask(mShowTasks.get(position));
        newTopTask.setTopNumber(Exam.TOP_THREE);
        adjustList = adjustOrder(mShowTasks.get(position).getTopNumber());
        adjustList.add(newTopTask);
        for (int i = 0; i < adjustList.size(); ++i) {
            mHelper.update(adjustList.get(i));
        }
        mShowMenuItem.setShowMenuItem(false);
        isShowCheckBox = false;
        mAdapter.customDeleteNotifyDataSetChanged();
    }

    private List<Exam> adjustOrder(Integer topNumber) {
        List<Exam> tmpList = new ArrayList<>();
        Exam tmpTask;
        Exam resultTask = null;
        for (int i = 0; i < mTopTasks.size(); ++i) {
            tmpTask = mTopTasks.get(i);
            if (tmpTask.getTopNumber() == topNumber) {
                continue;
            }
            if (tmpTask.getTopNumber() > 0) {
                resultTask = copyTask(tmpTask);
                if (resultTask.getTopNumber() == Exam.TOP_ONE) {
                    resultTask.setTopNumber(Exam.TOP_NORMAL);

                } else {

                    resultTask.setTopNumber(tmpTask.getTopNumber() - 1);
                }
                tmpList.add(resultTask);
            }
        }

        return tmpList;
    }

    // 由于数据库 查询得到的数据不能修改，故只能产生一个新的对象后填写数据后再做数据库操作
    private Exam copyTask(Exam tmpTask) {
        Exam result = new Exam();
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
     * control about the delete checkbox visible or not 处理长按事件
     */

    private void itemContainerLongClickedEvent() {
        isShowCheckBox = true;
        mShowMenuItem.setShowMenuItem(true);
        mAdapter.customNotifyDataSetChanged();
    }

    /**
     * control delete task listl 删除完成后调用 用于删除本地数据库的数据
     *
     * @param isDelete
     */

    public void finishDeleteAction(boolean isDelete) {
        isShowCheckBox = false;
        if (isDelete) {
            List<Exam> tasks = new ArrayList<>();
            for (int position : mItemsSelectStatus.keySet()) {
                if (mItemsSelectStatus.get(position)) {
                    tasks.add(mShowTasks.get(position));
                }
            }
            for (Exam task1 : tasks) {
                mHelper.delete(task1);
            }
            mAdapter.customDeleteNotifyDataSetChanged();
        }

        mAdapter.customNotifyDataSetChanged();
    }
//
//    public void finishTaskAction() {
//        isShowCheckBox = false;
//        List<Exam> tasks = new ArrayList<>();
//        for (int position : mItemsSelectStatus.keySet()) {
//            if (mItemsSelectStatus.get(position)) {
//                tasks.add(mShowTasks.get(position));
//            }
//        }
//        Exam upDateTask = new Exam();
//        for (Exam task1 : tasks) {
//            TranserverUtil.transPostTask(upDateTask, task1);
//            upDateTask.setStatus(Exam.STATUS_DATA_PASS);
//            mHelper.update(upDateTask);
//        }
//        mAdapter.customDeleteNotifyDataSetChanged();
//    }

    // 控制Menu展示的接口
    public interface ShowMainMenuItem {
        void setShowMenuItem(boolean isShow);
    }

    // 刷新接口
    @Override
    public void onRefresh() {
        PostDetailPresenter presenter = new PostDetailPresenter(mContext, this, mHelper);

        presenter.postArticleDetail(0, PostDetailPresenter.ACTION_PULL, Exam.STATUS_DATA_PASS);
        mRecyclerView.enable(false);
        mSwipeRefreshLayout.setEnabled(false);

    }

    // 加载更多接口
    @Override
    public void onLoadMore(EndlessRecyclerView view) {
        Long seconds = null;
        PostDetailPresenter presenter = new PostDetailPresenter(mContext, this, mHelper);
        if (mShowTasks.size() > 0) {
            try {
                presenter.postArticleDetail(mShowTasks.getLast().getExam_deadline(), PostDetailPresenter.ACTION_PUSH, Exam.STATUS_DATA_PASS);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mRecyclerView.onComplete();
            return;
        }
        mRecyclerView.enable(false);
        mSwipeRefreshLayout.setEnabled(false);
    }

    // 网络请求返回，当用户状态失效，退出此界面进入login页面
    @Override
    public void userNeedDoLogin() {

        getActivity().setResult(MainActivity.LOGIN_OUT_RESULT_CODE);
        getActivity().finish();

    }

    // 上拉刷新或下拉加载更多后，回调此方法进行数据的展示
    @Override
    public void notifyNewData(int action) {
        if (action == PostDetailPresenter.ACTION_PULL) {
            //TODO 下拉
            getFistDataList();
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            //TODO 上拉
            getMoreDataList();

            mRecyclerView.onComplete();
        }
        mRecyclerView.enable(true);
        mSwipeRefreshLayout.setEnabled(true);
    }

    // 网络请求错误后展示
    @Override
    public void notfyNetworkRequestErro() {
        Toast.makeText(mContext, "网络请求失败！", Toast.LENGTH_SHORT).show();
        if (mRecyclerView.isLoadingMore()) {
            mRecyclerView.onComplete();
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.enable(true);
        mSwipeRefreshLayout.setEnabled(true);

    }

    @Override
    public void makeMessage(String message) {

    }

    // 筛选处于发布状态 Exam
    private void getFistDataList() {
        Iterator<Exam> iterator = mHelper.getRealm().allObjectsSorted(Exam.class, Exam.EXAM_DEADLINE, true).iterator();
        mTasks.clear();
        int i = PAGE_COUNT;
        Exam data;
        while (iterator.hasNext() && i != 1) {
            data = iterator.next();
            if (data.getStatus() == Exam.STATUS_DATA_PASS) {
                mTasks.add(data);
                --i;
            }

        }
        screenTask(mTasks);
        return;


    }

    // 筛选处于发布状态的Exam，大于当前最小时间 加载更多时调用
    private void getMoreDataList() {
        Iterator<Exam> iterator = mHelper.getRealm().allObjectsSorted(Exam.class, Exam.EXAM_UPDATE_TIME, true).iterator();
        Exam data = mShowTasks.getLast();
//        if (mTopTasks.size() > 0) {
//            for (int i = 0; mTopTasks.size() > i; ++i) {
//                if (data.getExam_deadline() > mTopTasks.get(i).getExam_deadline()) {
//                    data = mTopTasks.get(i);
//                }
//            }
//        }
        Exam tmp;
        while (iterator.hasNext()) {
            tmp = iterator.next();
            if (tmp.getExam_deadline() < data.getExam_deadline()) {
                mTasks.add(tmp);
                break;
            }
        }
        for (int i = 0; i < 40 && iterator.hasNext(); ++i) {
            mTasks.add(iterator.next());
        }

    }
}
